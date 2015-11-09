package org.csstudio.saverestore.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.saverestore.VSnapshot;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 *
 * <code>SnapshotEditorInput</code> is the input that provides the snapshot viewer with the data to display.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotEditorInput implements IEditorInput {

    private final VSnapshot snapshot;

    /**
     * Creates a new editor input.
     *
     * @param snapshot the data provided by this input
     */
    public SnapshotEditorInput(VSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (VSnapshot.class.isAssignableFrom(adapter)) {
            return adapter.cast(snapshot);
        }
        return null;
    }

    /**
     * @return the snapshot data
     */
    public VSnapshot getSnapshot() {
        return snapshot;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    @Override
    public boolean exists() {
        return snapshot != null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    @Override
    public String getName() {
        if (snapshot.isSaved()) {
            Date t = snapshot.getSnapshot().get().getDate();
            String name = snapshot.getBeamlineSet().getName();
            SimpleDateFormat df = new SimpleDateFormat(" (MMM dd HH:mm:ss)");
            return name + df.format(t);
        } else {
            return snapshot.getBeamlineSet().getFullName();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return snapshot.getBeamlineSet().getFullName() + ": " + snapshot.getBeamlineSet().getPathAsString();
    }
}
