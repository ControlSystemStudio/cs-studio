package org.csstudio.alarm.beast.ui.alarmtable.actions;

import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * <code>NewTableAction</code> opens a new Alarm Table View.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class NewTableAction extends Action {
    private final AlarmTableView view;

    /**
     * Construct a new action.
     *
     * @param view the view that owns this action
     */
    public NewTableAction(final AlarmTableView view) {
        super(Messages.NewTableView);
        this.view = view;
    }

    @Override
    public void run() {
        try {
            view.getViewSite().getPage().showView(view.getViewSite().getId(),
                    AlarmTableView.newSecondaryID(view), IWorkbenchPage.VIEW_CREATE);
        } catch (PartInitException e) {
            MessageDialog.openError(view.getViewSite().getShell(), Messages.AlarmTableOpenErrorTitle,
                    NLS.bind(Messages.AlarmTableOpenErrorMessage, e.getMessage()));
        }
    }
}
