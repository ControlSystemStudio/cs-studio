package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

/** Action to allow adding/removing/ordering of table columns
 *
 *  @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
class ColumnConfigureAction extends Action
{
    private final AlarmTableView view;

    public ColumnConfigureAction(final AlarmTableView view)
    {
        super(Messages.ConfigureColumns);
        this.view = view;
    }

    @Override
    public void run()
    {
        ColumnWrapper[] columns = ColumnWrapper.getCopy(view.columns);
        ColumnConfigurer configurer = new ColumnConfigurer(view.getViewSite().getShell(), columns);
        if (configurer.open() == IDialogConstants.OK_ID)
        {
            columns = configurer.getColumns();
            view.setColumns(columns);
            //redoTheGUI
        }
    }
}