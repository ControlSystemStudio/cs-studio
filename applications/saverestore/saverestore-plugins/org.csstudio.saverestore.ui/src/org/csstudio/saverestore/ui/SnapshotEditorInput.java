/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.csstudio.saverestore.data.VSnapshot;
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

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal
        .withInitial(() -> new SimpleDateFormat(" (MMM dd HH:mm:ss)"));
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
     *
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
     *
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    @Override
    public boolean exists() {
        return snapshot != null;
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
        if (snapshot.isSaved()) {
            Instant t = snapshot.getSnapshot().get().getDate();
            String name = snapshot.getSaveSet().getName();
            return name + DATE_FORMAT.get().format(Date.from(t));
        } else {
            return snapshot.getSaveSet().getDisplayName();
        }
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
        return snapshot.getSaveSet().getFullyQualifiedName();
    }
}
