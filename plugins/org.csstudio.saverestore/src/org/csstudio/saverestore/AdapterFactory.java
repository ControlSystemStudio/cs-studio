package org.csstudio.saverestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

/**
 *
 * <code>AdapterFactory</code> provides a mechanism to transform the file editor input to a snapshot containing the data
 * from that file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class AdapterFactory implements IAdapterFactory {

    private static final Class<?>[] ADAPTER_TYPES = new Class[] { VSnapshot.class };

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
        if (VSnapshot.class.isAssignableFrom(adapterType)) {
            if (adaptableObject instanceof IFileEditorInput) {
                IFileEditorInput input = (IFileEditorInput) adaptableObject;
                IFile file = input.getFile();
                try {
                    String[] path = file.getFullPath().segments();
                    SnapshotContent sc = FileUtilities.readFromSnapshot(file.getContents());
                    Timestamp snapshotTime = Timestamp.of(sc.date);
                    BeamlineSet set = new BeamlineSet(new Branch("master", "master"), Optional.empty(), path, null);
                    Snapshot descriptor = new Snapshot(set, sc.date, "No Comment", "OS");
                    VSnapshot vs = new VSnapshot((Snapshot) descriptor, sc.names, sc.selected, sc.data, snapshotTime);
                    return adapterType.cast(vs);
                } catch (IOException | CoreException | ParseException ex) {
                    throw new IllegalArgumentException("The file " + file.getName() + " is not a valid snapshot file.");
                }
            } else if (adaptableObject instanceof FileStoreEditorInput) {
                FileStoreEditorInput input = (FileStoreEditorInput) adaptableObject;
                File file = new File(input.getURI());
                try (FileInputStream fis = new FileInputStream(file)) {
                    String absPath = file.getAbsolutePath().replace('\\', '/');
                    String[] path = absPath.split("\\/");
                    SnapshotContent sc = FileUtilities.readFromSnapshot(fis);
                    Timestamp snapshotTime = Timestamp.of(sc.date);
                    BeamlineSet set = new BeamlineSet(new Branch("master", "master"), Optional.empty(), path, null);
                    Snapshot descriptor = new Snapshot(set, sc.date, "No Comment", "OS");
                    VSnapshot vs = new VSnapshot((Snapshot) descriptor, sc.names, sc.selected, sc.data, snapshotTime);
                    return adapterType.cast(vs);
                } catch (IOException | ParseException ex) {
                    throw new IllegalArgumentException("The file " + file.getName() + " is not a valid snapshot file.");
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @Override
    public Class<?>[] getAdapterList() {
        return ADAPTER_TYPES;
    }

}
