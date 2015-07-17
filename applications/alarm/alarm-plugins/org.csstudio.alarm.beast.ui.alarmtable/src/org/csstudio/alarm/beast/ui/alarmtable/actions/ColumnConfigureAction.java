package org.csstudio.alarm.beast.ui.alarmtable.actions;

import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.ColumnConfigurer;
import org.csstudio.alarm.beast.ui.alarmtable.ColumnWrapper;
import org.csstudio.alarm.beast.ui.alarmtable.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

/**
 * <code>ColumnConfigureAction</code> opens a column configuration dialog and allows
 * configuring whith column are displayed in the table and in which order.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class ColumnConfigureAction extends Action {
    private final AlarmTableView view;

    /**
     * Constructs a new action that acts on the given alarm table view.
     *
     * @param view the view to configure its columns
     */
    public ColumnConfigureAction(final AlarmTableView view) {
        super(Messages.ConfigureColumns);
        this.view = view;
    }

    @Override
    public void run() {
        ColumnWrapper[] columns = view.getUpdatedColumns();
        String timeFormat = view.getTimeFormat();
        ColumnConfigurer configurer = new ColumnConfigurer(view.getViewSite().getShell(), columns, timeFormat,
                true, false);
        if (configurer.open() == IDialogConstants.OK_ID) {
            view.setColumns(configurer.getColumns());
            view.setTimeFormat(configurer.getTimeFormat());
        }
    }
}