/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static org.csstudio.utility.channel.CSSChannelUtils.getCSSChannelTagNames;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * @author shroffk
 * 
 */
public class RemoveTagAction implements IObjectActionDelegate {

	private Shell shell;
	private Collection<Channel> channels;

	/**
	 * 
	 */
	public RemoveTagAction() {
		super();
		this.channels = new HashSet<Channel>();
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
		ElementListSelectionDialog selectTags = new ElementListSelectionDialog(
				shell, new LabelProvider());
		
		selectTags.setTitle("Tag Selection");

		selectTags.setMessage("Select the Tags to be removed (* = any string, ? = any char):");
		selectTags.setMultipleSelection(true);
		Collection<String> existingTagNames = getCSSChannelTagNames(channels);
		selectTags.setElements(existingTagNames
				.toArray(new String[existingTagNames.size()]));
		selectTags.setBlockOnOpen(true);
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
		
		
//		ListSelectionDialog selectTags = new ListSelectionDialog(shell,
//				getCSSChannelTagNames(channels), new allTagsContentProvider(),
//				new allTagsLabelProvider(), "Select Tags to be removed.");
//		if (selectTags.open() == Window.OK) {
//			Object[] selected = selectTags.getResult();
//			Collection<String> selectedTags = new TreeSet<String>();
//			for (int i = 0; i < selected.length; i++) {
//				selectedTags.add((String) selected[i]);
//			}
//			if (selectedTags.size() > 0) {
//				Job job = new RemoveTagsJob("removeTags", channels,
//						selectedTags);
//				job.schedule();
//			}
//		}
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
			channels.clear();
			for (Iterator<Channel> iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				channels.add(iterator.next());
			}
		}
	}

}
