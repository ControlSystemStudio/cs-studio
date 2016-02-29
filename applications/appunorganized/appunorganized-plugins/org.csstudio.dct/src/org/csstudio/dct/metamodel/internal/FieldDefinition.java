package org.csstudio.dct.metamodel.internal;

import java.io.Serializable;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IMenuDefinition;
import org.csstudio.dct.metamodel.PromptGroup;

/**
 * Standard implementation of {@link IFieldDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class FieldDefinition implements IFieldDefinition, Serializable {
    private String extra;
    private String initial;
    private String interest;
    private String name;
    private String prompt;
    private PromptGroup promptGroup;
    private String size;
    private String special;
    private String type;
    private IMenuDefinition menuDefinition;

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param type
     *            the type
     */
    public FieldDefinition(String name, String type) {
        assert type != null;
        assert name != null;
        this.type = type;
        this.name = name;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getExtra() {
        return extra;
    }

    /**
     * Sets extra.
     *
     * @param extra
     *            extra
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getInitial() {
        return initial;
    }

    /**
     * Sets the initial value.
     *
     * @param initial
     *            the initial value
     */
    public void setInitial(String initial) {
        this.initial = initial;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getInterest() {
        return interest;
    }

    /**
     * Sets the interest.
     *
     * @param interest
     *            the interest
     */
    public void setInterest(String interest) {
        this.interest = interest;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IMenuDefinition getMenu() {
        return menuDefinition;
    }

    /**
     * Sets the menu.
     *
     * @param menuDefinition
     *            the menu
     */
    public void setMenuDefinition(IMenuDefinition menuDefinition) {
        this.menuDefinition = menuDefinition;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getPrompt() {
        return prompt;
    }

    /**
     * Sets the prompt.
     *
     * @param prompt
     *            the prompt
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public PromptGroup getPromptGroup() {
        return promptGroup;
    }

    /**
     * Sets the prompt group.
     *
     * @param promptGroup
     *            the prompt group
     */
    public void setPromptGroup(PromptGroup promptGroup) {
        this.promptGroup = promptGroup;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size
     *            the size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getSpecial() {
        return special;
    }

    /**
     * Sets the special.
     *
     * @param special
     *            the special
     */
    public void setSpecial(String special) {
        this.special = special;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
