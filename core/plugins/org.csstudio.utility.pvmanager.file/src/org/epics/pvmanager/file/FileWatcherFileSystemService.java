/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.util.time.TimeDuration;

/**
 *
 * @author carcassi
 */
public class FileWatcherFileSystemService implements FileWatcherService {
    
    private static final Logger log = Logger.getLogger(FileWatcherService.class.getName());
    
    private final ScheduledExecutorService exec;
    private final Runnable scanTask = new Runnable() {

        @Override
        public void run() {
            scan();
        }
    };
    private final List<Registration> registrations = new CopyOnWriteArrayList<>();

    public FileWatcherFileSystemService(ScheduledExecutorService exec, TimeDuration scanRate) {
        this.exec = exec;
        exec.scheduleWithFixedDelay(scanTask, 0, scanRate.toNanosLong(), TimeUnit.NANOSECONDS);
    }

    @Override
    public void addWatcher(File file, Runnable callback) {
        try {
            registrations.add(new Registration(file, callback));
        } catch (IOException ex) {
            log.log(Level.WARNING, "Notifications won't be enable for file " + file, ex);
        }
    }

    @Override
    public void removeWatcher(File file, Runnable callback) {
        Registration toClose = null;
        for (Registration registration : registrations) {
            if (registration.file.equals(file) && registration.callback.equals(callback)) {
                toClose = registration;
            }
        }
        
        if (toClose != null) {
            try {
                toClose.close();
            } catch (IOException ex) {
                log.log(Level.WARNING, "Exception while closing notifications for file " + file, ex);
            }
            registrations.remove(toClose);
        }
    }
    
    private void scan() {
        for (Registration registration : registrations) {
            registration.notifyChanges();
        }
    }
    
    private class Registration {
        final File file;
        final Runnable callback;
        final Path path;
        final WatchService watchService;

        Registration(File file, Runnable callback) throws IOException {
            this.file = file;
            this.callback = callback;
            this.path = file.toPath();
            this.watchService = path.getFileSystem().newWatchService();
            this.path.getParent().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        }
        
        void close() throws IOException {
            watchService.close();
        }
        
        void notifyChanges() {
            WatchKey key = watchService.poll();
            if (key != null) {
                boolean changed = false;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    Path path = (Path) watchEvent.context();
                    if (path != null && path.getFileName().equals(file.getName())) {
                        changed = true;
                    }
                }
                key.reset();
                try {
                    callback.run();
                } catch(RuntimeException ex) {
                    // Protecting from callback errors
                    log.log(Level.WARNING, "Exception on the file watcher callback", ex);
                }
            }
        }
        
    }
    
}
