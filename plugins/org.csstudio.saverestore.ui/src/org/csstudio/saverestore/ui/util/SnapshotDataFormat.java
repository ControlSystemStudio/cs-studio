package org.csstudio.saverestore.ui.util;

import javafx.scene.input.DataFormat;

/**
 *
 * <code>SnapshotDataFormat</code> is the a descriptor for the snapshot used in combination with JavaFX clipboard.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotDataFormat extends DataFormat {

    /** Singleton instance of the DataFormat */
    public static final DataFormat INSTANCE = new SnapshotDataFormat();

    private SnapshotDataFormat() {
        super("application/snapshot");
    }
}
