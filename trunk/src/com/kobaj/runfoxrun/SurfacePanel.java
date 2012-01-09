package com.kobaj.runfoxrun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

//surface class that updates and draws everything.
public class SurfacePanel extends DrawablePanel
{	
	private GameStates currentState;
	private GameStates oldState;
	
	private int width;
	private int height;
	
	private FPSManager fps;
	public InputManager im;
	private SoundManager sm;
	private PhysicsManager pm;
	
	private Sprite BigAnimate;
	
	private TitleScreen ts;
	private ContinousScreen cous;
	private PauseScreen ps;
	
	//semi arbitrary
	
	private Paint textPaint = new Paint();

	//might be changed to an image
	private custString pauseText;
	
	//construct our objects
	public SurfacePanel(Context context)
	{	
		super(context);
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		
		fps = new FPSManager();
		im = new InputManager();
		
		sm = new SoundManager(context);
		
		pm = new PhysicsManager(width, height);
		pm.setScrollRate(-10);
		
		currentState = GameStates.TitleScreen;
		oldState = GameStates.TitleScreen;
		
		ts = new TitleScreen();
		cous = new ContinousScreen();
		ps = new PauseScreen();
		ps.onInitialize(getResources(), R.drawable.titlescreen);
		
		pauseText = new custString("PAUSE", 3, 27);
		
		//semi arbitrary
		textPaint.setColor(Color.WHITE);
		textPaint.setStrokeWidth(8);
		textPaint.setStyle(Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(24);
		
		//arbitrary goes below here.
		BigAnimate = XMLHandler.readSerialFile(getResources(), R.raw.haloperms, Sprite.class);
	}
	
	//load in our resources
	public void onInitialize()
	{	
		ts.onInitialize(getResources(), R.drawable.titlescreen);
		
		//semi arbitrary
		
		pm.setPlayer(BigAnimate);
		
		//arbitrary
		
		sm.addSound(0, R.raw.collision);
		
		BigAnimate.onInitalize(getResources(), R.drawable.haloperms, (int)(width / 3.0f), 190, 33, 49);
		BigAnimate.setAnimation(CharStates.Running);
	}
	
	public void onUpdate(long gameTime)
	{
		fps.onUpdate(gameTime);
		im.onUpdate();
		
		if(currentState == GameStates.TitleScreen)
			onTitleScreen();
		else if(currentState == GameStates.SinglePlay)
		{
			//uncomment later
			//BigAnimate.onUpdate(fps.getDelta());
			//pm.onUpdate(fps.getDelta());
		}
		else if(currentState == GameStates.Continous)
		{
			BigAnimate.onUpdate(fps.getDelta());
			pm.onUpdate(fps.getDelta());
			cous.onUpdate(fps.getDelta());
			checkForUserPause();
		}
		else if(currentState == GameStates.Pause)
			onPauseScreen();
	}

	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if(currentState == GameStates.TitleScreen)
			ts.onDraw(canvas);
		else if (currentState == GameStates.SinglePlay)
		{
			//BigAnimate.onDraw(canvas);
			//onDrawSinglePlay(canvas);
		}
		else if(currentState == GameStates.Continous)
		{
			BigAnimate.onDraw(canvas);
			cous.onDraw(canvas);
			pauseText.onDraw(canvas);
		}
		else if(currentState == GameStates.Pause)
			ps.onDraw(canvas);
		
		//fps output
		canvas.drawText("FPS " + String.valueOf(fps.getFPS()), 600, 30, textPaint);
	}
	
	private void checkForUserPause()
	{
		for(int i = 0; i < im.fingerCount; i++)
		{
			if(im.getReleased(i))
			{
			if(pauseText.fingertap((int) im.getY(i), (int) im.getY(i)))
			{
				oldState = currentState;
				currentState = GameStates.Pause;
			}
			}
		}
	}
	
	//also special
	private void onPauseScreen()
	{
		GameStates newState = GameStates.Pause;
		
		for(int i = 0; i < im.fingerCount; i++)
		{
			if(im.getReleased(i))
			{
			newState = ps.onTouch((int) im.getX(i), (int) im.getY(i));
			
			if(newState != GameStates.Pause)
				break;
			}
		}
		
		if(newState == GameStates.Quit)
		{
			onUserQuit(); 
		}
		else if(newState == GameStates.TitleScreen)
		{
			oldState = GameStates.Pause;
			currentState = GameStates.TitleScreen;
		}
		else if(newState == GameStates.Resume)
		{
			currentState = oldState;
			oldState = GameStates.Pause;
		}
	}
	
	//special cause it handles a lot of stuff.
	private void onTitleScreen()
	{
		GameStates newState = GameStates.TitleScreen;
		
		for(int i = 0; i < im.fingerCount; i++)
		{
			if(im.getReleased(i))
			{
			newState = ts.onTouch((int) im.getX(i), (int) im.getY(i));
			
			if(newState != GameStates.TitleScreen)
				break;
			}
		}
		
		if(newState == GameStates.Quit)
		{
			onUserQuit(); 
		}
		else if(newState == GameStates.SinglePlay)
		{
			//currentState = GameStates.SinglePlay;
		}
		else if(newState == GameStates.Continous)
		{
			purgeManagers();
			cous = new ContinousScreen();
			cous.onInitialize(getResources(), im, pm);
			oldState = GameStates.TitleScreen;
			currentState = GameStates.Continous;
		}
	}
	
	private void purgeManagers()
	{
		pm.purge();
		sm.purge();
	}
	
	public void onScreenPause()
	{
		//when the game is paused by turning off.
		
	}

	public void onScreenResume()
	{
		
	}
	
	private void onUserQuit()
	{
		//probably save stuff before actually quitting.
		System.exit(0); 
	}

}
