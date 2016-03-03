package org.csstudio.dct.metamodel.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.metamodel.IChoice;
import org.csstudio.dct.metamodel.IMenuDefinition;


/**
 * Standard implementation of {@link IMenuDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class MenuDefinition implements IMenuDefinition, Serializable {

    private String name;
    private List<IChoice> choices;

    /**
     * Constructor.
     *
     * @param name a non-empty name
     */
    public MenuDefinition(String name) {
        assert name != null;
        this.name = name;
        choices = new ArrayList<IChoice>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<IChoice> choices) {
        this.choices = choices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChoice(IChoice choice) {
        choices.add(choice);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChoice(IChoice choice) {
        choices.remove(choice);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
