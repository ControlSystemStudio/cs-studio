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
package org.csstudio.saverestore.ui.browser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 *
 * <code>LazySnapshotStructuredSelection</code> is a selection that provides a set of process variables. This selection
 * only provides data through {@link #toArray()} method. All other methods return false data and should be avoided. The
 * reason is that we want to initialise the data (read from repository) only when the data is really needed and not just
 * for fun which is what RCP likes to do far too often and too soon.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
@SuppressWarnings({ "rawtypes" })
public class LazySnapshotStructuredSelection implements IStructuredSelection {

    private final Snapshot snapshot;
    private final DataProvider provider;

    private VSnapshot lazyData;

    /**
     * Constructs a new selection.
     *
     * @param snapshot the snapshot that was selected
     * @param provider the data provider which will be queried for data when needed
     */
    public LazySnapshotStructuredSelection(Snapshot snapshot, DataProvider provider) {
        this.snapshot = snapshot;
        this.provider = provider;
    }

    private VSnapshot getLazyData() {
        if (lazyData == null) {
            try {
                lazyData = provider.getSnapshotContent(snapshot);
            } catch (DataProviderException e) {
                throw new IllegalStateException(e);
            }
        }
        return lazyData;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return snapshot == null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredSelection#getFirstElement()
     */
    @Override
    public Object getFirstElement() {
        if (isEmpty()) {
            return null;
        }
        VSnapshot d = getLazyData();
        return d.getEntries().isEmpty() ? null : new ProcessVariable(d.getEntries().get(0).getPVName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredSelection#iterator()
     */
    @Override
    public Iterator iterator() {
        return toList().iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredSelection#size()
     */
    @Override
    public int size() {
        // return something for eclipse to know that there are data
        return lazyData == null ? 1 : lazyData.getEntries().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredSelection#toArray()
     */
    @Override
    public Object[] toArray() {
        if (snapshot == null) {
            return new Object[0];
        }
        return getLazyData().getEntries().stream().map(e -> new ProcessVariable(e.getPVName()))
            .toArray(ProcessVariable[]::new);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
     */
    @Override
    public List toList() {
        if (lazyData == null) {
            // return dummy stuff, because RCP calls this method and we do not want to initialise the snapshot too soon
            return Arrays.asList(new ProcessVariable("dummy"));
        } else {
            return lazyData.getEntries().stream().map(e -> new ProcessVariable(e.getPVName()))
                .collect(Collectors.toList());
        }

    }
}
