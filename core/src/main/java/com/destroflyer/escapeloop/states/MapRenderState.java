package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Gate;
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
    @Getter
    @Setter
    private boolean debug;

    @Override
    public void render() {
        Map map = mapState.getMap();
        drawBackground(map);
        for (MapObject mapObject : map.getObjects()) {
            if (!(mapObject instanceof Character)) {
                drawMapObject(mapObject);
            }
        }
        for (MapObject mapObject : map.getObjects()) {
            if (mapObject instanceof Character) {
                drawMapObject(mapObject);
            }
        }
    }

    private void drawBackground(Map map) {
        if (backgroundTexture == null) {
            backgroundTexture = new Texture("./maps/" + map.getName() + "/_composite.png");
        }
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
        spriteBatch.end();
    }

    private void drawMapObject(MapObject mapObject) {
        Body body = mapObject.getBody();
        int bodyX = convertMapSize(body.getPosition().x);
        int bodyY = convertMapSize(body.getPosition().y);
        float bodyAngle = -1 * body.getAngle();
        float alpha = getAlpha(mapObject);

        if (debug) {
            Array<Fixture> fixtures = body.getFixtureList();
            int fixtureIndex = 0;
            for (Fixture fixture : fixtures) {
                Shape shape = fixture.getShape();
                Color color = getShapeColor(mapObject, fixtureIndex, alpha);

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
        }

        TextureRegion textureRegion = mapObject.getCurrentTextureRegion();
        if (textureRegion != null) {
            Matrix4 originalBatchTransform = spriteBatch.getTransformMatrix().cpy();

            spriteBatch.begin();
            Matrix4 bodyTransform = new Matrix4();
            int xDirection = 1;
            if (mapObject instanceof Character) {
                Character character = (Character) mapObject;
                xDirection = character.getViewDirection();
            }
            int offsetX = convertMapSize(mapObject.getTextureOffset().x);
            int offsetY = convertMapSize(mapObject.getTextureOffset().y);
            int width = convertMapSize(mapObject.getTextureSize().x);
            int height = convertMapSize(mapObject.getTextureSize().y);
            bodyTransform.translate(bodyX + (xDirection * ((width / -2f) + offsetX)), bodyY + (height / -2f) + offsetY, 0);
            bodyTransform.rotateRad(0, 0, 1, bodyAngle);
            bodyTransform.scl(xDirection, 1, 1);
            spriteBatch.setTransformMatrix(bodyTransform);

            spriteBatch.setColor(1, 1, 1, alpha);

            int tilesX = 1;
            int tilesY = 1;
            float tileAngle = 0;
            if (mapObject instanceof Gate) {
                Gate gate = (Gate) mapObject;
                tilesX = (int) (gate.getWidth() / Map.TILE_SIZE);
                tilesY = (int) (gate.getHeight() / Map.TILE_SIZE);
                if (tilesY > tilesX) {
                    tileAngle = 90;
                }
            }
            int textureOffsetX = (((tilesX - 1) * width) / -2);
            int textureOffsetY = (((tilesY - 1) * height) / -2);
            for (int tileX = 0; tileX < tilesX; tileX++) {
                for (int tileY = 0; tileY < tilesY; tileY++) {
                    int x = textureOffsetX + convertMapSize(tileX * Map.TILE_SIZE);
                    int y = textureOffsetY + convertMapSize(tileY * Map.TILE_SIZE);
                    spriteBatch.draw(textureRegion, x, y, (width / 2f), (height / 2f), width, height, 1, 1, tileAngle);
                }
            }

            spriteBatch.setColor(1, 1, 1, 1);

            spriteBatch.setTransformMatrix(originalBatchTransform);
            spriteBatch.end();
        }
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
