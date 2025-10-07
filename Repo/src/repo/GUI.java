package repo;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
public class GUI {
	JFrame frame;
    JLabel numLabel, speedLabel, winLabel;
	JPanel gamePane, startPanel, controlPanel;
	JSlider numSlider, speedSlider;
    JButton continueBtn, pauseBtn;
    RPS[] items;
    int tps;
    boolean paused;
    Timer timer;
    
    // checks if a circular area at a specified point is within the frame
	public boolean isValid(int x, int y) {
		return ((x > 0 && x < gamePane.getWidth()-RPS.RADIUS) && (y > 0 && y < gamePane.getHeight()-RPS.RADIUS));
	}
    
	// checks if a circular area at a specified point intersects an area of an item, returns the intersected item if so
    public RPS itemAt(int x, int y, RPS self){
        if(isValid(x,y)){
            // checks each items distance to the point to see if the distance is close enough to have intersected
            for(RPS item : items){
	            if(item != null && item != self) {
	                int[] pos = item.getPos();
	                double distance = Math.hypot(pos[0]-x, pos[1]-y);
	                if(Math.abs(distance) < RPS.RADIUS){
	                    return item;
	                }
	            }
            }
        } 
        return null;
    }

    // moves an item to a desired position based on it's direction and speed
    public void move(RPS target){
    	tps = 30;
        int[] dir = target.getDir();
        int[] pos = target.getPos();
        int speed = 5;
        int[] targetPos = new int[] {pos[0]+(dir[0]*speed), pos[1]+(dir[1]*speed)};
        
        if(!isValid(targetPos[0], targetPos[1])) {
        	target.setDir(new int[] {-dir[0], -dir[1]});
        }
        
        RPS i = itemAt(targetPos[0], targetPos[1], target);
        if(i != null){ // checks if targeted position is intersecting an item
        	
        	// gets the intersected item and checks who wins
            if(target.getType() == "rock"){
                if(i.getType() == "paper"){
                    target.setType("paper");
                } else if(i.getType() == "scissors"){
                    i.setType("rock");
                } 
            } else if(target.getType() == "paper"){
                if(i.getType() == "scissors"){
                    target.setType("scissors");
                } else if (i.getType() == "rock"){
                    i.setType("paper");
                }
            } else if(target.getType() == "scissors"){
                if(i.getType() == "rock"){
                    target.setType("rock");
                } else if (i.getType() == "paper"){
                    i.setType("scissors");
                }
            }
            
            target.setLocation(targetPos[0], targetPos[1]);
            target.setPos(targetPos);
            target.randDir();
        } else {
            target.setLocation(targetPos[0], targetPos[1]);
            target.setPos(targetPos);
        }
    }
	public GUI() {
		// the x and y lengths from the frame width/height for startPanel and gamePane so they match up
		int panelSizeX = 105;
		int panelSizeY = 100;
		// the x and y positions from startPanel and gamePane
		int panelPosX = 75;
		int panelPosY = 50;
		
		// FRAME 
		frame = new JFrame("Rock Paper Scissors Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 650);
		
		// panel that shows up at the start of the simulation
        startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(panelPosX, panelPosY, frame.getWidth()-panelSizeX, frame.getHeight()-panelSizeY);
        startPanel.setBackground(Color.black);
        
        // ------- START PANEL COMPONENTS --------------------------------------
		// Number slider for amount of items
		numSlider = new JSlider(1, 50, 25);
		numSlider.setBounds(startPanel.getWidth()/2-100, startPanel.getHeight()/2, 200, 80);
		// Updates number of items showed to user
        numSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                numLabel.setText("Number of each item: " + numSlider.getValue());
            }
        });
        
        // Button to start program
        continueBtn = new JButton("Continue to simulation");
        continueBtn.setBounds(startPanel.getWidth()/2-90, startPanel.getHeight()/2+100, 180, 35);
        
        numLabel = new JLabel("Number of each item: " + numSlider.getValue());
        numLabel.setBounds(startPanel.getWidth()/2-80, startPanel.getHeight()/2-50, 160, 70);
        numLabel.setForeground(Color.white);
        // --------- START PANEL END ------------------------------------------
        
        // panel to put configuration controls on
        controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBackground(new Color(45, 45, 45));
        controlPanel.setBounds(0,0,frame.getWidth(), 50);
        
        // ---------- CONTROL PANEL COMPONENTS ---------------------------------------
        // slider that controls the tick speed of the simulation
        speedSlider = new JSlider(1, 100, 25);
        speedSlider.setBounds((int) Math.round(controlPanel.getWidth()*0.75)-100, 10, 200, 30);
        speedSlider.setBackground(controlPanel.getBackground());
        speedSlider.setEnabled(false);
        
        // label showing the speed of the simulation in ticks per second
        speedLabel = new JLabel("Ticks per second: 30");
        speedLabel.setBounds((int) Math.round(controlPanel.getWidth()*0.55)-100, 10, 200, 30);
        speedLabel.setForeground(Color.white);
        speedLabel.setEnabled(false);
        // creates another loop of moving the objects when changing the tick speed
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                tps = speedSlider.getValue();
                speedLabel.setText("Ticks per second:  " + tps);
                System.out.println(tps);
                if(timer != null) {
                	timer.cancel();
                }
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                    	if(!paused) {
                    		String type = null;
                    		boolean allSameType = true;
	                        for(RPS item : items){
	                        	
	                            move(item);
	                            type = (type == null) ? item.getType() : type;
	                            allSameType = (item.getType() == type) ? allSameType : false;
	                            
	                        }
	                        if(allSameType) {
	                        	winLabel.setText(type + " Wins!");
	                        	winLabel.setVisible(true);
	                        }
	                        gamePane.revalidate();
	                        gamePane.repaint();
                    	}
                    }
                }, 0, 1000/((tps == 0) ? 30 : tps));
            }
        });
        
        // button to pause and resume the simulation
        pauseBtn = new JButton("Pause Simulation");
        pauseBtn.setBounds(panelPosX, (controlPanel.getHeight()/2)-15, 150, 30);
        pauseBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		paused = !paused;
        		pauseBtn.setText((paused) ? "Resume Simulation" : "Pause Simulation");
        	}
        });
        pauseBtn.setEnabled(false);
        // ---------- CONTROL PANEL END -------------------------------------------
        
        // panel to put simulate the RPS objects on
        gamePane = new JPanel();
        gamePane.setLayout(null);
        gamePane.setBounds(panelPosX, panelPosY, frame.getWidth()-panelSizeX, frame.getHeight()-panelSizeY);
        gamePane.setBackground(new Color(20,20,20));
        gamePane.setEnabled(false);
        
        // sets settings for win label
        winLabel = new JLabel();
        winLabel.setBounds(gamePane.getWidth()/2-100, gamePane.getHeight()/2-100, 200, 200);
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winLabel.setVerticalAlignment(SwingConstants.CENTER);
        winLabel.setFont(new Font("Roboto", Font.BOLD, 30));
        winLabel.setForeground(Color.white);
        winLabel.setVisible(false);
        
        // continues to program
        continueBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                
            	// enables controls
            	speedSlider.setEnabled(true);
            	speedLabel.setEnabled(true);
            	pauseBtn.setEnabled(true);
            	
            	// gets rid of the start panel, shows game panel with simulation
                startPanel.setEnabled(false);
                startPanel.setVisible(false);
                gamePane.setEnabled(true);
                
                // gets value from slider
                int numEach = numSlider.getValue();
                items = new RPS[numEach*3];
                
                // initializes an equal amount of RPS objects from numSlider value
                // sets random starting position and direction
                for(int i = 0; i < 3; i++){
                    for(int ii = 0; ii < numEach; ii++){
                        int x = (int) Math.round(Math.random()*frame.getWidth());
                        int y = (int) Math.round(Math.random()*frame.getHeight());
                        while((itemAt(x,y, null) != null) || !isValid(x,y)){
                            x = (int) Math.round(Math.random()*frame.getWidth());
                            y = (int) Math.round(Math.random()*frame.getHeight());
                        }
                        int[] dir = {(Math.random() > .5) ? -1:1, (Math.random() > .5) ? -1:1};
                        RPS item = new RPS((i == 0) ? "rock" : (i == 1) ? "paper" : "scissors", x, y, dir);
                        item.setBounds(x,y, 20, 20);
                        items[(i*numEach)+ii] = item;
                        gamePane.add(item);
                        System.out.println("Placed " + item.getType());
                    }
                }
                
                // starts a timer and runs the simulation
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                    	if(!paused) {
                    		String type = null;
                    		boolean allSameType = true;
	                        for(RPS item : items){
	                            //System.out.print(item.getPos)
	                            move(item);
	                            type = (type == null) ? item.getType() : type;
	                            allSameType = (item.getType() == type) ? allSameType : false;
	                            
	                        }
	                        if(allSameType) {
	                        	winLabel.setText(type + " Wins!");
	                        	winLabel.setVisible(true);
	                        }
	                        gamePane.revalidate();
	                        gamePane.repaint();
                    	}
                    }
                }, 0, 1000/30);
            }
        });
        
        // add components to startPanel
        startPanel.add(numSlider);
        startPanel.add(numLabel);
        startPanel.add(continueBtn);
        
        gamePane.setComponentZOrder(winLabel, 0);
        gamePane.add(winLabel);
        
        // add components to controlPanel
        controlPanel.add(speedSlider);
        controlPanel.add(pauseBtn);
        controlPanel.add(speedLabel);
        
        // add panels to frame
        frame.add(startPanel);
        frame.add(gamePane);
        frame.add(controlPanel);
        
        frame.revalidate();
        frame.repaint();
		frame.setVisible(true);
	}
}