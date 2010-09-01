/**
 * 
 */
package org.csstudio.channelfinder.commands;

import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.model.XmlChannel;
import gov.bnl.channelfinder.model.XmlChannels;
import gov.bnl.channelfinder.model.XmlTag;

import java.util.Iterator;

import org.csstudio.channelfinder.views.SearchChannels;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 * 
 */
public class AddTags extends AbstractHandler {

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		System.out.println(HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getActivePartReference().getId());
		System.out.println(HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getActivePart().getSite().getId());
		XmlChannels channels = new XmlChannels();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (Iterator<XmlChannel> iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				channels.addChannel(iterator.next());
			}
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event)
					.getShell();
			InputDialog inputdialog = new InputDialog(shell, "Add Tag",
					"Tag name: ", null, new IInputValidator() {
						public String isValid(String newText) {
							if (newText != null)
								return null;
							else
								return "Please enter a valid Tag Name.";
						}
					});
			System.out.println(inputdialog.open());
			Job job = new addTags(new XmlTag(), channels);
			job.schedule();
		}
		return null;
	}

	public class addTags extends Job {

		private XmlTag tag;
		private XmlChannels channels;

		public addTags(XmlTag tag, XmlChannels channels) {
			super("add tag " + tag.getName());
			this.tag = tag;
			this.channels = channels;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			System.out.println("Adding tag " + tag.getName() + " to channels "
					+ channels.getChannelNames());
			try {
				wait(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
