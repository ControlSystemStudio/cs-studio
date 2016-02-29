package org.csstudio.dct.metamodel.internal;

import java.io.Serializable;

import org.csstudio.dct.metamodel.IChoice;


/**
 * Standard implementation of {@link IChoice}.
 *
 * @author Sven Wende
 *
 */
public final class Choice implements IChoice, Serializable {
    private String description;
    private String id;

    /**
     * Constructor.
     * @param id a non-empty id
     * @param description a non-empty description
     */
    public Choice(String id, String description) {
        assert id != null;
        assert description != null;
        this.description = description;
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
