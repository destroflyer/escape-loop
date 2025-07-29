package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.Particles;
import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Gate;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.Platform;
import com.destroflyer.escapeloop.game.objects.Player;

import lombok.Getter;
import lombok.Setter;

public class MapRenderState extends State {

    public MapRenderState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private Texture backgroundTexture;
    private Texture terrainTexture;
    @Getter
    @Setter
    private boolean debug;

    @Override
    public void render() {
        if (backgroundTexture == null) {
            backgroundTexture = new Texture("./maps/" + mapState.getMap().getName() + "/Background.png");
            terrainTexture = new Texture("./maps/" + mapState.getMap().getName() + "/Terrain.png");
        }
        drawFullScreenTexture(backgroundTexture);
        drawMapObjects(mapObject -> true, MapRenderLayer.BACKGROUND);
        drawFullScreenTexture(terrainTexture);
        drawMapObjects(mapObject -> !(mapObject instanceof Character) && !(mapObject instanceof Item), MapRenderLayer.FOREGROUND);
        drawMapObjects(mapObject -> mapObject instanceof Character, MapRenderLayer.FOREGROUND);
        drawMapObjects(mapObject -> mapObject instanceof Item, MapRenderLayer.FOREGROUND);
    }

    private void drawFullScreenTexture(Texture texture) {
        spriteBatch.begin();
        spriteBatch.draw(texture, 0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
        spriteBatch.end();
    }

    private void drawMapObjects(Predicate<MapObject> filter, MapRenderLayer layer) {
        for (MapObject mapObject : mapState.getMap().getObjects()) {
            if (filter.evaluate(mapObject)) {
                drawMapObject(mapObject, layer);
            }
        }
    }

    private void drawMapObject(MapObject mapObject, MapRenderLayer layer) {
        Body body = mapObject.getBody();
        int bodyX = convertMapSize(body.getPosition().x);
        int bodyY = convertMapSize(body.getPosition().y);
        float bodyAngle = body.getAngle();
        float alpha = getAlpha(mapObject);

        Matrix4 centerPositionTransform = new Matrix4();
        centerPositionTransform.translate(bodyX, bodyY, 0);

        Matrix4 centerPositionAndRotationTransform = centerPositionTransform.cpy();
        centerPositionAndRotationTransform.rotateRad(0, 0, 1, bodyAngle);

        int xDirection = 1;
        if (mapObject instanceof Character) {
            Character character = (Character) mapObject;
            xDirection = character.getViewDirection();
        }
        int textureOffsetX = convertMapSize(mapObject.getTextureOffset().x);
        int textureOffsetY = convertMapSize(mapObject.getTextureOffset().y);
        int textureWidth = convertMapSize(mapObject.getTextureSize().x);
        int textureHeight = convertMapSize(mapObject.getTextureSize().y);
        Matrix4 leftTopTransform = centerPositionAndRotationTransform.cpy();
        leftTopTransform.translate(xDirection * ((textureWidth / -2f) + textureOffsetX), (textureHeight / -2f) + textureOffsetY, 0);
        leftTopTransform.scl(xDirection, 1, 1);

        switch (layer) {
            case BACKGROUND:
                Particles particles = mapObject.getCurrentParticles();
                if (particles != null) {
                    drawParticles(particles, centerPositionTransform, textureWidth);
                }
                break;
            case FOREGROUND:
                TextureRegion textureRegion = mapObject.getCurrentTextureRegion();
                if (textureRegion != null) {
                    drawTexture(mapObject, textureRegion, leftTopTransform, textureWidth, textureHeight, alpha);
                }
                if (debug) {
                    drawDebugShape(mapObject, bodyX, bodyY, alpha);
                }
                break;
        }
    }

    private void drawParticles(Particles particles, Matrix4 centerPositionTransform, float textureWidth) {
        Matrix4 originalTransform = shapeRenderer.getTransformMatrix().cpy();

        int[] indices = new int[] { 1, 5, 2, 4, 0, 3 };
        float[] progresses = new float[indices.length];
        float progressOffset = ((mapState.getMap().getTime() % particles.getDuration()) / particles.getDuration());
        for (int i = 0; i < progresses.length; i++) {
            progresses[i] = (progressOffset + (((float) i) / progresses.length)) % 1;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setTransformMatrix(centerPositionTransform);

        for (int i = 0; i < indices.length; i++) {
            float indexPortion = ((float) indices[i] / (indices.length - 1));
            shapeRenderer.setColor(1, 1, 1, 1 - progresses[i]);
            switch (particles) {
                case CIRCLE:
                    int radius = 30;
                    shapeRenderer.circle(0, 0, progresses[i] * radius, 10);
                    break;
                case UP:
                case DOWN:
                    int length = 10;
                    int distance = 20;
                    float x = ((indexPortion - 0.5f) * textureWidth);
                    float yDirection = ((particles == Particles.UP) ? 1 : -1);
                    float y1 = (progresses[i] - 1.25f) * (yDirection * distance);
                    float y2 = y1 + (yDirection * length);
                    shapeRenderer.line(x, y1, x, y2);
                    break;
            }
        }

        shapeRenderer.setTransformMatrix(originalTransform);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawTexture(MapObject mapObject, TextureRegion textureRegion, Matrix4 leftTopTransform, int textureWidth, int textureHeight, float alpha) {
        Matrix4 originalTransform = spriteBatch.getTransformMatrix().cpy();

        Rectangle clipBounds = null;

        int tilesX = 1;
        int tilesY = 1;
        float tileAngle = 0;
        if (mapObject instanceof Gate) {
            Gate gate = (Gate) mapObject;
            tilesX = (int) (gate.getWidth() / Map.TILE_SIZE);
            tilesY = (int) (gate.getHeight() / Map.TILE_SIZE);
            if (tilesX > tilesY) {
                tileAngle = 90;
                clipBounds = new Rectangle(convertMapSize((gate.getOpenProgress() - 0.5f) * gate.getWidth()) + (textureWidth / 2f), ((tilesY / -2f) + 0.5f) * textureHeight, convertMapSize(gate.getWidth()), tilesX * textureHeight);
            } else {
                clipBounds = new Rectangle(((tilesX / -2f) + 0.5f) * textureWidth, convertMapSize((gate.getOpenProgress() - 0.5f) * gate.getHeight()) + (textureHeight / 2f), tilesY * textureWidth, convertMapSize(gate.getHeight()));
            }
        }

        if (clipBounds != null) {
            Rectangle scissor = new Rectangle();
            ScissorStack.calculateScissors(main.getViewport().getCamera(), leftTopTransform, clipBounds, scissor);
            if (!ScissorStack.pushScissors(scissor)) {
                return;
            }
        }

        spriteBatch.begin();
        spriteBatch.setTransformMatrix(leftTopTransform);
        spriteBatch.setColor(1, 1, 1, alpha);

        int tileOffsetX = (((tilesX - 1) * textureWidth) / -2);
        int tileOffsetY = (((tilesY - 1) * textureHeight) / -2);
        for (int tileX = 0; tileX < tilesX; tileX++) {
            for (int tileY = 0; tileY < tilesY; tileY++) {
                int x = tileOffsetX + convertMapSize(tileX * Map.TILE_SIZE);
                int y = tileOffsetY + convertMapSize(tileY * Map.TILE_SIZE);
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

    private void drawDebugShape(MapObject mapObject, int bodyX, int bodyY, float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Body body = mapObject.getBody();
        Array<Fixture> fixtures = body.getFixtureList();
        int fixtureIndex = 0;
        for (Fixture fixture : fixtures) {
            Shape shape = fixture.getShape();
            Color color = getShapeColor(mapObject, fixtureIndex, alpha * 0.5f);

            if (shape instanceof PolygonShape) {
                PolygonShape polygonShape = (PolygonShape) shape;

                int vertexCount = polygonShape.getVertexCount();
                float[] positions = new float[vertexCount * 2];
                int positionOffset = 0;
                for (int i = 0; i < vertexCount; i++) {
                    Vector2 localVertex = new Vector2();
                    polygonShape.getVertex(i, localVertex);
                    Vector2 worldVertex = body.getWorldPoint(localVertex);
                    positions[positionOffset++] = convertMapSize(worldVertex.x);
                    positions[positionOffset++] = convertMapSize(worldVertex.y);
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
                int radius = convertMapSize(circleShape.getRadius());

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(color);
                shapeRenderer.circle(bodyX, bodyY, radius);
                shapeRenderer.end();
            } else {
                throw new IllegalArgumentException("Unsupported shape: " + shape);
            }

            fixtureIndex++;
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private Color getShapeColor(MapObject mapObject, int fixtureIndex, float alpha) {
        if (mapObject instanceof Character) {
            Character character = (Character) mapObject;
            if (fixtureIndex == 1) {
                return character.isOnGround() ? new Color(1, 1, 0, 0.5f) : new Color(0, 1, 0, 0.5f);
            } else {
                return (character == mapState.getMap().getPlayer()) ? new Color(1, 0, 0, alpha) : new Color(0, 1, 0, alpha);
            }
        } else if (mapObject instanceof Platform) {
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

    private int convertMapSize(float coordinate) {
        float mapToPanel = (Main.VIEWPORT_WIDTH / mapState.getMap().getWidth());
        return Math.round(coordinate * mapToPanel);
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.F1:
                        debug = !debug;
                        break;
                }
                return false;
            }
        };
    }
}
