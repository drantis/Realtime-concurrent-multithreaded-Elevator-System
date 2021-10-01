package sysc.g1.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

public class ElevatorCarComponent extends JComponent implements ActionListener{
	
	Timer carAnimation = new Timer(20, this);
	Timer doorAnimation = new Timer(100,  this);
	
	private boolean closeDoor,animateCar,openDoor;
	
	private int x,y;
	
	private Point destinationFloorLoc;
	
	private int doorVelocity = 2;
	private int carVelocity = 2;
	
	private final Dimension carDimension = new Dimension(20, 26);
	private final int paddingOffset = 10;
	private final int floorLayoutOffset = 25;
	
	private Dimension carDoorDimension = new Dimension(carDimension.width, carDimension.height);
//	private Dimension carDoorDimension = new Dimension((carDimension.width/2), carDimension.height);
	
	public ElevatorCarComponent(int x, int y) {
		this.x = x+paddingOffset;
		this.y = y;
		this.animateCar = false;
		this.closeDoor = false;
		this.openDoor = false;
		destinationFloorLoc = new Point(0,0);
		setPreferredSize(carDimension);
	}
	
	public int getCarY(){
		return this.y;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Car Body
		g.setColor(Color.gray);
//		Rectangle carBody = new Rectangle(x, y, width, height);
//		carBody.setS
		g.fillRect(x, y- 79 + floorLayoutOffset, carDimension.width, carDimension.height);
//		g.setColor(Color.WHITE);
//		g.drawString("CLOSED", 10, 0);
		
		//Car Door (not animated yet)
		g.setColor(Color.darkGray);
		g.fillRect(x+2, y- 79 + floorLayoutOffset, carDoorDimension.width -3 , carDoorDimension.height);
//		g.fillRect((carDimension.width/2)+2+paddingOffset, 0+floorOffset, carDoorDimension.width -2 , carDoorDimension.height);
	}
	

	public void animateCar(Point destination) {
		
		this.destinationFloorLoc.x = destination.x;
		this.destinationFloorLoc.y = destination.y;
		
		this.closeDoor = true;
		this.openDoor = false;
		
		this.animateCar = true;
		carAnimation.start();
	}
	
	public void animateDoor(String query) {
//		this.animateDoor = true;
		
		
		this.animateCar = false;
		
		if(query.equalsIgnoreCase("open")) {
			this.openDoor = true;
			this.closeDoor = false;
			doorAnimation.start();
		} else if (query.equalsIgnoreCase("close")) {
			
			this.closeDoor = true;
			this.openDoor = false;
			doorAnimation.start();
		}
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(this.openDoor && this.carDoorDimension.width > (this.carDimension.width)/8 ) {
			this.carDoorDimension.width -= doorVelocity;
			System.out.println("Opening door");
			
			if(this.carDoorDimension.width < (this.carDimension.width)/8) {
				doorAnimation.stop();
			}
			
		}
		
		else if (this.closeDoor && this.carDoorDimension.width < (this.carDimension.width)/8 ) {
			
			this.carDoorDimension.width += doorVelocity;
			System.out.println("Closing door");
			
			if(this.carDoorDimension.width > (this.carDimension.width)/8) {
				doorAnimation.stop();
			}
		
		}
		
		else if (this.animateCar) {
			System.out.println("Moving Car");
			System.out.println(this.y);
			this.y -= carVelocity;
			
			if(Math.abs(this.y - destinationFloorLoc.y) <= 1) {
				carAnimation.stop();
			}
		}
		
		repaint();
		
	}
}


	