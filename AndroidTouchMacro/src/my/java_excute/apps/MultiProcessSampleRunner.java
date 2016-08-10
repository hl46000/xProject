package my.java_excute.apps;

import java.lang.management.ManagementFactory;

public class MultiProcessSampleRunner {
	public static void main(String[] args) {
		
		String name = ManagementFactory.getRuntimeMXBean().getName(); 
		String pidNumber = name.substring(0, name.indexOf("@"));
		
		if( args != null ) {
			for( String a : args ) {
				System.out.println( String.format( "PID[%s] : %s", pidNumber, a ));
			}
		}
	}
}
