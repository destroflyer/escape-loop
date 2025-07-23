package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.Character;
import com.destroflyer.escapeloop.game.Player;

import lombok.Getter;
import lombok.Setter;

public class MapRenderer {

    public MapRenderer(Map map) {
        this.map = map;
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        polygonSpriteBatch = new PolygonSpriteBatch();
        backgroundTexture = new Texture("./maps/" + map.getName() + "/_composite.png");
    }
    private Map map;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private PolygonSpriteBatch polygonSpriteBatch;
    private Texture backgroundTexture;
    @Getter
    @Setter
    private boolean debug;

    public void render() {
        drawBackground();
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

    private void drawBackground() {
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

        Texture texture = mapObject.getCurrentTexture();
        TextureRegion textureRegion = mapObject.getCurrentTextureRegion();
        if ((texture != null) || (textureRegion != null)) {
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

            if (texture != null) {
                spriteBatch.draw(texture, 0, 0, width, height);
            }
            if (textureRegion != null) {
                spriteBatch.draw(textureRegion, 0, 0, width, height);
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
                return (character == map.getPlayer()) ? new Color(1, 0, 0, alpha) : new Color(0, 1, 0, alpha);
            }
        }
        return new Color(0.05f, 0.05f, 0.05f, alpha);
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

    public void resize(Matrix4 projectionMatrix) {
        spriteBatch.setProjectionMatrix(projectionMatrix);
        shapeRenderer.setProjectionMatrix(projectionMatrix);
        polygonSpriteBatch.setProjectionMatrix(projectionMatrix);
    }

    private int convertMapSize(float coordinate) {
        float mapToPanel = (Main.VIEWPORT_WIDTH / map.getWidth());
        return Math.round(coordinate * mapToPanel);
    }
}
