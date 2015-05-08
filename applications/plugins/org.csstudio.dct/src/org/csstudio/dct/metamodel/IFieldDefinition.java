package org.csstudio.dct.metamodel;

/**
 * Represents a field definition.
 *
 * @author Sven Wende
 *
 */
public interface IFieldDefinition {
    /**
     * Returns the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the type.
     *
     * @return the type
     */
    String getType();

    /**
     * Returns the prompt.
     *
     * @return the prompt
     */
    String getPrompt();

    /**
     * Returns the prompt group.
     *
     * @return the prompt group
     */
    PromptGroup getPromptGroup();

    /**
     * Returns the interest.
     *
     * @return the interest
     */
    String getInterest();

    /**
     * Returns the special.
     *
     * @return the special
     */
    String getSpecial();

    /**
     * Returns the size.
     *
     * @return the size
     */
    String getSize();

    /**
     * Returns the extra.
     *
     * @return the extra
     */
    String getExtra();

    /**
     * Returns the initial value.
     *
     * @return the initial value
     */
    String getInitial();

    /**
     * Returns the menu.
     *
     * @return the menu
     */
    IMenuDefinition getMenu();

}
