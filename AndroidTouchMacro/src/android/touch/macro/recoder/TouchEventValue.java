package android.touch.macro.recoder;

public class TouchEventValue {
	public int type;
	public int id;
	public int value;
	
	public TouchEventValue( int type, int id, int value ) {
		this.type  = type;
		this.id    = id;
		this.value = value;
	}

	public TouchEventValue() {
	}

	/**
	 * @param line
	 */
	public void parser(String line) {
		String [] tokens = line.split(" ");
		
		type  = Integer.valueOf( tokens[1], 16 );
		id 	 = Integer.valueOf( tokens[2], 16 );
		try {
			value = tokens[3].startsWith("f") ? -1 : Integer.valueOf( tokens[3], 16 );
		} catch( Exception e ) {
			value = -1;
		}		
	}
}
