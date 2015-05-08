/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.sds.cursorservice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Stores the selected cursor for each cursor state declared by each cursor
 * selection rule.
 *
 * @author Joerg Rathlev
 */
public final class CursorSettings {

    /**
     * Internal map which stores the settings.
     */
    private Map<RuleDescriptor, Map<CursorState, AbstractCursor>> _settings;

    /**
     * Creates new cursor settings from the given collection of rules.
     *
     * @param rules
     *            the descriptors of the available cursor selection rules.
     */
    CursorSettings(final Collection<RuleDescriptor> rules) {
        assert rules != null;
        _settings = new HashMap<RuleDescriptor, Map<CursorState,AbstractCursor>>();
        for (RuleDescriptor rule : rules) {
            _settings.put(rule, new HashMap<CursorState, AbstractCursor>());
        }
    }

    /**
     * Creates a new cursor settings object with the same settings as the
     * specified settings.
     *
     * @param s
     *            the settings object whose settings are to be copied to these
     *            settings.
     */
    CursorSettings(final CursorSettings s) {
        assert s != null;
        _settings = new HashMap<RuleDescriptor, Map<CursorState, AbstractCursor>>(
                s._settings.size());
        for (RuleDescriptor rule : s._settings.keySet()) {
            _settings.put(rule, new HashMap<CursorState, AbstractCursor>(
                    s._settings.get(rule)));
        }
    }

    /**
     * Returns the cursor for the cursor state declared by the given cursor
     * selection rule.
     *
     * @param rule
     *            the cursor selection rule.
     * @param state
     *            the cursor state.
     * @return the cursor.
     */
    public AbstractCursor getCursor(final RuleDescriptor rule,
            final CursorState state) {
        AbstractCursor result = null;
        Map<CursorState, AbstractCursor> cursors = _settings.get(rule);
        if (cursors != null) {
            result = cursors.get(state);
        }
        return result;
    }

    /**
     * Sets the cursor for the given cursor state declared by the given cursor
     * selection rule.
     *
     * @param rule
     *            the cursor selection rule.
     * @param state
     *            the cursor state.
     * @param cursor
     *            the cursor for the state.
     */
    public void setCursor(final RuleDescriptor rule, final CursorState state,
            final AbstractCursor cursor) {
        Map<CursorState, AbstractCursor> cursors = _settings.get(rule);
        if (cursors == null) {
            // handle setting cursors for unknown rules gracefully
            cursors = new HashMap<CursorState, AbstractCursor>();
            _settings.put(rule, cursors);
        }
        cursors.put(state, cursor);
    }

}
