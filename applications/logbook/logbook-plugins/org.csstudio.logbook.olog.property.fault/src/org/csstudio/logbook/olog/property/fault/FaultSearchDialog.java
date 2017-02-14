/**
 *
 */
package org.csstudio.logbook.olog.property.fault;

import static org.csstudio.logbook.util.LogEntrySearchUtil.parseSearchMap;
import static org.csstudio.logbook.util.LogEntrySearchUtil.parseSearchString;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.csstudio.logbook.olog.property.fault.Fault.BeamLossState;
import org.csstudio.logbook.ui.util.UpdateLogEntryBuilder;
import org.csstudio.ui.util.DelayedNotificator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

/**
 *
 * A dialog box listing logbook, tags, start/end times and other search
 * conditions for log entries
 *
 * @author shroffk
 *
 */
public class FaultSearchDialog extends Dialog {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    private static final String FAULTDESC = "search";
    private static final String FAULTAREA = "fault.Area";
    private static final String FAULTSYSTEM = "fault.System";
    private static final String FAULTDEVICE = "fault.Device";
    private static final String FAULTOWNER = "fault.Assign";
    private static final String FAULTBEAMSTATE = "fault.BeamState";

    // GUI components
    private Text searchString;
    private CCombo areaCombo;
    private CCombo systemCombo;
    private Text text;

    // Model
    Map<String, String> searchParameters = new LinkedHashMap<String, String>();
    private CCombo deviceCombo;
    private CCombo ownerCombo;
    private CCombo beamStateCombo;
    
    private FaultConfiguration fc;

    public FaultSearchDialog(Shell parentShell, FaultConfiguration fc) {
        super(parentShell);
        this.fc = fc;
        setBlockOnOpen(false);
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
    }

    @Override
    public Control createDialogArea(Composite parent) {
        getShell().setText("Fault Search");
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("Search:");

        searchString = new Text(container, SWT.BORDER);
        searchString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Label lblText = new Label(container, SWT.NONE);
        lblText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblText.setText("Text:");

        text = new Text(container, SWT.BORDER);
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                searchParameters.put(FAULTDESC, "*"+text.getText()+"*");
                updateSearchString();
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblArea = new Label(container, SWT.NONE);
        lblArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblArea.setText("Area:");

        areaCombo = new CCombo(container, SWT.BORDER);
        areaCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchParameters.put(FAULTAREA, areaCombo.getItem(areaCombo.getSelectionIndex()));
                updateSearchString();
            }
        });
        areaCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        areaCombo.setItems(fc.getAreas().toArray(new String[fc.getAreas().size()]));

        Label lblSystem = new Label(container, SWT.NONE);
        lblSystem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblSystem.setText("System:");

        systemCombo = new CCombo(container, SWT.BORDER);
        systemCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchParameters.put(FAULTSYSTEM, systemCombo.getItem(systemCombo.getSelectionIndex()));
                updateSearchString();
            }
        });
        systemCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        systemCombo.setItems(fc.getSubsystems().toArray(new String[fc.getSubsystems().size()]));
        
        Label lblDevice = new Label(container, SWT.NONE);
        lblDevice.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDevice.setText("Device:");

        deviceCombo = new CCombo(container, SWT.BORDER);
        deviceCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchParameters.put(FAULTDEVICE, deviceCombo.getItem(deviceCombo.getSelectionIndex()));
                updateSearchString();
            }
        });
        deviceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        deviceCombo.setItems(fc.getDevices().toArray(new String[fc.getDevices().size()]));
        
        Label lblOwner = new Label(container, SWT.NONE);
        lblOwner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblOwner.setText("Owner:");

        ownerCombo = new CCombo(container, SWT.BORDER);
        ownerCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchParameters.put(FAULTOWNER, ownerCombo.getItem(ownerCombo.getSelectionIndex()));
                updateSearchString();
            }
        });
        ownerCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ownerCombo.setItems(fc.getGroups().stream().map(FaultConfiguration.Group::getName).toArray(String[]::new));

        Label lblBeamState = new Label(container, SWT.NONE);
        lblBeamState.setText("BeamState:");

        beamStateCombo = new CCombo(container, SWT.BORDER);
        beamStateCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchParameters.put(FAULTBEAMSTATE, beamStateCombo.getItem(beamStateCombo.getSelectionIndex()));
                updateSearchString();
            }
        });
        beamStateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        beamStateCombo.setItems(
                Arrays.asList(BeamLossState.values()).stream().map(BeamLossState::toString).toArray(String[]::new));
        return container;
    }

    /**
     * @return the searchParameters
     */
    public synchronized Map<String, String> getSearchParameters() {
        return searchParameters;
    }

    private void updateSearchString() {
        String newSearchText = getSearchParameters().entrySet().stream().filter(Objects::nonNull).map(e -> {
            return e.toString().replace("=", ":");
        }).collect(Collectors.joining(" "));
        Display.getDefault().asyncExec(() -> {
            searchString.setText(newSearchText);
        });
    }
}
