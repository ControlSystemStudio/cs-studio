package org.csstudio.dct.metamodel;

/**
 * Represents a choice in a menu.
 *
 * @author Sven Wende
 *
 */
public interface IChoice {
    /**
     * Returns the choice id.
     * @return the choice id
     */
    String getId();

    /**
     * Returns a description.
     *
     * @return a description
     */
    String getDescription();
}
