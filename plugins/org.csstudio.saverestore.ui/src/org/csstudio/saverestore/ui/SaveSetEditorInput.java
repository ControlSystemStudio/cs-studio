package org.csstudio.saverestore.ui;

import org.csstudio.saverestore.data.SaveSetData;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 *
 * <code>SaveSetEditorInput</code> is the editor input for the save set editor.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SaveSetEditorInput implements IEditorInput {

    private final SaveSetData saveSet;

    /**
     * Creates a new editor input.
     *
     * @param set the data provided by this input
     */
    public SaveSetEditorInput(SaveSetData set) {
        this.saveSet = set;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (SaveSetData.class.isAssignableFrom(adapter)) {
            return adapter.cast(saveSet);
        }
        return null;
    }

    /**
     * @return the save set
     */
    public SaveSetData getSaveSet() {
        return saveSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    @Override
    public boolean exists() {
        return saveSet != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    @Override
    public String getName() {
        return saveSet.getDescriptor().getPathAsString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return saveSet.getDescriptor().getPathAsString();
    }
}
