package com.kobaj.runfoxrun;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;

public class Creditables
{
	ArrayList<custString> Titles;
	ArrayList<custString> Name;
	
	private int amount;
	private int fadeTime = 1000;
	private int displayTime = 3000;
	
	private int displaying = 0;
	
	private boolean done = false;
	
	public boolean getDone()
	{
		return done;
	}
	
	public Creditables(Resources resources, int width, int height)
	{
		Titles = new ArrayList<custString>();
		Name = new ArrayList<custString>();
		
		ArrayList<String> temp = new ArrayList<String>();
		
		temp.add("Thanks for Playing");
		temp.add("Chief Programmer, Chief Design, Chief Art");
		temp.add("Music");
		temp.add("Sound engineer and QA");
		temp.add("QA");
		temp.add("Title Screen Art");
		temp.add("in the making of this game!");
		
		ArrayList<String> temp2 = new ArrayList<String>();
		
		temp2.add("");
		temp2.add("Jakob Griffith");
		temp2.add("Andrew Riley");
		temp2.add("Kevin Poisson");
		temp2.add("Ryan Knight");
		temp2.add("Raymond Griffith");
		temp2.add("Thanks to anyone who helped");
		
		for (String it : temp)
			Titles.add(new custString(resources, it, 0, 0));
		
		for (String it : temp2)
			Name.add(new custString(resources, it, 0, 0));
		
		for (custString it : Titles)
		{
			it.setColor(Color.TRANSPARENT, Color.TRANSPARENT);
			it.setPosition((int)(width / 2.0f - it.measureit() / 2f), (int) (height / 2f + 20.0f / 1.5f * SurfacePanel.scale));
		}
		
		for (custString it : Name)
		{
			it.setColor(Color.TRANSPARENT, Color.TRANSPARENT);
			it.setPosition((int)(width / 2.0f - it.measureit() / 2f), (int) (height / 2f - 20.0f / 1.5f * SurfacePanel.scale));	
		}
	}
	
	// really should make my own STATIC Math class...
	private float linInterp(float minX, float maxX, float value, float minY, float maxY)
	{
		if (minX == maxX)
			return minY;
		
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	public void onUpdate(float delta)
	{
		amount += delta;
		
		if (displaying < Titles.size())
		{
			if (amount < fadeTime)
			{
				// fade in
				
				int Multiplicity = (int) linInterp(0, fadeTime, amount, 0, 256);
				int mainColor = Color.argb(Multiplicity, 255, 255, 255);
				int outlineColor = Color.argb(Multiplicity, 0, 0, 0);
				Titles.get(displaying).setColor(mainColor, outlineColor);
				Name.get(displaying).setColor(mainColor, outlineColor);
			}
			
			else if (amount > fadeTime && amount < fadeTime + displayTime)
			{
				// display
				
			}
			
			else if (amount > fadeTime + displayTime && amount < fadeTime + displayTime + fadeTime)
			{
				// fade out
				
				int Multiplicity = (int) linInterp(fadeTime + displayTime, fadeTime + displayTime + fadeTime, amount, 256, 0);
				int mainColor = Color.argb(Multiplicity, 255, 255, 255);
				int outlineColor = Color.argb(Multiplicity, 0, 0, 0);
				Titles.get(displaying).setColor(mainColor, outlineColor);
				Name.get(displaying).setColor(mainColor, outlineColor);
			}
			
			else
			{
				// swap
				amount = 0;
				displaying++;
			}
		}
		else
			done = true;
	}
	
	public void onDraw(Canvas canvas)
	{
		Titles.get(displaying).onDraw(canvas);
		Name.get(displaying).onDraw(canvas);
	}
}
