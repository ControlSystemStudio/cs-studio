package org.csstudio.ui.util.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.csstudio.rap.core.DisplayManager;
import org.eclipse.swt.widgets.Display;



/**
 *
 * A singleton back thread which will help to execute tasks in UI thread.
 * This way we avoid slow downs, that occur on several
 * operating systems, when Display.asyncExec() is called very often from
 * background threads.
 *
 * This thread sleeps for a time, which is below the processing capacity of
 * human eyes and brain - so the user will not feel any delay.
 *
 * @author Sven Wende, Xihui Chen
 *
 */
public final class UIBundlingThread implements Runnable {
    /**
     * The singleton instance.
     */
    private static UIBundlingThread instance;

    /**
     * A queue, which contains runnables that process the events that occured
     * during the last SLEEP_TIME milliseconds.
     */
    private Queue<DisplayRunnable> tasksQueue;


    /**
     * Standard constructor.
     */
    private UIBundlingThread() {
        tasksQueue = new ConcurrentLinkedQueue<DisplayRunnable>();

        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(this, 100, 20, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance
     */
    public static synchronized UIBundlingThread getInstance() {
        if (instance == null) {
            instance = new UIBundlingThread();
        }
        return instance;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        if(!tasksQueue.isEmpty())
            processQueue();
    }

    /**
     * Process the complete queue.
     */
    private void processQueue() {
        Object[] taskArray;
        synchronized (this) {
            taskArray = tasksQueue.toArray();
            tasksQueue.clear();
        }
        DisplayRunnable r;
        for(Object o: taskArray){
            try {
                r=(DisplayRunnable)o;
                if(!r.display.isDisposed() &&
                        DisplayManager.getInstance().isDisplayAlive(r.display))
                    r.display.asyncExec(r.runnable);
            } catch (Exception e) {
            }
        }

    }

    /**
     * Adds the specified runnable to the queue. It must be called in UI thread.
     *
     * @param runnable
     *            the runnable
     */
    public synchronized void addRunnable(final Runnable runnable) {
        Display display = Display.getCurrent();
        if(display == null)
            throw new RuntimeException("This method must be called in UI thread!");
        tasksQueue.add(new DisplayRunnable(runnable, display));
    }


    /**
     * Adds the specified runnable to the queue.
     *
     * @param display the display to run the runnable.
     * @param runnable
     *            the runnable
     */
    public synchronized void addRunnable(final Display display, final Runnable runnable) {
        tasksQueue.add(new DisplayRunnable(runnable, display));
    }


    class DisplayRunnable {
        private Runnable runnable;
        private Display display;
        public DisplayRunnable(Runnable runnable, Display display) {
            this.runnable = runnable;
            this.display = display;
        }

    }

}
