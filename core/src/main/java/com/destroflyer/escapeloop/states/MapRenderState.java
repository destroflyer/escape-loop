package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Direction;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.MapText;
import com.destroflyer.escapeloop.game.Particles;
import com.destroflyer.escapeloop.game.PlayerPast;
import com.destroflyer.escapeloop.game.PlayerPastFrame;
import com.destroflyer.escapeloop.game.PlayerPastWithIndex;
import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Gate;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.Ground;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.util.RenderUtil;

import java.util.ArrayList;

public class MapRenderState extends State {

    public MapRenderState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private Texture backgroundTexture;
    private Texture terrainTexture;
    private Texture decorationTexture;
    private BitmapFont textFont = new BitmapFont();
    private GlyphLayout textLayout = new GlyphLayout();
    private Rectangle bounds = new Rectangle();
    private boolean debug;

    @Override
    public void render() {
        if (backgroundTexture == null) {
            backgroundTexture = new Texture("./textures/cave/background.png");
            terrainTexture = new Texture("./maps/" + mapState.getMap().getMapIndex() + "/terrain.png");
            decorationTexture = new Texture("./maps/" + mapState.getMap().getMapIndex() + "/decoration.png");
        }
        Cinematic cinematic = mapState.getMap().getCinematic();
        updateBounds(cinematic);
        drawFullScreenTexture(backgroundTexture);
        drawMapObjects(mapObject -> true, MapRenderLayer.BACKGROUND);
        drawFullScreenTexture(terrainTexture);
        drawFullScreenTexture(decorationTexture);
        if (cinematic == null) {
            drawMapTexts();
        }
        drawMapObjects(mapObject -> !(mapObject instanceof Character) && !(mapObject instanceof Item), MapRenderLayer.FOREGROUND);
        drawMapObjects(mapObject -> mapObject instanceof Character, MapRenderLayer.FOREGROUND);
        drawMapObjects(mapObject -> mapObject instanceof Item, MapRenderLayer.FOREGROUND);
        if (cinematic != null) {
            cinematic.render(spriteBatch, shapeRenderer);
        }
    }

    private void updateBounds(Cinematic cinematic) {
        bounds.set(0, 0, mapState.getMap().getWidth(), mapState.getMap().getHeight());
        if (cinematic != null) {
            cinematic.updateRenderBounds(bounds);
        }
    }

    private void drawFullScreenTexture(Texture texture) {
        float x = convertMapX(0);
        float y = convertMapY(0);
        float width = convertMapWidth(mapState.getMap().getWidth());
        float height = convertMapHeight(mapState.getMap().getHeight());
        spriteBatch.begin();
        spriteBatch.draw(texture, x, y, width, height);
        spriteBatch.end();
    }

    private void drawMapTexts() {
        for (MapText mapText : mapState.getMap().getTexts()) {
            int x = convertMapX(mapText.getPosition().x);
            int y = convertMapY(mapText.getPosition().y);
            String text = main.getSettingsState().replacePlaceholders(mapText.getText());
            drawCenteredText(x, y, text, Color.WHITE, mapText.getWidth());
        }
    }

    private void drawMapObjects(Predicate<MapObject> filter, MapRenderLayer layer) {
        for (MapObject mapObject : mapState.getMap().getObjects()) {
            if (mapObject.isVisible() && filter.evaluate(mapObject)) {
                drawMapObject(mapObject, layer);
            }
        }
    }

    private void drawMapObject(MapObject mapObject, MapRenderLayer layer) {
        Body body = mapObject.getBody();
        int bodyX = convertMapX(body.getPosition().x);
        int bodyY = convertMapY(body.getPosition().y);
        float bodyAngle = body.getAngle();
        float alpha = getAlpha(mapObject);

        // When circular objects are rotated and their textures are rendered as ellipse due to distorted bounds, this visual ellipse doesn't match their physics/hitbox circle anymore
        // Therefore, we just fixate their visual rotation for now
        if (isDistortedBounds() && (mapObject instanceof Item)) {
            bodyAngle = 0;
        }

        Direction textureDirection = mapObject.getTextureDirection();
        int textureDirectionX = ((textureDirection.getX() != 0) ? textureDirection.getX() : 1);
        int textureDirectionY = ((textureDirection.getY() != 0) ? textureDirection.getY() : 1);
        int textureOffsetX = convertMapWidth(mapObject.getTextureOffset().x);
        int textureOffsetY = convertMapHeight(mapObject.getTextureOffset().y);
        int textureWidth = convertMapWidth(mapObject.getTextureSize().x);
        int textureHeight = convertMapHeight(mapObject.getTextureSize().y);

        Matrix4 centerTransform = new Matrix4();
        centerTransform.translate(bodyX, bodyY, 0);

        Matrix4 centerAndRotationTransform = centerTransform.cpy();
        centerAndRotationTransform.rotateRad(0, 0, 1, bodyAngle);

        float centerToBottom = textureDirectionY * ((textureHeight / -2f) + textureOffsetY);

        Matrix4 centerTopTransform = centerAndRotationTransform.cpy();
        centerTopTransform.translate(0, -1 * centerToBottom, 0);

        Matrix4 centerBottomTransform = centerAndRotationTransform.cpy();
        centerBottomTransform.translate(0, centerToBottom, 0);

        Matrix4 leftBottomTransform = centerBottomTransform.cpy();
        leftBottomTransform.translate(textureDirectionX * ((textureWidth / -2f) + textureOffsetX), 0, 0);

        Matrix4 leftBottomWithDirectionTransform = leftBottomTransform.cpy();
        leftBottomWithDirectionTransform.scl(textureDirectionX, textureDirectionY, 1);

        switch (layer) {
            case BACKGROUND:
                Particles particles = mapObject.getParticles();
                if (particles != null) {
                    drawParticles(particles, centerTransform, textureWidth);
                }
                PlayerPastWithIndex playerPastWithIndex = getPlayerPastWithIndex(mapObject);
                if (playerPastWithIndex != null) {
                    drawPlayerPastTrajectory(playerPastWithIndex);
                }
                break;
            case FOREGROUND:
                if (mapObject.hasTexture()) {
                    drawTexture(mapObject, leftBottomWithDirectionTransform, textureWidth, textureHeight, alpha);
                }
                String speech = mapObject.getSpeech();
                if (speech != null) {
                    drawSpeech(speech, centerTopTransform);
                }
                if (debug) {
                    drawDebugShape(mapObject, bodyX, bodyY, alpha);
                }
                break;
        }
    }

    private void drawParticles(Particles particles, Matrix4 centerTransform, float textureWidth) {
        Matrix4 originalTransform = shapeRenderer.getTransformMatrix().cpy();

        int[] indices = new int[] { 1, 5, 2, 4, 0, 3 };
        float[] progresses = new float[indices.length];
        float progressOffset = ((mapState.getMap().getTime() % particles.getDuration()) / particles.getDuration());
        for (int i = 0; i < progresses.length; i++) {
            progresses[i] = (progressOffset + (((float) i) / progresses.length)) % 1;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setTransformMatrix(centerTransform);

        for (int i = 0; i < indices.length; i++) {
            float indexPortion = ((float) indices[i] / (indices.length - 1));
            shapeRenderer.setColor(1, 1, 1, 1 - progresses[i]);
            switch (particles) {
                case CIRCLE: {
                    float mapDiameter = progresses[i] * 0.75f;
                    int width = convertMapWidth(mapDiameter);
                    int height = convertMapHeight(mapDiameter);
                    float x = width / -2f;
                    float y = height / -2f;
                    shapeRenderer.ellipse(x, y, width, height, 10);
                    break;
                }
                case UP:
                case DOWN: {
                    int length = convertMapHeight(0.125f);
                    int distance = convertMapHeight(0.25f);
                    float x = ((indexPortion - 0.5f) * textureWidth);
                    float yDirection = ((particles == Particles.UP) ? 1 : -1);
                    float y1 = (progresses[i] - 1.25f) * (yDirection * distance);
                    float y2 = y1 + (yDirection * length);
                    shapeRenderer.line(x, y1, x, y2);
                    break;
                }
            }
        }

        shapeRenderer.setTransformMatrix(originalTransform);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawPlayerPastTrajectory(PlayerPastWithIndex playerPastWithIndex) {
        float maximumTime = mapState.getMap().getTime() + main.getSettingsState().getPreferences().getFloat("playerPastsTrajectoryDuration");
        ArrayList<Vector2> trajectoryPoints = new ArrayList<>();
        for (PlayerPastFrame frame : playerPastWithIndex.getPlayerPast().getRemainingFrames()) {
            if (frame.getTime() > maximumTime) {
                break;
            }
            int x = convertMapX(frame.getPosition().x);
            int y = convertMapY(frame.getPosition().y);
            trajectoryPoints.add(new Vector2(x, y));
        }
        Color color = getMapObjectTintColor(playerPastWithIndex.getPlayerPast().getPlayer());
        if (trajectoryPoints.size() > 1) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < trajectoryPoints.size() - 1; i++) {
                float progress = ((float) i) / (trajectoryPoints.size() - 1);
                color.a = 1 - progress;
                shapeRenderer.setColor(color);
                Vector2 start = trajectoryPoints.get(i);
                Vector2 end = trajectoryPoints.get(i + 1);
                shapeRenderer.line(start.x, start.y, end.x, end.y);
            }
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void drawTexture(MapObject mapObject, Matrix4 leftBottomWithDirectionTransform, int textureWidth, int textureHeight, float alpha) {
        Matrix4 originalTransform = spriteBatch.getTransformMatrix().cpy();

        Rectangle clipBounds = null;

        int tilesX = 1;
        int tilesY = 1;
        float tileAngle = 0;
        if (mapObject instanceof Ground) {
            Ground ground = (Ground) mapObject;
            tilesX = (int) (ground.getWidth() / Map.TILE_SIZE);
            tilesY = (int) (ground.getHeight() / Map.TILE_SIZE);
        } else if (mapObject instanceof Gate) {
            Gate gate = (Gate) mapObject;
            tilesX = (int) (gate.getWidth() / Map.TILE_SIZE);
            tilesY = (int) (gate.getHeight() / Map.TILE_SIZE);
            if (tilesX > tilesY) {
                tileAngle = 90;
                clipBounds = new Rectangle(convertMapX((gate.getOpenProgress() - 0.5f) * gate.getWidth()) + (textureWidth / 2f), ((tilesY / -2f) + 0.5f) * textureHeight, convertMapWidth(gate.getWidth()), tilesX * textureHeight);
            } else {
                clipBounds = new Rectangle(((tilesX / -2f) + 0.5f) * textureWidth, convertMapY((gate.getOpenProgress() - 0.5f) * gate.getHeight()) + (textureHeight / 2f), tilesY * textureWidth, convertMapHeight(gate.getHeight()));
            }
        }

        if (clipBounds != null) {
            Rectangle scissor = new Rectangle();
            ScissorStack.calculateScissors(main.getViewport().getCamera(), leftBottomWithDirectionTransform, clipBounds, scissor);
            if (!ScissorStack.pushScissors(scissor)) {
                return;
            }
        }

        spriteBatch.begin();
        spriteBatch.setTransformMatrix(leftBottomWithDirectionTransform);
        spriteBatch.setColor(getMapObjectTintColor(mapObject, alpha));

        int tileOffsetX = (((tilesX - 1) * textureWidth) / -2);
        int tileOffsetY = (((tilesY - 1) * textureHeight) / -2);
        for (int tileX = 0; tileX < tilesX; tileX++) {
            for (int tileY = 0; tileY < tilesY; tileY++) {
                float x = tileOffsetX + (tileX * textureWidth);
                float y = tileOffsetY + (tileY * textureHeight);
                TextureRegion textureRegion = mapObject.getTextureRegion(tileX, tileY, tilesX, tilesY);
                spriteBatch.draw(textureRegion, x, y, (textureWidth / 2f), (textureHeight / 2f), textureWidth, textureHeight, 1, 1, tileAngle);
            }
        }

        spriteBatch.setColor(1, 1, 1, 1);
        spriteBatch.setTransformMatrix(originalTransform);
        spriteBatch.end();

        if (clipBounds != null) {
            ScissorStack.popScissors();
        }
    }

    private Color getMapObjectTintColor(MapObject mapObject) {
        return getMapObjectTintColor(mapObject, 1);
    }

    private Color getMapObjectTintColor(MapObject mapObject, float alpha) {
        PlayerPastWithIndex playerPastWithIndex = getPlayerPastWithIndex(mapObject);
        if ((playerPastWithIndex != null) && main.getSettingsState().getPreferences().getBoolean("playerPastsDistinctColors")) {
            switch (playerPastWithIndex.getIndex()) {
                case 0: return new Color(1, 0, 0, alpha);
                case 1: return new Color(0, 1, 0, alpha);
                case 2: return new Color(0, 0, 1, alpha);
            }
        }
        return new Color(1, 1, 1, alpha);
    }

    private PlayerPastWithIndex getPlayerPastWithIndex(MapObject mapObject) {
        if (mapObject instanceof Player) {
            ArrayList<PlayerPast> playerPasts = mapState.getMap().getPlayerPasts();
            for (int i = 0; i < playerPasts.size(); i++) {
                PlayerPast playerPast = playerPasts.get(i);
                if (playerPast.getPlayer() == mapObject) {
                    return new PlayerPastWithIndex(i, playerPast);
                }
            }
        }
        return null;
    }

    private void drawSpeech(String speech, Matrix4 centerTopTransform) {
        Matrix4 originalShapeRendererTransform = shapeRenderer.getTransformMatrix().cpy();
        Matrix4 originalSpriteBatchTransform = spriteBatch.getTransformMatrix().cpy();

        shapeRenderer.setTransformMatrix(centerTopTransform);
        spriteBatch.setTransformMatrix(centerTopTransform);

        int width = 120;
        int height = 40;
        int offsetY = 15;
        int padding = 5;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect((width / -2f), offsetY, width, height);
        shapeRenderer.setColor(Color.BLACK);
        drawRectWithThickness((width / -2f), offsetY, width, height, 2);
        shapeRenderer.end();

        drawCenteredText(0, offsetY + (height / 2), speech, Color.BLACK, width - (2 * padding));

        shapeRenderer.setTransformMatrix(originalShapeRendererTransform);
        spriteBatch.setTransformMatrix(originalSpriteBatchTransform);
    }

    private void drawRectWithThickness(float x, float y, float width, float height, float lineThickness) {
        shapeRenderer.rect(x, y, width, lineThickness);
        shapeRenderer.rect(x, y + height - lineThickness, width, lineThickness);
        shapeRenderer.rect(x, y + lineThickness, lineThickness, height - (2 * lineThickness));
        shapeRenderer.rect(x + width - lineThickness, y + lineThickness, lineThickness, height - (2 * lineThickness));
    }

    private void drawDebugShape(MapObject mapObject, int bodyX, int bodyY, float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Body body = mapObject.getBody();
        Array<Fixture> fixtures = body.getFixtureList();
        int fixtureIndex = 0;
        for (Fixture fixture : fixtures) {
            Shape shape = fixture.getShape();
            Color color = getDebugShapeColor(mapObject, fixtureIndex, alpha * 0.5f);

            if (shape instanceof PolygonShape) {
                PolygonShape polygonShape = (PolygonShape) shape;

                int vertexCount = polygonShape.getVertexCount();
                float[] positions = new float[vertexCount * 2];
                int positionOffset = 0;
                for (int i = 0; i < vertexCount; i++) {
                    Vector2 localVertex = new Vector2();
                    polygonShape.getVertex(i, localVertex);
                    Vector2 worldVertex = body.getWorldPoint(localVertex);
                    positions[positionOffset++] = convertMapX(worldVertex.x);
                    positions[positionOffset++] = convertMapY(worldVertex.y);
                }
                short[] indices = new EarClippingTriangulator().computeTriangles(positions).items;

                Texture textureSolid;
                Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pixmap.setColor(color);
                pixmap.fill();
                textureSolid = new Texture(pixmap);
                PolygonRegion polygonRegion = new PolygonRegion(new TextureRegion(textureSolid), positions, indices);

                polygonSpriteBatch.begin();
                polygonSpriteBatch.draw(polygonRegion, 0, 0);
                polygonSpriteBatch.end();
            } else if (shape instanceof CircleShape) {
                CircleShape circleShape = (CircleShape) shape;
                int width = convertMapWidth(circleShape.getRadius() * 2);
                int height = convertMapHeight(circleShape.getRadius() * 2);

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(color);
                shapeRenderer.ellipse(bodyX - (width / 2f), bodyY - (height / 2f), width, height);
                shapeRenderer.end();
            } else {
                throw new IllegalArgumentException("Unsupported shape: " + shape);
            }

            fixtureIndex++;
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private Color getDebugShapeColor(MapObject mapObject, int fixtureIndex, float alpha) {
        if (mapObject instanceof Character) {
            Character character = (Character) mapObject;
            if (fixtureIndex == 1) {
                return character.isOnGround() ? new Color(1, 1, 0, 0.5f) : new Color(0, 1, 0, 0.5f);
            } else {
                return (character == mapState.getMap().getPlayer()) ? new Color(1, 0, 0, alpha) : new Color(0, 1, 0, alpha);
            }
        } else if (mapObject instanceof Ground) {
            return new Color(0.05f, 0.05f, 0.05f, alpha);
        }
        return new Color(0, 0, 1, alpha);
    }

    private float getAlpha(MapObject mapObject) {
        if (mapObject instanceof Player) {
            Player player = (Player) mapObject;
            if (!player.isCharacterCollisionsEnabled()) {
                return 0.25f;
            }
        }
        return 1;
    }

    private void drawCenteredText(int x, int y, String text, Color color, int targetWidth) {
        RenderUtil.drawCenteredText(spriteBatch, textLayout, textFont, x, y, text, color, targetWidth);
    }

    private int convertMapX(float mapX) {
        return convertMapWidth(mapX - bounds.x);
    }

    private int convertMapY(float mapY) {
        return convertMapHeight(mapY - bounds.y);
    }

    private int convertMapWidth(float width) {
        return convertMapSize(width, Main.VIEWPORT_WIDTH, bounds.width);
    }

    private int convertMapHeight(float height) {
        return convertMapSize(height, Main.VIEWPORT_HEIGHT, bounds.height);
    }

    private int convertMapSize(float mapSize, int viewportSize, float visibleMapSize) {
        float mapToPanel = (viewportSize / visibleMapSize);
        return Math.round(mapSize * mapToPanel);
    }

    private boolean isDistortedBounds() {
        return (bounds.width / bounds.height) != (((float) Main.VIEWPORT_WIDTH) / Main.VIEWPORT_HEIGHT);
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.F1) {
                    debug = !debug;
                    return true;
                }
                return false;
            }
        };
    }
}
