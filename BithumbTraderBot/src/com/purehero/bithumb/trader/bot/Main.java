package com.purehero.bithumb.trader.bot;
import java.util.Scanner;

import com.purehero.bithumb.util.Api_Client;


//public class Main extends javafx.application.Application {
public class Main {
	private static Main mainFrame = null;
	
	private final String apk_key 	= "64346e7c6622346faa0cff0ac46c73a3"; 
	private final String secure_key = "79639ec221ba0c958eef5d66a7f8fdaa";
	private Api_Client api = new Api_Client( apk_key, secure_key );
	
	private TraderBotThread traderBotThread = null;
	
	public static void main(String args[]) {
		mainFrame = new Main();
		// launch(args);
		mainFrame.doRun();
    }

	private void doRun() {
		traderBotThread = new TraderBotThread( api );
		traderBotThread.startThread();
		
		Scanner console = new Scanner(System.in);
		while( !console.hasNext()) {
			try { Thread.sleep( 100 ); } catch (InterruptedException e) { e.printStackTrace();}
		}
		console.close();
		
		traderBotThread.stopThread();		
	}
}

