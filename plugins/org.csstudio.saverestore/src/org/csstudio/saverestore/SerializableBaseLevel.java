package org.csstudio.saverestore;

import org.csstudio.saverestore.BaseLevel;

/**
 *
 * <code>SerializableBaseLevel</code> is an implementation of the {@link BaseLevel}, which is visible to all
 * save and restore plugins and is used whenever the {@link BeamlineSet} needs to be serialized.
 *
 * @see BeamlineSet#updateBaseLevel()
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SerializableBaseLevel extends BaseLevel {

    private static final long serialVersionUID = 7396901039726486878L;

    private final String storageName;
    private final String presentationName;

    /**
     * Constructs a new serializable base level.
     *
     * @param level the source
     */
    public SerializableBaseLevel(BaseLevel level) {
        this(level.getStorageName(), level.getPresentationName());
    }

    /**
     * Constructs a new serializable base level.
     *
     * @param storagetname the storage name of the base level
     * @param presentationName the presentation name of the base level
     */
    public SerializableBaseLevel(String storageName, String presentationName) {
        this.storageName = storageName;
        this.presentationName = presentationName;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.BaseLevel#getPresentationName()
     */
    @Override
    public String getPresentationName() {
        return presentationName;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.BaseLevel#getStorageName()
     */
    @Override
    public String getStorageName() {
        return storageName;
    }
}
