package repo;


import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class RPS extends JLabel {

	public static final int RADIUS = 20; // radius of the circular hit box
	public static final int DIAMETER = RADIUS*2; // diameter of the circular hit box
	private String type; // name of the object
	private int xPos; // x coordinate in middle of object
	private int yPos; // y coordinate in middle of object
	private int[] direction = new int[2]; // x and y direction
	ImageIcon rock = new ImageIcon((new ImageIcon(getClass().getResource("/imgs/rock.png"))).getImage().getScaledInstance(RADIUS, RADIUS, Image.SCALE_SMOOTH));
	ImageIcon paper = new ImageIcon((new ImageIcon(getClass().getResource("/imgs/paper.png"))).getImage().getScaledInstance(RADIUS, RADIUS, Image.SCALE_SMOOTH));
	ImageIcon scissors = new ImageIcon((new ImageIcon(getClass().getResource("/imgs/scissors.png"))).getImage().getScaledInstance(RADIUS, RADIUS, Image.SCALE_SMOOTH));
	
	public RPS (String type, int x, int y, int[] direction) {
		// checks if type is a rock, paper or scissors, complains if it's invalid
		if(type.equals("rock")) {
			this.type = "rock";
			super.setIcon(rock);
		} else if (type.equals("paper")) {
			this.type = "paper";
			super.setIcon(paper);
		} else if (type.equals("scissors")) {
			this.type = "scissors";
			super.setIcon(scissors);
		} else {
			System.out.println("Invalid type for RPS");
			return;
		}
		
		this.type = type;
		xPos = x;
		yPos = y;
		this.direction = direction;
	}
	
	// get methods
 	public int[] getPos() {
		return new int[] {xPos, yPos};
	}
	public String getType() {
		return type;
	}
	public int[] getDir() {
		return direction;
	}
	
	// set methods
    public void setType(String type) {
    	switch(type) {
    	case "rock":
    		this.type = type;
    		super.setIcon(rock);
    		break;
    	case "paper":
    		this.type = type;
    		super.setIcon(paper);
    		break;
    	case "scissors":
    		this.type = type;
    		super.setIcon(scissors);
    		break;
    	default:
    		System.out.println("Invalid call of setType for type '" + type + "'");
    	}
    }
    public void setDir(int[] d) {
		direction = d;
	}
    
    public void setPos(int[] pos) {
    	xPos = pos[0];
    	yPos = pos[1];
    }
    
    // reverse direction methods
	public void changeX() {
		direction[0] *= -1;
	}
	public void changeY() {
		direction[1] *= -1;
	}
	public void changeDir() {
		direction[0] *= -1;
		direction[1] *= -1;
	}
    public void randDir() {
    	direction[0] = (Math.random() > .5) ? -1:1;
    	direction[1] = (Math.random() > .5) ? -1:1;
    }
	
 }