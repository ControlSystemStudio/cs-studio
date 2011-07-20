/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

/**
 * An action which plays a .wav file.
 * 
 * @author Xihui Chen
 * 
 */
public class PlayWavFileAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(PROP_PATH, "WAV File Path",
				WidgetPropertyCategory.Basic, new Path(""),
				new String[] { "wav" }));

	}

	@Override
	public ActionType getActionType() {
		return ActionType.PLAY_SOUND;
	}

	@Override
	public void run() {

		Job job = new Job("Play wave file") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + getAbsolutePath(),
						IProgressMonitor.UNKNOWN);
				try {
					final InputStream in = ResourceUtil.pathToInputStream(
							getAbsolutePath(), false);
					Display display = 
							getWidgetModel().getRootDisplayModel().getViewer().getControl().getDisplay();
					display.asyncExec(new Runnable() {
						
						public void run() {							
							try {
								final BufferedInputStream bis;
								if (!(in instanceof BufferedInputStream))
									bis = new BufferedInputStream(in);
								else
									bis = (BufferedInputStream) in;

								final AudioInputStream stream = AudioSystem
										.getAudioInputStream(bis);
								Clip clip = AudioSystem.getClip();
								clip.open(stream);

								clip.addLineListener(new LineListener() {
									public void update(LineEvent event) {
										if (event.getType() == LineEvent.Type.STOP) {
											try {
												stream.close();
												bis.close();
												if (in != bis)
													in.close();
											} catch (IOException e) {
												OPIBuilderPlugin
														.getLogger()
														.log(Level.WARNING,
																"audio close error", e); //$NON-NLS-1$
											}
										}
									}
								});
								clip.start();
							} catch (Exception e) {
								final String message = "Failed to play wave file " + getPath(); //$NON-NLS-1$
								OPIBuilderPlugin.getLogger().log(Level.WARNING,
										message, e);
								ConsoleService.getInstance().writeError(
										message + "\n" + e.getMessage()); //$NON-NLS-1$
							}
						}
					});
				}catch (Exception e) {
					final String message = "Failed to connect to wave file " + getPath(); //$NON-NLS-1$
					OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
					ConsoleService.getInstance().writeError(
							message + "\n" + e.getMessage()); //$NON-NLS-1$
				} finally {
					monitor.done();
				}

				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

	private IPath getPath() {
		return (IPath) getPropertyValue(PROP_PATH);
	}

	private IPath getAbsolutePath() {
		// read file
		IPath absolutePath = getPath();
		if (!getPath().isAbsolute()) {
			absolutePath = ResourceUtil.buildAbsolutePath(getWidgetModel(),
					getPath());
		}
		return absolutePath;
	}

	@Override
	public String getDefaultDescription() {
		return super.getDefaultDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
