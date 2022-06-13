package org.unibl.etf.models.timer;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.concurrent.atomic.AtomicBoolean;

public class Timer extends Thread {
    private static final int WAIT_TIME = 1_000;
    private static final int MINUTE = 60;

    private static int elapsedTimeSeconds = 0;
    private static int elapsedTimeMinutes = 0;

    private volatile static AtomicBoolean pause;
    private volatile static Label timerLbl;

    public Timer(AtomicBoolean pauseFlag, Label timerLbl){
        pause = pauseFlag;
        Timer.timerLbl = timerLbl;
        this.setDaemon(true);
    }

    @Override
    public void run(){
        while(true) {
            if (!pause.get()) {
                ++elapsedTimeSeconds;
                elapsedTimeMinutes = elapsedTimeSeconds / MINUTE;
                if(elapsedTimeMinutes > 0)
                    Platform.runLater(() -> timerLbl.setText("Vrijeme trajanja igre: " + elapsedTimeMinutes + " min " +
                            elapsedTimeSeconds % MINUTE+ " s"));
                else
                    Platform.runLater(() -> timerLbl.setText("Vrijeme trajanja igre: " + elapsedTimeSeconds + " s"));
            }
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();//todo logger
            }
            System.out.println(elapsedTimeSeconds);
        }
    }

}
