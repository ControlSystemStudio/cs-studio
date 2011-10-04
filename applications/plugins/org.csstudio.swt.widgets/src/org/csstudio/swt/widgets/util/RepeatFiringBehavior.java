/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

public class RepeatFiringBehavior
{
	protected static final int
		INITIAL_DELAY = 250,
		STEP_DELAY = 40;
	
	protected int
		stepDelay = INITIAL_DELAY,
		initialDelay = STEP_DELAY;
	
	protected Timer timer;
	
	private Runnable runTask;
	private Display display;
	public RepeatFiringBehavior() {
		 display = Display.getCurrent();
	}
	
	public void pressed() {
		runTask.run();
		
		timer = new Timer();
		TimerTask runAction = new Task();

		timer.scheduleAtFixedRate(runAction, INITIAL_DELAY, STEP_DELAY);
	}

	public void canceled() {
		suspend();
	}	
	public void released() {
		suspend();
	}
	
	public void resume() {
		timer = new Timer();
		
		TimerTask runAction = new Task();
		
		timer.scheduleAtFixedRate(runAction, STEP_DELAY, STEP_DELAY);
	}
	
	public void suspend() {
		if (timer == null) return;
		timer.cancel();
		timer = null;
	}

	
	public void setRunTask(Runnable runTask) {
		this.runTask = runTask;
	}

class Task 
	extends TimerTask {
	
	public void run() {
		display.syncExec(runTask);
	}
}

}
