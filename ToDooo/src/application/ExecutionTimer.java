package application;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class ExecutionTimer {
	//@author A0112498B
	public static class StatusCheckTimerTask extends TimerTask {
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
	
	public static class SystemMsgTimerTask extends TimerTask {	
		private Timer _timer;
		
		public SystemMsgTimerTask() {
			_timer = new Timer();
		}
		
		public Timer getTimer() {
			return _timer;
		}
		 	
	    public void run() {
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	Execution.executeClearSystemMsg();
			    }
			});
			
			_timer.cancel();
	    }
	}
	
	//@author A0112537M
	public static class SystemMsgTimerSavePath extends TimerTask {
		private Timer _timer;
		
		public SystemMsgTimerSavePath() {
			_timer = new Timer();
		}
		
		public Timer getTimer() {
			return _timer;
		}
		
        public void run() {
    		Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	Execution.executeClearSavePathMsg();
    		    }
    		});
    		
            _timer.cancel();
        }
    }
	
	public static class SystemMsgTimerCleanSetting extends TimerTask {
		private Timer _timer;
		
		public SystemMsgTimerCleanSetting() {
			_timer = new Timer();
		}
		
		public Timer getTimer() {
			return _timer;
		}
		
        public void run() {
    		Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	Execution.executeClearCleanSettingMsg();	
    		    }
    		});
    		
    		_timer.cancel();
        }
	}
}
