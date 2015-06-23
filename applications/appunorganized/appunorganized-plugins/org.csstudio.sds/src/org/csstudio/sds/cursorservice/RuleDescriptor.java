package org.csstudio.sds.cursorservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Describes a {@link CursorSelectionRule}. The description includes the list
 * of cursor states that are declared for the rule (i.e., declared in the
 * contribution which contributes the rule).
 *
 * @author swende, Joerg Rathlev
 */
public final class RuleDescriptor {

    /**
     * The id of the described rule.
     */
    private String _id;

    /**
     * The description of the rule.
     */
    private String _description;

    /**
     * The cursor states declared for this rule.
     */
    private List<CursorState> _states;

    /**
     * The configuration element that declared this rule.
     */
    private IConfigurationElement _configurationElement;

    /**
     * Creates a new rule descriptor.
     *
     * @param id
     *            the id of the rule.
     * @param description
     *            the description of the rule.
     * @param states
     *            the {@code CursorState}s of this rule.
     * @param configurationElement
     *            the configuration element that declared this rule. May be
     *            <code>null</code> if this rule was not declared by an
     *            extension point.
     */
    RuleDescriptor(final String id, final String description,
            final Collection<CursorState> states,
            final IConfigurationElement configurationElement) {
        assert id != null;
        assert description != null;
        _id = id;
        _description = description;
        _states = new ArrayList<CursorState>(states);
        _configurationElement = configurationElement;
    }

    /**
     * Returns the configuration element that declared this rule.
     *
     * @return the configuration element that declared this rule, or
     *         <code>null</code> if this rule was not declared by an extension
     *         point.
     */
    IConfigurationElement configurationElement() {
        return _configurationElement;
    }

    /**
     * Returns an unmodifiable list of cursor states declared for the rule
     * described by this descriptor.
     *
     * @return an unmodifiable list of cursor states declared for the rule
     *         described by this descriptor.
     */
    public List<CursorState> cursorStates() {
        return Collections.unmodifiableList(_states);
    }

    /**
     * Returns the cursor state with the speicified id, or <code>null</code>
     * if the state is not defined for this rule.
     *
     * @param id
     *            the state id.
     * @return the cursor state, or <code>null</code> if the state does not
     *         exist.
     */
    CursorState state(final String id) {
        for (CursorState state : _states) {
            if (state.getId().equals(id)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Returns the id of the rule described by this descriptor.
     *
     * @return the id of the rule described by this descriptor.
     */
    public String getId() {
        return _id;
    }

    /**
     * Returns the description of the rule described by this descriptor.
     *
     * @return the description of the rule described by this descriptor.
     */
    public String getDescription() {
        return _description;
    }
}
