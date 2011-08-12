package org.csstudio.alarm.table.ui;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterSettingDialog extends StartEndDialog {
    
    private static final Logger LOG = LoggerFactory.getLogger(FilterSettingDialog.class);
    
    private final int FILTER_SETTING_SIZE = 8;
    private Filter _filter;
    private final ArrayList<String> _settingHistory;
    private final String[][] _messageProperties;
    private Combo[] _patternCombo;
    private Combo[] _propertyCombo;
    private Combo[] _conjunctionCombo;
    private final StoredFilters _storedFilters;
    private Combo _storedFiltersCombo;
    
    public FilterSettingDialog(Shell parentShell,
                               Filter filter,
                               StoredFilters storedFilters,
                               ArrayList<String> settingHistory,
                               String[][] messageProperties,
                               String timeFrom,
                               String timeTo) {
        super(parentShell, timeFrom, timeTo);
        _filter = filter;
        _storedFilters = storedFilters;
        _settingHistory = settingHistory;
        _messageProperties = messageProperties;
    }
    
    @Override
    @Nonnull
    protected Control createDialogArea(@Nonnull final Composite parent) {
        
        final Composite area = (Composite) super.createDialogArea(parent);
        
        final Group box = new Group(area, 0);
        box.setText("Filter Conditions");
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        box.setLayout(layout);
        GridData gd;
        Label explanation = new Label(box, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 4;
        explanation.setLayoutData(gridData);
        explanation.setText("Die Suche ignoriert Gross-/Kleinschreibung. Als Wildcard"
                + " koennen '*' fuer beliebig viele Zeichen\n und '?' für genau "
                + "ein Zeichen verwendet werden.");
        // Property: ____property____ Value: ___value___
        Label l;
        _patternCombo = new Combo[FILTER_SETTING_SIZE];
        _propertyCombo = new Combo[FILTER_SETTING_SIZE];
        _conjunctionCombo = new Combo[FILTER_SETTING_SIZE];
        for (int i = 0; i < _propertyCombo.length; ++i) {
            if(i > 0) { // new row
                _conjunctionCombo[i] = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
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
            _propertyCombo[i].setLayoutData(new GridData(SWT.FILL, 0, true, false));
            _propertyCombo[i].select(0);
            
            l = new Label(box, 0);
            l.setText("Pattern");
            l.setLayoutData(new GridData());
            
            _patternCombo[i] = new Combo(box, SWT.DROP_DOWN);
            _patternCombo[i].add("");
            for (String pattern : _settingHistory)
                _patternCombo[i].add(pattern);
            _patternCombo[i].setVisibleItemCount(20);
            _patternCombo[i].setToolTipText("Pattern to search for");
            _patternCombo[i].setLayoutData(new GridData(SWT.FILL, 0, true, false));
        }
        
        Label label = new Label(box, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
        
        addStoredFilterItems(box);
        setCurrentSettings();
        
        //if filter name is available set it to comboBox
        if(_filter.getName() != null) {
            for (int i = 0; i < _storedFiltersCombo.getItemCount(); i++) {
                String item = _storedFiltersCombo.getItem(i);
                if(item.equals(_filter.getName())) {
                    _storedFiltersCombo.select(i);
                    break;
                }
            }
        }
        return parent;
    }
    
    /**
     * Add combo box for stored filters and save button to filter dialog.
     * 
     * @param box 
     */
    private void addStoredFilterItems(Group box) {
        
        Composite storedFilterComposite = new Composite(box, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 4;
        storedFilterComposite.setLayoutData(gridData);
        
        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        storedFilterComposite.setLayout(layout);
        
        Label storeLabel = new Label(storedFilterComposite, SWT.NONE);
        storeLabel.setText("Filterauswahl, zum speichern Namen\nvergeben und 'Save' druecken.");
        
        //Combobox for stored filters and listener to set the selected filter.
        _storedFiltersCombo = new Combo(storedFilterComposite, SWT.DROP_DOWN);
        _storedFiltersCombo.setLayoutData(new GridData(150, 60));
        _storedFiltersCombo.setSize(280, 20);
        for (Filter filter : _storedFilters.getFilterList()) {
            _storedFiltersCombo.add(filter.getName());
        }
        _storedFiltersCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String selectedFilterName = _storedFiltersCombo.getText();
                Filter filterCopy = _storedFilters.getCopyOfFilter(selectedFilterName);
                if(filterCopy != null) {
                    _filter = filterCopy;
                }
                setDefaultSettings();
                setCurrentSettings();
            }
        });
        addSaveButton(storedFilterComposite);
        addDeleteButton(storedFilterComposite);
    }
    
    private void addDeleteButton(Composite storedFilterComposite) {
        Button deleteButton = new Button(storedFilterComposite, SWT.PUSH);
        deleteButton.setText("Delete");
        deleteButton.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                readFilterItems();
                //Because the user can edit the filter after selection, it is not sufficient
                //to check just the filter name, before deletion.
                boolean isNotChanged = false;
                for (Filter filter : _storedFilters.getFilterList()) {
                    if(_filter.compareWithoutTime(filter)) {
                        isNotChanged = true;
                        break;
                    }
                }
                
                if(isNotChanged) {
                    for (int i = 0; i < _storedFiltersCombo.getItemCount(); i++) {
                        String item = _storedFiltersCombo.getItem(i);
                        if(item.equals(_filter.getName())) {
                            _storedFiltersCombo.remove(i);
                            _storedFilters.removeFilterByName(_filter.getName());
                            //set first filter
                            _storedFiltersCombo.select(0);
                            setDefaultSettings();
                            if(_storedFiltersCombo.getItemCount() > 0) {
                                String newFilterName = _storedFiltersCombo.getItem(0);
                                Filter filterCopy = _storedFilters.getCopyOfFilter(newFilterName);
                                if(filterCopy != null) {
                                    _filter = filterCopy;
                                    setCurrentSettings();
                                }
                                break;
                            } else {
                                _storedFiltersCombo.setText("");
                            }
                        }
                    }
                }
            }
        });
        
    }
    
    private void addSaveButton(Composite storedFilterComposite) {
        Button saveButton = new Button(storedFilterComposite, SWT.PUSH);
        saveButton.setText("Save");
        saveButton.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                readFilterItems();
                Filter filterCopy = _filter.copy();
                filterCopy.setName(_storedFiltersCombo.getText());
                //the current time range should not be stored.
                filterCopy.setFrom(null);
                filterCopy.setTo(null);
                addNewFilter(filterCopy);
                _storedFiltersCombo.add(filterCopy.getName());
            }
            
            /**
             * Check weather the name of new filter is already in the list and delete it.
             */
            private void addNewFilter(@Nonnull Filter filterCopy) {
                for (Filter filter : _storedFilters.getFilterList()) {
                    if(filter.getName().equals(filterCopy.getName())) {
                        _storedFilters.removeFilterByName(filter.getName());
                    }
                }
                _storedFilters.addCopyOfFilter(filterCopy);
            }
        });
    }
    
    private void readFilterItems() {
        if(_filter != null) {
            _filter.clearFilter();
            for (int i = 0; i < FILTER_SETTING_SIZE; i++) {
                if(_patternCombo[i].getText().length() > 0) {
                    String relation;
                    if(i < (FILTER_SETTING_SIZE - 1)) {
                        relation = _conjunctionCombo[i + 1].getItem( (_conjunctionCombo[i + 1]
                                .getSelectionIndex()));
                    } else {
                        relation = "END";
                    }
                    FilterItem filterItem = new FilterItem(_propertyCombo[i].getItem( (_propertyCombo[i]
                                                                   .getSelectionIndex())),
                                                           _patternCombo[i].getText(),
                                                           relation);
                    _filter.setFilterItem(filterItem);
                }
            }
        }
        if(_settingHistory != null) {
            for (Combo element : _patternCombo) {
                String comboText = element.getText();
                if(comboText.length() > 0) {
                    boolean inHistory = false;
                    for (String historyElement : _settingHistory) {
                        if(historyElement.equals(comboText)) {
                            inHistory = true;
                            break;
                        }
                    }
                    if(!inHistory) {
                        _settingHistory.add(0, comboText);
                    }
                }
            }
        }
        if(_settingHistory != null) {
            while (_settingHistory.size() > 20) {
                try {
                    _settingHistory.remove(20);
                } catch (IndexOutOfBoundsException e) {
                    LOG.error("list of filter items not larger than threshold", e);
                }
            }
        }
    }
    
    /**
     * Set settings from the current filter to ui filter item elements.
     */
    private void setCurrentSettings() {
        if(_filter == null) {
            return;
        } else if(_filter.getFilterItems() == null) {
            return;
        }
        ArrayList<FilterItem> filterItems = _filter.getFilterItems();
        String[] propertyItems = _propertyCombo[0].getItems();
        String[] patternItems = _patternCombo[0].getItems();
        int j = 0;
        for (FilterItem filterItem : filterItems) {
            String property = filterItem.getProperty();
            for (int i = 0; i < propertyItems.length; i++) {
                if(_propertyCombo[j].getItem(i).equalsIgnoreCase(property)) {
                    _propertyCombo[j].select(i);
                    break;
                }
            }
            String pattern = filterItem.getOriginalValue();
            boolean patternInHistory = false;
            for (int i = 0; i < patternItems.length; i++) {
                String item = null;
                try {
                    item = _patternCombo[j].getItem(i);
                    if(item.equalsIgnoreCase(pattern)) {
                        _patternCombo[j].select(i);
                        patternInHistory = true;
                        break;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(!patternInHistory) {
                //Because filter items can be saved and set from preferences the new pattern has
                //to be set for all combo boxes at once.
                setPatternToAllCombos(pattern);
                //select the new (last) item
                _patternCombo[j].select( (_patternCombo[j].getItems().length - 1));
            }
            if(j < (FILTER_SETTING_SIZE - 1)) {
                if(_conjunctionCombo[j + 1].getItem(1).equalsIgnoreCase(filterItem.getRelation())) {
                    _conjunctionCombo[j + 1].select(1);
                } else {
                    _conjunctionCombo[j + 1].select(0);
                }
            }
            j++;
        }
    }
    
    /**
     * Set new pattern to all combo boxes
     * @param pattern
     */
    private void setPatternToAllCombos(String pattern) {
        for (Combo comboBox : _patternCombo) {
            comboBox.add(pattern);
        }
    }
    
    /**
     * Set all ui filter items to default.
     */
    private void setDefaultSettings() {
        for (int i = 0; i < FILTER_SETTING_SIZE; i++) {
            _propertyCombo[i].select(0);
            _patternCombo[i].select(0);
            if(i < FILTER_SETTING_SIZE - 1) {
                _conjunctionCombo[i + 1].select(0);
            }
        }
    }
    
    /** Memorize entered/selected data */
    @Override
    protected void okPressed() {
        readFilterItems();
        super.okPressed();
    }
}
