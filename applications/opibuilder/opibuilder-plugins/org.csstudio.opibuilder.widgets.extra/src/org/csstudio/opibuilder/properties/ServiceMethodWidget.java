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
import org.diirt.service.Service;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceRegistry;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

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
    private Composite rootComposite;
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

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    gridLayout.marginHeight = 2;
    gridLayout.horizontalSpacing = 2;
    setLayout(gridLayout);


    rootComposite = new Composite(this, SWT.NONE | SWT.DOUBLE_BUFFERED);
    GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
    rootComposite.setLayoutData(gd_composite);
    rootComposite.setLayout(new FormLayout());


    Label lblMethodName = new Label(rootComposite, SWT.NONE);
    FormData fd_lblMethodName = new FormData();
    fd_lblMethodName.top = new FormAttachment(0, 10);
    fd_lblMethodName.left = new FormAttachment(0, 5);
    lblMethodName.setLayoutData(fd_lblMethodName);
    lblMethodName.setText("Method Name:");

    Button btnNewButton = new Button(rootComposite, SWT.NONE);
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

    text_method = new Text(rootComposite, SWT.BORDER);
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

    lblServiceMethodDescription = new Label(rootComposite, SWT.NONE);
    FormData fd_lblServiceMethodDescription = new FormData();
    fd_lblServiceMethodDescription.right = new FormAttachment(100, -5);
    fd_lblServiceMethodDescription.top = new FormAttachment(text_method, 10);
    fd_lblServiceMethodDescription.left = new FormAttachment(0, 5);
    lblServiceMethodDescription
        .setLayoutData(fd_lblServiceMethodDescription);

    Label lblArguments = new Label(rootComposite, SWT.NONE);
    FormData fd_lblArguments = new FormData();
    fd_lblArguments.top = new FormAttachment(lblServiceMethodDescription, 10);
    fd_lblArguments.left = new FormAttachment(0, 5);
    lblArguments.setLayoutData(fd_lblArguments);
    lblArguments.setText("Arguments:");

    Label argPrefLbl = new Label(rootComposite, SWT.NONE);
    FormData fb_argPrefLbl = new FormData();
    fb_argPrefLbl.top = new FormAttachment(lblArguments, 10);
    fb_argPrefLbl.left = new FormAttachment(0, 5);
    argPrefLbl.setLayoutData(fb_argPrefLbl);
    argPrefLbl.setText("Argument Prefix:");

    text_arg_prefix = new Text(rootComposite, SWT.BORDER);
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
    fd_text_arg_prefix.top = new FormAttachment(lblArguments, 7);
    fd_text_arg_prefix.left = new FormAttachment(30, 0);
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

    resultPvTableViewerComposite = new Composite(rootComposite, SWT.NONE);
    FormData fd_composite_1 = new FormData();
    fd_composite_1.right = new FormAttachment(100, -5);
    fd_composite_1.left = new FormAttachment(0, 5);
    fd_composite_1.top = new FormAttachment(argPrefLbl, 10);
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
        @Override
        public Image getImage(Object element) {
        return null;
        }

        @Override
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
        30, 100, true));
    tblclmnNewColumn_2.setText("result");

    TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
        resultPvTableViewer, SWT.NONE);
    tableViewerColumn_3.setEditingSupport(new EditingSupport(
        resultPvTableViewer) {
        @Override
        protected boolean canEdit(Object element) {
        return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(resultPvTableViewer.getTable());
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object getValue(Object element) {
        return ((Entry<String, String>) element).getValue();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void setValue(Object element, Object value) {
        setResultPv(
            ((Entry<String, String>) element).getKey(),
            (String) value);
        }

    });
    tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public Image getImage(Object element) {
        return null;
        }

        @Override
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
        70, 100, true));
    tblclmnNewColumn_3.setText("pv/formula");
    resultPvTableViewer.setContentProvider(new ArrayContentProvider());


    Label lblResults = new Label(rootComposite, SWT.NONE);
    FormData fd_lblResults = new FormData();
    fd_lblResults.top  = new FormAttachment(resultPvTableViewerComposite, 10);
    fd_lblResults.left = new FormAttachment(0, 5);
    lblResults.setLayoutData(fd_lblResults);
    lblResults.setText("Results:");

    Label lblResultPrefix = new Label(rootComposite, SWT.NONE);
    FormData fd_lblResultPrefix = new FormData();
    fd_lblResultPrefix.top = new FormAttachment(lblResults, 10);
    fd_lblResultPrefix.left = new FormAttachment(0, 5);
    lblResultPrefix.setLayoutData(fd_lblResultPrefix);
    lblResultPrefix.setText("Result Prefix:");


    text_result_prefix = new Text(rootComposite, SWT.BORDER);
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
    fd_text_result_prefix.top = new FormAttachment(lblResults, 7);
    fd_text_result_prefix.right = new FormAttachment(100, -5);
    fd_text_result_prefix.left = new FormAttachment(lblResultPrefix, 10);
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




    argumentPvTableViewerComposite = new Composite(rootComposite, SWT.NONE);
    FormData fd_composite = new FormData();
    fd_composite.right = new FormAttachment(100, -5);
    fd_composite.top = new FormAttachment(lblResultPrefix, 10);
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
        @Override
        public Image getImage(Object element) {
        return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element != null && element instanceof Entry) {
            return ((Entry<String, String>) element).getKey();
        }
        return "";
        }
    });
    TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
    tcl_composite.setColumnData(tblclmnNewColumn, new ColumnWeightData(30,
        100, true));
    tblclmnNewColumn.setText("argument name");
    TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
        argumentPvTableViewer, SWT.NONE);
    tableViewerColumn_1.setEditingSupport(new EditingSupport(
        argumentPvTableViewer) {

        @Override
        protected boolean canEdit(Object element) {
        return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(argumentPvTableViewer.getTable());
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object getValue(Object element) {
        return ((Entry<String, String>) element).getValue();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void setValue(Object element, Object value) {
        setArgumentPv(
            ((Entry<String, String>) element).getKey(),
            (String) value);
        }

    });
    tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public Image getImage(Object element) {
        return null;
        }

        @Override
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
        70, 100, true));
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
            ServiceRegistry.getDefault().getRegisteredServiceNames());
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
        rootComposite.layout();
    } else {
        lblServiceMethodDescription.setText("");
        text_arg_prefix.setEnabled(false);
        text_arg_prefix.setText("");
        argumentPvTableViewer.setInput(null);
        text_result_prefix.setEnabled(false);
        text_result_prefix.setText("");
        resultPvTableViewer.setInput(null);
        rootComposite.layout();
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
