package org.csstudio.alarm.beast.ui.alarmtable.actions;

import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.FilterType;
import org.csstudio.alarm.beast.ui.alarmtable.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * <code>FilterAction</code> toggles the filter type on the alarm table view. It allows to switch between
 * showing the alarms belonging to the root selected in the alarm tree, to the arbitrary root, or to a specific
 * item in an arbitrary root.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FilterAction extends Action implements IPropertyListener {
    private final AlarmTableView view;
    private final FilterType filter;

    /**
     * Construct a new action.
     *
     * @param view the view to act on
     * @param filter the filter which this button enables
     * @param selected the default (selection) value for this action
     */
    public FilterAction(AlarmTableView view, FilterType filter, boolean selected) {
        super(null, AS_RADIO_BUTTON);
        this.view = view;
        this.filter = filter;
        setChecked(selected);
        switch (filter) {
            case TREE:
                setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/sync.gif")); //$NON-NLS-1$
                break;
            case ROOT:
                setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/root.gif")); //$NON-NLS-1$
                setEnabled(view.getFilterItemPath() != null);
                break;
            case ITEM:
                setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/item.gif")); //$NON-NLS-1$
                setEnabled(view.getFilterItemPath() != null);
                break;
        }
        this.view.addPropertyListener(this);
        updateToolTip();
    }

    @Override
    public void propertyChanged(Object source, int propId) {
        if (AlarmTableView.PROP_FILTER == propId) {
            FilterType selectedFilter = view.getFilterType();
            setChecked(this.filter == selectedFilter);
        } else if (AlarmTableView.PROP_FILTER_ITEM == propId) {
            //ROOT and ITEM filters are enable only when a filter item is set on the table
            if (view.getFilterItemPath() == null) {
                setEnabled(filter == FilterType.TREE);
            } else {
                setEnabled(true);
            }
            updateToolTip();
        }
    }

    private void updateToolTip() {
        switch (filter) {
            case TREE:
                setToolTipText(Messages.FilterTreeTT);
                break;
            case ROOT:
                String path = view.getFilterItemPath();
                if (path == null)
                    setToolTipText(NLS.bind(Messages.FilterRootTT,"N/A")); //$NON-NLS-1$
                else
                    setToolTipText(NLS.bind(Messages.FilterRootTT,AlarmTableView.getConfigNameFromPath(path)));
                break;
            case ITEM:
                String path2 = view.getFilterItemPath();
                setToolTipText(NLS.bind(Messages.FilterItemTT, path2 == null ? "N/A" : path2)); //$NON-NLS-1$
                break;
        }
    }


    @Override
    public void run() {
        view.setFilterType(filter);
    }
}
