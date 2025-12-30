package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.game.Skins;
import com.destroflyer.escapeloop.game.animations.EnemyAnimations;
import com.destroflyer.escapeloop.game.animations.PlayerAnimations;
import com.destroflyer.escapeloop.states.models.SkinCheckbox;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class AchievementsState extends UiState {

    private HashMap<String, SkinCheckbox> playerSkinCheckboxes = new HashMap<>();
    private HashMap<String, SkinCheckbox> enemySkinsCheckboxes = new HashMap<>();

    @Override
    public void create() {
        super.create();
        Table table = new Table();

        table.add().colspan(2);

        Label titleLabel = new Label("Achievements", main.getSkinLarge());
        table.add(titleLabel).colspan(2).padBottom(40);

        table.row();

        createSkinCheckboxes(
            table,
            Skins.PLAYER,
            playerSkinCheckboxes,
            skin -> PlayerAnimations.get(skin).getAnimationsWithTimeMachine().getRunAnimation(),
            -28,
            skin -> main.getSkinsState().selectPlayerSkin(skin),
            main.getSkinsState().getSelectedPlayerSkin()
        );

        table.row();

        createSkinCheckboxes(
            table,
            Skins.ENEMY,
            enemySkinsCheckboxes,
            skin -> EnemyAnimations.get(skin).getRunAnimation(),
            0,
            skin -> main.getSkinsState().selectEnemySkin(skin),
            main.getSkinsState().getSelectedEnemySkin()
        );

        table.row();

        table.add().colspan(2);

        TextButton backButton = new TextButton("Ok", main.getSkinLarge());
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMainMenu();
            }
        });
        table.add(backButton).colspan(2).fill().padTop(40);

        table.setFillParent(true);
        table.center();

        stage.addActor(table);
    }

    private void createSkinCheckboxes(Table table, Skin[] allSkins, HashMap<String, SkinCheckbox> skinCheckboxes, Function<Skin, Animation<TextureRegion>> getAnimation, float offsetY, Consumer<Skin> selectSkin, Skin selectedSkin) {
        CheckBox[] checkBoxes = new CheckBox[allSkins.length];
        for (int i = 0; i < allSkins.length; i++) {
            Skin skin = allSkins[i];
            CheckBox checkBox = new CheckBox(skin.getTitle(), main.getSkinLarge());
            checkBox.getCells().first().padRight(10);
            checkBox.setChecked(skin.getName().equals(selectedSkin.getName()));
            checkBox.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectSkin.accept(skin);
                }
            });
            table.add(checkBox).left().padLeft((i == 0) ? 0 : 20);

            Animation<TextureRegion> animation = getAnimation.apply(skin);
            TextureRegionDrawable animationDrawable = new TextureRegionDrawable(animation.getKeyFrame(0));
            Image image = new Image(animationDrawable);
            table.add(image).size(64, 64).padTop(offsetY);

            skinCheckboxes.put(skin.getName(), new SkinCheckbox(animation, animationDrawable));

            checkBoxes[i] = checkBox;
        }
        new ButtonGroup<>(checkBoxes);
    }

    private void backToMainMenu() {
        switchToState(main.getMainMenuState());
        playButtonSound();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        updateSkinCheckboxes(playerSkinCheckboxes);
        updateSkinCheckboxes(enemySkinsCheckboxes);
    }

    private void updateSkinCheckboxes(HashMap<String, SkinCheckbox> skinCheckboxes) {
        for (SkinCheckbox skinCheckbox : skinCheckboxes.values()) {
            skinCheckbox.getAnimationDrawable().setRegion(skinCheckbox.getAnimation().getKeyFrame(main.getTime(), true));
        }
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backToMainMenu();
                    return true;
                }
                return false;
            }
        };
    }
}
