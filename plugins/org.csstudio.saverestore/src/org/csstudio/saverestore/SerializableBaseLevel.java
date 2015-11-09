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
public class SerializableBaseLevel implements BaseLevel {

    private static final long serialVersionUID = 7396901039726486878L;

    private final String storageName;
    private final String presentationName;

    /**
     * Constructs a new serializable base level.
     *
     * @param level the source
     */
    public SerializableBaseLevel(BaseLevel level) {
        this.storageName = level.getStorageName();
        this.presentationName = level.getPresentationName();
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
