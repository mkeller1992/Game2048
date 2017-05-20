package ch.bfh.game2048.view;

import java.util.Observable;

/**
 * Timer:
 * 
 * Counts the time elapsed in the game (in milliseconds)
 * and notifies the Observer-Classes, so that they can display the updated time
 * 
 */

public class Timer extends Observable implements Runnable {

	long millisElapsed;
	Thread timerThread;

	long lastMillis;

	public Timer() {

	}

	public long getMillisElapsed() {
		return millisElapsed;
	}

	public void start() {
		lastMillis = System.currentTimeMillis();
		timerThread = new Thread(this);
		timerThread.setDaemon(true);
		timerThread.start();
	}

	public void stop() {
		timerThread.interrupt();
	}

	public void reset() {
		stop();
		this.millisElapsed = 0;
		lastMillis = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
				increaseMillis();
			} catch (InterruptedException e) {
				increaseMillis();
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * long diff: computes the time elapsed since the start of the execution of run()
	 * the current time is stored in lastMillis before another round of run() is being executed
	 * Observers are getting notified about the change of time
	 * 
	 */

	private void increaseMillis() {
		long diff = System.currentTimeMillis() - lastMillis;
		millisElapsed += diff;
		lastMillis = System.currentTimeMillis();
		this.setChanged();
		this.notifyObservers();
	}
}
