package android.touch.macro.ex;

import javax.swing.JMenuItem;

public class JMenuItemEx extends JMenuItem {
	private static final long serialVersionUID = 4151320743231590921L;
	public int objectID;
	
	public JMenuItemEx(String text) { 
		super(text); 
	}
	
	public JMenuItemEx(String text, int id) { 
		super(text); 
		objectID = id;
	}
}
