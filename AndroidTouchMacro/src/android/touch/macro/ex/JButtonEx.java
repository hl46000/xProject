package android.touch.macro.ex;

import javax.swing.JButton;

public class JButtonEx extends JButton {
	private static final long serialVersionUID = 3788073060688317423L;
	public int objectID;
	
	public JButtonEx(String name) { 
		super(name);
	}
	
	public JButtonEx(String name, int id ) { 
		super(name); 
		objectID = id;
	}
}
