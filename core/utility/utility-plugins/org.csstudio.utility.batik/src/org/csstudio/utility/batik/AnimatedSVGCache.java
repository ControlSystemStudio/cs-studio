/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.commons.lang.time.DateUtils;
import org.csstudio.java.thread.ExecutionService;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * Cache that stores images until the simple time of all {@link TimedElement} of the current {@link TimedDocumentRoot}
 * has been reset.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class AnimatedSVGCache {

    /**
     * Maximum number of images to be handled by the cache. If more, the cache is flushed.
     */
    private final int maxSize;

    /**
     * Interface to be notified of new images when the cache is taking over standard Batik management.
     */
    interface AnimatedSVGCacheListener {
        void newImage(Image newImage);
    }

    private final AnimatedSVGCacheListener listener;

    private List<Entry> entries = Collections.synchronizedList(new LinkedList<Entry>());

    /**
     * Cache main task.
     */
    private ScheduledFuture<?> scheduledMain;

    /**
     * Time root of the current SVG document.
     */
    private final TimedDocumentRoot timedDocumentRoot;

    private Map<TimedElement, TimedElementHandler> timeHandlers = Collections
            .synchronizedMap(new HashMap<TimedElement, AnimatedSVGCache.TimedElementHandler>());

    private boolean disposed = false;
    private boolean filled = false;

    private boolean alignedToNearestSecond = false;
    private boolean running = false;
    private boolean initialized = false;
    private int repeatCount = 0;

    private final Display swtDisplay;

    public AnimatedSVGCache(final Display swtDisplay, final TimedDocumentRoot timedDocumentRoot,
            final AnimatedSVGCacheListener listener, final int maxSize) {
        this.timedDocumentRoot = timedDocumentRoot;
        this.listener = listener;
        this.swtDisplay = swtDisplay;
        this.maxSize = maxSize;
    }

    public void dispose() {
        stopProcessing();
        synchronized (entries) {
            for (Entry entry : entries) {
                entry.dispose();
            }
        }
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Stop Processing & asynchronously flushes all images in the cache.
     */
    public void flush() {
        stopProcessing();
        // Asynchronously dispose all cached images
        final List<Entry> entriesCopy = new ArrayList<Entry>(entries);
        timeHandlers.clear();
        entries.clear();
        filled = false;
        repeatCount = 0;
        initialized = false;
        Runnable flushTask = new Runnable() {
            public void run() {
                for (Entry entry : entriesCopy) {
                    entry.dispose();
                }
                entriesCopy.clear();
                Activator.getLogger().log(Level.FINE, "SVG cache FLUSHED");
            }
        };
        new Thread(flushTask).start();
    }

    public Image addImage(BufferedImage awtImage) {
        if (filled) {
            return null;
        }
        if (entries.size() == maxSize) {
            flush();
        }
        Image image = null;
        long currentWaitTime = Long.MAX_VALUE;
        if (!initialized) { // Initialize
            for (TimedElement te : timedDocumentRoot.getChildren()) {
                timeHandlers.put(te, new TimedElementHandler(te));
            }
            entries.clear();
            filled = false;
            repeatCount = 0;
            initialized = true;
        } else {
            // Compare time of each element with previous call
            for (TimedElement te : timedDocumentRoot.getChildren()) {
                if (timeHandlers.get(te) != null && timeHandlers.get(te).update(te)
                        && timeHandlers.get(te).getWaitTime() < currentWaitTime) {
                    // Take the minimum
                    currentWaitTime = timeHandlers.get(te).getWaitTime();
                }
            }
            // Check if all images have been cached
            filled = hasRepeated();
            // Avoid first repeat
            if (filled && repeatCount == 0) {
                repeatCount++;
                // Reset time counters
                timeHandlers.clear();
                for (TimedElement te : timedDocumentRoot.getChildren()) {
                    timeHandlers.put(te, new TimedElementHandler(te));
                }
                filled = false;
            }
        }
        if (filled) { // End of the loop, back to first image
            entries.get(0).setWaitTime(currentWaitTime);
            return entries.get(0).getImage();
        }
        ImageData imageData = SVGUtils.toSWT(swtDisplay, awtImage);
        image = new Image(swtDisplay, imageData);
        // Avoid first repeat
        if (repeatCount == 0) {
            return image;
        }
        entries.add(new Entry(currentWaitTime, image));
        return image;
    }

    // Return true if all animations have been repeated at least once
    private boolean hasRepeated() {
        if (!initialized) {
            return false; // case of flush
        }
        for (TimedElement te : timedDocumentRoot.getChildren()) {
            if (timeHandlers.get(te) != null && !timeHandlers.get(te).hasRepeated()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        return entries.size() == maxSize;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFilled() {
        return filled;
    }

    public int getSize() {
        return entries.size();
    }

    public void setAlignedToNearestSecond(boolean alignedToNearestSecond) {
        this.alignedToNearestSecond = alignedToNearestSecond;
    }

    /**
     * Notify images in the same order & time interval as received.
     */
    public void startProcessing() {
        if (entries.size() == 0) {
            return;
        }
        Runnable animationTask = new Runnable() {
            private long lastUpdateTime = 0;
            private int currentIndex = 0;

            public void run() {
                long currentTime = System.currentTimeMillis();
                Entry currentEntry = entries.get(currentIndex);
                // use Math.abs() to ensure that the system
                // time adjust won't cause problem
                if (Math.abs(currentTime - lastUpdateTime) >= currentEntry.getWaitTime()) {
                    if (listener != null) {
                        listener.newImage(currentEntry.getImage());
                    }
                    currentIndex++;
                    if (currentIndex >= entries.size()) {
                        currentIndex = 0;
                    }
                    lastUpdateTime = currentTime;
                }
            }
        };
        if (scheduledMain != null) {
            scheduledMain.cancel(true);
            scheduledMain = null;
        }
        running = false;
        long initialDelay = 100;
        if (alignedToNearestSecond) {
            Date now = new Date();
            Date nearestSecond = DateUtils.round(now, Calendar.SECOND);
            initialDelay = nearestSecond.getTime() - now.getTime();
            if (initialDelay < 0) {
                initialDelay += 1000; // number of milliseconds in 1 seconds
            }
        }
        scheduledMain = ExecutionService.getInstance().getScheduledExecutorService()
                .scheduleAtFixedRate(animationTask, initialDelay, 10, TimeUnit.MILLISECONDS);
        running = true;
    }

    public void stopProcessing() {
        if (scheduledMain != null) {
            scheduledMain.cancel(true);
            scheduledMain = null;
        }
        running = false;
    }

    public void restartProcessing() {
        stopProcessing();
        startProcessing();
    }

    /**
     * Cache entry with an {@link Image} and a delay to wait before displaying it.
     */
    private class Entry {

        private final Image image;
        private long waitTime;

        public Entry(long waitTime, Image image) {
            this.waitTime = waitTime;
            this.image = image;
        }

        public float getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(long waitTime) {
            this.waitTime = waitTime;
        }

        public Image getImage() {
            return image;
        }

        public void dispose() {
            if (image != null && !image.isDisposed()) {
                image.dispose();
            }
        }

        @Override
        public String toString() {
            return "Entry [waitTime=" + waitTime + "]";
        }

    }

    /**
     * Handles simple time of a {@link TimedElement}.
     */
    private class TimedElementHandler {

        private float previousTime;
        private float lastTime;
        private float simpleDur;
        private boolean repeated = false;

        private float currentWaitTime = 0f;
        private float waitTimeSum = 0f;

        public TimedElementHandler(TimedElement te) {
            this.simpleDur = te.getSimpleDur();
            this.previousTime = te.getSimpleTime();
            this.lastTime = te.getSimpleTime();
        }

        public boolean update(TimedElement te) {
            float newTime = te.getSimpleTime();
            if (repeated || newTime == lastTime) {
                return false; // not updated
            }
            previousTime = lastTime;
            lastTime = newTime;
            currentWaitTime = lastTime - previousTime;
            if (currentWaitTime < 0) {
                currentWaitTime += simpleDur;
            }
            waitTimeSum += currentWaitTime;
            if (lastTime < previousTime) {
                // if simple time is reset, the element loop has ended
                repeated = true;
            }
            return true;
        }

        public boolean hasRepeated() {
            return repeated;
        }

        public long getWaitTime() {
            return (long) (currentWaitTime * 1000);
        }

        @Override
        public String toString() {
            return "TimedElementHandler [previousTime=" + previousTime + ", lastTime=" + lastTime + ", simpleDur="
                    + simpleDur + ", repeated=" + repeated + ", currentWaitTime=" + currentWaitTime + ", waitTimeSum="
                    + waitTimeSum + "]";
        }

    }
}
