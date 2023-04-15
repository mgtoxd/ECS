package uk.ac.soton.comp1206.util;

import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimer {
    private Timer timer = new Timer();
    private long remainingMillis;
    private final CountDownTimerCallback callback;
    private final float reductionFactor;

    public CountDownTimer(long durationMillis, CountDownTimerCallback callback, float reductionFactor) {
        this.remainingMillis = durationMillis;
        this.callback = callback;
        this.reductionFactor = reductionFactor;
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                remainingMillis -= 100;
                if (remainingMillis >= 0) {
                    callback.onTick(remainingMillis);
                } else {
                    stop();
                    callback.onFinish();
                }
            }
        }, 0, 100);
    }

    public void stop() {
        timer.cancel();
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    public interface CountDownTimerCallback {
        void onTick(long remainingMillis);
        void onFinish();
    }
}