package repo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSliderUI;

public class SliderUI extends BasicSliderUI {
	public class tO implements ImageObserver {
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			// TODO Auto-generated method stub
			if((infoflags & ImageObserver.ALLBITS) != 0) {
				return false;
			} else {
				return true;
			}
		}
		
	}
	
	
	public SliderUI(JSlider b) {
		super(b);
	}
	public void paintThumb(Graphics g) {
		File f = new File(getClass().getResource("/imgs/knob.png").getPath());
		Image i;
		try {
			i = ImageIO.read(f).getScaledInstance(thumbRect.width*2, thumbRect.height*2, Image.SCALE_SMOOTH);
			g.drawImage(i, thumbRect.x-(thumbRect.width/2), thumbRect.y-12, null);
		} catch (IOException e) {
			System.out.print("Error loading image: ");
			e.printStackTrace();
		}
	}
	
	public void paintTrack(Graphics g) {
		File f = new File(getClass().getResource("/imgs/track.png").getPath());
		Image i;
		
		try {
			i = ImageIO.read(f).getScaledInstance(trackRect.width, trackRect.height, Image.SCALE_SMOOTH);
			g.drawImage(i, trackRect.x, trackRect.y, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		g.setColor(this.trackColor);
//		g.drawLine(trackRect.x, trackRect.y+2+(trackRect.height/2), trackRect.x+(trackRect.width), trackRect.y+2+(trackRect.height/2));
//		g.drawLine(trackRect.x, trackRect.y+1+(trackRect.height/2), trackRect.x+(trackRect.width), trackRect.y+1+(trackRect.height/2));
			
		
	}
	
}
