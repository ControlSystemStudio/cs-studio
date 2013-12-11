package org.csstudio.shift.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.csstudio.shift.util.ShiftSearchUtil;
import org.csstudio.ui.util.DelayedNotificator;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Joiner;

public class ShiftSearchDialog extends Dialog {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
	    changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
	    changeSupport.removePropertyChangeListener(listener);
    }

    // GUI components
    private Text searchString;
    private MultipleSelectionCombo<String> shiftCombo;
    private MultipleSelectionCombo<String> typeCombo;
    private Text textOwner;
    private Text textStatus;
    private Text textFrom;
    private Text textTo;

    // Model
    List<String> shifts = Collections.emptyList();
    List<String> types = Collections.emptyList();
    Map<String, String> searchParameters = new LinkedHashMap<String, String>();
 
    public ShiftSearchDialog(final Shell parentShell, final List<String> shifts, final List<String> types, final Map<String, String> initialSearchParameters) {
        super(parentShell);
        this.shifts = shifts;
        this.types = types;
        setBlockOnOpen(false);
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
        this.searchParameters = initialSearchParameters;
    }

    @Override
    public Control createDialogArea(final Composite parent) {
        getShell().setText("Advance Shift Search");
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));
        this.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
            if (event.getPropertyName().equals("searchParameters")) {
                initialize();
            }

            }
        });

        final Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("Search:");

        searchString = new Text(container, SWT.BORDER);
        searchString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        searchString.addModifyListener(new ModifyListener() {

            private DelayedNotificator notificator = new DelayedNotificator(
                250, TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(final ModifyEvent e) {
            	notificator.delayedExec(searchString, new Runnable() {

                @Override
                public void run() {
                	setSearchParameters(ShiftSearchUtil.parseSearchString(searchString.getText()));
                }
            });
            }
        });

        final Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        final Label lblText = new Label(container, SWT.NONE);
        lblText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblText.setText("Owner:");

        textOwner = new Text(container, SWT.BORDER);
        textOwner.addModifyListener(new ModifyListener() {

            private DelayedNotificator notificator = new DelayedNotificator(
                250, TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(ModifyEvent e) {
            notificator.delayedExec(textOwner, new Runnable() {

                @Override
                public void run() {
                searchParameters.put(ShiftSearchUtil.SEARCH_KEYWORD_OWNER, textOwner.getText());
                updateSearch();
                }
            });
            }
        });

        textOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//
        final Label lblStatus = new Label(container, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblStatus.setText("Status:");

        textStatus = new Text(container, SWT.BORDER);
        textStatus.addModifyListener(new ModifyListener() {

            private DelayedNotificator notificator = new DelayedNotificator(
                250, TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(ModifyEvent e) {
            notificator.delayedExec(textStatus, new Runnable() {

                @Override
                public void run() {
                searchParameters.put(ShiftSearchUtil.SEARCH_KEYWORD_STATUS, textStatus.getText());
                updateSearch();
                }
            });
            }
        });

        textStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));        
//
        final Label lbShifts = new Label(container, SWT.NONE);
        lbShifts.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lbShifts.setText("Shifts:");

        shiftCombo = new MultipleSelectionCombo<String>(container, SWT.NONE);
        shiftCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        shiftCombo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
            	searchParameters.put(ShiftSearchUtil.SEARCH_KEYWORD_SHIFTS,Joiner.on(",").join(shiftCombo.getSelection()));
            	updateSearch();
            }
        });
        final Label lbTypes = new Label(container, SWT.NONE);
        lbTypes.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lbTypes.setText("Type:");
        typeCombo = new MultipleSelectionCombo<String>(container, SWT.NONE);
        typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        typeCombo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
	            searchParameters.put(ShiftSearchUtil.SEARCH_KEYWORD_TYPE,Joiner.on(",").join(typeCombo.getSelection()));
	            updateSearch();
            }
        });


        final Label lblFrom = new Label(container, SWT.NONE);
        lblFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFrom.setText("From:");
        textFrom = new Text(container, SWT.BORDER);
        textFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textFrom.addModifyListener(new ModifyListener() {
            private DelayedNotificator notificator = new DelayedNotificator(250, TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(final ModifyEvent e) {
            	notificator.delayedExec(textFrom, new Runnable() {

                @Override
                public void run() {
	                searchParameters.put(ShiftSearchUtil.SEARCH_KEYWORD_START, textFrom.getText());
	                updateSearch();
                }
            });
            }
        });

        final Label lblTo = new Label(container, SWT.NONE);
        lblTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblTo.setText("To:");
        textTo = new Text(container, SWT.BORDER);
        textTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textTo.addModifyListener(new ModifyListener() {

            private DelayedNotificator notificator = new DelayedNotificator(250, TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(final ModifyEvent e) {
            	notificator.delayedExec(textTo, new Runnable() {

                @Override
                public void run() {
                	searchParameters.put(ShiftSearchUtil.SEARCH_KEYWORD_END, textTo.getText());
                	updateSearch();
                }
            });
            }
        });
        initialize();
        return container;

    }

    private void initialize() {
        Map<String, String> initSearchParameters = new HashMap<String,String>();
        initSearchParameters.putAll(searchParameters);
        shiftCombo.setItems(shifts);
        typeCombo.setItems(types);
        for (String keyword : initSearchParameters.keySet()) {
            if (ShiftSearchUtil.SEARCH_KEYWORD_TEXT.equals(keyword)) {
            	textOwner.setText(initSearchParameters.get(keyword));
            }
            if (ShiftSearchUtil.SEARCH_KEYWORD_SHIFTS.equals(keyword)) {
            	shiftCombo.setSelection(initSearchParameters.get(keyword));
            }
            if (ShiftSearchUtil.SEARCH_KEYWORD_TYPE.equals(keyword)) {
            	typeCombo.setSelection(initSearchParameters.get(keyword));
            }
            if (ShiftSearchUtil.SEARCH_KEYWORD_START.equals(keyword)) {
            	textFrom.setText(initSearchParameters.get(keyword));
            }
            if (ShiftSearchUtil.SEARCH_KEYWORD_END.equals(keyword)) {
            	textTo.setText(initSearchParameters.get(keyword));
            }
            if (ShiftSearchUtil.SEARCH_KEYWORD_OWNER.equals(keyword)) {
            	textOwner.setText(initSearchParameters.get(keyword));
            }
            if (ShiftSearchUtil.SEARCH_KEYWORD_STATUS.equals(keyword)) {
            	textStatus.setText(initSearchParameters.get(keyword));
            }
        }
    }

    protected void updateSearch() {
	    searchString.setText(ShiftSearchUtil.parseSearchMap(searchParameters));
    }


    /**
     * @param searchParameters
     *            the searchParameters to set
     */
    private synchronized void setSearchParameters(final Map<String, String> searchParameters) {
        final Map<String, String> OldValue = this.searchParameters;
        this.searchParameters = searchParameters;
        changeSupport.firePropertyChange("searchParameters", OldValue, this.searchParameters);
    }

    public String getSearchString() {
	    return ShiftSearchUtil.parseSearchMap(searchParameters);
    }

}
