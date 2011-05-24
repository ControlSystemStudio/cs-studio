package org.csstudio.multichannelviewer.actions;

import gov.bnl.channelfinder.api.Channel;

import java.util.Iterator;

import org.csstudio.multichannelviewer.ChannelsListView;
import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.csstudio.multichannelviewer.model.IChannelGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class OpenChannelsControlListView implements IObjectActionDelegate {

	private ISelection selection;
	private IChannelGroup channels;

	@Override
	public void run(IAction action) {
		if (selection == null) {

		} else if ((selection != null)
				& (selection instanceof IStructuredSelection)) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			channels = new CSSChannelGroup("New Channel Group");
			for (Iterator<Channel> iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				channels.addChannel(iterator.next());
			}
		}
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			ChannelsListView channelsView = (ChannelsListView) page.showView(
					ChannelsListView.ID, ChannelsListView.createNewInstance(),
					IWorkbenchPage.VIEW_ACTIVATE);
			channelsView.setChannelsGroup(channels);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}
}
