package application;

import java.util.ArrayList;

public class ShutdownHook {
	/*
	 * Code is executed when the user exits
	 * the program by typing exit
	 */
	public void attachShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Main.storage.updateAndSaveFile();
			}
		});
	}
}