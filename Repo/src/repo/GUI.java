package repo;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GUI {
	JFrame frame;
    JLabel numLabel, speedLabel;
	JPanel gamePane, configPane, controlPanel;
	JSlider numSlider, speedSlider;
    JButton continueBtn, pauseBtn;
    RPS[] items;
    int tps;
    boolean paused;
    Timer timer;

    String userPick = null; // User's Choice (rock, paper, scissors)

    // Checking that point (x, y) is inside the game area
	public boolean isValid(int x, int y) {
		return ((x > 0 && x < gamePane.getWidth()-RPS.RADIUS) && (y > 0 && y < gamePane.getHeight()-RPS.RADIUS));
	}

    // checking if a circular area at a specified point intersects an area of an item, returns the intersected item if so
    public RPS itemAt(int x, int y, RPS self){
        if(isValid(x,y)){
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

    public void move(RPS target){
    	tps = 30;
        int[] dir = target.getDir();
        int[] pos = target.getPos();
        int[] targetPos = new int[] {pos[0]+(dir[0]*10), pos[1]+(dir[1]*10)};
        
        if(!isValid(targetPos[0], targetPos[1])) {
        	target.setDir(new int[] {-dir[0], -dir[1]});
        }
        
        RPS i = itemAt(targetPos[0], targetPos[1], target);
        if(i != null){ 
            if(target.getType().equals("rock")){
                if(i.getType().equals("paper")){
                    target.setType("paper");
                } else if(i.getType().equals("scissors")){
                    i.setType("rock");
                } 
            } else if(target.getType().equals("paper")){
                if(i.getType().equals("scissors")){
                    target.setType("scissors");
                } else if (i.getType().equals("rock")){
                    i.setType("paper");
                }
            } else if(target.getType().equals("scissors")){
                if(i.getType().equals("rock")){
                    target.setType("rock");
                } else if (i.getType().equals("paper")){
                    i.setType("scissors");
                }
            }
            
            target.setLocation(targetPos[0], targetPos[1]);
            target.setPos(targetPos);
            target.setDir(new int[] {-dir[0], -dir[1]});
        } else {
            target.setLocation(targetPos[0], targetPos[1]);
            target.setPos(targetPos);
        }
    }

	public GUI() {
		frame = new JFrame("Rock Paper Scissors Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize((int) Math.round(650*1.618), 650);
		
		numSlider = new JSlider();
		numSlider.setBounds((int) Math.round((650*1.618/2)-100), (int) Math.round((650/2)-40), 200, 80);
        numSlider.setMaximum(50);
        
        continueBtn = new JButton("Continue to simulation");
        numLabel = new JLabel("Number of each item: " + numSlider.getValue());
        numLabel.setBounds((int) Math.round((650*1.618/2)-80), (int) Math.round((650/2)-85), 160, 70);
        
        numSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                numLabel.setText("Number of each item: " + numSlider.getValue());
            }
        });
        
        continueBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
            	// Prompting the user what they want to pick
            	String[] options = {"rock", "paper", "scissors"}; // Making an array of options
            	userPick = (String) JOptionPane.showInputDialog(frame,"Which one do you pick?","Your Guess",JOptionPane.QUESTION_MESSAGE,null,options,options[0]); //Prompting the user in a new window
            	if(userPick == null) {
            		return;
            	}

            	JFrame gameFrame = new JFrame("RPS Simulator");
                gameFrame.setSize((int) Math.round(650*1.618)+100, 750);
                gamePane = new JPanel();
                gamePane.setLayout(null);
                gamePane.setBounds(50, 50, (int) Math.round(650*1.618), 650);
                controlPanel = new JPanel();
                controlPanel.setLayout(null);
                controlPanel.setBackground(new Color(230, 230, 230));
                controlPanel.setBounds(0,0,frame.getWidth(), 50);

                pauseBtn = new JButton("Pause Simulation");
                pauseBtn.setBounds((int) Math.round(controlPanel.getWidth()*0.15)-75, (controlPanel.getHeight()/2)-15, 150, 30);
                pauseBtn.addActionListener(new ActionListener() {
                	@Override
                	public void actionPerformed(ActionEvent e) {
                		paused = !paused;
                		pauseBtn.setText((paused) ? "Unpause Simulation" : "Pause Simulation");
                	}
                });

                speedSlider = new JSlider();
                speedSlider.setValue(30);
                speedSlider.setBounds((int) Math.round(controlPanel.getWidth()*0.75)-100, 10, 200, 30);
                speedSlider.setMinimum(1);
                speedSlider.setBackground(new Color(230, 230, 230));

                speedLabel = new JLabel("Ticks per second: 30");
                speedLabel.setBounds((int) Math.round(controlPanel.getWidth()*0.55)-100, 10, 200, 30);

                controlPanel.add(speedSlider);
                controlPanel.add(pauseBtn);
                controlPanel.add(speedLabel);
                gameFrame.add(gamePane);
                gameFrame.add(controlPanel);
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                
                int numEach = numSlider.getValue();
                items = new RPS[numEach*3];
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

                frame.setVisible(false);
                gameFrame.setVisible(true);

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
	                            allSameType = (item.getType().equals(type)) && allSameType;
	                        }
	                        if(allSameType) {
	                        	paused = true;
	                        	speedLabel.setText(type + " Wins!");
	                        	// Comparing userPick and actual winner
	                        	if(type.equals(userPick)) {
	                        		JOptionPane.showMessageDialog(gameFrame, "You guessed it correct!");
	                        	} else {
	                        		JOptionPane.showMessageDialog(gameFrame, "You guessed it wrong! Winner: " + type);
	                        	}
	                        }
	                        gamePane.revalidate();
	                        gamePane.repaint();
                    	}
                    }
                }, 0, 1000/30);
            }
        });

        continueBtn.setBounds((int) Math.round((650*1.618/2)-90), (int) Math.round((650/2)+45), 180, 35);
        
        configPane = new JPanel();
		configPane.setLayout(null);
		configPane.add(numSlider);
        configPane.add(numLabel);
        configPane.add(continueBtn);
        
		frame.setContentPane(configPane);
		frame.setVisible(true);
	}
}
