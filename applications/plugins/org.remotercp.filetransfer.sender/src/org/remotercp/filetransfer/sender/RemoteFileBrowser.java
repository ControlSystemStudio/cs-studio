package org.remotercp.filetransfer.sender;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemBrowserContainerAdapter;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemListener;
import org.eclipse.ecf.filetransfer.RemoteFileSystemException;
import org.eclipse.ecf.filetransfer.events.IRemoteFileSystemEvent;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class RemoteFileBrowser {

	private IRemoteFileSystemBrowserContainerAdapter adapter;

	public void browseRemoteFilesystem(ID userID) {
		adapter = OsgiServiceLocatorUtil.getOSGiService(
				FiletransferSenderActivator.getBundleContext(),
				IRemoteFileSystemBrowserContainerAdapter.class);

		try {
			IFileID fileID = FileIDFactory.getDefault().createFileID(
					adapter.getBrowseNamespace(),
					new Object[] { userID, "C:/" });

			adapter.sendBrowseRequest(fileID, new IRemoteFileSystemListener() {

				public void handleRemoteFileEvent(IRemoteFileSystemEvent event) {
					// TODO Auto-generated method stub

				}

			});
		} catch (RemoteFileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileCreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
