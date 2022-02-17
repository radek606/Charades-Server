/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ick.kalambury.util;

public abstract class CountDownTimer {

    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private long mStopTimeInFuture;

    private TimerThread timer;

    private boolean mCancelled = false;

    public CountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        timer = new TimerThread();
    }

    public synchronized final void cancel() {
        mCancelled = true;
        timer = null;
    }

    public synchronized final CountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = System.currentTimeMillis() + mMillisInFuture;
        timer.start();
        return this;
    }

    public abstract void onTick(long millisUntilFinished);
    public abstract void onFinish();

    private class TimerThread extends Thread {

        TimerThread() {
            setName("TimerThread");
        }

        @Override
        public void run() {
            while (!mCancelled) {
                final long millisLeft = mStopTimeInFuture - System.currentTimeMillis();
                try {
                    if (millisLeft <= 0) {
                        synchronized (CountDownTimer.this) {
                            mCancelled = true;
                            onFinish();
                        }
                    } else {
                        long lastTickStart = System.currentTimeMillis();
                        synchronized (CountDownTimer.this) {
                            onTick(millisLeft);
                        }

                        // take into account user's onTick taking time to execute
                        long lastTickDuration = System.currentTimeMillis() - lastTickStart;
                        long delay;

                        if (millisLeft < mCountdownInterval) {
                            // just delay until done
                            delay = millisLeft - lastTickDuration;

                            // special case: user's onTick took more than interval to
                            // complete, trigger onFinish without delay
                            if (delay < 0) delay = 0;
                        } else {
                            delay = mCountdownInterval - lastTickDuration;

                            // special case: user's onTick took more than interval to
                            // complete, skip to next interval
                            while (delay < 0) delay += mCountdownInterval;
                        }

                        sleep(delay);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

