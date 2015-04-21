/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.util;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.internal.console.ConsoleView;

/**Implementation code for {@link ConsoleServiceSSHelper}
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class ConsoleServiceSSHelperImpl extends ConsoleServiceSSHelper {

	private static final String ENTER = "\n"; //$NON-NLS-1$

	/**
	 * The IO console.
	 */
	private IOConsole console = null;
	
	/**
	 * The original system output stream.
	 */
	private PrintStream originalSystemOut = null;

	/**
	 * The console output stream.
	 */
	private IOConsoleOutputStream errorStream, warningStream, infoStream, generalStream;
		
	{
		console = new IOConsole("BOY Console", null);       

		generalStream = console.newOutputStream();
		
		// Values are from https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871#c5
		console.setWaterMarks(400000, 500000);
		
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		consolePlugin.getConsoleManager().addConsoles(
				new IConsole[] { console });
 
		
	}
	
	/**
	 * Direct system output to BOY console. 
	 * <b>Warning: </b>To make this take effect for the Python script calling this method, 
	 * it is required to rerun the OPI with the Python script so that the Python interpreter
	 * has a chance to reload system output. 
	 */
	public void turnOnSystemOutput(){
		if(originalSystemOut == null){
			originalSystemOut = System.out;
			System.setOut(new PrintStream(generalStream));
			//will cause CSS hang up
//			System.setIn(console.getInputStream());
		}
	}
	
	/**
	 * Turn off displaying system output in BOY console and 
	 * reset system output to original output.
	 * <b>Warning: </b>It is required to rerun the OPI if this method is called from Python script. 
	 */
	public void turnOffSystemOutput() {
		if (originalSystemOut != null) {
			System.setOut(originalSystemOut);
			originalSystemOut = null;

			// WARNING: It is not possible to reconfigure the logger here! This
			// method is called in the UI thread, so reconfiguring the logger
			// here would mean that the UI thread waits for the logger, which
			// could cause a deadlock.
		}
	}
	


	private String getTimeString(){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
	    return sdf.format(cal.getTime());

	}
	
	

	/**Write error information to the OPI console.
	 * @param message the output string.
	 */
	public void writeError(final String message){
		switch (PreferencesHelper.getConsolePopupLevel()) {
		case ALL:
			popConsoleView();
			break;
		default:
			break;
		}

		final String output = getTimeString() + " ERROR: " + message + ENTER;
		UIBundlingThread.getInstance().addRunnable(new Runnable() {

			public void run() {
				if(errorStream == null){
					errorStream = console.newOutputStream();
					errorStream.setColor(CustomMediaFactory.getInstance().getColor(
							CustomMediaFactory.COLOR_RED));
				}
				writeToConsole(errorStream, output);
			}
		});


	}

	/**Write warning information to the OPI console.
	 * @param message the output string.
	 */
	public void writeWarning(String message){
		final String output = getTimeString() + " WARNING: " + message+ ENTER;
		switch (PreferencesHelper.getConsolePopupLevel()) {
		case ALL:
			popConsoleView();
			break;
		default:
			break;
		}
		UIBundlingThread.getInstance().addRunnable(new Runnable() {

			public void run() {
				if(warningStream == null){
					warningStream = console.newOutputStream();
					warningStream.setColor(CustomMediaFactory.getInstance().getColor(
							CustomMediaFactory.COLOR_ORANGE));
				}
				writeToConsole(warningStream, output);
			}
		});

	}

	/**Write information to the OPI console.
	 * @param message the output string.
	 */
	public void writeInfo(String message){
		final String output = getTimeString() + " INFO: " + message+ ENTER;
		switch (PreferencesHelper.getConsolePopupLevel()) {
		case ALL:
		case ONLY_INFO:
			popConsoleView();
			break;
		default:
			break;
		}
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				if(infoStream == null){
					infoStream = console.newOutputStream();
					infoStream.setColor(CustomMediaFactory.getInstance().getColor(
							CustomMediaFactory.COLOR_BLACK));
				}
				writeToConsole(infoStream, output);
			}
		});

	}

	public void writeString(final String s){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				if(infoStream == null){
					infoStream = console.newOutputStream();
					infoStream.setColor(CustomMediaFactory.getInstance().getColor(
							CustomMediaFactory.COLOR_BLACK));
				}
				writeToConsole(infoStream, s);
			}
		});
	}
	
	public void writeString(final String s, final RGB color){
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			public void run() {
				IOConsoleOutputStream stream = console.newOutputStream();
				try {
					stream.setColor(CustomMediaFactory.getInstance().getColor(color));
					writeToConsole(stream, s);
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
					}
				}
			}
		});
	}
	



	/**Write string to the console.
	 * @param output
	 */
	private void writeToConsole(IOConsoleOutputStream stream, String output){
		try {
			stream.write(output);
		} catch (IOException e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Write Console error",e); //$NON-NLS-1$
		}
	}

	private void popConsoleView(){
		if(PlatformUI.getWorkbench() != null &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() !=null &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
					findView("org.eclipse.ui.console.ConsoleView") == null){		//$NON-NLS-1$
			UIBundlingThread.getInstance().addRunnable(new Runnable() {
				public void run() {
					try {
						final IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
							getActivePage().showView("org.eclipse.ui.console.ConsoleView"); //$NON-NLS-1$
						if(view != null && view instanceof ConsoleView){
							UIBundlingThread.getInstance().addRunnable(new Runnable() {
								@Override
								public void run() {
									((ConsoleView)view).display(console);									
								}
							});
						}
					} catch (PartInitException e) {
			            OPIBuilderPlugin.getLogger().log(Level.WARNING, "ConsoleView activation error",e); //$NON-NLS-1$
					}
				}
			});
		}
	}
}
