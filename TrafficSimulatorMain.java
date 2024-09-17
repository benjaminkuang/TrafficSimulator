/**
 * CMSC 335 Project 3 Traffic Simulator
 * @author Benjamin Kuang
 * Date: March 9, 2021
 * File: TrafficSimulatorMain.java
 *
 * This is the main class for the Traffic Simulator Project.
 * This has the main GUI window for the program.
 */

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class TrafficSimulatorMain
{
	public static void main(String[] args)
	{
		//Instantiate the main window
		new TrafficSimulatorWindow();
	}
}

//Class for the GUI
class TrafficSimulatorWindow
{
	//Various constants
	protected static final int MAX_INTERSECTIONS = 5;
	private static final int MAX_CARS = 5;
	private static final int RED_TIME = 12;
	private static final int YELLOW_TIME = 2;
	private static final int GREEN_TIME = 10;
	private static final int MAX_SPEED_METERS_SECOND = 27;

	//The main frame
	private JFrame frame = new JFrame();

	//Many JPanels
	private JPanel titlePanel;
	private JPanel timePanel;
	private JPanel operationButtonsPanel;
	private JPanel sliderPanel;
	private JPanel intersectionPanel;
	protected static JPanel intersectionPiecesPanels[] = new JPanel[MAX_INTERSECTIONS];
	private JPanel intersectionSpinnerPanel;
	private JPanel intersectionButtonPanel;
	private JPanel tablePanel;
	private JPanel carButtonsPanel;
	private JPanel radioButtonsPanel;

	//The labels, text fields, buttons, and other components
	private JLabel titleLabel;
	private JLabel currentTimeLabel;
	protected static JLabel timeLabel;
	private JButton startButton;
	private JButton continuePauseButton;
	private JButton stopButton;
	private JLabel sliderLabel;
	private JSlider roadSlider;
	private JLabel intersectionLabels[] = new JLabel[MAX_INTERSECTIONS];
	protected static JLabel intersectionActiveLabels[] = new JLabel[MAX_INTERSECTIONS];
	private JLabel redLabels[] = new JLabel[MAX_INTERSECTIONS];
	private JLabel yellowLabels[] = new JLabel[MAX_INTERSECTIONS];
	private JLabel greenLabels[] = new JLabel[MAX_INTERSECTIONS];
	protected static JSpinner intersectionsRedSpinners[] = new JSpinner[MAX_INTERSECTIONS];
	protected static JSpinner intersectionsYellowSpinners[] = new JSpinner[MAX_INTERSECTIONS];
	protected static JSpinner intersectionsGreenSpinners[] = new JSpinner[MAX_INTERSECTIONS];
	private JButton addIntersectionButton;
	private JButton removeIntersectionButton;
	protected static JTable carTable;
	private JButton addCarButton;
	private JButton removeCarButton;
	private JButton updateCarButton;
	private JTextField updateCarSpeedTextField;
	private JRadioButton milesRadioButton;
	private JRadioButton kilometersRadioButton;
	private JRadioButton hoursRadioButton;
	private JRadioButton secondsRadioButton;

	//Extra variables for logic
	private int currentNumberIntersections = 3;
	private int currentNumberCars = 3;
	private Time time;
	private Intersection intersections[] = new Intersection[MAX_INTERSECTIONS];
	private Car cars[] = new Car[MAX_INTERSECTIONS];
	private String[] columnNames = {"Car", "X-Pos", "Y-Pos", "Speed"};
	protected static Object[][] carData = new Object[MAX_CARS][4];
	private boolean isMeters = true;
	private boolean isSeconds = true;
	private boolean simulationActive = false;

	public TrafficSimulatorWindow()
	{
		//Set the Layout
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		//Set the title
		frame.setTitle("Traffic Simulator");

		//Set the size of the window
		frame.setSize(1000, 520);

		//Action for the close button
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Build the panel
		buildPanel();

		//Add panels to frame
		frame.add(titlePanel);
		frame.add(timePanel);
		frame.add(operationButtonsPanel);
		frame.add(sliderPanel);
		frame.add(intersectionPanel);
		frame.add(intersectionSpinnerPanel);
		frame.add(intersectionButtonPanel);
		frame.add(tablePanel);
		frame.add(carButtonsPanel);
		frame.add(radioButtonsPanel);

		//Build logic
		buildLogic();

		//Build Slider
		buildSlider();

		//Display the window
		frame.setVisible(true);

	}

	private void buildPanel()
	{
		//Create the panels
		titlePanel = new JPanel();
		timePanel = new JPanel();
		sliderPanel = new JPanel();
		operationButtonsPanel = new JPanel();
		intersectionPanel = new JPanel();
		intersectionSpinnerPanel = new JPanel();
		intersectionButtonPanel = new JPanel();
		tablePanel = new JPanel();
		carButtonsPanel = new JPanel();
		radioButtonsPanel = new JPanel();

		//Create labels, text fields, buttons, and other components
		titleLabel = new JLabel("Traffic Simulator Program");
		currentTimeLabel = new JLabel("Current time: ");
		timeLabel = new JLabel();
		startButton = new JButton("Start");
		continuePauseButton = new JButton("Continue");
		stopButton = new JButton("Stop");
		sliderLabel = new JLabel("Car 1");
		roadSlider = new JSlider(JSlider.HORIZONTAL, 0, currentNumberIntersections*1000, 0);
		addIntersectionButton = new JButton("Add Intersection");
		removeIntersectionButton = new JButton("Remove Intersection");
		carTable = new JTable(carData, columnNames);
		addCarButton = new JButton("Add Car");
		removeCarButton = new JButton("Remove Car");
		updateCarButton = new JButton("Update Car");
		updateCarSpeedTextField = new JTextField(10);
		milesRadioButton = new JRadioButton("Feet");
		kilometersRadioButton = new JRadioButton("Meters");
		hoursRadioButton = new JRadioButton("Hours");
		secondsRadioButton = new JRadioButton("Seconds");

		//Adjust JTable Size
		carTable.setPreferredScrollableViewportSize(new Dimension(400, 80));
		carTable.setFillsViewportHeight(true);

		//Selected Starting Radio Buttons
		kilometersRadioButton.setSelected(true);
		secondsRadioButton.setSelected(true);

		//Grouping Radio Buttons
		ButtonGroup distanceUnits = new ButtonGroup();
		ButtonGroup timeUnits = new ButtonGroup();
		distanceUnits.add(milesRadioButton);
		distanceUnits.add(kilometersRadioButton);
		timeUnits.add(hoursRadioButton);
		timeUnits.add(secondsRadioButton);

		//Add action listeners to the buttons
		startButton.addActionListener(new startButtonListener());
		continuePauseButton.addActionListener(new continuePauseButtonListener());
		stopButton.addActionListener(new stopButtonListener());
		addIntersectionButton.addActionListener(new addIntersectionButtonListener());
		removeIntersectionButton.addActionListener(new removeIntersectionButtonListener());
		addCarButton.addActionListener(new addCarButtonListener());
		removeCarButton.addActionListener(new removeCarButtonListener());
		updateCarButton.addActionListener(new updateCarButtonListener());
		milesRadioButton.addActionListener(new milesRadioButtonListener());
		kilometersRadioButton.addActionListener(new kilometersRadioButtonListener());
		hoursRadioButton.addActionListener(new hoursRadioButtonListener());
		secondsRadioButton.addActionListener(new secondsRadioButtonListener());

		//ListSelectionListener to JTable
		carTable.getSelectionModel().addListSelectionListener(new carTableListSelectionListener());

		//Create Intersection panel
		createIntersectionPanel();

		//Attach components to panels
		titlePanel.add(titleLabel);
		timePanel.add(currentTimeLabel);
		timePanel.add(timeLabel);
		operationButtonsPanel.add(startButton);
		operationButtonsPanel.add(continuePauseButton);
		operationButtonsPanel.add(stopButton);
		sliderPanel.add(sliderLabel);
		sliderPanel.add(roadSlider);
		intersectionButtonPanel.add(addIntersectionButton);
		intersectionButtonPanel.add(removeIntersectionButton);
		tablePanel.add(new JScrollPane(carTable));
		carButtonsPanel.add(addCarButton);
		carButtonsPanel.add(removeCarButton);
		carButtonsPanel.add(updateCarButton);
		carButtonsPanel.add(updateCarSpeedTextField);
		radioButtonsPanel.add(milesRadioButton);
		radioButtonsPanel.add(kilometersRadioButton);
		radioButtonsPanel.add(new JLabel("            "));
		radioButtonsPanel.add(hoursRadioButton);
		radioButtonsPanel.add(secondsRadioButton);
	}

	private void buildLogic()
	{
		//Start the Time Thread
		time = new Time();
		new Thread(time).start();

		//Start Intersection Threads
		for(int x = 0; x < currentNumberIntersections; x++)
		{
			intersections[x] = new Intersection(x, RED_TIME, YELLOW_TIME, GREEN_TIME);
			new Thread(intersections[x]).start();
		}

		//Start Car Threads
		for(int x = 0; x < currentNumberCars; x++)
		{
			cars[x] = new Car(x + 1, (int) (Math.random() * (MAX_SPEED_METERS_SECOND - 0 + 1) + 0), currentNumberIntersections);
			new Thread(cars[x]).start();
		}
	}

	//Build the Slider
	private void buildSlider()
	{
		//Select first row
		carTable.setRowSelectionInterval(0, 0);
		//Set the starting value
		roadSlider.setValue(0);
		//Formatting
		roadSlider.setMajorTickSpacing(500);
		roadSlider.setMinorTickSpacing(100);
		roadSlider.setPaintTicks(true);
		roadSlider.setPaintLabels(true);
		roadSlider.setPreferredSize(new Dimension(800, 60));
		roadSlider.repaint();
		//Start the Slider Updater Thread
		new Thread(new RoadSliderUpdater()).start();
	}

	private void createIntersectionPanel()
	{
		//Create the intersections
		for(int x = 0; x < currentNumberIntersections; x++)
		{
			buildNewIntersection(x + 1);
		}
	}

	//Build each intersection
	private void buildNewIntersection(int currentIntersection)
	{
		int current = currentIntersection - 1;
		//The intersection labels
		intersectionLabels[current] = new JLabel("Interesection " + currentIntersection + ": ");
		intersectionActiveLabels[current] = new JLabel("");
		//A JSpinner for each Red/Yellow/Green Light with a column size of 2
		intersectionsRedSpinners[current] = new JSpinner(new SpinnerNumberModel(RED_TIME, 1, 999, 1));
		((JSpinner.DefaultEditor) intersectionsRedSpinners[current].getEditor()).getTextField().setColumns(2);
		intersectionsYellowSpinners[current] = new JSpinner(new SpinnerNumberModel(YELLOW_TIME, 1, 999, 1));
		((JSpinner.DefaultEditor) intersectionsYellowSpinners[current].getEditor()).getTextField().setColumns(2);
		intersectionsGreenSpinners[current] = new JSpinner(new SpinnerNumberModel(GREEN_TIME, 1, 999, 1));
		((JSpinner.DefaultEditor) intersectionsGreenSpinners[current].getEditor()).getTextField().setColumns(2);
		// R/Y/G Labels for the Spinners
		redLabels[current] = new JLabel("R");
		yellowLabels[current] = new JLabel("Y");
		greenLabels[current] = new JLabel("G");

		//Attach Change Listeners
		intersectionsRedSpinners[current].addChangeListener(new TrafficLightSpinnerChangeListener("R", current));
		intersectionsYellowSpinners[current].addChangeListener(new TrafficLightSpinnerChangeListener("Y", current));
		intersectionsGreenSpinners[current].addChangeListener(new TrafficLightSpinnerChangeListener("G", current));

		//Layout Manager Formatting the Intersections to each other
		JPanel intersectionStatePanel = new JPanel();
		JPanel intersectionSpinnersStatePanel = new JPanel();

		//The top part of the intersection
		intersectionStatePanel.add(intersectionLabels[current]);
		intersectionStatePanel.add(intersectionActiveLabels[current]);

		//The bottom part of the intersection
		intersectionSpinnersStatePanel.add(redLabels[current]);
		intersectionSpinnersStatePanel.add(intersectionsRedSpinners[current]);
		intersectionSpinnersStatePanel.add(yellowLabels[current]);
		intersectionSpinnersStatePanel.add(intersectionsYellowSpinners[current]);
		intersectionSpinnersStatePanel.add(greenLabels[current]);
		intersectionSpinnersStatePanel.add(intersectionsGreenSpinners[current]);

		//Make the combined intersection panel
		intersectionPiecesPanels[current] = new JPanel();
		intersectionPiecesPanels[current].setLayout(new BoxLayout(intersectionPiecesPanels[current], BoxLayout.Y_AXIS));
		//Add the top and bottoms pieces together
		intersectionPiecesPanels[current].add(intersectionStatePanel);
		intersectionPiecesPanels[current].add(intersectionSpinnersStatePanel);
		//Add the combined intersection to the completed intersections array
		intersectionPanel.add(intersectionPiecesPanels[current]);
	}

	//ActionListener for the Start Button
	private class startButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//Starts up the intersection
			for(int x=0; x<currentNumberIntersections; x++)
			{
				intersections[x].startIntersection();
			}
			//Starts up the cars
			for(int x=0; x<currentNumberCars; x++)
			{
				cars[x].runCar();
			}
			//Changes the Continue/Pause button to Pause
			continuePauseButton.setText("Pause");
			//Toggles the Simulation to Active
			simulationActive = true;
		}
	}

	//ActionListener for the Continue/Pause button
	private class continuePauseButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If it is the continue button
			if(continuePauseButton.getText().equals("Continue"))
			{
				//Starts up the intersection
				for(int x=0; x<currentNumberIntersections; x++)
				{
					intersections[x].startIntersection();
				}
				//Starts up the cars
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].runCar();
				}
				//Changes the Continue/Pause button to Pause
				continuePauseButton.setText("Pause");
				//Toggles the Simulation to Active
				simulationActive = true;
			}
			else //The button says Pause
			{
				//Pauses the intersection
				for(int x=0; x<currentNumberIntersections; x++)
				{
					intersections[x].waitIntersection();
				}
				//Pauses the cars
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].stopCar();
				}
				//Changes the Continue/Pause button to Continue
				continuePauseButton.setText("Continue");
				//Toggles the Simulation to Inactive
				simulationActive = false;
			}
		}
	}

	//ActionListener for the Stop button
	private class stopButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//Stops and resets the Intersections
			for(int x=0; x<currentNumberIntersections; x++)
			{
				intersections[x].waitIntersection();
				intersections[x].resetIntersection();
			}
			//Stops and resets the Cars
			for(int x=0; x<currentNumberCars; x++)
			{
				cars[x].stopCar();
				cars[x].setXPosition(0);
			}
			//Changes the Continue/Pause button to Continue
			continuePauseButton.setText("Continue");
			//Toggles the Simulation to Inactive
			simulationActive = false;
		}
	}

	//ActionListener for the Add Intersection button
	private class addIntersectionButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If the maximum number of intersections has not been reached
			if(currentNumberIntersections < MAX_INTERSECTIONS)
			{
				//Build a new Intersection panel
				buildNewIntersection(currentNumberIntersections + 1);
				//Build a new Intersection object
				intersections[currentNumberIntersections] = new Intersection(currentNumberIntersections, RED_TIME, YELLOW_TIME, GREEN_TIME);
				//Start a new Intersection thread
				new Thread(intersections[currentNumberIntersections]).start();

				//If the simulation is currently active, run the Intersection as well
				if(simulationActive)
				{
					intersections[currentNumberIntersections].startIntersection();
				}

				//Increment the current number of intersections
				currentNumberIntersections++;

				//Inform the cars of the new intersection
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].updateNumberIntersections(currentNumberIntersections);
				}
				//Repaint the window
				frame.repaint();
			}
		}
	}

	//ActionListener for the Remove Intersection button
	private class removeIntersectionButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(currentNumberIntersections > 0)
			{
				int current = currentNumberIntersections - 1;
				//Removes the last intersection from the panel
				intersectionPanel.remove(intersectionPiecesPanels[current]);
				//Null the panel and spinners
				intersectionPiecesPanels[current] = null;
				intersectionsRedSpinners[current] = null;
				intersectionsYellowSpinners[current] = null;
				intersectionsGreenSpinners[current] = null;
				//Cancel the Intersection thread
				intersections[current].cancel();
				//Decrement the number of intersections
				currentNumberIntersections--;
				//Inform the cars of the removal of an intersection
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].updateNumberIntersections(currentNumberIntersections);
				}
				//Repaint the window
				frame.repaint();
			}
		}
	}

	//SpinnerChangeListener for Traffic Spinners
	private class TrafficLightSpinnerChangeListener implements ChangeListener
	{
		//This identifies which Traffic Light Color and Intersection each Spinner belongs to
		String lightColor;
		int intersectionNumber;

		TrafficLightSpinnerChangeListener(String color, int intersection)
		{
			lightColor = color;
			intersectionNumber = intersection;
		}

		public void stateChanged(ChangeEvent e)
		{
			//Depending on which Traffic Light Color the Spinner is, inform the matching Intersection that a R/Y/G value has changed
			if(lightColor.equals("R"))
			{
				intersections[intersectionNumber].setRedTime((int) intersectionsRedSpinners[intersectionNumber].getValue());
			}
			else if(lightColor.equals("Y"))
			{
				intersections[intersectionNumber].setYellowTime((int) intersectionsYellowSpinners[intersectionNumber].getValue());
			}
			else if(lightColor.equals("G"))
			{
				intersections[intersectionNumber].setGreenTime((int) intersectionsGreenSpinners[intersectionNumber].getValue());
			}
		}
	}

	//ListSelectionListener for the Table, gets the currently selected row when the row changes
	private class carTableListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			//If there are no cars
			if(currentNumberCars == 0)
			{
				//Do nothing
			}
			//If the currently selected row is more than the number of cars
			else if(carTable.getSelectedRow() >= currentNumberCars)
			{
				//Select the last most active row
				carTable.setRowSelectionInterval(currentNumberCars-1, currentNumberCars-1);
			}
			else //The selected row is a correct row
			{
				//Update the Speed Text Field
				updateCarSpeedTextField.setText(String.valueOf(cars[carTable.getSelectedRow()].getMaxSpeed()));
				//Set the slider Label
				sliderLabel.setText(String.valueOf(carData[carTable.getSelectedRow()][0]));
			}
		}
	}

	//ActionListener for the Add Car button
	private class addCarButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If MAX_CARS has not been reached
			if(currentNumberCars < MAX_CARS)
			{
				//Add a new car
				cars[currentNumberCars] = new Car(currentNumberCars + 1, (int) (Math.random() * (MAX_SPEED_METERS_SECOND - 0 + 1) + 0), currentNumberIntersections);
				//Start up a new Car thread
				new Thread(cars[currentNumberCars]).start();
				//Start up the Car if the simulation is active
				if(simulationActive)
				{
					cars[currentNumberCars].runCar();
				}
				//Increment the number of cars
				currentNumberCars++;
			}
		}
	}

	//ActionListener for the Remove Car button
	private class removeCarButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If there is a car to remove
			if(currentNumberCars > 0)
			{
				//Decrement the current number of cars
				currentNumberCars--;
				//Cancel the last Car object's thread
				cars[currentNumberCars].cancel();
				//Null the last Car object
				cars[currentNumberCars] = null;
				//Clear the last row in the table
				carData[currentNumberCars][0] = "";
				carData[currentNumberCars][1] = "";
				carData[currentNumberCars][2] = "";
				carData[currentNumberCars][3] = "";
				//Repaint the table
				carTable.repaint();
				//Select the last active car in the table
				if(currentNumberCars > 0)
				{
					carTable.setRowSelectionInterval(currentNumberCars-1, currentNumberCars-1);
				}
			}
		}
	}

	//ActionListener for the Update Car button
	private class updateCarButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If a valid row has been selected
			if(carTable.getSelectedRow() != -1)
			{
				try
				{
					//Attempt to get a number to set the selected car's max speed
					cars[carTable.getSelectedRow()].setMaxSpeed(Integer.valueOf(updateCarSpeedTextField.getText()));
				}
				catch(NumberFormatException numE)
				{
					//If an integer is not entered the operation does nothing
				}
			}
		}
	}

	//The Miles Radio Button Listener
	private class milesRadioButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If the radio group was selected meters
			if(isMeters)
			{
				//Informs the cars that the unit has been changed
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].toggleUnitsDistance();
				}
				//Sets meters to false
				isMeters = false;
			}
		}
	}

	//The Kilometers Radio Button Listener
	private class kilometersRadioButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If the radio group was not selected meters
			if(!isMeters)
			{
				//Informs the cars that the unit has been changed
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].toggleUnitsDistance();
				}
				//Sets meters to true
				isMeters = true;
			}
		}
	}

	//The Hours Radio Button Listener
	private class hoursRadioButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If the radio group was selected seconds
			if(isSeconds)
			{
				//Informs the cars that the unit has been changed
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].toggleUnitsTime();
				}
				//Switch the distance unit buttons to their hour versions
				milesRadioButton.setText("Miles");
				kilometersRadioButton.setText("Kilometers");
				//Set the seconds to false
				isSeconds = false;
			}
		}
	}

	//The Seconds Radio Button Listener
	private class secondsRadioButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If the radio group was not selected seconds
			if(!isSeconds)
			{
				//Informs the cars that the unit has been changed
				for(int x=0; x<currentNumberCars; x++)
				{
					cars[x].toggleUnitsTime();
				}
				//Switch the distance unit buttons to their seconds versions
				milesRadioButton.setText("Feet");
				kilometersRadioButton.setText("Meters");
				//Set the seconds to true
				isSeconds = true;
			}
		}
	}

	//Runnable Class for a Road Slider Updater Thread
	private class RoadSliderUpdater implements Runnable
	{
		public RoadSliderUpdater()
		{

		}

		//The run method will always run
		public void run()
		{
			try
			{
				while(true)
				{
					try
					{
						//This will update the values on the slider based on the currently selected row in the table
						roadSlider.setValue(cars[carTable.getSelectedRow()].getXPosition());
						//This will update the values on the slider if the number of intersections change
						roadSlider.setMaximum(cars[carTable.getSelectedRow()].getMaxXPosition());
						//Repaint the slider
						roadSlider.repaint();
					}
					catch(NullPointerException e) //If no cars are running and the table is empty
					{
						//Sets the slider to the zero position
						roadSlider.setValue(0);
					}

					Thread.sleep(10);
				}
			}
			catch(InterruptedException e)
			{
				//Do nothing
			}
		}
	}
}