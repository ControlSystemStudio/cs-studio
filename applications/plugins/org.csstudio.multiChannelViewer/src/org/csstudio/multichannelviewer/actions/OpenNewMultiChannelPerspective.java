/**
 * 
 */
package org.csstudio.multichannelviewer.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.csstudio.multichannelviewer.MultiChannelPlot;
import org.csstudio.multichannelviewer.PerspectiveFactory;
import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author shroffk
 * 
 *         Handler for a command which opens a new MultiChannelPerspective
 * 
 */
public class OpenNewMultiChannelPerspective extends AbstractHandler implements
		IHandler {

	@SuppressWarnings("deprecation")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Collection<ICSSChannel> channels = new ArrayList<ICSSChannel>();
		// the most straightforward ;>
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				channels.add((ICSSChannel) Platform.getAdapterManager().getAdapter(iterator.next(), ICSSChannel.class));				
			}
		}

		// create an empty editor
		MultiChannelPlot editor = MultiChannelPlot.createInstance();
		editor.getCSSChannelGroup().addChannels(channels);

		final IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindow(event);
		// Get "Open Behavior" preference.
		AbstractUIPlugin plugin = (AbstractUIPlugin) Platform
				.getPlugin(PlatformUI.PLUGIN_ID);
		IPreferenceStore store = plugin.getPreferenceStore();
		String pref = store
				.getString(IWorkbenchPreferenceConstants.OPEN_NEW_PERSPECTIVE);

		// Implement open behavior.
		try {
			if (pref.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_WINDOW))
				window.getWorkbench().openWorkbenchWindow(
						PerspectiveFactory.PERSPECTIVE_ID, null);
			else if (pref
					.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_PAGE))
				window.openPage(PerspectiveFactory.PERSPECTIVE_ID, null);
			else if (pref
					.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE)) {
				IPerspectiveRegistry reg = window.getWorkbench()
						.getPerspectiveRegistry();
				window.getActivePage()
						.setPerspective(
								reg.findPerspectiveWithId(PerspectiveFactory.PERSPECTIVE_ID));
			}
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

}
