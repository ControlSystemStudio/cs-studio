package org.remotercp.filetransfer.sender;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IOutgoingFileTransfer;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDataEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDoneEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendPausedEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendResumedEvent;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.progress.ProgressView;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.progress.handler.ProgressViewHandler;
import org.remotercp.util.dialogs.RemoteExceptionHandler;
import org.remotercp.util.filesize.FilesizeConverterUtil;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class RemoteFileSender {

	private final static Logger logger = Logger
			.getLogger(RemoteFileSender.class.getName());

	private FileDialog fileDialog;

	private ISendFileTransferContainerAdapter adapter;

	/**
	 * Sends the given file to the provided Users.
	 * 
	 * @param file
	 *            The file to be sent
	 * @param userIDs
	 *            List of recipients
	 * @throws ECFException
	 *             If no file transfer service is available
	 */
	public void sendFile(ID[] userIDs) throws ECFException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		Assert.isNotNull(shell);

		File file = null;
		fileDialog = new FileDialog(shell);

		final String filePath = fileDialog.open();

		if (filePath != null) {
			file = new File(filePath);

			try {
				sendRemoteFile(file, userIDs);
			} catch (MalformedURLException e) {
				RemoteExceptionHandler
						.handleException(
								e,
								"The provided file can not be send due to an MalformedURLException caused by RemoteFileSender");
				e.printStackTrace();
			}

		}

	}

	private void sendRemoteFile(File file, ID[] userIDs)
			throws SendFileTransferException, MalformedURLException,
			FileCreateException {

		logger.info("Trying to send a remote file...");

		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				FiletransferSenderActivator.getBundleContext(),
				ISessionService.class);

		Assert.isNotNull(sessionService);

		adapter = (ISendFileTransferContainerAdapter) sessionService
				.getContainer().getAdapter(
						ISendFileTransferContainerAdapter.class);

		Assert.isNotNull(adapter);

		// perform sending
		for (ID userID : userIDs) {
			try {
				String fileName = file.getName();
				IFileID fileID = FileIDFactory.getDefault().createFileID(
						adapter.getOutgoingNamespace(),
						new Object[] { userID, fileName });

				logger.info("Sending file to user: " + userID.getName());
				adapter.sendOutgoingRequest(fileID, file,
						new RemoteFileTransferListener(userID, fileName), null);

			} catch (SendFileTransferException e) {
				RemoteExceptionHandler
						.handleException(e,
								"Unable to send file to user user: "
										+ userID.getName());
				e.printStackTrace();
			}
		}

	}

	/**
	 * This class is a file transfer listener which will handel the response of
	 * the file transfer request, the progress of the transfer, cancelling
	 * operations, done operations, pause and resume operations.
	 * 
	 * @return {@link IFileTransferListener} the listener for a file transfer
	 */
	private class RemoteFileTransferListener implements IFileTransferListener {

		private ID userID;
		private long bytesSent = 0;
		private long fileLength = 0;
		private static final int SLEEPTIME = 1000;
		private int worked = 0;
		private int diff = 0;
		private Job progressJob;
		private final String fileName;

		public RemoteFileTransferListener(ID userID, String fileName) {
			this.userID = userID;
			this.fileName = fileName;
		}

		public void handleTransferEvent(IFileTransferEvent event) {
			logger.info("Event occured: " + event);

			// Resonse event
			if (event instanceof IOutgoingFileTransferResponseEvent) {
				final IOutgoingFileTransferResponseEvent response = (IOutgoingFileTransferResponseEvent) event;
				logger.info("User response to file transfer: "
						+ response.requestAccepted());

				if (!response.requestAccepted()) {
					// TODO inform user about not accepted event, but
					// Message dialog is probably not the best way
					this.openErrorDialog();
				}
			}

			// Send data event
			if (event instanceof IOutgoingFileTransferSendDataEvent) {
				final IOutgoingFileTransferSendDataEvent dataEvent = (IOutgoingFileTransferSendDataEvent) event;
				final IOutgoingFileTransfer fileTransfer = dataEvent
						.getSource();

				bytesSent = fileTransfer.getBytesSent();
				fileLength = fileTransfer.getFileLength();

				// start progress only once for each file transfer
				if (progressJob == null) {
					this.createNewProgressJob(fileTransfer);
				}
			}

			/*
			 * done events are coused either by ending the file transfer or
			 * cancelling by the receiver
			 */
			if (event instanceof IOutgoingFileTransferSendDoneEvent) {
				IOutgoingFileTransferSendDoneEvent doneEvent = (IOutgoingFileTransferSendDoneEvent) event;
				// cancel transmitting data
				doneEvent.getSource().cancel();
			}

			if (event instanceof IOutgoingFileTransferSendPausedEvent) {
				logger.info("File transfer paused");
			}

			if (event instanceof IOutgoingFileTransferSendResumedEvent) {
				logger.info("File transfer resumed");
			}

		}

		/*
		 * Creates a new Job which displays the progress of a file transfer in a
		 * progress view
		 */
		protected void createNewProgressJob(
				final IOutgoingFileTransfer fileTransfer) {

			progressJob = new Job("Sending data to: " + userID.getName()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {

					IStatus progressResult = handleProgress(monitor,
							fileTransfer);

					logger.info("Start progress for admin");

					// check if admin cancelled the file
					// transfer
					if (progressResult == Status.CANCEL_STATUS) {
						fileTransfer.cancel();
					}

					return progressResult;
				}
			};
			// don't display dialog, run in background
			// start as soon as possible
			progressJob.schedule();
		}

		/*
		 * Opens an error dialog with some cancel information
		 */
		protected void openErrorDialog() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					final Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();

					Status status = new Status(IStatus.ERROR,
							FiletransferSenderActivator.PLUGIN_ID,
							"The file transfer has been canceled by user.");

					ErrorDialog
							.openError(
									shell,
									"File transfer error",
									"Either user denied file transfer or the request timed out",
									status);

				}
			});
		}

		/**
		 * Is responsible for handling the progress in the job dialog as well as
		 * cancel events caused by user.
		 * 
		 * @param monitor
		 *            The monitor for displaying the progress
		 * @return Status whether the job has been completed successfully or not
		 */
		protected IStatus handleProgress(IProgressMonitor monitor,
				IOutgoingFileTransfer fileTransfer) {

			monitor.beginTask("Sending file: " + fileName, (int) fileLength);

			setFokusOnProgressView();

			/*
			 * Run the loop till:
			 * 
			 * 1. file transfer finished
			 * 
			 * 2. monitor has not been cancelled
			 * 
			 * 3. file transfer has not been cancelled by sender
			 */
			for (worked = 0; worked < fileLength && !monitor.isCanceled()
					&& !fileTransfer.isDone(); worked += diff) {
				// determine the next worked step for monitor
				diff = (int) bytesSent - worked;
				try {
					Thread.sleep(SLEEPTIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				monitor.worked(diff);

				String bytesSentString = FilesizeConverterUtil
						.getAppropriateFilesize(bytesSent);
				String fileLengthString = FilesizeConverterUtil
						.getAppropriateFilesize(fileLength);
				String speed = FilesizeConverterUtil
						.getAppropriateFilesize(diff);

				monitor.subTask("Data sent: " + bytesSentString + "/"
						+ fileLengthString + " with " + speed + "/sec");

				logger.info("Data sent: "
						+ FilesizeConverterUtil
								.getAppropriateFilesize(bytesSent));

			}

			monitor.done();

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

	}

	protected void setFokusOnProgressView() {
		ProgressViewHandler.setFocus();
	}
}
