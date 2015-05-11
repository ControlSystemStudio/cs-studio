/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.components.ui.internal.editparts;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.csstudio.sds.components.model.TimerModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableTimerFigure;
import org.csstudio.sds.internal.model.logic.ScriptEngine;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IScript;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.scripting.RunnableScript;
import org.csstudio.sds.util.ExecutionService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EditPart controller for the Rectangle widget. The controller mediates between
 * {@link TimerModel} and {@link RefreshableTimerFigure}.
 *
 * @author Kai Meyer & Sven Wende
 *
 */
public final class TimerEditPart extends AbstractWidgetEditPart {

    private static final Logger LOG = LoggerFactory.getLogger(TimerEditPart.class);

    private long _lastExecution;

    @SuppressWarnings("unchecked")
    private ScheduledFuture _scheduledFuture1;
    @SuppressWarnings("unchecked")
    private ScheduledFuture _scheduledFuture2;

    /**
     * The used {@link ScriptEngine}.
     */
    private ScriptEngine _scriptEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();
        startScriptExecution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        cancelScriptExecution();
        super.deactivate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        TimerModel timerModel = getTimerModel();

        RefreshableTimerFigure timerFigure = new RefreshableTimerFigure();
        timerFigure.setVisible(timerModel.isVisible());

        return timerFigure;
    }

    /**
     * Return the associated {@link TimerModel}.
     *
     * @return The TimerModel
     */
    private TimerModel getTimerModel() {
        return (TimerModel) this.getCastedModel();
    }

    /**
     * Configures the internal timer.
     */
    private void startScriptExecution() {
        final TimerModel model = this.getTimerModel();

        if (model.isAccesible() && model.getDelay() > 0
                && model.getScriptPath() != null
                && getExecutionMode().equals(ExecutionMode.RUN_MODE)
                && _scheduledFuture1 == null && _scheduledFuture2 == null) {

            IPath path = model.getScriptPath();
            if (!path.isEmpty()) {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                        path);
                try {
                    // load the script
                    IScript script = new RunnableScript(file.getName(), file
                            .getContents());
                    _scriptEngine = new ScriptEngine(script);

                    // runnable for the script execution
                    Runnable r = new Runnable() {

                        public void run() {
                            if (_scriptEngine != null) {
                                Thread t = new Thread(new Runnable() {
                                    public void run() {
                                        _scriptEngine.processScript();
                                    }
                                });
                                t.start();

                                _lastExecution = System.currentTimeMillis();
                            }
                        }
                    };

                    // runnable for updating the figure to see the progress
                    Runnable r2 = new Runnable() {
                        public void run() {
                            new CheckedUiRunnable() {
                                @Override
                                protected void doRunInUi() {
                                    long t = System.currentTimeMillis()
                                            - _lastExecution;
                                    double p = Math.min(1.0, (double) t
                                            / model.getDelay());
                                    RefreshableTimerFigure figure = (RefreshableTimerFigure) getFigure();
                                    figure.setPercentage(p);
                                }
                            };
                        }
                    };

                    _lastExecution = System.currentTimeMillis();

                    // schedule the runnables
                    _scheduledFuture1 = ExecutionService.getInstance()
                            .getScheduledExecutorService().scheduleAtFixedRate(
                                    r, model.getDelay(), model.getDelay(),
                                    TimeUnit.MILLISECONDS);

                    _scheduledFuture2 = ExecutionService.getInstance()
                            .getScheduledExecutorService().scheduleAtFixedRate(
                                    r2, 100, 100, TimeUnit.MILLISECONDS);

                } catch (Exception e) {
                    LOG.error("Could not start timer.");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {

                cancelScriptExecution();
                startScriptExecution();

                return true;
            }
        };
        setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, handler);
        setPropertyChangeHandler(TimerModel.PROP_DELAY, handler);
        setPropertyChangeHandler(TimerModel.PROP_SCRIPT, handler);
        setPropertyChangeHandler(TimerModel.PROP_ACCESS_GRANTED, handler);
    }



    private void cancelScriptExecution() {
        _scriptEngine = null;

        if (_scheduledFuture1 != null) {
            _scheduledFuture1.cancel(true);
            _scheduledFuture1 = null;
        }
        if (_scheduledFuture2 != null) {
            _scheduledFuture2.cancel(true);
            _scheduledFuture2 = null;
        }

    }

}
