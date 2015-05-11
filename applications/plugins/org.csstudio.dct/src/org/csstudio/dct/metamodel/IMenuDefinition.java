package org.csstudio.dct.metamodel;

import java.util.List;

/**
 * Represents a menu definition.
 *
 * @author Sven Wende
 *
 */
public interface IMenuDefinition {
    /**
     * Returns the name of the menu.
     *
     * @return the menu name
     */
    String getName();

    /**
     * Returns all choices.
     *
     * @return all choice
     */
    List<IChoice> getChoices();

    /**
     * Adds a choice.
     *
     * @param choice the choice
     */
    void addChoice(IChoice choice);

    /**
     * Removes a choice.
     * @param choice the choice
     */
    void removeChoice(IChoice choice);
}
