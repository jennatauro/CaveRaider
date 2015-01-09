package caveraider;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by jennatauro on 08/01/2015.
 */
//http://stackoverflow.com/questions/21488311/libgdx-how-to-create-a-button
public class Controls implements ApplicationListener {
    Stage stage;
    Texture tX;
    BitmapFont font;
    MainCharacter mainCharacter;
    TextButton tbFireButton, tbShieldButton;
    TextButton.TextButtonStyle tbsFireButton, tbsShieldButton;
    Skin skFireButton, skShieldButton;
    TextureAtlas taFireButton, taShieldButton;
    int nSHeight, nSWidth, nCharacterRot, nCharacterRotDeg;
    SpriteBatch sbBatch;


    Touchpad touchpad;
    Touchpad.TouchpadStyle touchpadStyle;
    Skin touchpadSkin;
    Drawable touchBackground;
    Drawable touchKnob;

    public void setMainCharacter(MainCharacter mainCharacter_) {
        mainCharacter = mainCharacter_;
    }


    @Override
    public void create() {

        nSHeight = Gdx.graphics.getHeight();
        nSWidth = Gdx.graphics.getWidth();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        sbBatch = new SpriteBatch();//use to draw multiple sprites at once apparently better
        tX = new Texture(Gdx.files.internal("X.png"));


        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture(Gdx.files.internal("touchpadback.png")));
        touchpadSkin.add("touchKnob", new Texture(Gdx.files.internal("touchpadknob.png")));
        touchpadStyle = new Touchpad.TouchpadStyle();
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setSize(nSWidth * 350 / 1794, nSHeight * 350 / 1080);
        stage.addActor(touchpad);

        skFireButton = new Skin(); //setting up the button
        taFireButton = new TextureAtlas(Gdx.files.internal("FireButton.pack"));
        skFireButton.addRegions(taFireButton);
        tbsFireButton = new TextButton.TextButtonStyle();
        tbsFireButton.font = font;
        tbsFireButton.up = skFireButton.getDrawable("FireButton");
        tbsFireButton.down = skFireButton.getDrawable("pressedFireButton");
        tbsFireButton.checked = skFireButton.getDrawable("FireButton");
        tbFireButton = new TextButton("", tbsFireButton);
        tbFireButton.setSize(nSWidth * 200 / 1794, nSHeight * 200 / 1080);
        tbFireButton.setPosition(nSWidth - (nSWidth * 400 / 1794), 0);
        tbFireButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainCharacter.makeFireBall();//Make a new fireball
                return true;
            }
        });
        stage.addActor(tbFireButton);


        skShieldButton = new Skin(); //setting up the button
        taShieldButton = new TextureAtlas(Gdx.files.internal("ShieldButtons.pack"));
        skShieldButton.addRegions(taShieldButton);
        tbsShieldButton = new TextButton.TextButtonStyle();
        tbsShieldButton.font = font;
        tbsShieldButton.up = skShieldButton.getDrawable("shieldButtonUp");
        tbsShieldButton.down = skShieldButton.getDrawable("shieldButtonDown");
        tbsShieldButton.checked = skShieldButton.getDrawable("shieldButtonUp");
        tbShieldButton = new TextButton("", tbsShieldButton);
        tbShieldButton.setSize(nSWidth * 200 / 1794, nSHeight * 200 / 1080);
        tbShieldButton.setPosition(nSWidth - (nSWidth * 300 / 1794), (nSHeight * 200 / 1080));
        tbShieldButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainCharacter.bShieldR = true;
                return true;
            }
        });
        stage.addActor(tbShieldButton);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        if (touchpad.getKnobPercentX() > .75) {
            nCharacterRot = 7;
            nCharacterRotDeg = 90;
            mainCharacter.setCharacterVelocity(1, 0);
        } else if (touchpad.getKnobPercentX() < -.75) {
            nCharacterRot = 6;
            nCharacterRotDeg = 270;
            mainCharacter.setCharacterVelocity(-1, 0);
        } else if (touchpad.getKnobPercentY() > .75) {
            nCharacterRot = 4;
            nCharacterRotDeg = 180;
            mainCharacter.setCharacterVelocity(0, 1);
        } else if (touchpad.getKnobPercentY() < -.75) {
            nCharacterRot = 5;
            nCharacterRotDeg = 0;
            mainCharacter.setCharacterVelocity(0, -1);
        }
        if (touchpad.getKnobPercentY() > -.75 && touchpad.getKnobPercentY() < .75 && touchpad.getKnobPercentX() > -.75 && touchpad.getKnobPercentX() < .75) {
            mainCharacter.setCharacterVelocity(0, 0);
            if (nCharacterRot == 7) {
                nCharacterRot = 3;
            } else if (nCharacterRot == 6) {
                nCharacterRot = 2;
            } else if (nCharacterRot == 5) {
                nCharacterRot = 1;
            } else if (nCharacterRot == 4) {
                nCharacterRot = 0;
            }

        }
        mainCharacter.setCharacterRotation(nCharacterRot, nCharacterRotDeg);


        stage.draw();
        sbBatch.begin();
        if (mainCharacter.nShieldTimer > 100 && mainCharacter.nShieldTimer < 400) {
            sbBatch.draw(tX, nSWidth - (nSWidth * 300 / 1794), (nSHeight * 200 / 1080), nSWidth * 200 / 1794, nSHeight * 200 / 1080);
        }
        sbBatch.end();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
