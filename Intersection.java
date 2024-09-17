/**
 * CMSC 335 Project 3 Traffic Simulator
 * @author Benjamin Kuang
 * Date: March 9, 2021
 * File: Intersection.java
 *
 * This is the Intersection class for the Traffic Simulator Project.
 * This has the operations for an Intersection in the simulation.
 */

import java.awt.Color;

enum TrafficLightColor
{
	RED, YELLOW, GREEN
}

public class Intersection implements Runnable
{
	private int intersectionID = -1; //The intersection's number
	private TrafficLightColor currentTrafficLight; //Holds the current traffic light color
	//The amount of time each color stays on in seconds
	private int redTime;
	private int yellowTime;
	private int greenTime;
	private boolean stop = false; //set to true to stop the simulation
	private boolean reset = false; //true when the light has changed
	private boolean wait = true; //true if the Intersection is waiting

	public Intersection(int id, int red, int yellow, int green)
	{
		//Assign the intersection number
		intersectionID = id;
		//Assign times to timers
		redTime = red;
		yellowTime = yellow;
		greenTime = green;
		//Starts traffic light at Green
		currentTrafficLight = TrafficLightColor.GREEN;
		//Sets the JLabel
		TrafficSimulatorWindow.intersectionActiveLabels[intersectionID].setText(getColor());
		updateColor();
	}

	public void run()
	{
		while(!stop)
		{
			if(!wait)
			{
				try
				{
					//Switch statement uses the color light times
					switch(currentTrafficLight)
					{
						case RED:
							Thread.sleep(redTime*1000);
							if(!reset)
							{
								currentTrafficLight = TrafficLightColor.GREEN;
							}
							else
							{
								reset = false;
							}
							break;
						case YELLOW:
							Thread.sleep(yellowTime*1000);
							if(!reset)
							{
								currentTrafficLight = TrafficLightColor.RED;
							}
							else
							{
								reset = false;
							}
							break;
						case GREEN:
							Thread.sleep(greenTime*1000);
							if(!reset)
							{
								currentTrafficLight = TrafficLightColor.YELLOW;
							}
							else
							{
								reset = false;
							}
							break;
					}
					//Sets the JLabel
					TrafficSimulatorWindow.intersectionActiveLabels[intersectionID].setText(getColor());
					updateColor();
				}
				catch(InterruptedException e)
				{

				}
			}
			else
			{
				try
				{
					Thread.sleep(200);
				}
				catch(InterruptedException e)
				{

				}
			}
		}
	}

	//Return current color
	synchronized String getColor()
	{
		return currentTrafficLight.name();
	}

	//Updates the current color in the main window display
	private void updateColor()
	{
		switch(currentTrafficLight)
		{
			case RED:
				TrafficSimulatorWindow.intersectionActiveLabels[intersectionID].setForeground(Color.red);
				break;
			case YELLOW:
				TrafficSimulatorWindow.intersectionActiveLabels[intersectionID].setForeground(Color.yellow);
				break;
			case GREEN:
				TrafficSimulatorWindow.intersectionActiveLabels[intersectionID].setForeground(Color.green);
				break;
		}
	}

	//Setter for the red time
	public void setRedTime(int red)
	{
		redTime = red;
	}

	//Setter for the yellow time
	public void setYellowTime(int yellow)
	{
		yellowTime = yellow;
	}

	//Setter for the green time
	public void setGreenTime(int green)
	{
		greenTime = green;
	}

	//Waits the Intersection and prevents a sleeping thread from changing the light when waiting
	public void waitIntersection()
	{
		wait = true;
		reset = true;
	}

	//Starts up the intersection
	public void startIntersection()
	{
		wait = false;
	}

	//Resets the Intersection back to the beginning, prevents a sleeping thread from changing the light when reseting
	public void resetIntersection()
	{
		currentTrafficLight = TrafficLightColor.GREEN;
		//Sets the JLabel
		TrafficSimulatorWindow.intersectionActiveLabels[intersectionID].setText(getColor());
		updateColor();
		reset = true;
	}

	//Stop the intersection
	synchronized void cancel()
	{
		stop = true;
	}
}