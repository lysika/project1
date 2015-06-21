package fr.lysika.gdxgame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import fr.lysika.gdxgame.ConstantGame;
import fr.lysika.gdxgame.Gdxgame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(ConstantGame.GAME_WIDTH, ConstantGame.GAME_HEIGHT);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new Gdxgame();
        }
}