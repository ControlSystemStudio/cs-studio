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
package org.csstudio.saverestore.ui.util;

import org.csstudio.saverestore.data.Snapshot;

import javafx.scene.input.DataFormat;

/**
 *
 * <code>SnapshotDataFormat</code> is the the data format descriptor for a {@link Snapshot} used in combination with
 * JavaFX clipboard.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class SnapshotDataFormat extends DataFormat {

    /** Singleton instance of the DataFormat */
    public static final DataFormat INSTANCE = new SnapshotDataFormat();

    private SnapshotDataFormat() {
        super("application/snapshot");
    }
}
