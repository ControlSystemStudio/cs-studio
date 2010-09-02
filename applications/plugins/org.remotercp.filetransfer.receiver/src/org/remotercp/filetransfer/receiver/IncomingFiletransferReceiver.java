package org.remotercp.filetransfer.receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.filetransfer.IFileTransferInfo;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransfer;
import org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IFileTransferRequestEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceivePausedEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveResumedEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.remotercp.common.servicelauncher.IRemoteServiceLauncher;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.filetransfer.receiver.dialogs.AcceptFiletransferDialog;
import org.remotercp.util.dialogs.RemoteExceptionHandler;
import org.remotercp.util.filesize.FilesizeConverterUtil;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class IncomingFiletransferReceiver implements
		IIncomingFileTransferRequestListener, IRemoteServiceLauncher {

	private Logger logger = Logger.getLogger(IncomingFiletransferReceiver.class
			.getName());

	private FileOutputStream out;

	private File localFile;

	public void handleFileTransferRequest(final IFileTransferRequestEvent event) {
		logger.info("Filestransfer request received.");

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				ID requesterID = event.getRequesterID();
				IFileTransferInfo fileTransferInfo = event
						.getFileTransferInfo();
				File file = fileTransferInfo.getFile();

				try {
					// open a wizard to accept or deny file
					int acceptIncomingFile = acceptIncomingFile(file.getName(),
							requesterID.getName());

					// Accepted
					if (acceptIncomingFile == Window.OK) {
						logger
								.info("Filetransfer accepted. Store file on disk");

						/*
						 * open a file dialog to geht the path for directory to
						 * store the file
						 */
						localFile = getStoreFileLocation(file);
						out = new FileOutputStream(localFile);

						// start file transfer
						event.accept(out, getFileTransferListener());
					} else {
						// Rejected
						event.reject();
						logger.info("Filetransfer denied");
					}
				} catch (IOException e) {
					RemoteExceptionHandler.handleException(e,
							"File could not be stored. ");
					e.printStackTrace();
				} catch (IncomingFileTransferException e) {
					RemoteExceptionHandler.handleException(e,
							"Incoming file could not be stored on disk");
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Opens a wizard with a request for file transfer. User ca accept or deny
	 * the file transfer.
	 * 
	 * @param fileName
	 * @param sender
	 * @return
	 * @throws IOException
	 */
	protected int acceptIncomingFile(final String fileName, final String sender)
			throws IOException {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		int acceptFile = -1;
		AcceptFiletransferDialog acceptDialog = new AcceptFiletransferDialog(
				fileName, sender);

		WizardDialog dialog = new WizardDialog(shell, acceptDialog) {
			// change shell size
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				newShell.setSize(400, 300);

				// center the Dialog
				Rectangle bounds = Display.getCurrent().getBounds();
				Point size = newShell.getSize();
				int xPosition = (bounds.width - size.x) / 2;
				int yPosition = (bounds.height - size.y) / 2;
				newShell.setLocation(xPosition, yPosition);
			}

			/*
			 * Override the text of the finish Button
			 */
			protected void createButtonsForButtonBar(Composite parent) {
				super.createButtonsForButtonBar(parent);
				Button finishButton = getButton(IDialogConstants.FINISH_ID);
				finishButton.setText("OK");
			}
		};
		acceptFile = dialog.open();

		return acceptFile;
	}

	/**
	 * Creates for given File a local file on disk. The location where to store
	 * the file will be asked in a FileSaveDialog.
	 * 
	 * @param file
	 *            The file to store
	 * @return The new file with a correct path
	 */
	protected File getStoreFileLocation(File file) {
		File localFile = null;
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		// open save Dialog
		FileDialog saveDialog = new FileDialog(shell, SWT.SAVE);
		saveDialog.setFileName(file.getName());
		String savePath = saveDialog.open();

		if (savePath != null) {

			// create new local file
			localFile = new File(savePath);

			// String sepr = System.getProperty("file.separator");
			// fileSaved = file.renameTo(localFile);
		}

		return localFile;
	}

	/**
	 * Returns a new listener for file transfer events.
	 * 
	 * @return {@link IFileTransferListener} for file transfer events.
	 */
	protected IFileTransferListener getFileTransferListener() {
		return new IFileTransferListener() {
			private long bytesReceived = 0;
			private long fileLength = 0;
			private static final int SLEEPTIME = 1000;
			// private boolean progressStarted = false;
			// private boolean DOCANCEL = false;
			private int worked = 0;
			private int diff = 0;
			private Job progressJob;

			public void handleTransferEvent(IFileTransferEvent event) {
				logger.info("File transfer event occured: " + event.toString());
				if (event instanceof IIncomingFileTransferReceiveDataEvent) {

					logger.info("Receive data event ");
					final IIncomingFileTransferReceiveDataEvent dataReceiver = (IIncomingFileTransferReceiveDataEvent) event;
					final IIncomingFileTransfer fileTransfer = dataReceiver
							.getSource();

					if (fileTransfer.isDone()) {
						logger.info("File transfer completed or canceled");
						fileTransfer.cancel();
					}

					bytesReceived = fileTransfer.getBytesReceived();
					fileLength = fileTransfer.getFileLength();

					// start progress only once for each file transfer
					if (progressJob == null) {

						// open progress bar
						progressJob = new Job("Receiving data....") {
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								IStatus progressResult = handleProgress(
										monitor, fileTransfer);

								if (progressResult == Status.CANCEL_STATUS) {
									fileTransfer.cancel();
									try {
										/*
										 * wait a sec, otherwise the file won't
										 * be deleted probably because it's in
										 * use
										 */
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									localFile.delete();
								}

								return progressResult;
							}
						};

						// show dialog for user
						progressJob.setUser(true);
						// start as soon as possible
						progressJob.schedule();
					}
				}

				if (event instanceof IIncomingFileTransferReceiveStartEvent) {
					logger.info("Receive start event");
					IIncomingFileTransferReceiveStartEvent start = (IIncomingFileTransferReceiveStartEvent) event;
					try {
						start.receive(localFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (event instanceof IIncomingFileTransferReceivePausedEvent) {
					logger.info("Receiving data paused event");
				}

				if (event instanceof IIncomingFileTransferReceiveResumedEvent) {
					logger.info("Receive data resumed event");
				}

				if (event instanceof IIncomingFileTransferReceiveDoneEvent) {
					IIncomingFileTransferReceiveDoneEvent doneEvent = (IIncomingFileTransferReceiveDoneEvent) event;
					/*
					 * Has no effect if, file transfer has been completed. Has
					 * only effect if sender canceled the file transfer
					 */
					doneEvent.getSource().cancel();

					logger.info("Receive done event");

					if (out != null) {
						try {
							out.flush();
							out.close();
						} catch (IOException e) {
							RemoteExceptionHandler.handleException(e,
									"Storing remote file on local disk failed");
							e.printStackTrace();
						}
					}
				}

			}

			/**
			 * Is responsible for handling the progress in the job dialog as
			 * well as cancel events caused by user.
			 * 
			 * @param monitor
			 *            The monitor for displaying the progress
			 * @return Status whether the job has been completed successfully or
			 *         not
			 */
			protected IStatus handleProgress(IProgressMonitor monitor,
					final IIncomingFileTransfer fileTransfer) {

				monitor.beginTask("Receiving file: " + localFile.getName(),
						(int) fileLength);

				for (worked = 0; worked < fileLength && !monitor.isCanceled()
						&& !fileTransfer.isDone(); worked += diff) {
					diff = (int) bytesReceived - worked;
					try {
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e) {
						RemoteExceptionHandler.handleException(e,
								"Progress job interruped");
						e.printStackTrace();
					}

					monitor.worked(diff);

					String bytesReceivedString = FilesizeConverterUtil
							.getAppropriateFilesize(bytesReceived);
					String fileLengthString = FilesizeConverterUtil
							.getAppropriateFilesize(fileLength);
					String speed = FilesizeConverterUtil
							.getAppropriateFilesize(diff);

					monitor.subTask("Data received: " + bytesReceivedString
							+ "/" + fileLengthString + " with " + speed
							+ "/sec");

					logger.info("Bytes received: " + bytesReceivedString + "/"
							+ fileLengthString);

				}

				monitor.done();

				if (monitor.isCanceled()) {
					logger.info("File transfer cancelled.");

					return Status.CANCEL_STATUS;
				}

				return Status.OK_STATUS;
			}
		};
	}

	public void startServices() {

		logger.info("******* Starting service: "
				+ IncomingFiletransferReceiver.class.getName() + " ********");

		ISessionService service = OsgiServiceLocatorUtil.getOSGiService(
				FiletransferReceiverActivator.getBundleContext(),
				ISessionService.class);
		Assert.isNotNull(service);

		ISendFileTransferContainerAdapter adapter = (ISendFileTransferContainerAdapter) service
				.getContainer().getAdapter(
						ISendFileTransferContainerAdapter.class);
		Assert.isNotNull(adapter);

		adapter.addListener(new IncomingFiletransferReceiver());

	}
}
