package de.desy.language.snl.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import de.desy.language.snl.SNLConstants;
import de.desy.language.snl.nature.SNLNature;
import de.desy.language.snl.ui.SNLUiActivator;

public class NewSNLProjectWizard extends BasicNewProjectResourceWizard {

	private NewSNLProjectPage mainPage;

	private IProject newProject;

	@Override
	public void addPages() {
		this.mainPage = new NewSNLProjectPage("New SNL Project");
		this.addPage(this.mainPage);
	}

	@Override
	public void init(final IWorkbench workbench,
			final IStructuredSelection selection) {
		// this.selection = selection;
		super.init(workbench, selection);
		this.setWindowTitle("SNL Project Wizard"); //$NON-NLS-1$
//		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(
//				NewSNLProjectWizard.class, "newfolder_wiz.gif"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (!this.invokeRunnable(this.getRunnable())) {
			return false;
		}
		final IResource resource = this.getSelectedResource();
		this.selectAndReveal(resource);
		if ((resource != null) && (resource.getType() == IResource.FILE)) {
			final IFile file = (IFile) resource;
			// Open editor on new file.
			final IWorkbenchWindow dw = this.getWorkbench()
					.getActiveWorkbenchWindow();
			if (dw != null) {
				try {
					final IWorkbenchPage page = dw.getActivePage();
					if (page != null) {
						IDE.openEditor(page, file, true);
					}
				} catch (final PartInitException e) {
					MessageDialog.openError(dw.getShell(), "Error", e
							.getMessage());
				}
			}
		}
		return true;
	}

	protected IResource getSelectedResource() {
		return this.getNewProject();
	}

	public IRunnableWithProgress getRunnable() {
		return new WorkspaceModifyDelegatingOperation(
				new IRunnableWithProgress() {
					public void run(final IProgressMonitor imonitor)
							throws InvocationTargetException,
							InterruptedException {
						final Exception except[] = new Exception[1];
						// ugly, need to make the wizard page run in a non ui
						// thread so that this can go away!!!
						NewSNLProjectWizard.this.getShell().getDisplay()
								.syncExec(new Runnable() {
									public void run() {
										final IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
												new IRunnableWithProgress() {
													public void run(
															IProgressMonitor monitor)
															throws InvocationTargetException,
															InterruptedException {

														final IProgressMonitor fMonitor;
														if (monitor == null) {
															fMonitor = new NullProgressMonitor();
														} else {
															fMonitor = monitor;
														}
														fMonitor
																.beginTask(
																		"Creating project",
																		3);
														try {
															NewSNLProjectWizard.this
																	.createNewProject(new SubProgressMonitor(
																			fMonitor,
																			1));
														} catch (CoreException e) {
															except[0] = e;
														}
														fMonitor.done();
													}
												});
										try {
											NewSNLProjectWizard.this
													.getContainer().run(false,
															true, op);
										} catch (final InvocationTargetException e) {
											except[0] = e;
										} catch (final InterruptedException e) {
											except[0] = e;
										}
									}
								});
						if (except[0] != null) {
							if (except[0] instanceof InvocationTargetException) {
								throw (InvocationTargetException) except[0];
							}
							if (except[0] instanceof InterruptedException) {
								throw (InterruptedException) except[0];
							}
							throw new InvocationTargetException(except[0]);
						}
					}
				});
	}

	/**
	 * Utility method: call a runnable in a WorkbenchModifyDelegatingOperation
	 */
	protected boolean invokeRunnable(final IRunnableWithProgress runnable) {
		final IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
				runnable);
		try {
			this.getContainer().run(true, true, op);
		} catch (final InvocationTargetException e) {
			final Shell shell = this.getShell();

			final Throwable th = e.getTargetException();
			this.errorDialog(shell, "Error", "Couldn't invoke Runnable", th,
					false);
			try {
				this.mainPage.getProjectHandle().delete(false, false, null);
			} catch (final CoreException ignore) {
			} catch (final UnsupportedOperationException ignore) {
			}
			return false;
		} catch (final InterruptedException e) {
			return false;
		}
		return true;
	}

	/**
	 * Utility method with conventions
	 */
	private void errorDialog(final Shell shell, final String title,
			String message, final Throwable t, final boolean logError) {

		IStatus status;
		if (t instanceof CoreException) {
			status = ((CoreException) t).getStatus();
			// if the 'message' resource string and the IStatus' message are the
			// same,
			// don't show both in the dialog
			if ((status != null) && message.equals(status.getMessage())) {
				message = null;
			}
		} else {
			status = new Status(IStatus.ERROR, SNLUiActivator.PLUGIN_ID, -1,
					"Internal Error: ", t); //$NON-NLS-1$	
		}
		ErrorDialog.openError(shell, title, message, status);
	}

	protected void doRun(final IProgressMonitor monitor) throws CoreException {
		this.createNewProject(monitor);
	}

	/**
	 * Creates a new project resource with the selected name.
	 * <p>
	 * In normal usage, this method is invoked after the user has pressed Finish
	 * on the wizard; the enablement of the Finish button implies that all
	 * controls on the pages currently contain valid values.
	 * </p>
	 * <p>
	 * Note that this wizard caches the new project once it has been
	 * successfully created; subsequent invocations of this method will answer
	 * the same project resource without attempting to create it again.
	 * </p>
	 * 
	 * @return the created project resource, or <code>null</code> if the
	 *         project was not created
	 */
	protected IProject createNewProject(final IProgressMonitor monitor)
			throws CoreException {

		if (this.getNewProject() != null) {
			return this.getNewProject();
		}

		// get a project handle
		IProject newProjectHandle = null;
		try {
			newProjectHandle = this.mainPage.getProjectHandle();
		} catch (final UnsupportedOperationException e) {
			e.printStackTrace();
		}

		// get a project descriptor
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(newProjectHandle.getName());
		description.setLocation(null);
		String[] natureIds = description.getNatureIds();
		if (natureIds != null) {
			final String[] oldNatureIds = natureIds;
			natureIds = new String[oldNatureIds.length + 1];
			System
					.arraycopy(oldNatureIds, 0, natureIds, 0,
							oldNatureIds.length);
			natureIds[oldNatureIds.length] = SNLNature.getNatureId();
		} else {
			natureIds = new String[] { SNLNature.getNatureId() };
		}
		description.setNatureIds(natureIds);

		this.newProject = this.createSNLProject(description, newProjectHandle,
				monitor, "SNL");
		return this.newProject;

	}

	/**
	 * Creates a SNL project resource given the project handle and description.
	 * 
	 * @param description
	 *            the project description to create a project resource for
	 * @param projectHandle
	 *            the project handle to create a project resource for
	 * @param monitor
	 *            the progress monitor to show visual progress with
	 * 
	 * @exception CoreException
	 *                if the operation fails
	 * @exception OperationCanceledException
	 *                if the operation is canceled
	 */
	private IProject createSNLProject(final IProjectDescription description,
			final IProject projectHandle, final IProgressMonitor monitor,
			final String projectID) throws CoreException,
			OperationCanceledException {

		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					monitor.beginTask("Creating SNL Project...", 3); //$NON-NLS-1$
					if (!projectHandle.exists()) {
						projectHandle.create(description,
								new SubProgressMonitor(monitor, 1));
						projectHandle.open(monitor);
						this.createFolder(projectHandle, SNLConstants.SOURCE_FOLDER.getValue(), monitor);
						this.createFolder(projectHandle, SNLConstants.BIN_FOLDER.getValue(), monitor);
					}

					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}

					// Open first.
					projectHandle.open(IResource.BACKGROUND_REFRESH,
							new SubProgressMonitor(monitor, 1));

				} finally {
					monitor.done();
				}
			}

			private void createFolder(final IProject projectHandle,
					final String folderName, final IProgressMonitor monitor)
					throws CoreException {
				final IFolder folder = projectHandle.getFolder(folderName);
				if (!folder.exists()) {
					folder.create(true, true, monitor);
				}
			}

		}, ResourcesPlugin.getWorkspace().getRoot(), 0, monitor);
		return projectHandle;
	}
}
