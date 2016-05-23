package org.csstudio.alarm.beast.history.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A UI to display {@link AlarmHistoryQueryParameters}.
 *
 * @author shroffk
 *
 */
public class AlarmHistoryQueryParametersWidget extends Composite {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private Text PVs;

    public AlarmHistoryQueryParametersWidget(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        Label lblPVs = new Label(this, SWT.NONE);
        lblPVs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        lblPVs.setText("PVs:");

        PVs = new Text(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        PVs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        PVs.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent evt) {
                if (PVs.getText().isEmpty()) {
                    pvNames = Collections.emptyList();
                } else {
                    pvNames = Arrays.asList(PVs.getText().split("\\r?\\n")).stream().map(e -> new ProcessVariable(e))
                            .collect(Collectors.toList());
                }
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    private List<ProcessVariable> pvNames = new ArrayList<ProcessVariable>();

    public void setPVs(List<ProcessVariable> list) {
        List<String> names = list.stream().map(ProcessVariable::getName).collect(Collectors.toList());
        PVs.setText(String.join("\n", names));
    }

    public List<ProcessVariable> getPVs() {
        return pvNames;
    }

}
