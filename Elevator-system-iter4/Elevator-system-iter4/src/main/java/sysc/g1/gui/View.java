/**
 * @author komiljon
 *
 */

package sysc.g1.gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class View extends JFrame{

	
	private static GridLayout layout = new GridLayout(0,5);
	private static GridLayout panelLayout = new GridLayout(23,0);		
	private static JTextArea elementName;
	private static Point floorCoordinates[] = new Point[22];
	private static ElevatorCarComponent car [] = new ElevatorCarComponent[4];
	private static JPanel[] elevatorPanel = new JPanel[4];
	private static JPanel floorPanel = new JPanel();
	private static Map<String, Point> floorBindings = new HashMap();
	private static final Point groundFloorXY = new Point(0,898);
	
	public static void main(String[] args) {
		
		View view = new View("SYSC 3303 Elevator System");
		view.setLayout(layout);
		
		init_floor();
		init_elevator();
		
		//Adding the floor and elevator panels
		view.add(floorPanel);
		for(byte x = 0; x < elevatorPanel.length; x++) {
			view.add(elevatorPanel[x]);
		}
		
		
		view.pack();
		
		
		//Storing the XY of each floor
		for (int i = 0; i < floorPanel.getComponents().length-2; i++) {
			String floorName = "FLOOR "+(floorCoordinates.length-(i));
			floorCoordinates[i] = floorPanel.getComponent(i+2).getLocation();
			floorBindings.put(floorName, floorCoordinates[i]);
		}
		
		//THIS SECTION IS FOR TESTING PURPOSES (door closing doesnt work yet!!!)
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		car[0].animateDoor("open");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		car[2].animateDoor("open");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		car[3].animateCar(floorBindings.get("FLOOR 4"));
		
		for (String x: floorBindings.keySet()) {
			System.out.println(x+"-->"+floorBindings.get(x));
		}
		
	}
	
	
	public View(String title) {
		this.setTitle(title);
		this.setSize(700,900);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		this.setVisible(true);
	}
	
	private static void init_floor() {
		//Floor Panel
			floorBindings.put("FLOOR 1", groundFloorXY);
			floorPanel.setPreferredSize(new Dimension(200, 900));
			floorPanel.setLayout(panelLayout);
			floorPanel.add(new JLabel("Floor"));
			for (int x = panelLayout.getRows()-2; x >= 0; x--) {
				
				elementName = new JTextArea("Floor "+(x+1));
				
				floorPanel.add(elementName);
			}
		}
		
		private static void init_elevator() {
		
				//Setting Elevator Panels
				for(byte x = 0; x < elevatorPanel.length; x++) {
					elevatorPanel[x] = new JPanel();
					elevatorPanel[x].setPreferredSize(new Dimension(105, 900));
					elevatorPanel[x].setLayout( new BoxLayout(elevatorPanel[x],BoxLayout.PAGE_AXIS));
					elevatorPanel[x].add(new JLabel("Elevator "+(x+1)));
					
				//Putting cars at inital position (floor 1)
					car[x] = new ElevatorCarComponent(groundFloorXY.x,groundFloorXY.y);
					
//					System.out.println("CAR-->"+(x+1)+" --->"+car[x].getCarY());
					
					elevatorPanel[x].add(car[x]);

//					for (byte y = 0; y < panelLayout.getRows()-1; y++) {
//						
//						//Putting cars at inital position (floor 1)
//						if (y == panelLayout.getRows()-2) {
//							car = new ElevatorCarComponent();
//							elevatorPanel[x].add(car);
//							car.animateDoor();
//							continue;
//						}
//						
//						elevatorPanel[x].add(new JPanel());
//						
//					}
					
				}
		}

}
