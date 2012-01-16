package com.kobaj.runfoxrun;

import java.util.HashSet;
import java.util.Iterator;

import android.graphics.Rect;

public class PhysicsManager
{
	@SuppressWarnings("unused")
	private int width;
	private int height;
	
	private boolean set = false;
	private Sprite physObj;
	private HashSet<Sprite> interactables;
	
	private HashSet<Sprite> backgroundables;
	
	public final float jumpFloat = -3.0f / 10.0f;
	public final float userAcc = 30f / 100000.0f;
	private float userVel = 0;
	private float userVelOld = 0;
	
	private float scrollValue;
	private float scrollDelta = 0;
	private float scrollProgress = 0;
	
	private boolean death = false;
	private boolean reverse = false;
	
	private boolean touching = false;
	
	public static float speedUp = 12.5f;
	
	public void jump()
	{
		if (touching)
		{
			userVel = jumpFloat;
			physObj.setAnimation(CharStates.Jump);
		}
	}
	
	public boolean getDeath()
	{
		return death;
	}
	
	public float getScrollDelta()
	{
		return scrollDelta;
	}
	
	public float getScrollProgress()
	{
		return scrollProgress;
	}
	
	public void setScrollProgress(float value)
	{
		for (Iterator<Sprite> it = interactables.iterator(); it.hasNext();)
		{
			Sprite temp = it.next();
			temp.setxPos(temp.getxPos() + scrollProgress - value);
		}
		
		// in a perfect world I would add a boolean to see if the user wants to
		// reset the background.
		// here I will just pretend he does not.
		/*
		 * for(Iterator<Sprite> it = backgroundables.iterator(); it.hasNext();)
		 * { Sprite temp = it.next(); temp.setxPos(temp.getxPos() -
		 * scrollProgress / 10.0f - value / 10.0f); }
		 */
		
		scrollProgress = value;
	}
	
	public PhysicsManager(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		interactables = new HashSet<Sprite>();
		backgroundables = new HashSet<Sprite>();
	}
	
	public void removePhys(Sprite input)
	{
		interactables.remove(input);
	}
	
	public void addPhys(Sprite input)
	{
		interactables.add(input);
	}
	
	public void removeBackgroundPhys(Sprite input)
	{
		backgroundables.remove(input);
	}
	
	public void addBackgroundPhys(Sprite input)
	{
		backgroundables.add(input);
	}
	
	public void setScrollRate(float value)
	{
		scrollValue = value;
	}
	
	public float getScrollRate()
	{
		return scrollValue;
	}
	
	public void onUpdate(float delta)
	{
		userVelOld = userVel;
		
		touching = false;
		
		if (set)
			if (!reverse)
			{
				// make our user go doooown.
				userVel += (float) (userAcc * delta);
				physObj.setyPos((physObj.getyPos() + (userVel * delta)));
			}
			else
				physObj.setyPos(-100);
		
		float amount = scrollValue * delta; // arbitrary
		
		if (reverse)
			amount = -amount * speedUp;
		
		scrollDelta = -amount;
		scrollProgress += scrollDelta;
		
		if (scrollProgress <= 0)
			reverse = false;
		
		// try to iterate only once
		for (Iterator<Sprite> i = interactables.iterator(); i.hasNext();)
		{
			Sprite element = i.next();
			{
				if (set)
				{
					if (physObj.getxPos() + physObj.getWidth() >= element.getxPos() && physObj.getxPos() <= element.getxPos() + element.getWidth())
						checkForCollisions(element);
				}
				
				element.setxPos((element.getxPos() + amount));
			}
		}
		
		// backgrounds
		for (Iterator<Sprite> i = backgroundables.iterator(); i.hasNext();)
		{
			Sprite element = i.next();
			element.setxPos((element.getxPos() + amount / 10.0f));
		}
		
		if(set)
		if (physObj.getyPos() > height)
		{
			if(!reverse)
			death = true;
		}
		
		if(set)
		if (userVel > 0 && userVelOld <= 0)
		{
			physObj.setAnimation(CharStates.LevelOut);
		}
		
		if(set)
		if (userVel == 0 && (userVelOld != 0))
		{
			physObj.setAnimation(CharStates.GoingDown);
		}
		
	}
	
	// really should make my own Math class...
	private float linInterp(float minX, float maxX, float value, float minY, float maxY)
	{
		if (minX == maxX)
			return minY;
		
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	private void handleCollisions(int amount, boolean death)
	{
		int checkamount = (int) linInterp(-.001f, 0.25f, Math.abs(userVel), 10, physObj.getHeight());
		
		if (death)
		{
			if(!reverse)
			this.death = true;
		}
		else if (Math.abs(amount) >= checkamount)
		{
			if(!reverse)
			this.death = true;
		}
		else if (Math.abs(amount) > 0) // bit relaxed
		{
			touching = true;
			userVel = 0;
			
			physObj.setyPos((physObj.getyPos() - amount + 1));
		}
	}
	
	private void checkForCollisions(Sprite element)
	{
		OuterLoop:
		for (Iterator<physRect> i = element.getPhysRect().iterator(); i.hasNext();)
		{
			physRect elementPhysRect = i.next();
			for (Iterator<physRect> e = physObj.getPhysRect().iterator(); e.hasNext();)
			{
				physRect physObjPhysRect = e.next();
				
				int amount = collisionDetec(elementPhysRect.getCollRect(), physObjPhysRect.getCollRect());
				
				if (element.getCollectable() == CollectableStates.collectable && amount != 0)
				{
					element.setCollectable(CollectableStates.collected);
					break OuterLoop;
				}
				else if (amount != 0)
				{
					handleCollisions(amount, elementPhysRect.getHurts());
					break OuterLoop;
				}
			}
		}
	}
	
	private int collisionDetec(Rect obj1, Rect obj2)
	{
		Rect collision = new Rect();
		
		if (collision.setIntersect(obj1, obj2))
		{
			if (collision.top < obj1.top)
				return -collision.height();
			
			return collision.height();
		}
		else
			return 0;
	}
	
	public void setPlayer(Sprite input)
	{
		// only ever once. Final doesn't work.
		if (!set)
		{
			physObj = input;
			set = true;
		}
	}
	
	public void unsetPlayer()
	{
		set = false;
		physObj = null;
		userVel = 0;
	}
	
	public void purge()
	{
		interactables.clear();
		backgroundables.clear();
		
		scrollProgress = 0;
		
		reset();
	}
	
	public void reset()
	{
		scrollDelta = 0;
		userVel = 0;
		death = false;
	}
	
	public void levelReset()
	{
		reset();
		reverse = true;
	}
	
	public void nextLevel()
	{
		interactables.clear();
	}
	
}