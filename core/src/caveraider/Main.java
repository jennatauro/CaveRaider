package caveraider;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;


/**
 * Created by jennatauro on 08/01/2015.
 */
//http://obviam.net/index.php/getting-started-in-android-game-development-with-libgdx-create-a-working-prototype-in-a-day-tutorial-part-1/
public class Main extends Game {
    OrthographicCamera camera;
    MainCharacter mainCharacter;
    Enemy bat;
    Controls controls;
    int nNumberOfMaps = 2;
    Map[] armMaps;

    @Override
    public void create() {//did this change
        camera = new OrthographicCamera();
        mainCharacter = new MainCharacter();
        armMaps = new Map[nNumberOfMaps];//Building the array of maps and passing the camera via the constructor
        for (int i = 0; i < nNumberOfMaps; i++) {
            armMaps[i] = new Map(i, camera);
            armMaps[i].create();
        }
        controls = new Controls();
        controls.setMainCharacter(mainCharacter);
        mainCharacter.setMaps(armMaps);
        mainCharacter.setCamera(camera);
        mainCharacter.create();
        bat = new Enemy();
        bat.setMainCharacter(mainCharacter);
        bat.setMaps(armMaps);
        bat.setCamera(camera);

        bat.create();

        controls.create();


        System.out.println("Width" + Gdx.graphics.getWidth());
        System.out.println("Height" + Gdx.graphics.getHeight());
    }


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        armMaps[mainCharacter.nCurrentMap].render();
        mainCharacter.render();
        controls.render();

        bat.setFx(mainCharacter.getCharacterX());
        bat.setFy(mainCharacter.getCharacterY());
        bat.setShield(mainCharacter.getShield());
        bat.render();

    }

    @Override
    public void pause() {
        super.pause();


    }

    @Override
    public void resume() {
        super.resume();

    }

    @Override
    public void dispose() {
        super.dispose();

    }


}
