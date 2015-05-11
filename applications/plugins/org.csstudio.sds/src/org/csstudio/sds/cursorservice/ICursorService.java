package org.csstudio.sds.cursorservice;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;

public interface ICursorService {

    /**
     * The name of the project, where cursor images are searched.
     */
    public static final String CURSORS_PROJECT_NAME = "SDS Cursors";
    /**
     * The maximum number of cursor states that a selection rule can declare.
     */
    public static final int MAX_CURSOR_STATES = 10;
    /**
     * Cursor instance representing the system default cursor.
     */
    public static final AbstractCursor SYSTEM_DEFAULT_CURSOR = new AbstractCursor(
            "cursor.default", "System Defaut") {
    };
    /**
     * ID of the default cursor selection rule.
     */
    public static final String DEFAULT_RULE_ID = "cursor.strategy.default";

    /**
     * Returns a list of the available cursors.
     *
     * @return a list of the available cursors.
     */
    List<AbstractCursor> availableCursors();

    /**
     * Returns an unmodifiable list of the available cursor selection rules.
     *
     * @return an unmodifiable list of rule descriptors for the available cursor
     *         selection rules.
     */
    List<RuleDescriptor> availableRules();

    /**
     * Applies the cursor to the given widget based on the selection rule
     * configured by the user.
     *
     * @param widget
     *            the widget.
     */
    void applyCursor(final AbstractWidgetModel widget);

    /**
     * Returns the preferred rule.
     *
     * @return a descriptor of the preferred rule.
     */
    RuleDescriptor getPreferredRule();

    /**
     * Sets the preferred cursor selection rule. The preferred rule is stored in
     * the preference store.
     *
     * @param rule
     *            the preferred rule.
     */
    void setPreferredRule(final RuleDescriptor rule);

    /**
     * Find the cursor with the specified id.
     *
     * @param id
     *            the id.
     * @return a cursor, or <code>null</code> if none was found for that id.
     */
    AbstractCursor findCursor(final String id);

    /**
     * Returns the current cursor preferences. Changes in the returned object
     * are not immediately reflected in the preference store. Use the
     * {@link #setPreferences(CursorSettings)} method to change the stored
     * preferences.
     *
     * @return the current cursor settings from the preferences.
     */
    CursorSettings getPreferences();

    /**
     * Sets the cursor preferences to the specified settings.
     *
     * @param settings
     *            the settings.
     */
    void setPreferences(final CursorSettings settings);

}