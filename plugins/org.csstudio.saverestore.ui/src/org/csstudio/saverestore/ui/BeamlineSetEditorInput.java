package org.csstudio.saverestore.ui;

import org.csstudio.saverestore.BeamlineSetData;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 *
 * <code>BeamlineSetEditorInput</code> is the editor input for the beamline set editor.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSetEditorInput implements IEditorInput {

    private final BeamlineSetData beamlineSet;

    /**
     * Creates a new editor input.
     *
     * @param set the data provided by this input
     */
    public BeamlineSetEditorInput(BeamlineSetData set) {
        this.beamlineSet = set;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (BeamlineSetData.class.isAssignableFrom(adapter)) {
            return adapter.cast(beamlineSet);
        }
        return null;
    }

    /**
     * @return the beamline set
     */
    public BeamlineSetData getBeamlineSet() {
        return beamlineSet;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    @Override
    public boolean exists() {
        return beamlineSet != null;
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
        return beamlineSet.getDescriptor().getPathAsString();
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
        return beamlineSet.getDescriptor().getPathAsString();
    }
}
