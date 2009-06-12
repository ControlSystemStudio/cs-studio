package org.csstudio.alarm.table.ui;

import java.util.ArrayList;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CopyOfFilterSettingDialog extends Dialog {

    private final int FILTER_SETTING_SIZE = 8;
    private final Filter _filter;
    private final ArrayList<String> _settingHistory;
    private final String[][] _messageProperties;
    private Text[] _patternText;
    private Combo[] _propertyCombo;
    private Combo[] _conjunctionCombo;

    public CopyOfFilterSettingDialog(Shell parentShell, Filter filter,
            ArrayList<String> settingHistory, String[][] messageProperties) {
        super(parentShell);
        _filter = filter;
        _settingHistory = settingHistory;
        _messageProperties = messageProperties;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        final Composite area = (Composite) super.createDialogArea(parent);

        final Composite box = new Composite(area, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        box.setLayout(layout);
        GridData gd;

        // Property: ____property____ Value: ___value___
        Label l;
        _patternText = new Text[FILTER_SETTING_SIZE];
        _propertyCombo = new Combo[FILTER_SETTING_SIZE];
        _conjunctionCombo = new Combo[FILTER_SETTING_SIZE];
        for (int i = 0; i < _propertyCombo.length; ++i) {
            if (i > 0) { // new row
                _conjunctionCombo[i] = new Combo(box, SWT.DROP_DOWN
                        | SWT.READ_ONLY);
                _conjunctionCombo[i].add("AND");
                _conjunctionCombo[i].add("OR");
                _conjunctionCombo[i].select(0);
                gd = new GridData();
                gd.horizontalSpan = 2;
                gd.grabExcessHorizontalSpace = true;
                gd.horizontalAlignment = SWT.RIGHT;
                _conjunctionCombo[i].setLayoutData(gd);
                gd = new GridData();
                Label dummy = new Label(box, SWT.NONE);
                dummy.setLayoutData(gd);
                dummy = new Label(box, SWT.NONE);
                dummy.setLayoutData(gd);
            }
            l = new Label(box, 0);
            l.setText("Property");
            l.setLayoutData(new GridData());

            _propertyCombo[i] = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
            _propertyCombo[i].setToolTipText("List of message properties");
            _propertyCombo[i].setVisibleItemCount(25);
            for (String[] prop : _messageProperties)
                _propertyCombo[i].add(prop[1]);
            _propertyCombo[i].setLayoutData(new GridData(SWT.FILL, 0, true,
                    false));
            _propertyCombo[i].select(0);

            l = new Label(box, 0);
            l.setText("Pattern");
            l.setLayoutData(new GridData());

            _patternText[i] = new Text(box, SWT.BORDER);
            _patternText[i].setToolTipText("Pattern to search for");
            _patternText[i]
                    .setLayoutData(new GridData(SWT.FILL, 0, true, false));
        }

        setCurrentSettings();

        return parent;

    }

    private void setCurrentSettings() {
        if (_filter == null) {
            return;
        } else if (_filter.getFilterItems() == null) {
            return;
        }
        ArrayList<FilterItem> filterItems = _filter.getFilterItems();
        String[] items = _propertyCombo[0].getItems();
        int j = 0;
        for (FilterItem filterItem : filterItems) {
            String property = filterItem.getProperty();
            for (int i = 0; i < items.length; i++) {
                if (_propertyCombo[j].getItem(i).equalsIgnoreCase(property)) {
                    _propertyCombo[j].select(i);
                    break;
                }
            }
            _patternText[j].setText(filterItem.getValue());
            if (j < FILTER_SETTING_SIZE) {
                if (_conjunctionCombo[j + 1].getItem(0)
                        .equalsIgnoreCase(filterItem.getRelation())) {
                    _conjunctionCombo[j + 1].select(0);
                } else {
                    _conjunctionCombo[j + 1].select(1);
                }
            }
            j++;
        }
    }

    /** Memorize entered/selected data */
    @Override
    protected void okPressed() {
        if (_filter != null) {
            _filter.clearFilter();
            for (int i = 0; i < FILTER_SETTING_SIZE; i++) {
                if (_patternText[i].getText().length() > 0) {
                    FilterItem filterItem = new FilterItem(_propertyCombo[i]
                            .getItem((_propertyCombo[i].getSelectionIndex())),
                            _patternText[i].getText(), _conjunctionCombo[i + 1]
                                    .getItem((_conjunctionCombo[i + 1]
                                            .getSelectionIndex())));
                    _filter.setFilterItem(filterItem);
                }
            }
        }
        if (_settingHistory != null) {
            for (Text element : _patternText) {
                if (element.getText().length() > 0) {
                    _settingHistory.add(element.getText());
                }
            }
        }
        super.okPressed();
    }
}
