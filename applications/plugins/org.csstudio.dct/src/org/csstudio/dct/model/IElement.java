package org.csstudio.dct.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an arbitrary model element.
 *
 * @author Sven Wende
 *
 */
public interface IElement extends Serializable {

    /**
     * Returns the id for this model element. The id is globally unique.
     *
     * @return a globally unique id
     */
    UUID getId();

    /**
     * Returns the name of the element.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets the name of the element.
     *
     * @param name
     *            the name of the element
     */
    void setName(String name);

    /**
     * Returns true, if this element is inherited.
     *
     * @return true, if this element is inherited
     */
    boolean isInherited();


    /**
     * Accepts the specified visitor.
     *
     * @param visitor
     *            the visitor
     */
    void accept(IVisitor visitor);
}
