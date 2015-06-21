package fr.lysika.gdxgame;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class gdxgame extends ApplicationAdapter {

	public static final Integer BUCKET_WIDTH = 64;
	public static final Integer BUCKET_HEIGHT = 64;
	public static final Integer RAIN_DROP_WIDTH = 64;
	public static final Integer RAIN_DROP_HEIGHT = 64;
	public static final Integer BUCKET_FLOOR_GAP = 20;
	public static final Integer GAME_WIDTH = 800;
	public static final Integer GAME_HEIGHT = 480;
	public static final Integer BUCKET_SPEED = 300;
	public static final Integer RAIN_FORCE = 1000000000;
	public static final Integer RAIN_SPEED = 200;

	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Vector3 touchPos;
	private long lastDropTime;

	@Override
	public void create() {
		// Load image
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// Load Music
		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// Start the playback of music
		rainMusic.setLooping(true);
		rainMusic.play();

		// Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// Create Sprite Batch:
		batch = new SpriteBatch();

		// Create the bucket aera
		bucket = new Rectangle();
		bucket.height = BUCKET_HEIGHT;
		bucket.width = BUCKET_WIDTH;
		bucket.x = GAME_WIDTH / 2 - BUCKET_WIDTH / 2;
		bucket.y = BUCKET_FLOOR_GAP;

		// Create the rainDrop area
		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	@Override
	public void render() {

		// Screen cleazn has blue color
		Gdx.gl.glClearColor(0, 0, 204, 1);
		// Clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update the camera once per frame
		camera.update();

		// render the bucket
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);

		}
		batch.end();

		// Manage bucket move
		// First we ask the input module whether the screen is currently touched
		// (or a mouse button is pressed) by calling Gdx.input.isTouched().
		// Next we want to transform the touch/mouse coordinates to our camera's
		// coordinate system.
		// This is necessary because the coordinate system in which touch/mouse
		// coordinates are reported might be different than the coordinate
		// system we use to represent objects in our world.

		if (Gdx.input.isTouched()) {
			touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - BUCKET_WIDTH / 2;

		}

		/*
		 * We want the bucket to move without acceleration, at two hundred
		 * pixels/units per second, either to the left or the right. To
		 * implement such time-based movement we need to know the time that
		 * passed in between the last and the current rendering frame. Here's
		 * how we can do all this:
		 */
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			bucket.x -= BUCKET_SPEED * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			bucket.x += BUCKET_SPEED * Gdx.graphics.getDeltaTime();

		// keep bucket in bound
		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > GAME_WIDTH - BUCKET_WIDTH)
			bucket.x = GAME_WIDTH - BUCKET_WIDTH;

		// Render the rain
		if (TimeUtils.nanoTime() - lastDropTime > RAIN_FORCE)
			spawnRaindrop();

		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= RAIN_SPEED * Gdx.graphics.getDeltaTime();
			if (raindrop.y + RAIN_DROP_HEIGHT < 0)
				iter.remove();
			// Rain overlaps bucket
			if (raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

	/**
	 * doing rain
	 */
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, GAME_WIDTH - RAIN_DROP_WIDTH);
		raindrop.y = GAME_HEIGHT;
		raindrop.width = RAIN_DROP_WIDTH;
		raindrop.height = RAIN_DROP_HEIGHT;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}
