package fr.lysika.gdxgame;

import java.nio.channels.GatheringByteChannel;
import java.util.Iterator;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import fr.lysika.gdxgame.ConstantGame;
import fr.lysika.gdxgame.recangle.RectangleFruit;

public class GameScreen implements Screen {

	
	final Gdxgame game;
	// lower equal to upper force
		double temps=0;
	  	public static final int BANANA_BOLD = 2;
	  	public static final int APPLE_BOLD = 3;
	  	public static final int CHERRY_BOLD = 5;
	  	public int fruitSpeed 		= 200;
	  	public int fruitForce 		= 1000000000;
	  	public int numberOfOut = 0;
	  	private Texture backgroundTexture;
	  	private Sprite backgroundSprite;
	  	private Texture bananaImage;
	  	private Texture appleImage;
	  	private Texture cherryImage;
	  	private Texture bucketImage;
	  	private Sound dropSound;
	  	private Music rainMusic;
	  	private OrthographicCamera camera;
	  	private Rectangle bucket;
	  	private Array<RectangleFruit> raindrops;
	  	private long lastDropTime;
	  	private int dropsGathered;
	  	private int dropsTotal;

	    public GameScreen(final Gdxgame gam) {
	    	
	        this.game = gam;

	        // load the images for the droplet and the bucket, 64x64 pixels each
	        bananaImage = new Texture(Gdx.files.internal("banana_64.png"));
	        appleImage = new Texture(Gdx.files.internal("apple.png"));
	        cherryImage = new Texture(Gdx.files.internal("cherry_64.png"));
	        bucketImage = new Texture(Gdx.files.internal("basket.png"));
	        loadTextures();

	        // load the drop sound effect and the rain background "music"
	        dropSound = Gdx.audio.newSound(Gdx.files.internal("fruit.wav"));
	        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("ambient.mp3"));
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
	        raindrops = new Array<RectangleFruit>();
	        spawnRaindrop();

	    }

	    private void spawnRaindrop() {
	        RectangleFruit raindrop = getFruit();
	        raindrop.x = MathUtils.random(0, ConstantGame.GAME_WIDTH - ConstantGame.CHERRY_WIDTH);
	        raindrop.y = ConstantGame.GAME_HEIGHT;
	        raindrop.width = ConstantGame.CHERRY_WIDTH;
	        raindrop.height = ConstantGame.CHERRY_HEIGHT;
	        raindrops.add(raindrop);
	        lastDropTime = TimeUtils.nanoTime();
	        dropsTotal++;
	    }
	    
	    /**
	     * Get the fruit
	     * @return
	     */
	    private RectangleFruit getFruit(){
	    	RectangleFruit rectangle = new RectangleFruit();
	        rectangle.x = MathUtils.random(0, ConstantGame.GAME_WIDTH - ConstantGame.CHERRY_WIDTH);
	        rectangle.y = ConstantGame.GAME_HEIGHT;
	        rectangle.width = ConstantGame.CHERRY_WIDTH;
	        rectangle.height = ConstantGame.CHERRY_HEIGHT;
	    	int number = MathUtils.random(0, BANANA_BOLD + APPLE_BOLD + CHERRY_BOLD);
	    	
	    	if (number <= BANANA_BOLD){
	    		rectangle.setTexture(bananaImage);
	    		rectangle.setPoint(20);
	    	}else if (number <= APPLE_BOLD){
	    		rectangle.setTexture(appleImage);
	    		rectangle.setPoint(5);
	    	}else{
	    		rectangle.setTexture(cherryImage);
	    		rectangle.setPoint(1);
	    	}
	    	return rectangle;
	    }

	    @Override
	    public void render(float delta) {
	        // clear the screen with a dark blue color. The
	        // arguments to glClearColor are the red, green
	        // blue and alpha component in the range [0,1]
	        // of the color to be used to clear the screen.
	        Gdx.gl.glClearColor(50, 205, 50, 1);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        // tell the camera to update its matrices.
	        camera.update();

	        // tell the SpriteBatch to render in the
	        // coordinate system specified by the camera.
	        game.batch.setProjectionMatrix(camera.combined);

	        // begin a new batch and draw the bucket and
	        // all drops
	        game.batch.begin();
	        renderBackground();
	        game.font.draw(game.batch, "Nombre de points: " + dropsGathered, 0, ConstantGame.GAME_HEIGHT);
	        game.font.draw(game.batch, "temps: " + temps, 0, ConstantGame.GAME_HEIGHT- 40);
	        game.font.draw(game.batch, "speed: " + fruitSpeed, 0, ConstantGame.GAME_HEIGHT- 60);
	        game.font.draw(game.batch, "force: " + fruitForce, 0, ConstantGame.GAME_HEIGHT- 80);
	        game.batch.draw(bucketImage, bucket.x, bucket.y);
	        for (RectangleFruit raindrop : raindrops) {
	            game.batch.draw(raindrop.getTexture(), raindrop.x, raindrop.y);
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
	        if (TimeUtils.nanoTime() - lastDropTime > fruitForce)
	            spawnRaindrop();

	        // move the raindrops, remove any that are beneath the bottom edge of
	        // the screen or that hit the bucket. In the later case we increase the 
	        // value our drops counter and add a sound effect.
	        Iterator<RectangleFruit> iter = raindrops.iterator();
	        while (iter.hasNext()) {
	        	RectangleFruit raindrop = iter.next();
	            raindrop.y -= fruitSpeed * Gdx.graphics.getDeltaTime();
	            if (raindrop.y + ConstantGame.CHERRY_HEIGHT < 0){
	                iter.remove();
	                numberOfOut++;
	            }
	            	
	            if (raindrop.overlaps(bucket)) {
	            	dropsGathered+=raindrop.getPoint();
	                dropSound.play();
	                iter.remove();
	            }
	        }
	        
	        manageNextRender();
	    }

	    private void manageNextRender() {
	    	Float delta = Gdx.graphics.getDeltaTime();
	    	temps += delta;
	    	if (temps > 3){
	    		fruitSpeed = fruitSpeed + 10;
	    		temps = 0;
	    	}
	    	
	    	fruitForce = fruitForce - 200000;
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
	        bananaImage.dispose();
	        bucketImage.dispose();
	        dropSound.dispose();
	        rainMusic.dispose();
	    }
	    
	    private void loadTextures() {
	        backgroundTexture = new Texture(Gdx.files.internal("forest.png"));
	        backgroundSprite =new Sprite(backgroundTexture);
	    }

	    public void renderBackground() {
	        backgroundSprite.draw(game.batch);
	    }
}
