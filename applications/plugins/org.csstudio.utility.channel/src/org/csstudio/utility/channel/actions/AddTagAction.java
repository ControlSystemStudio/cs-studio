package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.model.XmlChannel;
import gov.bnl.channelfinder.model.XmlChannels;
import gov.bnl.channelfinder.model.XmlTag;

import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AddTagAction implements IObjectActionDelegate {

	private Shell shell;
	private XmlChannels channels;
	final private IPreferencesService prefs;

	/**
	 * Constructor for AddTagAction.
	 */
	public AddTagAction() {
		super();
		this.channels = new XmlChannels();
		this.prefs = Platform.getPreferencesService();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		InputDialog inputdialog = new InputDialog(shell, "Add Tag",
				"Tag name: ", null, new IInputValidator() {
					public String isValid(String newText) {
						if ((newText != null) && (!newText.isEmpty()))
							return null;
						else
							return "Please enter a valid Tag Name.";
					}
				});
		inputdialog.setBlockOnOpen(true);
		if (inputdialog.open() == Window.OK) {
			String tagName = inputdialog.getValue();
			String owner = prefs.getString("org.csstudio.channelfinder",
					"user", null, null);
			Job job = new AddTagsJob("addTags", channels, new XmlTag(tagName,
					owner));
			job.schedule();
		}

		// BusyIndicator.showWhile(shell.getDisplay(), (Runnable) job);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@SuppressWarnings("unchecked")
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
