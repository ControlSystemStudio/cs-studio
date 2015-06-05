package org.csstudio.alarm.beast.ui.alarmtable.actions;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;

/**
 * <code>ShowFilterAction</code> opens a dialog through which user can type in the name of the alarm tree item
 * to use for filtering.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ShowFilterAction extends Action {

    private final AlarmTableView view;

    public ShowFilterAction(AlarmTableView view) {
        super(Messages.SelectFilter, AS_PUSH_BUTTON);
        this.view = view;
    }

    @Override
    public void run() {
        final AlarmTreeRoot rootItem = view.getModel().getConfigTree().getRoot();
        String item = view.getFilterItemPath();
        InputDialog dialog = new InputDialog(view.getViewSite().getShell(),
                Messages.SelectFilterDialogTitle, Messages.SelectFilterDialogMessage,
                item, new IInputValidator(){
            @Override
            public String isValid(String newText) {
                if (newText == null || newText.isEmpty()) {
                    return null;
                }
                AlarmTreeItem item = rootItem.getItemByPath(newText);
                if (item == null) {
                    return NLS.bind(Messages.SelectFilterItemNonExisting, newText);
                }
                return null;
            }
        });

        if (dialog.open() == Window.OK) {
            String newValue = dialog.getValue();
            if (newValue == null || newValue.isEmpty() || newValue.equals(rootItem.getPathName())) {
                try {
                    view.setFilterItemPath(newValue);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot set the filter item path " + newValue, e); //$NON-NLS-1$
                }
            }
        }
    }
}
