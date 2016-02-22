package org.csstudio.saverestore.ui;

import org.csstudio.saverestore.data.VSnapshot;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 * <code>ISnapshotReceiver</code> is an extension of the workbench part, which also provides the shell, can receive
 * snapshots and check for the dirtiness of the part. In all its glory this interface is the public API of the
 * {@link SnapshotViewerEditor} and is mainly use to completely separate the UI part from the business logic, which
 * allows for easier testing.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface ISnapshotReceiver extends IWorkbenchPart, IShellProvider {

    /**
     * Adds a snapshot to this receiver. This snapshot is compared to the base snapshot if it exists.
     *
     * @param data the snapshot data
     * @param useBackgroundThread true to add the snapshot in background or false to add them in the same thread
     *          this method is invoked from
     */
    void addSnapshot(VSnapshot snapshot, boolean useBackgroundThread);

    /**
     * Checks if the receiver is dirty and properly marks it.
     */
    void checkDirty();

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.window.IShellProvider#getShell()
     */
    @Override
    default Shell getShell() {
        return getSite().getShell();
    }
}
