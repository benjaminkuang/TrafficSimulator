/**
 * CMSC 335 Project 3 Traffic Simulator
 * @author Benjamin Kuang
 * Date: March 9, 2021
 * File: Car.java
 *
 * This is the Car class for the Traffic Simulator Project.
 * This has the operations for a Car in the simulation.
 */

public class Car implements Runnable
{
	//Some unit conversion constants
	private static final double METERS_PER_MILE = 1609.34;
	private static final double FEET_PER_METER = 3.2808;

	//Variables for the running of the car
	private int carNumber;
	private int numberIntersections;
	private double xPosition;
	private double yPosition;
	private double maxXPosition;
	private double carMaxSpeed;
	private double carSpeed;
	private String[] intersectionStatus = new String[TrafficSimulatorWindow.MAX_INTERSECTIONS];
	private double[] intersectionPositions = new double[TrafficSimulatorWindow.MAX_INTERSECTIONS];

	//Holds car data
	private String[] car = new String[4];

	//Simulation trackers
	private boolean simulationActive = true;
	private boolean carActive = false;

	private static final int SIMULATION_RATE = 100; //Simulation actions per second

	private boolean isMilesFeet = false; //Start as metric
	private boolean isHours = false; //Start as seconds

	//Build the car object
	public Car(int carID, int speed, int intersections)
	{
		carNumber = carID;
		numberIntersections = intersections;

		//This sets the car's speed based on the current units
		if(isMilesFeet) //If imperial miles/feet
		{
			if(isHours) //If hours
			{
				//Miles per hour
				carMaxSpeed = speed * METERS_PER_MILE / 3600.0; //3600 seconds in an hour
			}
			else //Else seconds
			{
				//Feet per second
				carMaxSpeed = speed / FEET_PER_METER;
			}
		}
		else //Metric km/meters
		{
			if(isHours) //If hours
			{
				//Kilometers per hour
				carMaxSpeed = speed * 1000.0 / 3600.0; //1000 meters in a kilometer, 3600 seconds in an hour
			}
			else //Else seconds
			{
				//Meters per second
				carMaxSpeed = speed;
			}
		}

		//Positions at 0
		xPosition = 0;
		yPosition = 0; //No Y-Position

		calculateMaxXPosition(intersections);

		if(numberIntersections == 0)
		{
			//Blank, no action
		}
		else
		{
			//Set the positions of each intersection
			for(int x = 0; x < numberIntersections; x++)
			{
				intersectionPositions[x] = x * 1000 + 500;
			}
		}

		//Car name identifier
		car[0] = "Car " + String.valueOf(carNumber);
		//Inform the table in the main window of the car's name
		TrafficSimulatorWindow.carData[carNumber-1][0] = car[0];
	}

	public void run()
	{
		while(!Thread.currentThread().isInterrupted() && simulationActive)
		{
			try
			{
				updateAllIntersectionsStatus(); //Update the intersections status

				if(carActive)
				{
					//Check the traffic light in front of the car
					if(intersectionStatus[(((int) xPositionWraparound(xPosition+500))/1000)].equals("RED"))
					{
						//If the traffic light is RED
						//Check if the car gets within 5 meters of a RED light
						if( (((((int) xPositionWraparound(xPosition+500))/1000)+1)*1000) - ((int) xPositionWraparound(xPosition+500)) < 5)
						{
							carSpeed = 0; //Then it will stop
						}
						if(xPosition < 10) //If the car is within 10 meters of the zero mark
						{
							carSpeed = carMaxSpeed; //Car will go at full speed
						}
					}
					else //Either the light is YELLOW or GREEN or the car is not within 5 meters of a RED light
					{
						carSpeed = carMaxSpeed; //Car goes at max speed
					}

					//Advance the car's position
					//Car speed is in meters per second, and the SIMULATION_RATE in a second
					xPosition = xPositionWraparound(xPosition + (carSpeed/((double) SIMULATION_RATE)));
				}

				updateDisplay();

				Thread.sleep(1000/SIMULATION_RATE);
			}
			catch(InterruptedException e)
			{
				//Do nothing
			}
		}
	}

	//Calculate the maximum x position based on the number of intersections
	private void calculateMaxXPosition(int intersections)
	{
		if(intersections == 0)
		{
			maxXPosition = 1000;
		}
		else
		{
			maxXPosition = intersections * 1000;
		}
	}

	//Return the maximum x position
	public int getMaxXPosition()
	{
		return (int) maxXPosition;
	}

	//If the car has reached the end of the road, loop back to the beginning
	private double xPositionWraparound(double x)
	{
		if (x >= maxXPosition)
		{
			return x - maxXPosition;
		}
		else
		{
			return x;
		}
	}

	//Gets the status of all intersections
	private void updateAllIntersectionsStatus()
	{
		for(int x = 0; x < numberIntersections; x++)
		{
			intersectionStatus[x] = TrafficSimulatorWindow.intersectionActiveLabels[x].getText();
		}
	}

	//Allows the car to be informed of intersection changes
	public void updateIntersectionStatus(int intersectionID, String status)
	{
		intersectionStatus[intersectionID] = status;
	}

	//Update the number of intersections
	public void updateNumberIntersections(int intersections)
	{
		numberIntersections = intersections;
		calculateMaxXPosition(intersections);
	}

	public int getXPosition()
	{
		return (int) xPosition;
	}

	public int getYPosition()
	{
		return (int) yPosition;
	}

	public void setXPosition(int x)
	{
		xPosition = x;
	}

	public int getMaxSpeed()
	{
		double result = 0;
		if(isMilesFeet)
		{
			if(isHours)
			{
				//Miles per hour
				result = carMaxSpeed * 3600.0 / METERS_PER_MILE;
			}
			else
			{
				//Feet per second
				result = carMaxSpeed * FEET_PER_METER;
			}
		}
		else
		{
			if(isHours)
			{
				//Kilometers per hour
				result = carMaxSpeed * 3600.0 / 1000.0;
			}
			else
			{
				//Meters per second
				result = carMaxSpeed;
			}
		}
		return (int) result;
	}

	public void setMaxSpeed(int speed)
	{
		if(isMilesFeet)
		{
			if(isHours)
			{
				//Miles per hour
				carMaxSpeed = speed * METERS_PER_MILE / 3600.0;
			}
			else
			{
				//Feet per second
				carMaxSpeed = speed / FEET_PER_METER;
			}
		}
		else
		{
			if(isHours)
			{
				//Kilometers per hour
				carMaxSpeed = speed * 1000 / 3600.0;
			}
			else
			{
				//Meters per second
				carMaxSpeed = speed;
			}
		}
	}

	public void runCar()
	{
		carActive = true;
	}

	public void stopCar()
	{
		carActive = false;
	}

	public void toggleUnitsDistance()
	{
		if(isMilesFeet) //Currently imperial
		{
			isMilesFeet = false; //Set to metric

		}
		else //Currently metric
		{
			isMilesFeet = true; //Set to imperial
		}
	}

	public void toggleUnitsTime()
	{
		if(isHours) //Currently hours
		{
			isHours = false; //Change to seconds
		}
		else //Currently seconds
		{
			isHours = true; //Change to hours
		}
	}

	public void updateDisplay()
	{
		if(isMilesFeet)
		{
			if(isHours) //In hours
			{
				//Miles per hour
				TrafficSimulatorWindow.carData[carNumber-1][1] = getXPosition()/METERS_PER_MILE;
				TrafficSimulatorWindow.carData[carNumber-1][2] = getYPosition();
				TrafficSimulatorWindow.carData[carNumber-1][3] = carSpeed/METERS_PER_MILE*3600.0;;
			}
			else //In seconds
			{
				//Feet per second
				TrafficSimulatorWindow.carData[carNumber-1][1] = getXPosition()*FEET_PER_METER;
				TrafficSimulatorWindow.carData[carNumber-1][2] = getYPosition();
				TrafficSimulatorWindow.carData[carNumber-1][3] = carSpeed*FEET_PER_METER;
			}
		}
		else //In metric
		{
			if(isHours) //In hours
			{
				//Kilometers per hour
				TrafficSimulatorWindow.carData[carNumber-1][1] = getXPosition()/1000.0;;
				TrafficSimulatorWindow.carData[carNumber-1][2] = getYPosition();
				TrafficSimulatorWindow.carData[carNumber-1][3] = carSpeed/1000.0*3600.0;;
			}
			else //In seconds
			{
				//Meters per second
				TrafficSimulatorWindow.carData[carNumber-1][1] = getXPosition();
				TrafficSimulatorWindow.carData[carNumber-1][2] = getYPosition();
				TrafficSimulatorWindow.carData[carNumber-1][3] = carSpeed;
			}
		}
		TrafficSimulatorWindow.carTable.repaint();
	}

	public void cancel()
	{
		simulationActive = false;
	}
}