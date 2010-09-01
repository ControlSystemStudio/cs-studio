/**
 * 
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.model.XmlChannel;
import gov.bnl.channelfinder.model.XmlChannels;
import gov.bnl.channelfinder.model.XmlTag;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * @author shroffk
 * 
 */
public class RemoveTagAction implements IObjectActionDelegate {

	private Shell shell;
	private XmlChannels channels;
	private Collection<String> allTags = new TreeSet<String>();

	/**
	 * 
	 */
	public RemoveTagAction() {
		super();
		this.channels = new XmlChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		Collection<String> allProperties = new TreeSet<String>();
		allTags.clear();

		try {
			Iterator<XmlChannel> itr = channels.getChannels().iterator();
			while (itr.hasNext()) {
				XmlChannel element = itr.next();
				allProperties.addAll(element.getPropertyNames());
				allTags.addAll(element.getTagNames());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ListSelectionDialog selectTags = new ListSelectionDialog(shell,
				allTags, new allTagsContentProvider(),
				new allTagsLabelProvider(), "Select Tags to be removed.");
		if (selectTags.open() == Window.OK) {
			Object[] selected = selectTags.getResult();
			Collection<String> selectedTags = new TreeSet<String>();
			for (int i = 0; i < selected.length; i++) {
				selectedTags.add((String) selected[i]);
			}
			if (selectedTags.size() > 0) {
				Job job = new RemoveTagsJob("removeTags", channels,
						selectedTags);
				job.schedule();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(IAction action, ISelection selection) {

		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			channels.getChannels().clear();
			for (Iterator<XmlChannel> iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				channels.addChannel(iterator.next());
			}
		}
	}

}
