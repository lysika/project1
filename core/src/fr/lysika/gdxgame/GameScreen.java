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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import fr.lysika.gdxgame.recangle.RectangleFruit;

public class GameScreen implements Screen {

	
		final Gdxgame game;
		// lower equal to upper force
		double temps=0;
		public static final int DIAMOND_BOLD = 1;
		public static final int HEART_BOLD = 3;
	  	public static final int BANANA_BOLD = 8;
	  	public static final int APPLE_BOLD = 18;
	  	public static final int CHERRY_BOLD = 38;
	  	public static final int FRUIT_SPEED = 200;
	  	public static final int FRUIT_BOLD = 1000000000;
	  	public int fruitSpeed 		= FRUIT_SPEED;
	  	public int fruitForce 		= FRUIT_BOLD;
	  	public int numberOfOut = 0;
	  	private Texture backgroundTexture;
	  	private Sprite backgroundSprite;
	  	private Texture bananaImage;
	  	private Texture appleImage;
	  	private Texture cherryImage;
	  	private Texture bucketImage;
	  	private Texture diamondImage;
	  	private Texture lifeImage;
	  	private Sound dropSound;
	  	private Music gameMusic;
	  	private OrthographicCamera camera;
	  	private Rectangle bucket;
	  	private Array<RectangleFruit> fruitdrops;
	  	private long lastDropTime;
	  	private int points;
	  	private Array<RectangleFruit> lifes = new Array<RectangleFruit>();;
	  	private int nbLife = ConstantGame.NUMBER_OF_LIFE;
	  	
	  	
	    public GameScreen(final Gdxgame gam) {
	    	
	        this.game = gam;

	        loadTextures();

	        // load the drop sound effect and the rain background "music"
	        dropSound = Gdx.audio.newSound(Gdx.files.internal("fruit.wav"));
	        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("ambient.mp3"));
	        gameMusic.setLooping(true);

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

	        // create the fruit array and spawn the first raindrop
	        fruitdrops = new Array<RectangleFruit>();
	        spawnFruitdrop();
	        
	        // create life line
	        doLifeLine(nbLife);

	    }

	    private void spawnFruitdrop() {
	        RectangleFruit fruitdrop = getFruit();
	        fruitdrop.x = MathUtils.random(0, ConstantGame.GAME_WIDTH - ConstantGame.CHERRY_WIDTH);
	        fruitdrop.y = ConstantGame.GAME_HEIGHT;
	        fruitdrop.width = ConstantGame.CHERRY_WIDTH;
	        fruitdrop.height = ConstantGame.CHERRY_HEIGHT;
	        fruitdrops.add(fruitdrop);
	        lastDropTime = TimeUtils.nanoTime();
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
	    	int number = MathUtils.random(0, DIAMOND_BOLD + HEART_BOLD +BANANA_BOLD + APPLE_BOLD + CHERRY_BOLD);
	    	if (number <= DIAMOND_BOLD){
	    		rectangle.setTexture(diamondImage);
	    		rectangle.setPoint(100);
	    	}
	    	else if (number <= HEART_BOLD){
	    		rectangle.setTexture(lifeImage);
	    		rectangle.setPoint(0);
	    	}    	
	    	else if (number <= BANANA_BOLD){
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
	        doLifeLine(nbLife);
	        for (RectangleFruit rec : lifes){
	        	 game.batch.draw(rec.getTexture(), rec.x, rec.y);
	        }
	        
	        game.font.draw(game.batch, "Nombre de points: " + points, 0, ConstantGame.GAME_HEIGHT);
	        game.font.draw(game.batch, "temps: " + temps, 0, ConstantGame.GAME_HEIGHT- 40);
	        game.font.draw(game.batch, "speed: " + fruitSpeed, 0, ConstantGame.GAME_HEIGHT- 60);
	        game.font.draw(game.batch, "force: " + fruitForce, 0, ConstantGame.GAME_HEIGHT- 80);
	        game.batch.draw(bucketImage, bucket.x, bucket.y);
	        for (RectangleFruit raindrop : fruitdrops) {
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
	            spawnFruitdrop();

	        // move the raindrops, remove any that are beneath the bottom edge of
	        // the screen or that hit the bucket. In the later case we increase the 
	        // value our drops counter and add a sound effect.
	        Iterator<RectangleFruit> iter = fruitdrops.iterator();
	        while (iter.hasNext()) {
	        	RectangleFruit raindrop = iter.next();
	            raindrop.y -= fruitSpeed * Gdx.graphics.getDeltaTime();
	            if (raindrop.y + ConstantGame.CHERRY_HEIGHT < 0){
	                iter.remove();
	                nbLife--;
	    	        if (nbLife < 0){
	    	        	MainMenuScreen screen = new MainMenuScreen(game);
	    	        	screen.setLastPoint(points);
	    	        	game.setScreen(screen);
	    				dispose();
	    	        }	                
	            }
	            	
	            if (raindrop.overlaps(bucket)) {
	            	points+=raindrop.getPoint();
	                dropSound.play();
	                
	                if (raindrop.getTexture().equals(lifeImage)){
	                	if (nbLife < ConstantGame.NUMBER_OF_LIFE){
	                		nbLife++;
	                	}
	                }
	                else if (raindrop.getTexture().equals(diamondImage)){
	                	fruitSpeed = (int) (fruitSpeed * 0.8) ;
	                	fruitForce = (int) (fruitForce * 1.3);
	                	
	                	fruitSpeed = (fruitSpeed < FRUIT_SPEED) ? FRUIT_SPEED : fruitSpeed;
	                	fruitForce = (fruitForce > FRUIT_BOLD) ? FRUIT_BOLD : fruitForce;
	                }
	                iter.remove();
	            }
	        }
	        
	        manageLevelUp();
	        
	    }

	    private void manageLevelUp() {
	    	Float delta = Gdx.graphics.getDeltaTime();
	    	temps += delta;
	    	if (temps > 3){
	    		fruitSpeed = fruitSpeed + 12;
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
	        gameMusic.play();
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
	        cherryImage.dispose();
	        lifeImage.dispose();
	        dropSound.dispose();
	        gameMusic.dispose();
	    }
	    
	    private void loadTextures() {
	        // load the images for the droplet and the bucket, 64x64 pixels each
	        bananaImage = new Texture(Gdx.files.internal("banana_64.png"));
	        appleImage = new Texture(Gdx.files.internal("apple.png"));
	        diamondImage = new Texture(Gdx.files.internal("diamond_64.png"));
	        lifeImage = new Texture(Gdx.files.internal("heart_64.png"));
	        cherryImage = new Texture(Gdx.files.internal("cherry_64.png"));
	        bucketImage = new Texture(Gdx.files.internal("basket.png"));
	        backgroundTexture = new Texture(Gdx.files.internal("forest.png"));
	        backgroundSprite =new Sprite(backgroundTexture);
	        
	    }
	    
	    private void doLifeLine(int nbLife){
	        lifes.clear(); 
	        for (int ii=0 ; ii<=nbLife-1;ii++){
	        	RectangleFruit life = new RectangleFruit(lifeImage);
	        	life.x = 0;
	        	life.y = ConstantGame.GAME_HEIGHT - 100 - (ConstantGame.HEART_HEIGHT+5)*ii;
	        	life.width = ConstantGame.HEART_WIDTH;
	        	life.height = ConstantGame.HEART_HEIGHT;
	        	lifes.add(life);
	        }
	    }

	    public void renderBackground() {
	        backgroundSprite.draw(game.batch);
	    }
}
