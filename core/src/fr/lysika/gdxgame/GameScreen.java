package fr.lysika.gdxgame;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import fr.lysika.gdxgame.ConstantGame;

public class GameScreen implements Screen {

	  final Gdxgame game;

	    Texture dropImage;
	    Texture bucketImage;
	    Sound dropSound;
	    Music rainMusic;
	    OrthographicCamera camera;
	    Rectangle bucket;
	    Array<Rectangle> raindrops;
	    long lastDropTime;
	    int dropsGathered;

	    public GameScreen(final Gdxgame gam) {
	        this.game = gam;

	        // load the images for the droplet and the bucket, 64x64 pixels each
	        dropImage = new Texture(Gdx.files.internal("droplet.png"));
	        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

	        // load the drop sound effect and the rain background "music"
	        dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
	        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
	        rainMusic.setLooping(true);

	        // create the camera and the SpriteBatch
	        camera = new OrthographicCamera();
	        camera.setToOrtho(false, ConstantGame.GAME_WIDTH, ConstantGame.GAME_HEIGHT);

	        // create a Rectangle to logically represent the bucket
	        bucket = new Rectangle();
	        bucket.x = ConstantGame.GAME_WIDTH / 2 - ConstantGame.BUCKET_WIDTH / 2; // center the bucket horizontally
	        bucket.y = ConstantGame.BUCKET_FLOOR_GAP; // bottom left corner of the bucket is 20 pixels above
	                        // the bottom screen edge
	        bucket.width = ConstantGame.BUCKET_WIDTH;
	        bucket.height = ConstantGame.BUCKET_HEIGHT;

	        // create the raindrops array and spawn the first raindrop
	        raindrops = new Array<Rectangle>();
	        spawnRaindrop();

	    }

	    private void spawnRaindrop() {
	        Rectangle raindrop = new Rectangle();
	        raindrop.x = MathUtils.random(0, ConstantGame.GAME_WIDTH - ConstantGame.RAIN_WIDTH);
	        raindrop.y = ConstantGame.GAME_HEIGHT;
	        raindrop.width = ConstantGame.RAIN_WIDTH;
	        raindrop.height = ConstantGame.RAIN_HEIGHT;
	        raindrops.add(raindrop);
	        lastDropTime = TimeUtils.nanoTime();
	    }

	    @Override
	    public void render(float delta) {
	        // clear the screen with a dark blue color. The
	        // arguments to glClearColor are the red, green
	        // blue and alpha component in the range [0,1]
	        // of the color to be used to clear the screen.
	        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        // tell the camera to update its matrices.
	        camera.update();

	        // tell the SpriteBatch to render in the
	        // coordinate system specified by the camera.
	        game.batch.setProjectionMatrix(camera.combined);

	        // begin a new batch and draw the bucket and
	        // all drops
	        game.batch.begin();
	        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, ConstantGame.GAME_HEIGHT);
	        game.batch.draw(bucketImage, bucket.x, bucket.y);
	        for (Rectangle raindrop : raindrops) {
	            game.batch.draw(dropImage, raindrop.x, raindrop.y);
	        }
	        game.batch.end();

	        // process user input
	        if (Gdx.input.isTouched()) {
	            Vector3 touchPos = new Vector3();
	            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	            camera.unproject(touchPos);
	            bucket.x = touchPos.x - ConstantGame.BUCKET_WIDTH / 2;
	        }
	        if (Gdx.input.isKeyPressed(Keys.LEFT))
	            bucket.x -= ConstantGame.BUCKET_SPEED * Gdx.graphics.getDeltaTime();
	        if (Gdx.input.isKeyPressed(Keys.RIGHT))
	            bucket.x += ConstantGame.BUCKET_SPEED * Gdx.graphics.getDeltaTime();

	        // make sure the bucket stays within the screen bounds
	        if (bucket.x < 0)
	            bucket.x = 0;
	        if (bucket.x > ConstantGame.GAME_WIDTH - ConstantGame.BUCKET_WIDTH)
	            bucket.x = ConstantGame.GAME_WIDTH - ConstantGame.BUCKET_WIDTH;

	        // check if we need to create a new raindrop
	        if (TimeUtils.nanoTime() - lastDropTime > ConstantGame.RAIN_FORCE)
	            spawnRaindrop();

	        // move the raindrops, remove any that are beneath the bottom edge of
	        // the screen or that hit the bucket. In the later case we increase the 
	        // value our drops counter and add a sound effect.
	        Iterator<Rectangle> iter = raindrops.iterator();
	        while (iter.hasNext()) {
	            Rectangle raindrop = iter.next();
	            raindrop.y -= ConstantGame.RAIN_SPEED * Gdx.graphics.getDeltaTime();
	            if (raindrop.y + ConstantGame.RAIN_HEIGHT < 0)
	                iter.remove();
	            if (raindrop.overlaps(bucket)) {
	                dropsGathered++;
	                dropSound.play();
	                iter.remove();
	            }
	        }
	    }

	    @Override
	    public void resize(int width, int height) {
	    }

	    @Override
	    public void show() {
	        // start the playback of the background music
	        // when the screen is shown
	        rainMusic.play();
	    }

	    @Override
	    public void hide() {
	    }

	    @Override
	    public void pause() {
	    }

	    @Override
	    public void resume() {
	    }

	    @Override
	    public void dispose() {
	        dropImage.dispose();
	        bucketImage.dispose();
	        dropSound.dispose();
	        rainMusic.dispose();
	    }
}
