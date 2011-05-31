package org.csstudio.multichannelviewer.actions;

import gov.bnl.channelfinder.api.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.csstudio.multichannelviewer.MultiChannelPlot;
import org.csstudio.multichannelviewer.PerspectiveFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class OpenMultiChannelPlot implements IWorkbenchWindowActionDelegate {

	private ISelection selection;
	private IWorkbenchWindow window;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		Collection<Channel> channels = new ArrayList<Channel>();
		// Open the editor
		try {
			if (selection != null & selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				channels.clear();
				for (Iterator<Channel> iterator = strucSelection.iterator(); iterator
						.hasNext();) {
					channels.add(iterator.next());
				}
			}

			MultiChannelPlot editor = MultiChannelPlot.createInstance();
			editor.getCSSChannelGroup().addChannels(channels);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Open the persepective
		openPerspective(PerspectiveFactory.PERSPECTIVE_ID, null);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	/**
	 * Implements Open Perspective.
	 */
	private void openPerspective(String perspId, IAdaptable input) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		// Get "Open Behavior" preference.
		AbstractUIPlugin plugin = (AbstractUIPlugin) Platform
				.getPlugin(PlatformUI.PLUGIN_ID);
		IPreferenceStore store = plugin.getPreferenceStore();
		String pref = store
				.getString(IWorkbenchPreferenceConstants.OPEN_NEW_PERSPECTIVE);

		// Implement open behavior.
		try {
			if (pref
					.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_WINDOW))
				workbench.openWorkbenchWindow(perspId, input);
			else if (pref
					.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_PAGE))
				window.openPage(perspId, input);
			else if (pref
					.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE)) {
				IPerspectiveRegistry reg = workbench.getPerspectiveRegistry();
				window.getActivePage().setPerspective(
						reg.findPerspectiveWithId(perspId));
			}
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}

}
