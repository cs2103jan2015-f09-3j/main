//@author A0112498B
package application;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;

public class StatusCheckTimerTask extends TimerTask {
	private Timer _timer;
	
	public StatusCheckTimerTask() {
		_timer = new Timer();
	}
	
	public Timer getTimer() {
		return _timer;
	}
	 	
    public void run() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
    			Execution.executeUpdateStatus();    	
		    }
		});
    }
}
