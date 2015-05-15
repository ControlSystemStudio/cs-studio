/**
 *
 */
package org.csstudio.opibuilder.properties;

import static org.csstudio.opibuilder.properties.ServiceMethodDescription.createServiceMethodDescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceRegistry;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author shroffk
 *
 */
public class ServiceMethodWidget extends BeanComposite {

    private Text text_method;
    private Text text_arg_prefix;
    private Text text_result_prefix;
    private Label lblServiceMethodDescription;
    private Color initialForegroundColor;

    private Table argumentPvTable;
    private Table resultPvTable;
    private TableViewer argumentPvTableViewer;
    private TableViewer resultPvTableViewer;
    private Composite resultPvTableViewerComposite;
    private Composite argumentPvTableViewerComposite;

    private final String DEFAULT_PREFIX = "loc://${DID}";
    // Model
    private Map<String, Service> services;

    private String argumentPrefix;
    private boolean useArgumentPrefix;
    private String resultPrefix;
    private boolean useResultPrefix;
    private ServiceMethodDescription serviceMethodDescription;

    public ServiceMethodWidget(Composite parent, int style, ServiceMethodDescription serviceMethodDescription) {
    super(parent, style);
    setLayout(new FormLayout());

    Label lblMethodName = new Label(this, SWT.NONE);
    FormData fd_lblMethodName = new FormData();
    fd_lblMethodName.top = new FormAttachment(0, 10);
    fd_lblMethodName.left = new FormAttachment(0, 5);
    lblMethodName.setLayoutData(fd_lblMethodName);
    lblMethodName.setText("Method Name:");

    Button btnNewButton = new Button(this, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        ServiceTreeDialog serviceTreeDialog = new ServiceTreeDialog(
            getShell());
        if (serviceTreeDialog.open() == Window.OK) {
            init(serviceTreeDialog.getSelectedServiceMethodDescription().getService(),
             serviceTreeDialog.getSelectedServiceMethodDescription().getMethod());
        }
        }
    });
    FormData fd_btnNewButton = new FormData();
    fd_btnNewButton.top = new FormAttachment(0, 5);
    fd_btnNewButton.right = new FormAttachment(100, -5);
    btnNewButton.setLayoutData(fd_btnNewButton);
    btnNewButton.setText("Search");

    text_method = new Text(this, SWT.BORDER);
    initialForegroundColor = text_method.getForeground();
    FormData fd_text_method = new FormData();
    fd_text_method.right = new FormAttachment(btnNewButton, -5);
    fd_text_method.left = new FormAttachment(lblMethodName, 5);
    fd_text_method.top = new FormAttachment(0, 7);
    text_method.setLayoutData(fd_text_method);
    text_method.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.CR) {
            if(text_method.getText().split("/").length == 2){
            init(text_method.getText().split("/")[0], text_method.getText().split("/")[1]);
            }
        }
        }
    });

    lblServiceMethodDescription = new Label(this, SWT.NONE);
    FormData fd_lblServiceMethodDescription = new FormData();
    fd_lblServiceMethodDescription.right = new FormAttachment(100, -5);
    fd_lblServiceMethodDescription.top = new FormAttachment(0, 40);
    fd_lblServiceMethodDescription.left = new FormAttachment(0, 5);
    lblServiceMethodDescription
        .setLayoutData(fd_lblServiceMethodDescription);

    Label lblNewLabel_1 = new Label(this, SWT.NONE);
    FormData fd_lblNewLabel_1 = new FormData();
    fd_lblNewLabel_1.top = new FormAttachment(0, 65);
    fd_lblNewLabel_1.left = new FormAttachment(0, 5);
    lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
    lblNewLabel_1.setText("Arguments:");

    Label lblNewLabel_2 = new Label(this, SWT.NONE);
    FormData fd_lblNewLabel_2 = new FormData();
    fd_lblNewLabel_2.top = new FormAttachment(0, 93);
    fd_lblNewLabel_2.left = new FormAttachment(0, 5);
    lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
    lblNewLabel_2.setText("Argument Prefix:");

    text_arg_prefix = new Text(this, SWT.BORDER);
    text_arg_prefix.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseUp(MouseEvent e) {
        if (!useArgumentPrefix) {
            useArgumentPrefix = true;
            text_arg_prefix.setForeground(initialForegroundColor);
        }
        }
    });
    FormData fd_text_arg_prefix = new FormData();
    fd_text_arg_prefix.right = new FormAttachment(100, -5);
    fd_text_arg_prefix.top = new FormAttachment(0, 90);
    fd_text_arg_prefix.left = new FormAttachment(0, 120);
    text_arg_prefix.setLayoutData(fd_text_arg_prefix);
    text_arg_prefix.setEnabled(false);
    text_arg_prefix.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
        // argument prefix set
        if (e.keyCode == SWT.CR) {
            argumentPrefix = text_arg_prefix.getText();
            setArgumentPvs(calculateArgumentPvs(getServiceMethodDescription().getArgumentPvs().keySet(), argumentPrefix));
        }
        }
    });

    resultPvTableViewerComposite = new Composite(this, SWT.NONE);
    FormData fd_composite_1 = new FormData();
    fd_composite_1.bottom = new FormAttachment(100, -5);
    fd_composite_1.right = new FormAttachment(100, -5);
    fd_composite_1.left = new FormAttachment(0, 5);
    resultPvTableViewerComposite.setLayoutData(fd_composite_1);
    TableColumnLayout tcl_composite_1 = new TableColumnLayout();
    resultPvTableViewerComposite.setLayout(tcl_composite_1);

    resultPvTableViewer = new TableViewer(resultPvTableViewerComposite,
        SWT.BORDER | SWT.FULL_SELECTION);
    resultPvTable = resultPvTableViewer.getTable();
    resultPvTable.setHeaderVisible(true);
    resultPvTable.setLinesVisible(true);

    TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
        resultPvTableViewer, SWT.NONE);
    tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element != null && element instanceof Entry) {
            return ((Entry<String, String>) element).getKey();
        }
        return "";
        }
    });
    TableColumn tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
    tcl_composite_1.setColumnData(tblclmnNewColumn_2, new ColumnWeightData(
        50, 100, true));
    tblclmnNewColumn_2.setText("result");

    TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
        resultPvTableViewer, SWT.NONE);
    tableViewerColumn_3.setEditingSupport(new EditingSupport(
        resultPvTableViewer) {
        protected boolean canEdit(Object element) {
        return true;
        }

        protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(resultPvTableViewer.getTable());
        }

        @SuppressWarnings("unchecked")
        protected Object getValue(Object element) {
        return ((Entry<String, String>) element).getValue();
        }

        @SuppressWarnings("unchecked")
        protected void setValue(Object element, Object value) {
        setResultPv(
            ((Entry<String, String>) element).getKey(),
            (String) value);
        }

    });
    tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element != null && element instanceof Entry) {
            return ((Entry<String, String>) element).getValue();
        }
        return "";
        }
    });
    TableColumn tblclmnNewColumn_3 = tableViewerColumn_3.getColumn();
    tcl_composite_1.setColumnData(tblclmnNewColumn_3, new ColumnWeightData(
        50, 100, true));
    tblclmnNewColumn_3.setText("pv/formula");
    resultPvTableViewer.setContentProvider(new ArrayContentProvider());

    text_result_prefix = new Text(this, SWT.BORDER);
    text_result_prefix.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseUp(MouseEvent e) {
            if(!useResultPrefix){
            useResultPrefix = true;
            text_result_prefix.setForeground(initialForegroundColor);
            }
        }
    });
    FormData fd_text_result_prefix = new FormData();
    fd_text_result_prefix.bottom = new FormAttachment(
        resultPvTableViewerComposite, -5);
    fd_text_result_prefix.right = new FormAttachment(100, -5);
    fd_text_result_prefix.left = new FormAttachment(0, 120);
    text_result_prefix.setLayoutData(fd_text_result_prefix);
    text_result_prefix.setEnabled(false);
    text_result_prefix.addKeyListener(new KeyAdapter() {

        @Override
        public void keyReleased(KeyEvent e) {
        // result prefix
        if (e.keyCode == SWT.CR) {
            resultPrefix = text_result_prefix.getText();
            setResultPvs(calculateResultPvs(getServiceMethodDescription().getResultPvs().keySet(), resultPrefix));
        }
        }
    });

    Label lblResultPrefix = new Label(this, SWT.NONE);
    FormData fd_lblResultPrefix = new FormData();
    fd_lblResultPrefix.top = new FormAttachment(text_result_prefix, 3,
        SWT.CENTER);
    fd_lblResultPrefix.left = new FormAttachment(0, 5);
    lblResultPrefix.setLayoutData(fd_lblResultPrefix);
    lblResultPrefix.setText("Result Prefix:");

    Label lblNewLabel_3 = new Label(this, SWT.NONE);
    FormData fd_lblNewLabel_3 = new FormData();
    fd_lblNewLabel_3.bottom = new FormAttachment(text_result_prefix);
    fd_lblNewLabel_3.left = new FormAttachment(0, 5);
    lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
    lblNewLabel_3.setText("Results:");

    argumentPvTableViewerComposite = new Composite(this, SWT.NONE);
    FormData fd_composite = new FormData();
    fd_composite.bottom = new FormAttachment(lblNewLabel_3, -5);
    fd_composite.right = new FormAttachment(100, -5);
    fd_composite.top = new FormAttachment(0, 121);
    fd_composite.left = new FormAttachment(0, 5);
    argumentPvTableViewerComposite.setLayoutData(fd_composite);
    TableColumnLayout tcl_composite = new TableColumnLayout();
    argumentPvTableViewerComposite.setLayout(tcl_composite);

    argumentPvTableViewer = new TableViewer(argumentPvTableViewerComposite,
        SWT.BORDER | SWT.FULL_SELECTION);
    argumentPvTable = argumentPvTableViewer.getTable();
    argumentPvTable.setHeaderVisible(true);
    argumentPvTable.setLinesVisible(true);

    TableViewerColumn tableViewerColumn = new TableViewerColumn(
        argumentPvTableViewer, SWT.NONE);
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element != null && element instanceof Entry) {
            return ((Entry<String, String>) element).getKey();
        }
        return "";
        }
    });
    TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
    tcl_composite.setColumnData(tblclmnNewColumn, new ColumnWeightData(50,
        100, true));
    tblclmnNewColumn.setText("argument name");
    TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
        argumentPvTableViewer, SWT.NONE);
    tableViewerColumn_1.setEditingSupport(new EditingSupport(
        argumentPvTableViewer) {

        protected boolean canEdit(Object element) {
        return true;
        }

        protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(argumentPvTableViewer.getTable());
        }

        @SuppressWarnings("unchecked")
        protected Object getValue(Object element) {
        return ((Entry<String, String>) element).getValue();
        }

        @SuppressWarnings("unchecked")
        protected void setValue(Object element, Object value) {
        setArgumentPv(
            ((Entry<String, String>) element).getKey(),
            (String) value);
        }

    });
    tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element != null && element instanceof Entry) {
            return ((Entry<String, String>) element).getValue();
        }
        return "";
        }
    });
    TableColumn tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
    tcl_composite.setColumnData(tblclmnNewColumn_1, new ColumnWeightData(
        50, 100, true));
    tblclmnNewColumn_1.setText("pv/formula");
    argumentPvTableViewer.setContentProvider(new ArrayContentProvider());

    addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals("serviceMethodDescription")){
            updateUI();
        }else{
            updateUI();
        }
        }
    });

    this.serviceMethodDescription = serviceMethodDescription;
    useArgumentPrefix = false;
    argumentPrefix = DEFAULT_PREFIX + "_"
            + serviceMethodDescription.getService()
            + "_"
            + serviceMethodDescription.getMethod()
            + "_";
    useResultPrefix = false;
    resultPrefix = DEFAULT_PREFIX + "_"
            + serviceMethodDescription.getService()
            + "_"
            + serviceMethodDescription.getMethod()
            + "_";
    updateUI();
    }

    private void init(String service, String method) {
    // search for the service/method
    if (services == null) {
        services = new HashMap<String, Service>();
        ArrayList<String> serviceNames = new ArrayList<String>(
            ServiceRegistry.getDefault().listServices());
        Collections.sort(serviceNames);
        for (String serviceName : serviceNames) {
        services.put(serviceName, ServiceRegistry.getDefault()
            .findService(serviceName));
        }
    }
    ServiceMethod serviceMethod = null;
    serviceMethod = services.get(service) != null ? services.get(service)
        .getServiceMethods().get(method) : null;
    if (serviceMethod != null) {
        serviceMethodDescription = createServiceMethodDescription(service,
            serviceMethod);
        argumentPrefix = DEFAULT_PREFIX + "_"
            + serviceMethodDescription.getService() + "_"
            + serviceMethodDescription.getMethod() + "_";
        useArgumentPrefix = true;
        resultPrefix = DEFAULT_PREFIX + "_"
            + serviceMethodDescription.getService() + "_"
            + serviceMethodDescription.getMethod() + "_";
        useResultPrefix = true;
        serviceMethodDescription.setArgumentPvs(calculateArgumentPvs(
            getServiceMethodDescription().getArgumentPvs().keySet(),
            argumentPrefix));
        serviceMethodDescription.setResultPvs(calculateResultPvs(
            getServiceMethodDescription().getResultPvs().keySet(),
            resultPrefix));
        setServiceMethodDescription(serviceMethodDescription);
    } else {
        lblServiceMethodDescription.setText("Invalid Service/Method name");
        serviceMethodDescription = createServiceMethodDescription();
        argumentPrefix = "";
        useArgumentPrefix = false;
        resultPrefix = "";
        useResultPrefix = false;
        setServiceMethodDescription(serviceMethodDescription);
    }
    updateUI();
    getShell().pack();
    }

    private void updateUI() {
    if (serviceMethodDescription != null) {
        text_method.setText(serviceMethodDescription.getService() + "/"
            + serviceMethodDescription.getMethod());
        lblServiceMethodDescription.setText(serviceMethodDescription
            .getDescription());
        text_arg_prefix.setEnabled(true);
        if (useArgumentPrefix) {
        if (argumentPvTableViewer.isCellEditorActive()) {
            useArgumentPrefix = false;
            text_arg_prefix.setForeground(getDisplay().getSystemColor(
                SWT.COLOR_GRAY));
        } else {
            text_arg_prefix.setForeground(initialForegroundColor);
        }
        } else {
        text_arg_prefix.setForeground(getDisplay().getSystemColor(
            SWT.COLOR_GRAY));
        }
        text_arg_prefix.setText(argumentPrefix);
        argumentPvTableViewer.setInput(serviceMethodDescription
            .getArgumentPvs().entrySet());
        text_result_prefix.setEnabled(true);
        if (useResultPrefix) {
        if (resultPvTableViewer.isCellEditorActive()) {
            useResultPrefix = false;
            text_result_prefix.setForeground(getDisplay()
                .getSystemColor(SWT.COLOR_GRAY));
        } else {
            text_result_prefix.setForeground(initialForegroundColor);
        }
        } else {
        text_result_prefix.setForeground(getDisplay().getSystemColor(
            SWT.COLOR_GRAY));
        }
        text_result_prefix.setText(resultPrefix);
        resultPvTableViewer.setInput(serviceMethodDescription
            .getResultPvs().entrySet());
        layout();
    } else {
        lblServiceMethodDescription.setText("");
        text_arg_prefix.setEnabled(false);
        text_arg_prefix.setText("");
        argumentPvTableViewer.setInput(null);
        text_result_prefix.setEnabled(false);
        text_result_prefix.setText("");
        resultPvTableViewer.setInput(null);
        layout();
    }
    }

    private Map<String, String> calculateArgumentPvs(Set<String> argumentNames,
        String prefix) {
    Map<String, String> argumentPvs = new HashMap<String, String>();
    for (String argument : argumentNames) {
        argumentPvs.put(argument, prefix + argument);
    }
    return argumentPvs;
    }

    private Map<String, String> calculateResultPvs(Set<String> resultNames,
        String prefix) {
    Map<String, String> resultPvs = new HashMap<String, String>();
    for (String result : resultNames) {
        resultPvs.put(result, prefix + result);
    }
    return resultPvs;
    }

    private void setArgumentPvs(Map<String, String> argumentPvs) {
    Object oldValue = this.serviceMethodDescription.getArgumentPvs();
    this.serviceMethodDescription.setArgumentPvs(argumentPvs);
    changeSupport.firePropertyChange("argumentPvs", oldValue,
        this.serviceMethodDescription.getArgumentPvs());
    }

    private void setArgumentPv(String key, String value) {
    this.serviceMethodDescription.setArgumentPv(key, value);
    changeSupport.firePropertyChange("argumentPvs", null,
        this.serviceMethodDescription.getArgumentPvs());
    }

    private void setResultPvs(Map<String, String> resultPvs) {
    Object oldValue = this.serviceMethodDescription.getResultPvs();
    this.serviceMethodDescription.setResultPvs(resultPvs);
    changeSupport.firePropertyChange("resultPvs", oldValue,
        this.serviceMethodDescription.getResultPvs());
    }

    private void setResultPv(String key, String value) {
    this.serviceMethodDescription.setResultPv(key, value);
    changeSupport.firePropertyChange("resultPvs", null,
        this.serviceMethodDescription.getResultPvs());
    }

    private void setServiceMethodDescription(
        ServiceMethodDescription serviceMethodDescription) {
    ServiceMethodDescription oldValue = this.serviceMethodDescription;
    this.serviceMethodDescription = serviceMethodDescription;
    changeSupport.firePropertyChange("serviceMethodDescription", oldValue,
        this.serviceMethodDescription);
    }

    /**
     * @return the serviceMethodDescription
     */
    public ServiceMethodDescription getServiceMethodDescription() {
    return serviceMethodDescription;
    }

}
