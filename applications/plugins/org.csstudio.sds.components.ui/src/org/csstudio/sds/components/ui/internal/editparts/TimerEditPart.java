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

import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.TimerModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableTimerFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Rectangle widget. The controller mediates between
 * {@link TimerModel} and {@link RefreshableTimerFigure}.
 * 
 * @author Kai Meyer
 * 
 */
public final class TimerEditPart extends AbstractWidgetEditPart {

	/**
	 * The internal {@link TimerTask}, which runs the configured script.
	 */
	private TimerTask _task;
	/**
	 * The internal {@link Timer}.
	 */
	private Timer _timer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		RefreshableTimerFigure timerFigure = new RefreshableTimerFigure();
		timerFigure.setVisible(getExecutionMode().equals(ExecutionMode.EDIT_MODE));
		this.configureTimer();
		return timerFigure;
	}

	/**
	 * Configures the internal timer.
	 */
	private void configureTimer() {
		if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
			TimerModel model = (TimerModel) getCastedModel();
			if (model.getEnabled()) {
				_timer = new Timer();
				_task = createTimerTask();
				_timer.schedule(_task, model.getDelay(), model.getDelay());	
			}
		}		
	}
	
	/**
	 * Creates a {@link TimerTask}. 
	 * @return The created {@link TimerTask}
	 */
	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				CentralLogger.getInstance().info(TimerEditPart.this, "Timer executed");
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler layerHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (getExecutionMode().equals(ExecutionMode.RUN_MODE) && _timer!=null) {
					boolean execute = (Boolean) newValue;
					if (execute && _task==null) {
						_task = createTimerTask();
						TimerModel model = (TimerModel) getCastedModel();
						_timer.schedule(_task, model.getDelay(), model.getDelay());
					} else if (!execute && _task!=null) {
						_task.cancel();
						_task = null;
					}	
				}
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, layerHandler);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		_task.cancel();
		_timer.cancel();
		super.deactivate();
	}

}
