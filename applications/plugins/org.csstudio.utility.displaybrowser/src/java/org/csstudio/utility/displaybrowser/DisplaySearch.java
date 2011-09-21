/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.utility.displaybrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.persistence.IDisplayModelLoadListener;
import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 13.09.2011
 */
public class DisplaySearch extends Observable {

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 14.09.2011
     */
    private final class newResultNotifyer implements Runnable {
        @Override
        public void run() {
            setChanged();
            notifyObservers(_resulte);
        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 14.09.2011
     */
    private final class SearchOneFileJob extends Job {
        private final File _file;
        private final Integer _fileCount;

        /**
         * Constructor.
         * @param name
         * @param file
         * @param fileCount
         */
        private SearchOneFileJob(final String name, final File file, final Integer fileCount) {
            super(name);
            _file = file;
            _fileCount = fileCount;
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            monitor.beginTask("Search in "+_file.getName(), 8);
            final Map<IProcessVariableAddress, File> result = searchInDisplay(_file, _regEx, monitor);
            monitor.worked(6);
            _resulte.putAll(result);
            monitor.worked(7);
            Display.getDefault().asyncExec(new fileCountNotifyer(_fileCount));
            Display.getDefault().asyncExec(new newResultNotifyer());
            monitor.worked(8);
            finished();
            monitor.done();
            _workingJobs.remove(this);
            return Status.OK_STATUS;
        }

        private Map<IProcessVariableAddress, File> searchInDisplay(@Nonnull final File file,
                                                                   @Nonnull final String regEx, final IProgressMonitor monitor) {
           InputStream fileInputStream;

           final Map<IProcessVariableAddress, File> result = new HashMap<IProcessVariableAddress, File>();
           try {
               fileInputStream = new FileInputStream (file);
               final DisplayModel displayModel = new DisplayModel(false);
               monitor.worked(1);
               PersistenceUtil.syncFillModel(displayModel, fileInputStream);
               monitor.worked(2);
               final Set<IProcessVariableAddress> allPvAdressesSet = new HashSet<IProcessVariableAddress>();
               allPvAdressesSet.addAll(displayModel.getAllPvAdresses());
               final List<AbstractWidgetModel> widgets = displayModel.getWidgets();
               monitor.worked(3);
               for (final AbstractWidgetModel widget : widgets) {
                   allPvAdressesSet.addAll(widget.getAllPvAdresses());
               }
               monitor.worked(4);
               for (final IProcessVariableAddress iProcessVariableAddress : allPvAdressesSet) {
                   if (iProcessVariableAddress.getProperty().matches(regEx)) {
                       result.put(iProcessVariableAddress, file);
                   }
               }
               monitor.worked(5);
           } catch (final FileNotFoundException e) {
               e.printStackTrace();
           }

           return result;
       }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 14.09.2011
     */
    private final class fileCountNotifyer implements Runnable {
        private final Integer _fileCount;

        /**
         * Constructor.
         * @param fileCount
         */
        public fileCountNotifyer(final Integer fileCount) {
            _fileCount = fileCount;
        }

        @Override
        public void run() {
            setChanged();
            notifyObservers(_fileCount);
        }
    }

    private boolean _displayPropertiesLoaded;
    private final Set<File> _files;
    private final IDisplayModelLoadListener _loadListener = new IDisplayModelLoadListener() {

        @Override
        public void onDisplayModelLoaded() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDisplayPropertiesLoaded() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onErrorsOccured(final List<String> errors) {
            // TODO Auto-generated method stub

        }
    };
    private boolean _modelLoaded;
    private final String _regEx;
    private final Map<IProcessVariableAddress, File> _resulte = Collections.synchronizedMap(new HashMap<IProcessVariableAddress, File>());
    private final AtomicInteger _jobCounter = new AtomicInteger(0);
    private List<Job> _workingJobs;
    private AtomicBoolean _chanceled;

    /**
     * Constructor.
     */
    public DisplaySearch(@Nonnull final Set<File> files, @Nonnull final String regEx) {
        _files = files;
        _regEx = regEx;

    }

    public void startSearch() {
        _chanceled = new AtomicBoolean(false);
        _workingJobs = Collections.synchronizedList(new ArrayList<Job>());
         final Job job = new Job("SDS Display Search") {

             /*TODO: wenn die Job zahl über eins gesetzt wird kommt es im StripChartPlotPostProcessor
                     zu einem AssertionError
              */
            private final static int _MAX_RUNNIG_JOBS = 1;

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                monitor.beginTask("Search SDS Display", _files.size());
                Integer fileCount = 0;
                for (final File file : _files) {
                    fileCount++;
                    _jobCounter.incrementAndGet();
                    while (_jobCounter.get()>_MAX_RUNNIG_JOBS) {
                        if(_chanceled.get()) {
                            return Status.CANCEL_STATUS;
                        }
                        getThread().yield();
                    }
                    if(_chanceled.get()) {
                        return Status.CANCEL_STATUS;
                    }
                    final SearchOneFileJob searchOneFileJob = new SearchOneFileJob("SDS Display Search file", file, fileCount);
                    searchOneFileJob.schedule();
                    _workingJobs.add(searchOneFileJob);
                }
                monitor.done();
                _workingJobs.remove(this);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        _workingJobs.add(job);
    }

    /**
     *
     */
    private void finished() {
        final int decrementAndGet = _jobCounter.decrementAndGet();
        if(decrementAndGet<=0) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    setChanged();
                    notifyObservers(Boolean.TRUE);
                }
            });

        }

    }

    /**
     *
     */
    public void stopSearch() {
        _chanceled.set(true);
        for (final Job job : _workingJobs) {
            job.cancel();
        }
    }


}
