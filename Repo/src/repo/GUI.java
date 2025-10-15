package repo;
import javax.swing.event.*;

import repo.Main.resetObserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
public class GUI {
	JFrame frame;
    JLabel numLabel, speedLabel, winLabel,  speedCounter, rockCounter, paperCounter, scissorsCounter, guessLabel;
    JLabel speedCounterImg, controlPanelImg, startPanelImg, rockCounterImg, paperCounterImg, scissorsCounterImg, rockLabel, paperLabel, scissorsLabel, numImg, ventImg;
	JPanel gamePane, startPanel, controlPanel;
	JSlider numSlider, speedSlider;
    JButton continueBtn, pauseBtn, resetBtn, softReset;
    
    // image initialization
	ImageIcon startButton = new ImageIcon(getClass().getResource("/imgs/startButton.png"));
	ImageIcon stopButton = new ImageIcon(getClass().getResource("/imgs/stopButton.png"));
	ImageIcon controlBack = new ImageIcon(getClass().getResource("/imgs/controlPanelBack.png"));
	ImageIcon counterOff = new ImageIcon (new ImageIcon(getClass().getResource("/imgs/counterOff.png")).getImage().getScaledInstance(60, 70, Image.SCALE_SMOOTH));
	ImageIcon counterOn = new ImageIcon (new ImageIcon(getClass().getResource("/imgs/counterOn.png")).getImage().getScaledInstance(60, 70, Image.SCALE_SMOOTH));
	ImageIcon counter = new ImageIcon (new ImageIcon(getClass().getResource("/imgs/counter.png")).getImage().getScaledInstance(45, 40, Image.SCALE_SMOOTH));
	ImageIcon speedLabelImg = new ImageIcon(getClass().getResource("/imgs/speedLabelImg.png"));
	ImageIcon rockLabelImg = new ImageIcon(getClass().getResource("/imgs/rockLabel.png"));
	ImageIcon paperLabelImg = new ImageIcon(getClass().getResource("/imgs/paperLabel.png"));
	ImageIcon scissorsLabelImg = new ImageIcon(getClass().getResource("/imgs/scissorsLabel.png"));
	ImageIcon startPanelBack = new ImageIcon(getClass().getResource("/imgs/startPanelBack.png"));
	ImageIcon frameBack = new ImageIcon(getClass().getResource("/imgs/frameBack.png"));
	ImageIcon continueBtnImg = new ImageIcon(getClass().getResource("/imgs/continueButton.png"));
	ImageIcon continueBtnImg2 = new ImageIcon(getClass().getResource("/imgs/continueButton2.png"));
	ImageIcon resetButton = new ImageIcon(new ImageIcon(getClass().getResource("/imgs/resetButtonClose.png")).getImage().getScaledInstance(25, 80, Image.SCALE_SMOOTH));
	ImageIcon resetButton2 = new ImageIcon(new ImageIcon(getClass().getResource("/imgs/resetButtonOpen.png")).getImage().getScaledInstance(25, 80, Image.SCALE_SMOOTH));
	ImageIcon resetButton3 = new ImageIcon(new ImageIcon(getClass().getResource("/imgs/resetButtonOn.png")).getImage().getScaledInstance(25, 80, Image.SCALE_SMOOTH));
	ImageIcon vent = new ImageIcon(getClass().getResource("/imgs/vent.png"));
	ImageIcon shuffleBtn1 = new ImageIcon(new ImageIcon(getClass().getResource("/imgs/shuffleButton1.png")).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
	ImageIcon shuffleBtn2 = new ImageIcon(new ImageIcon(getClass().getResource("/imgs/shuffleButton2.png")).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
	ImageIcon rockPick = new ImageIcon((new ImageIcon(getClass().getResource("/imgs/rock.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    ImageIcon paperPick = new ImageIcon((new ImageIcon(getClass().getResource("/imgs/paper.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    ImageIcon scissorsPick = new ImageIcon((new ImageIcon(getClass().getResource("/imgs/scissors.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));	
 		
    RPS[] items; // array to have all the rock paper and scissors objects in
    int tps; // ticks per second variable, saved to by numSlider
    boolean paused; // used to check if the simulation should be paused
    Timer timer; // timer used to loop through frames in the simulation
    int rockNum, paperNum, scissorsNum; // ints to keep track of the number of each object
    String userPick; // item the user guessed would win
    
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
        int[] dir = target.getDir();
        int[] pos = target.getPos();
        int speed = 5;
        int[] targetPos = new int[] {pos[0]+(dir[0]*speed), pos[1]+(dir[1]*speed)};
        
        if(!isValid(targetPos[0], targetPos[1])) {
        	if(pos[0] <= RPS.RADIUS || pos[0] >= gamePane.getWidth()-RPS.DIAMETER)
        		target.changeX();
        	if (pos[1] <= RPS.RADIUS || pos[1] >= gamePane.getHeight()-RPS.DIAMETER)
        		target.changeY();
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
            	guessLabel.setVisible(true);
            	
            	if (userPick.equals(type)) {
            		guessLabel.setText("You guessed correct");
            	} else if (!userPick.equals(type)) {
            		guessLabel.setText("You guessed wrong");
            	}
            	
            	if(type.equals("rock")) 
            		rockCounterImg.setIcon(counterOn);
            	else if (type.equals("paper")) 
            		paperCounterImg.setIcon(counterOn);
            	else
            		scissorsCounterImg.setIcon(counterOn);
            }
            
            updateCounters();
            
            gamePane.revalidate();
            gamePane.repaint();
    	}
    	speedSlider.repaint();
    }
	public GUI(resetObserver observer) {
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
        frame.setIconImage(frameBack.getImage());
		frame.setSize(1000, 650);
		frame.setBackground(Color.black);
		
		// panel that shows up at the start of the simulation
        startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(panelPosX, panelPosY, frame.getWidth()-panelSizeX, frame.getHeight()-panelSizeY);
        startPanel.setBackground(Color.black);
        
        // ------- START PANEL COMPONENTS --------------------------------------
        // image for start panel
        startPanelImg = new JLabel(new ImageIcon(startPanelBack.getImage().getScaledInstance(startPanel.getWidth(), startPanel.getHeight(), Image.SCALE_SMOOTH)));
        startPanelImg.setBounds(0, 0, startPanel.getWidth(), startPanel.getHeight());
        
		// Number slider for amount of items
		numSlider = new JSlider(1, 50, 25);
		numSlider.setBounds(startPanel.getWidth()/2-100, startPanel.getHeight()/2-60, 200, 80);
		numSlider.setUI(new SliderUI(numSlider));
		numSlider.setFocusable(false);
		numSlider.setOpaque(false);
		// Updates number of items showed to user
        numSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
            	numSlider.repaint();
                numLabel.setText(""+numSlider.getValue());
            }
        });
        
        // Button to start program
        continueBtn = new JButton(continueBtnImg);
        continueBtn.setPressedIcon(continueBtnImg2);
        continueBtn.setBounds(startPanel.getWidth()/2-90, startPanel.getHeight()/2+55, continueBtnImg.getIconWidth(), continueBtnImg.getIconHeight());
        continueBtn.setFont(video.deriveFont(20f));
        continueBtn.setBorderPainted(false);
        
        // label for the number of each object
        numLabel = new JLabel(""+numSlider.getValue());
        numLabel.setBounds(startPanel.getWidth()/2-185, startPanel.getHeight()/2-50, 80, 70);
        numLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        numLabel.setFont(board.deriveFont(24f));
        numLabel.setForeground(Color.white);
        
        // background imgae for num label
        numImg = new JLabel(counter);
        numImg.setBounds(numLabel.getX()+21, numLabel.getY()-3, numLabel.getWidth(), numLabel.getHeight());
        
        // Add RPS pick buttons to the start panel
        JButton rockBtn = new JButton(rockPick);
        rockBtn.setBounds(startPanel.getWidth()/2 - 150, startPanel.getHeight()/2 + 10, 40, 40);
        rockBtn.setContentAreaFilled(false);
        rockBtn.setBorderPainted(false);

        JButton paperBtn = new JButton(paperPick);
        paperBtn.setBounds(startPanel.getWidth()/2 - 20, startPanel.getHeight()/2 + 10, 40, 40);
        paperBtn.setContentAreaFilled(false);
        paperBtn.setBorderPainted(false);

        JButton scissorsBtn = new JButton(scissorsPick);
        scissorsBtn.setBounds(startPanel.getWidth()/2 + 110, startPanel.getHeight()/2 + 10, 40, 40);
        scissorsBtn.setContentAreaFilled(false);
        scissorsBtn.setBorderPainted(false);
        
        // Action Listeners
        rockBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		
            		userPick = "rock";
                System.out.println("User Picked " + userPick);
                // Example: visually indicate selection
                rockBtn.setBorderPainted(true);
                rockBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                paperBtn.setBorderPainted(false);
                scissorsBtn.setBorderPainted(false);
                // TODO: store player's choice in a variable, e.g., playerChoice = "rock";
            }
        });

        paperBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		userPick = "paper";
            		System.out.println("User Picked " + userPick);
                paperBtn.setBorderPainted(true);
                paperBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                rockBtn.setBorderPainted(false);
                scissorsBtn.setBorderPainted(false);
                // TODO: playerChoice = "paper";
            }
        });

        scissorsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            		userPick = "scissors";
            		System.out.println("User Picked " + userPick);
                scissorsBtn.setBorderPainted(true);
                scissorsBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                rockBtn.setBorderPainted(false);
                paperBtn.setBorderPainted(false);
                // TODO: playerChoice = "scissors";
            }
        });
        
        // --------- START PANEL END ------------------------------------------
        
        // panel to put configuration controls on
        controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBackground(new Color(40, 40, 40));
        controlPanel.setBounds(0,0,frame.getWidth()-15, frame.getHeight());
        
        
        // ---------- CONTROL PANEL COMPONENTS ---------------------------------------
        // image for the control panel
        controlPanelImg = new JLabel();
        controlPanelImg.setIcon(new ImageIcon(frameBack.getImage().getScaledInstance(controlPanel.getWidth(), controlPanel.getHeight(), Image.SCALE_SMOOTH)));
        controlPanelImg.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        // slider that controls the tick speed of the simulation
        speedSlider = new JSlider(1, 99, 25);
        speedSlider.setBounds(330, 35, 200, 30);
        speedSlider.setOpaque(false);
        speedSlider.setEnabled(false);
        speedSlider.setUI(new SliderUI(speedSlider));
        
        // label showing the speed of the simulation in ticks per second
        speedLabel = new JLabel(speedLabelImg);
        speedLabel.setBounds(speedSlider.getX()+5, 5, 200, 30);
        speedSlider.setFocusable(false);
        
        
        // creates another loop of moving the objects when changing the tick speed
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
            	speedSlider.repaint();
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
        speedCounter.setFont(board.deriveFont(24f));
        speedCounter.setForeground(Color.white);
        speedCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        speedCounter.setBounds(speedSlider.getX()-60, 20, 50, 40);
        
        // background image for the speed counter
        speedCounterImg = new JLabel();
        speedCounterImg.setBounds(speedCounter.getX()+9, speedCounter.getY()-3, speedCounter.getWidth(), speedCounter.getHeight());
        speedCounterImg.setIcon(counter);
        
        // shows number of rocks
        rockCounter = new JLabel("0");
        rockCounter.setFont(board.deriveFont(24f));
        rockCounter.setForeground(Color.white);
        rockCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        rockCounter.setBounds(5, 140, 60, 40);
        
        // image for rock counter
        rockCounterImg = new JLabel();
        rockCounterImg.setBounds(rockCounter.getX()+4, rockCounter.getY()-35, rockCounter.getWidth(), rockCounter.getHeight()+30);
        rockCounterImg.setIcon(counterOff);
        
        // rock label
        rockLabel = new JLabel(rockLabelImg);
        rockLabel.setBounds(rockCounter.getX()+10, rockCounter.getY()+40, rockLabelImg.getIconWidth(), rockLabelImg.getIconHeight());
        
        // shows number of papers
        paperCounter = new JLabel("0");
        paperCounter.setFont(board.deriveFont(24f));
        paperCounter.setForeground(Color.white);
        paperCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        paperCounter.setBounds(5, 280, 60, 40);
        
        // image for paper counter
        paperCounterImg = new JLabel();
        paperCounterImg.setBounds(paperCounter.getX()+4, paperCounter.getY()-35, paperCounter.getWidth(), paperCounter.getHeight()+30);
        paperCounterImg.setIcon(counterOff);
        
        // paper label
        paperLabel = new JLabel(paperLabelImg);
        paperLabel.setBounds(paperCounter.getX()+5, paperCounter.getY()+40, paperLabelImg.getIconWidth(), paperLabelImg.getIconHeight());
        
        // shows number of scissors
        scissorsCounter = new JLabel("0");
        scissorsCounter.setFont(board.deriveFont(24f));
        scissorsCounter.setForeground(Color.white);
        scissorsCounter.setHorizontalAlignment(SwingConstants.RIGHT);
        scissorsCounter.setBounds(5, 420, 60, 40);
        
        // image for scissors counter
        scissorsCounterImg = new JLabel();
        scissorsCounterImg.setBounds(scissorsCounter.getX()+4, scissorsCounter.getY()-35, scissorsCounter.getWidth(), scissorsCounter.getHeight()+30);
        scissorsCounterImg.setIcon(counterOff);
        
        // scissors label
        scissorsLabel = new JLabel(scissorsLabelImg);
        scissorsLabel.setBounds(scissorsCounter.getX()+4, scissorsCounter.getY()+40, scissorsLabelImg.getIconWidth(), scissorsLabelImg.getIconHeight());
        
        // button to pause and resume the simulation
        pauseBtn = new JButton();
        pauseBtn.setIcon(startButton);
        pauseBtn.setBounds(panelPosX, 8, startButton.getIconWidth(), startButton.getIconHeight());
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
        
        // vent for looks
        ventImg = new JLabel(new ImageIcon(vent.getImage().getScaledInstance(vent.getIconWidth()-20, vent.getIconHeight()-20, Image.SCALE_SMOOTH)));
        ventImg.setBounds(660, -5, vent.getIconWidth(), vent.getIconHeight());
        ventImg.setOpaque(false);
        
        // basically a shuffle button to randomize the current items into equal amounts
        softReset = new JButton(shuffleBtn1);
        softReset.setPressedIcon(shuffleBtn2);
        softReset.setBounds(550, 5, shuffleBtn1.getIconWidth(), shuffleBtn1.getIconHeight());
        softReset.setBorderPainted(false);
        softReset.setFocusable(false);
        softReset.setContentAreaFilled(false);
        softReset.setDisabledIcon(shuffleBtn1);
        softReset.setEnabled(false);
        softReset.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		boolean initial = paused;
        		paused = true;
        		RPS[] newArr = new RPS[items.length];
        		int numR = 0, numP = 0, numS = 0;
        		for(int i = 0; i < items.length; i++) {
        			RPS item = items[(int)Math.round(Math.random()*(items.length-1))];
        			while(Arrays.asList(newArr).contains(item) || item == null) {
        				item = items[(int)Math.round(Math.random()*(items.length-1))];
        			}
        			int typeInt = i / numSlider.getValue();
    				if(typeInt < 1) {
    					item.setType("rock");
    					numR++;
    				} else if (typeInt < 2) {
    					item.setType("paper");
    					numP++;
    				} else {
    					item.setType("scissors");
    					numS++;
    				}
    				newArr[i] = item;
        		}
        		rockNum = numR;
        		scissorsNum = numS;
        		paperNum = numP;
        		
        		updateCounters();
        		
        		guessLabel.setVisible(false);
        		winLabel.setVisible(false);
        		winLabel.setText("");
        		winLabel.setText("");
        		
        		rockCounterImg.setIcon(counterOff);
        		scissorsCounterImg.setIcon(counterOff);
        		paperCounterImg.setIcon(counterOff);
        		
        		try {
					Thread.sleep(5);
					paused = initial;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        });
        
        // reset button to close and re open the GUI class
        resetBtn = new JButton(resetButton);
        resetBtn.setBounds(920, -15, resetButton.getIconWidth(), resetButton.getIconHeight());
        resetBtn.setPressedIcon(resetButton3);
        resetBtn.setRolloverIcon(resetButton2);
        resetBtn.setBorderPainted(false);
        resetBtn.setFocusable(false);
        resetBtn.setContentAreaFilled(false);
        resetBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		observer.onReset();
        		frame.dispose();
        	}
        });
        // ---------- CONTROL PANEL END -------------------------------------------
        
        // panel to put simulate the RPS objects on
        gamePane = new JPanel();
        gamePane.setLayout(null);
        gamePane.setBounds(panelPosX, panelPosY, frame.getWidth()-panelSizeX, frame.getHeight()-panelSizeY);
        gamePane.setBackground(new Color(20,20,20));
        gamePane.setEnabled(false);
        
        // -----------------GAME PANE START------------------------
        //sets settings for guess Label
        guessLabel = new JLabel();
        guessLabel.setBounds(gamePane.getWidth()/2-200, gamePane.getHeight()/2-70, 400, 200);
        guessLabel.setHorizontalAlignment(SwingConstants.CENTER);
        guessLabel.setVerticalAlignment(SwingConstants.CENTER);
        guessLabel.setFont(video.deriveFont(30f));
        guessLabel.setForeground(Color.white);
        guessLabel.setVisible(false);
        
        // sets settings for win label
        winLabel = new JLabel();
        winLabel.setBounds(gamePane.getWidth()/2-200, gamePane.getHeight()/2-100, 400, 200);
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winLabel.setVerticalAlignment(SwingConstants.CENTER);
        winLabel.setFont(video.deriveFont(30f));
        winLabel.setForeground(Color.white);
        winLabel.setVisible(false);
        // ----------------GAME PANE END------------------------
        
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
                softReset.setEnabled(true);
                
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
        startPanel.add(numImg);
        
        startPanel.add(rockBtn);
        startPanel.add(paperBtn);
        startPanel.add(scissorsBtn);
        
        startPanel.add(startPanelImg);
        
        gamePane.add(guessLabel);
        gamePane.add(winLabel);
        
        // add components to controlPanel
        controlPanel.add(speedSlider);
        controlPanel.add(speedLabel);
        controlPanel.add(speedCounter);
        controlPanel.add(speedCounterImg);
        controlPanel.add(resetBtn);
        
        controlPanel.add(rockCounter);
        controlPanel.add(rockCounterImg);
        controlPanel.add(rockLabel);
        
        controlPanel.add(paperCounter);
        controlPanel.add(paperCounterImg);
        controlPanel.add(paperLabel);
        
        controlPanel.add(scissorsCounter);
        controlPanel.add(scissorsCounterImg);
        controlPanel.add(scissorsLabel);
        
        controlPanel.add(softReset);
        
        controlPanel.add(pauseBtn);
        
        controlPanel.add(ventImg);
        
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
