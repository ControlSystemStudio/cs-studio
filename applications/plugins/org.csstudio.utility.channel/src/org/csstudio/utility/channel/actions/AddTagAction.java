package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.Tag;

import java.beans.ExceptionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.utility.channelfinder.Activator;
import org.csstudio.utility.channelfinder.CFClientManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class AddTagAction implements IObjectActionDelegate {

	private Shell shell;
	private Collection<Channel> channels;
	final private IPreferencesService prefs;

	/**
	 * Constructor for AddTagAction.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public AddTagAction() {
		super();
		this.channels = new HashSet<Channel>();
		this.prefs = Platform.getPreferencesService();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 * @wbp.parser.entryPoint
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 * @wbp.parser.entryPoint
	 */
	public void run(IAction action) {
		Collection<String> existingTagNames = null;
		GetAllTags getAllTags = new GetAllTags();
		System.out.println(Thread.currentThread().getName());
		getAllTags.addExceptionListener(new ExceptionListener() {

			@Override
			public void exceptionThrown(Exception e) {
				final Exception exception = e;
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {

							@Override
							public void run() {
								Status status = new Status(
										Status.ERROR,
										Activator.PLUGIN_ID,
										((ChannelFinderException) exception).getMessage(),
										exception.getCause());
								ErrorDialog.openError(shell,
										"Error retrieving all the tag names.",
										exception.getMessage(), status);
							}
						});
			}
		});
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			existingTagNames = executor.submit(getAllTags).get();
		} catch (InterruptedException e1) {
		} catch (ExecutionException e1) {
		}

		AddTagDialog dialog = new AddTagDialog(shell, existingTagNames);
		dialog.setBlockOnOpen(true);
		if (dialog.open() == Window.OK) {
			System.out.println(dialog.getValue());
			String tagName = dialog.getValue();
			Tag.Builder tag = tag(tagName);
			if (existingTagNames.contains(tagName)) {
				System.out.println("using an existing tag.");
			} else if (tagName != null && !tagName.equals("")) {
				System.out.println("new Tag");
				if (!MessageDialog
						.openConfirm(
								shell,
								"Confirm Tag Creation",
								"Tag: "
										+ tagName
										+ " does not exist, would you like to create a new one?"))
					return;
				Job create = new CreateTagJob("Create Tag", tag(tagName, null));
				create.schedule();
			}
			Job job = new AddTag2ChannelsJob("AddTags", channels, tag);
			job.schedule();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings("unchecked")
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

	private class GetAllTags implements Callable<Collection<String>> {
		private List<ExceptionListener> listeners = new CopyOnWriteArrayList<ExceptionListener>();

		public void addExceptionListener(ExceptionListener listener) {
			this.listeners.add(listener);
		}

		public void removeExceptionListener(ExceptionListener listener) {
			this.listeners.remove(listener);
		}

		@Override
		public Collection<String> call() throws Exception {
			try {
				return CFClientManager.getClient().getAllTags();
			} catch (ChannelFinderException e) {
				for (ExceptionListener listener : this.listeners) {
					listener.exceptionThrown(e);
				}
				return null;
			}
		}

	}
}
