package android.touch.macro.util;

public class Log {
	/**
	 * 
	 * @param format
	 * @param args
	 */
	public static void d( String format, Object...args ) {
		log_print( "[DEBUG]", format, args); 
	}
	
	/**
	 * @param format
	 * @param args
	 */
	public static void i( String format, Object...args ) {
		log_print( "[INFO]", format, args);
	}
	
	/**
	 * @param type
	 * @param format
	 * @param args
	 */
	private static void log_print( String type, String format, Object...args ) {
		String logData = String.format( type + " " + format, args );
		System.out.println( logData );
	}
}
