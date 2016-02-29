package org.csstudio.dct.model.internal;

import java.io.Serializable;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.util.CompareUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * Standard implementation of {@link IElement}.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractElement implements IElement, IAdaptable, Serializable {
    private static final long serialVersionUID = 6033398826670082191L;

    private String name;
    private UUID id;

    public AbstractElement() {
    }

    /**
     * Constructor.
     *
     * @param name
     *            the name
     */
    public AbstractElement(String name) {
        this.name = name;
        id = UUID.randomUUID();
    }

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param id
     *            the id
     */
    public AbstractElement(String name, UUID id) {
        assert id != null;
        this.name = name;
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final UUID getId() {
        return id;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        // result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof AbstractElement) {
            AbstractElement element = (AbstractElement) obj;

            if (CompareUtil.equals(getName(), element.getName())) {
                if (CompareUtil.equals(getId(), element.getId())) {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}
