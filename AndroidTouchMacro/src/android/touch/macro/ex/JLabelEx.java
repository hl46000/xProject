package android.touch.macro.ex;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;

public class JLabelEx extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6451436367224789442L;
	Image img = null;
	int x, y;

	public void setImage( Image img, int x, int y ) {
		this.img = img;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void paint(Graphics g) { 
		super.paint(g);
		
		if( img != null ) {
			g.drawImage(img, x, y, null);
		}
	}

	@Override
	public void update(Graphics g) {
		super.update(g);
		
		if( img != null ) {
			g.drawImage(img, x, y, null);
		}
	}

}
