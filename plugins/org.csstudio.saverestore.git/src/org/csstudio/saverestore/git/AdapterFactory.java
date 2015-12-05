package org.csstudio.saverestore.git;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.git.GitManager.DescriptionDateData;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.VType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IFileEditorInput;

/**
 *
 * <code>AdapterFactory</code> provides a mechanism to transform the file editor input to a snapshot containing
 * the data from that file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class AdapterFactory implements IAdapterFactory {

    private static final Class<?>[] ADAPTER_TYPES = new Class[]{VSnapshot.class};

    private final DateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
        if (adaptableObject instanceof IFileEditorInput && VSnapshot.class.isAssignableFrom(adapterType)) {
            IFileEditorInput input = (IFileEditorInput)adaptableObject;
            IFile file = input.getFile();
            try {
                String[] path = file.getFullPath().segments();
                DescriptionDateData<VType> ddp = GitManager.readFromSnapshot(file.getContents());
                Timestamp snapshotTime = Timestamp.of(TIMESTAMP_FORMATTER.parse(ddp.description));
                BeamlineSet set = new BeamlineSet(new Branch("master","master"), Optional.empty(), path,
                        GitDataProvider.ID);
                Snapshot descriptor = new Snapshot(set,new Date(),"No Comment","OS");
                VSnapshot vs = new VSnapshot((Snapshot) descriptor, ddp.names, ddp.selected, ddp.data,
                        snapshotTime);
                return adapterType.cast(vs);
            } catch (IOException | CoreException | ParseException ex) {
                throw new IllegalArgumentException("The file " + file.getName() + " is not a valid snapshot file.");
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @Override
    public Class<?>[] getAdapterList() {
        return ADAPTER_TYPES;
    }

}
