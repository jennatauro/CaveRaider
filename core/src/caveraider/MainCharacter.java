package caveraider;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;


/**
 * Created by jennatauro on 08/01/2015.
 */

//https://github.com/libgdx/libgdx/wiki/2D-Animation
public class MainCharacter implements ApplicationListener {
    Map[] armMaps;
    OrthographicCamera camera;
    float fCharacterVelocityX = 0, fCharacterVelocityY = 0, fCharacterX, fCharacterY, fCharacterWidth, fCharacterHeight;
    int nSHeight, nSWidth, nCharacterRotation = 1, nCharacterRotationDeg = 0, nLayerCount, nCurrentMap = 0, nVelocityX, nVelocityY, nShieldTimer;
    Animation[] araWalking;
    ArrayList<FireBall> arlFireBalls;
    Texture tTemp, tFireBall, tShield;
    SpriteBatch sbSpriteBatch;
    float stateTime;
    float fOldX, fOldY, tileWidth, tileHeight;
    boolean bCollidedX, bCollidedY, bJustSet, bShieldR, bShieldT;

    public float getCharacterY() {
        return fCharacterY;
    }
    public float getCharacterX() {
        return fCharacterX;
    }

    public void setMaps(Map[] armMaps_) {
        armMaps = armMaps_;
    }

    public void setCamera(OrthographicCamera camera_) {
        camera = camera_;
    }

    public void makeFireBall() {//This makes a new fireball
        arlFireBalls.add(new FireBall(tFireBall, fCharacterX + (fCharacterWidth / 8), fCharacterY + (fCharacterHeight / 8), nCharacterRotationDeg, camera));
    }


    @Override
    public void create() {
        nSHeight = Gdx.graphics.getHeight(); //use to make scaling
        nSWidth = Gdx.graphics.getWidth();
        nVelocityX = nSWidth * 10 / 1794;
        nVelocityY = nSHeight * 10 / 1080;
        camera.setToOrtho(false, nSWidth, nSHeight);
        camera.update();
        fCharacterWidth = nSWidth * 110 / 1794;
        fCharacterHeight = nSHeight * 120 / 1080;
        tFireBall = new Texture(Gdx.files.internal("FireBall.png"));
        tShield = new Texture(Gdx.files.internal("Shield.png"));
        araWalking = new Animation[8];//array of animations
        arlFireBalls = new ArrayList<FireBall>();
        sbSpriteBatch = new SpriteBatch();//use to draw multiple sprites at once apparently better
        for (int i = 0; i < 8; i++) {
            int k = 1;
            tTemp = new Texture(Gdx.files.internal("Sprite" + i + ".png"));
            if (i > 3) {
                k = 3;
            }
            araWalking[i] = build(tTemp, 1, k);//Populating an array of animations using my method BuildAnimation
        }
        stateTime = 0f;
        tileWidth = armMaps[nCurrentMap].nMapScale * (armMaps[nCurrentMap].arclCollisionLayer[0].getTileWidth());//Grabbing the tile width for the tiledMap
        tileHeight = armMaps[nCurrentMap].nMapScale * (armMaps[nCurrentMap].arclCollisionLayer[0].getTileHeight());
        fCharacterX = 87 * tileWidth - (tileWidth / 2);
        fCharacterY = 13 * tileHeight - (tileHeight / 2);

    }

    public Animation build(Texture tTexture, int nRows, int nCols) {
        TextureRegion[] trTextureRegion;
        Animation aAnimation;
        int nCount1 = 0;
        TextureRegion[][] tmp = TextureRegion.split(tTexture, tTexture.getWidth() / nCols, tTexture.getHeight() / nRows);//Making an array that holds the region of each image and the image in that region
        trTextureRegion = new TextureRegion[nCols * nRows];//Making a 1d array with a length that is the same as the number of regions
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                trTextureRegion[nCount1++] = tmp[i][j];//Filling the 1d array with the regions from the 2d array
            }
        }
        aAnimation = new Animation(0.10f, trTextureRegion);//Making animation with the array that is the texture region and setting the frame rate
        return aAnimation;
    }


    @Override
    public void resize(int width, int height) {

    }

    public void setCharacterRotation(int nRotation, int nRotationDeg) {
        nCharacterRotation = nRotation;
        nCharacterRotationDeg = nRotationDeg;
    }

    public void setCharacterVelocity(int nVelocityX_, int nVelocityY_) {
        fCharacterVelocityX = nVelocityX_ * nVelocityX;
        fCharacterVelocityY = nVelocityY_ * nVelocityY;
    }

    public boolean getTileID(float fX, float fY, float nWidth, String sID) {// this is slightly complicated but its basically grabbing the tile that the character is standing on and getting the ID
        boolean bCollided = false;
        for (nLayerCount = 0; nLayerCount < armMaps[nCurrentMap].tiledMap.getLayers().getCount() - 1; nLayerCount++) {

            bCollided = armMaps[nCurrentMap].arclCollisionLayer[nLayerCount].getCell((int) ((fX + nWidth / 4) / tileWidth), (int) (fY / tileHeight))
                    .getTile().getProperties().containsKey(sID);

            bCollided |= armMaps[nCurrentMap].arclCollisionLayer[nLayerCount].getCell((int) ((fX + 3 * nWidth / 4) / tileWidth), (int) (fY / tileHeight))
                    .getTile().getProperties().containsKey(sID);

            bCollided |= armMaps[nCurrentMap].arclCollisionLayer[nLayerCount].getCell((int) ((fX + nWidth / 2) / tileWidth), (int) (fY / tileHeight))
                    .getTile().getProperties().containsKey(sID);
        }
        return bCollided;
    }

    @Override
    public void render() {
        camera.position.set(fCharacterX, fCharacterY, 0);
        sbSpriteBatch.setProjectionMatrix(camera.combined);
        camera.update();


        fOldX = fCharacterX;//This is used for resetting the players position if they hit a wall
        fOldY = fCharacterY;

        fCharacterX += fCharacterVelocityX;//Move character
        bCollidedX = getTileID(fCharacterX, fCharacterY, fCharacterWidth, "Block");//Did it touched a tile with the block ID

        if (bCollidedX) {//If it touched a tile with the block ID reset the position
            fCharacterX = fOldX;
        }

        fCharacterY += fCharacterVelocityY;//This is the same as the previous bit but for the Y direction
        bCollidedY = getTileID(fCharacterX, fCharacterY, fCharacterWidth, "Block");

        if (bCollidedY) {
            fCharacterY = fOldY;
        }


        if (getTileID(fCharacterX, fCharacterY, fCharacterWidth, "MoveUp")) {//This checks if the character is standing on a door
            if (nCurrentMap < armMaps.length - 1 && !bJustSet) {
                fCharacterY += 50 * tileHeight;

                nCurrentMap++;//If the character was standing on a door change the map
                bJustSet = true;//So it only changes the map one time and not every time render is called
            }
        }

        if (getTileID(fCharacterX, fCharacterY, fCharacterWidth, "Walkable")) {//If the character has moved off the door the map can be changed again
            bJustSet = false;
        }


        if (getTileID(fCharacterX, fCharacterY, fCharacterWidth, "MoveDown")) {//Same as the previous bit but for the door the decreases the map index
            if (nCurrentMap > 0 && !bJustSet) {
                nCurrentMap--;
                fCharacterX -= 50 * tileWidth - (tileWidth / 2);
                bJustSet = true;
            }
        }

        if (getTileID(fCharacterX, fCharacterY, fCharacterWidth, "Walkable")) {
            bJustSet = false;
        }


        stateTime += Gdx.graphics.getDeltaTime();//Getting a time to select a frame from the animation
        sbSpriteBatch.begin();
        if (bShieldR) {
            if (nShieldTimer <= 100) {
                sbSpriteBatch.draw(tShield, fCharacterX - fCharacterWidth / 8, fCharacterY - fCharacterHeight / 8, nSWidth * 135 / 1794, nSHeight * 150 / 1080);
                bShieldT = true;
            } else {
                bShieldT = false;
            }
            nShieldTimer++;
            if (nShieldTimer == 400) {
                bShieldR = false;
            }
        } else {
            nShieldTimer = 0;
        }


        if (nCharacterRotationDeg == 90) {
            sbSpriteBatch.draw(araWalking[nCharacterRotation].getKeyFrame(stateTime, true), fCharacterX, fCharacterY, fCharacterWidth, fCharacterHeight);//Drawing the animation from the array of animations based on the character rotation
        } else if (nCharacterRotationDeg == 0) {
            sbSpriteBatch.draw(araWalking[nCharacterRotation].getKeyFrame(stateTime, true), fCharacterX, fCharacterY, fCharacterWidth, fCharacterHeight);//Drawing the animation from the array of animations based on the character rotation
        }else if (nCharacterRotationDeg == 180) {
            sbSpriteBatch.draw(araWalking[nCharacterRotation].getKeyFrame(stateTime, true), fCharacterX, fCharacterY, fCharacterWidth, fCharacterHeight);//Drawing the animation from the array of animations based on the character rotation

        }else if (nCharacterRotationDeg == 270) {
            sbSpriteBatch.draw(araWalking[nCharacterRotation].getKeyFrame(stateTime, true), fCharacterX, fCharacterY, fCharacterWidth, fCharacterHeight);//Drawing the animation from the array of animations based on the character rotation

        }


        sbSpriteBatch.end();
        for (int i = 0; i < arlFireBalls.size(); i++) {//This renders all the fireballs
            arlFireBalls.get(i).render();
            if (arlFireBalls.get(i).bounds(tileWidth, tileHeight)) {
                arlFireBalls.remove(i);
            }
        }
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

