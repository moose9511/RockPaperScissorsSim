package repo;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
public class GUI {
	JFrame frame;
    JLabel numLabel, speedLabel, winLabel,  speedCounter, rockCounter, paperCounter, scissorsCounter;
    JLabel speedCounterImg, controlPanelImg, rockCounterImg, paperCounterImg, scissorsCounterImg;
	JPanel gamePane, startPanel, controlPanel;
	JSlider numSlider, speedSlider;
    JButton continueBtn, pauseBtn;
    
    // image initialization
	ImageIcon startButton = new ImageIcon(getClass().getResource("/imgs/startButton.png"));
	ImageIcon stopButton = new ImageIcon(getClass().getResource("/imgs/stopButton.png"));
	ImageIcon controlBack = new ImageIcon(getClass().getResource("/imgs/controlPanelBack.png"));
	ImageIcon counterOff = new ImageIcon(getClass().getResource("/imgs/counterOff.png"));
	ImageIcon counterOn = new ImageIcon(getClass().getResource("/imgs/counterOn.png"));
	ImageIcon counter = new ImageIcon(getClass().getResource("/imgs/counter.png"));
 		
    RPS[] items; // array to have all the rock paper and scissors objects in
    int tps; // ticks per second variable, saved to by numSlider
    boolean paused; // used to check if the simulation should be paused
    Timer timer; // timer used to loop through frames in the simulation
    int rockNum, paperNum, scissorsNum; // ints to keep track of the number of each object
    
    // adds to a specified type, removes changed type
    public void addToCounter(String type) {
    	if(type.equals("rock")) {
    		rockNum += 1;
    		scissorsNum -= 1;
    	} else if (type.equals("paper")) {
    		paperNum += 1;
    		rockNum -= 1;
    	} else {
    		scissorsNum += 1;
    		paperNum -= 1;
    	}
    }
    
    // updates the counters
    public void updateCounters() {
    	rockCounter.setText(""+rockNum);
    	paperCounter.setText(""+paperNum);
    	scissorsCounter.setText(""+scissorsNum);
    }
    
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
        	target.changeDir();
        }
        
        RPS i = itemAt(targetPos[0], targetPos[1], target);
        if(i != null){ // checks if targeted position is intersecting an item
        	
        	// gets the intersected item and checks who wins
            if(target.getType() == "rock"){
                if(i.getType() == "paper"){
                    target.setType("paper");
                    addToCounter("paper");
                } else if(i.getType() == "scissors"){
                    i.setType("rock");
                    addToCounter("rock");
                } 
            } else if(target.getType() == "paper"){
                if(i.getType() == "scissors"){
                    target.setType("scissors");
                    addToCounter("scissors");
                } else if (i.getType() == "rock"){
                    i.setType("paper");
                    addToCounter("paper");
                }
            } else if(target.getType() == "scissors"){
                if(i.getType() == "rock"){
                    target.setType("rock");
                    addToCounter("rock");
                } else if (i.getType() == "paper"){
                    i.setType("scissors");
                    addToCounter("scissors");
                }
            }
            
            target.setLocation(targetPos[0], targetPos[1]);
            target.setPos(targetPos);
            target.changeDir();
        } else {
            target.setLocation(targetPos[0], targetPos[1]);
            target.setPos(targetPos);
        }
    }
    
    // plays a frame of the simulation, used in timed loops
    public void playFrame() {
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
            	
            	if(type.equals("rock")) {
            		rockCounterImg.setIcon(counterOn);
            	}
            }
            
            updateCounters();
            
            gamePane.revalidate();
            gamePane.repaint();
    	}
    }
	public GUI() {
		// the x and y lengths from the frame width/height for startPanel and gamePane so they match up
		int panelSizeX = 105;
		int panelSizeY = 120;
		// the x and y positions from startPanel and gamePane
		int panelPosX = 75;
		int panelPosY = 70;
		
		// font initialization
		Font board = null;
		Font video = null;
		try {
			board = Font.createFont(Font.TRUETYPE_FONT, new File("src/font/board.ttf")).deriveFont(20f);
			video = Font.createFont(Font.TRUETYPE_FONT, new File("src/font/video.ttf"));
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		numSlider.setBackground(Color.black);
		// Updates number of items showed to user
        numSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                numLabel.setText(""+numSlider.getValue());
            }
        });
        
        // Button to start program
        continueBtn = new JButton("START");
        continueBtn.setBounds(startPanel.getWidth()/2-90, startPanel.getHeight()/2+100, 180, 35);
        continueBtn.setFont(video.deriveFont(20f));
        
        numLabel = new JLabel(""+numSlider.getValue());
        numLabel.setBounds(startPanel.getWidth()/2-80, startPanel.getHeight()/2-50, 160, 70);
        numLabel.setFont(board);
        numLabel.setForeground(Color.white);
        // --------- START PANEL END ------------------------------------------
        
        // panel to put configuration controls on
        controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBackground(new Color(45, 45, 45));
        controlPanel.setBounds(0,0,frame.getWidth(), 70);
        
        
        // ---------- CONTROL PANEL COMPONENTS ---------------------------------------
        // image for the control panel
        controlPanelImg = new JLabel();
        controlPanelImg.setIcon(controlBack);
        controlPanelImg.setBounds(controlPanel.getX(), controlPanel.getY(), controlBack.getIconWidth(), controlBack.getIconHeight());
        
        // slider that controls the tick speed of the simulation
        speedSlider = new JSlider(1, 99, 25);
        speedSlider.setBounds(330, controlPanel.getHeight()/2, 200, 30);
        speedSlider.setOpaque(false);
        speedSlider.setEnabled(false);
        
        // label showing the speed of the simulation in ticks per second
        speedLabel = new JLabel("Ticks per second");
        speedLabel.setFont(video.deriveFont(19f));
        speedLabel.setBounds(speedSlider.getX()+5, controlPanel.getHeight()/2-25, 200, 30);
        speedLabel.setForeground(Color.white);
        
        // creates another loop of moving the objects when changing the tick speed
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                tps = speedSlider.getValue();
                speedCounter.setText(""+tps);
                if(timer != null) {
                	timer.cancel();
                }
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                    	playFrame();
                    }
                }, 0, 1000/((tps == 0) ? 30 : tps));
            }
        });
        
        speedCounterImg = new JLabel();
        
        // counter for the ticks per second slider
        speedCounter = new JLabel(""+speedSlider.getValue());
        speedCounter.setFont(board.deriveFont(20f));
        speedCounter.setForeground(Color.white);
        speedCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        speedCounter.setBounds(speedSlider.getX()-54, controlPanel.getHeight()/2-17, 40, 40);
        
        // background image for the speed counter
        speedCounterImg = new JLabel();
        speedCounterImg.setBounds(speedCounter.getX()+4, speedCounter.getY()-3, speedCounter.getWidth(), speedCounter.getHeight());
        speedCounterImg.setIcon(new ImageIcon(counter.getImage().getScaledInstance(speedCounterImg.getWidth(), speedCounterImg.getHeight(), Image.SCALE_SMOOTH)));
        
        // shows number of rocks
        rockCounter = new JLabel("0");
        rockCounter.setFont(board.deriveFont(20f));
        rockCounter.setForeground(Color.white);
        rockCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        rockCounter.setBounds(5, 140, 60, 40);
        
        // image for rock counter
        rockCounterImg = new JLabel();
        rockCounterImg.setBounds(rockCounter.getX()+4, rockCounter.getY()-35, rockCounter.getWidth(), rockCounter.getHeight()+30);
        rockCounterImg.setIcon(new ImageIcon(counterOff.getImage().getScaledInstance(rockCounterImg.getWidth(), rockCounterImg.getHeight(), Image.SCALE_SMOOTH)));
        
        // shows number of papers
        paperCounter = new JLabel("0");
        paperCounter.setFont(board.deriveFont(20f));
        paperCounter.setForeground(Color.white);
        paperCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        paperCounter.setBounds(5, 280, 60, 40);
        
        // shows number of scissors
        scissorsCounter = new JLabel("0");
        scissorsCounter.setFont(board.deriveFont(20f));
        scissorsCounter.setForeground(Color.white);
        scissorsCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        scissorsCounter.setBounds(5, 420, 60, 40);
        
        // button to pause and resume the simulation
        pauseBtn = new JButton();
        pauseBtn.setIcon(startButton);
        pauseBtn.setBounds(panelPosX, (controlPanel.getHeight()/2)-25, startButton.getIconWidth(), startButton.getIconHeight());
        pauseBtn.setBorderPainted(false);
        pauseBtn.setContentAreaFilled(false);
        pauseBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		paused = !paused;

        		if(paused) 
        			pauseBtn.setIcon(stopButton);
        		else
        			pauseBtn.setIcon(startButton);
        	}
        });
        // ---------- CONTROL PANEL END -------------------------------------------
        
        // panel to put simulate the RPS objects on
        gamePane = new JPanel();
        gamePane.setLayout(null);
        gamePane.setBounds(panelPosX, panelPosY, frame.getWidth()-panelSizeX, frame.getHeight()-panelSizeY);
        gamePane.setBackground(new Color(20,20,20));
        gamePane.setEnabled(false);
        
        // sets settings for win label
        winLabel = new JLabel();
        winLabel.setBounds(gamePane.getWidth()/2-200, gamePane.getHeight()/2-100, 400, 200);
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winLabel.setVerticalAlignment(SwingConstants.CENTER);
        winLabel.setFont(video.deriveFont(30f));
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
                
                rockNum = numEach;
                paperNum = numEach;
                scissorsNum = numEach;
                updateCounters();
                
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
                    }
                }
                
                // starts a timer and runs the simulation
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                    	playFrame();
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
        controlPanel.add(speedLabel);
        controlPanel.add(speedCounter);
        controlPanel.add(speedCounterImg);
        controlPanel.add(rockCounter);
        controlPanel.add(rockCounterImg);
        controlPanel.add(paperCounter);
        controlPanel.add(scissorsCounter);
        controlPanel.add(pauseBtn);
        controlPanel.add(controlPanelImg);
        
        // add panels to frame
        frame.add(startPanel);
        frame.add(gamePane);
        frame.add(controlPanel);
        
        frame.revalidate();
        frame.repaint();
		frame.setVisible(true);
	}
}