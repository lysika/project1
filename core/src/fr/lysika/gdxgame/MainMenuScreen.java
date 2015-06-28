package fr.lysika.gdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {

	final Gdxgame game;

	private OrthographicCamera camera;
	private Integer lastPoint ;
	private double temps = 0;
	public MainMenuScreen(final Gdxgame gam) {
		game = gam;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, ConstantGame.GAME_WIDTH, ConstantGame.GAME_HEIGHT);

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0.5f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		if (lastPoint != null){
			game.font.draw(game.batch, "Ton score est de "+ lastPoint, 100, ConstantGame.GAME_HEIGHT - 100);
		}
		game.font.draw(game.batch, "Appuyer sur Entree pour commencer ", 100, 100);
		game.batch.end();

		Float time = Gdx.graphics.getDeltaTime();
    	temps += time;
    	if (temps > 1){
    		if (Gdx.input.isTouched()) {
    			game.setScreen(new GameScreen(game));
    			dispose();
    		}
    	}


	}

	public void setLastPoint(Integer lastPoint) {
		this.lastPoint = lastPoint;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
