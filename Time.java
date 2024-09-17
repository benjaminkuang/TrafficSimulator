/**
 * CMSC 335 Project 3 Traffic Simulator
 * @author Benjamin Kuang
 * Date: March 9, 2021
 * File: Time.java
 *
 * This is the Time class for the Traffic Simulator Project.
 * This implements a Thread that increments a clock in the main window.
 */

public class Time implements Runnable
{
	int updatesPerSecond = 5; //Want to update clock 5 times a second to prevent excessive delay

	public void run()
	{
		updatesPerSecond = 1000/updatesPerSecond; //Gets the delay time in milliseconds
		while(!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive())
		{
			TrafficSimulatorWindow.timeLabel.setText(getTime()); //Set the time
			try
			{
				Thread.sleep(updatesPerSecond);
			}
			catch(InterruptedException e)
			{
				//Thread continues
			}
		}
	}

	//Gets a Time in HH:MM:SS AM/PM Format
	private String getTime()
	{
		String result = "";

		//Get the current local time
		java.time.LocalDateTime currentTime = java.time.LocalDateTime.now();

		//Get the current hour
		int hour = currentTime.getHour();
		//Holder string for the AM or PM result
		String AMorPM;
		if(hour < 12) //In a 24 hour clock, if the current hour is less than 12, it is AM
		{
			AMorPM = "AM";
			if(hour == 0) //00:XX in a 24 hour format would be 12:XX AM so add 12 to the hour
			{
				hour = 12;
			}
		}
		else //Otherwise the current hour is more than 12 and it is PM
		{
			AMorPM = "PM";
			if(hour != 12) //If the current hour is not 12
			{
				hour -= 12; //Subtract 12, for example 16:XX in 24 hour format would become 4:XX, leaves 12:XX as it is
			}
		}

		String hoursCorrected;
		//Add a 0 prefix to hours if hours needs it
		if(hour < 10)
		{
			hoursCorrected = "0" + String.valueOf(hour);
		}
		else
		{
			hoursCorrected = String.valueOf(hour);
		}


		//Holder string for the minutes result
		String minutes;
		//Adds a 0 as a prefix to minutes if minutes is less than 10, otherwise XX:05 would display as XX:5
		if(currentTime.getMinute() < 10) //If minutes is less than 10
		{
			minutes = "0" + String.valueOf(currentTime.getMinute()); //Add a 0 prefix
		}
		else //Otherwise minutes is more than 10
		{
			minutes = String.valueOf(currentTime.getMinute()); //Minutes is fine as is
		}

		//Holder string for the seconds result
		String seconds;
		//Adds a 0 as a prefix to seconds if seconds is less than 10, otherwise 05 would display as 5
		if(currentTime.getSecond() < 10) //If seconds is less than 10
		{
			seconds = "0" + String.valueOf(currentTime.getSecond()); //Add a 0 prefix
		}
		else //Otherwise seconds is more than 10
		{
			seconds = String.valueOf(currentTime.getSecond()); //Minutes is fine as is
		}

		//Gets the correct current hour, minutes, and AM/PM designation
		result += hoursCorrected + ":" + minutes + ":" + seconds + " " + AMorPM;

		return result;
	}
}