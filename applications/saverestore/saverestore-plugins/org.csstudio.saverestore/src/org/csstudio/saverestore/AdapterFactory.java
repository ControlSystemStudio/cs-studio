/* *****************************************************************
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 *******************************************************************/
package org.csstudio.saverestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.Optional;

import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

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
                    Instant snapshotTime = sc.getDate();
                    SaveSet set = new SaveSet(new Branch(), Optional.empty(), path, null);
                    Snapshot descriptor = new Snapshot(set, sc.getDate(), "<No Comment>", "<OS>");
                    VSnapshot vs = new VSnapshot((Snapshot) descriptor, sc.getEntries(), snapshotTime);
                    return adapterType.cast(vs);
                } catch (IOException | CoreException | ParseException ex) {
                    throw new IllegalArgumentException(
                        String.format("The file %s is not a valid snapshot file.", file.getName()), ex);
                }
            } else if (adaptableObject instanceof IURIEditorInput) {
                IURIEditorInput input = (IURIEditorInput) adaptableObject;
                URI uri = input.getURI();
                try (InputStream stream = uri.toURL().openStream()) {
                    String absPath = uri.toString().replace('\\', '/');
                    String[] path = absPath.split("\\/");
                    SnapshotContent sc = FileUtilities.readFromSnapshot(stream);
                    Instant snapshotTime = sc.getDate();
                    SaveSet set = new SaveSet(new Branch(), Optional.empty(), path, null);
                    Snapshot descriptor = new Snapshot(set, sc.getDate(), "<No Comment>", "<OS>");
                    VSnapshot vs = new VSnapshot((Snapshot) descriptor, sc.getEntries(), snapshotTime);
                    return adapterType.cast(vs);
                } catch (IOException | ParseException ex) {
                    throw new IllegalArgumentException(
                        String.format("The file %s is not a valid snapshot file.", uri.toString()), ex);
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
