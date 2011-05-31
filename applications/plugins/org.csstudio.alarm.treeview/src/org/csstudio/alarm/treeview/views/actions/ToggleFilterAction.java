/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview.views.actions;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Toggles the filter.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2010
 */
public final class ToggleFilterAction extends Action {
    private final AlarmTreeView _alarmTreeView;
    private final ViewerFilter _currentAlarmFilter;
    private final TreeViewer _viewer;

    /**
     * Constructor.
     * @param text
     * @param style
     * @param alarmTreeView
     * @param currentAlarmFilter
     * @param viewer
     */
    ToggleFilterAction(@Nonnull final String text,
                       @Nonnull final int style,
                       @Nonnull final AlarmTreeView alarmTreeView,
                       @Nonnull final ViewerFilter currentAlarmFilter,
                       @Nonnull final TreeViewer viewer) {
        super(text, style);
        _alarmTreeView = alarmTreeView;
        _currentAlarmFilter = currentAlarmFilter;
        _viewer = viewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (_alarmTreeView.getIsFilterActive()) {
            _viewer.removeFilter(_currentAlarmFilter);
            _alarmTreeView.setIsFilterActive(Boolean.FALSE);
        } else {
            _viewer.addFilter(_currentAlarmFilter);
            _alarmTreeView.setIsFilterActive(Boolean.TRUE);
        }
    }
}
