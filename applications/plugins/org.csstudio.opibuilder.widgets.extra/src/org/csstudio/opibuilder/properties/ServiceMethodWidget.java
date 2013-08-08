/**
 * 
 */
package org.csstudio.opibuilder.properties;

import static org.csstudio.opibuilder.properties.ServiceMethodDescription.createServiceMethodDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

/**
 * @author shroffk
 * 
 */
public class ServiceMethodWidget extends BeanComposite {

    private Text text_method;
    private Text text_arg_prefix;
    private Text text_result_prefix;
    private Label lblServiceMethodDescription;

    private final String DEFAULT_PREFIX = "loc://${DID}";
    // Model
    private Map<String, Service> services;
    private String description;
    private String argumentPrefix;
    private String resultPrefix;
    private ServiceMethodDescription serviceMethodDescription;

    private Table argumentPvTable;
    private Table resultPvTable;
    private TableViewer argumentPvTableViewer;
    private TableViewer resultPvTableViewer;
    private Composite resultPvTableViewerComposite;
    private Composite argumentPvTableViewerComposite;

    public ServiceMethodWidget(Composite parent, int style) {
	super(parent, style);
	setLayout(new FormLayout());

	Label lblMethodName = new Label(this, SWT.NONE);
	FormData fd_lblMethodName = new FormData();
	fd_lblMethodName.top = new FormAttachment(0, 10);
	fd_lblMethodName.left = new FormAttachment(0, 5);
	lblMethodName.setLayoutData(fd_lblMethodName);
	lblMethodName.setText("Method Name:");

	Button btnNewButton = new Button(this, SWT.NONE);
	FormData fd_btnNewButton = new FormData();
	fd_btnNewButton.top = new FormAttachment(0, 5);
	fd_btnNewButton.right = new FormAttachment(100, -5);
	btnNewButton.setLayoutData(fd_btnNewButton);
	btnNewButton.setText("Search");

	text_method = new Text(this, SWT.BORDER);
	FormData fd_text_method = new FormData();
	fd_text_method.right = new FormAttachment(btnNewButton, -5);
	fd_text_method.left = new FormAttachment(lblMethodName, 5);
	fd_text_method.top = new FormAttachment(0, 7);
	text_method.setLayoutData(fd_text_method);
	text_method.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CR) {
		    // search for the service/method
		    if (services == null) {
			services = new HashMap<String, Service>();
			ArrayList<String> serviceNames = new ArrayList<String>(
				ServiceRegistry.getDefault().listServices());
			Collections.sort(serviceNames);
			for (String serviceName : serviceNames) {
			    services.put(serviceName, ServiceRegistry
				    .getDefault().findService(serviceName));
			}
		    }
		    String[] sm = text_method.getText().split("/");
		    String service = "";
		    ServiceMethod serviceMethod = null;
		    if (sm.length == 2) {
			service = sm[0];
			serviceMethod = services.get(service) != null ? services
				.get(service).getServiceMethods().get(sm[1])
				: null;
			if (serviceMethod != null) {
			    serviceMethodDescription = createServiceMethodDescription(
				    service, serviceMethod);
			    description = serviceMethod.getDescription();
			    argumentPrefix = DEFAULT_PREFIX + "_"
				    + serviceMethodDescription.getService()
				    + "_"
				    + serviceMethodDescription.getMethod()
				    + "_";
			    resultPrefix = DEFAULT_PREFIX + "_"
				    + serviceMethodDescription.getService()
				    + "_"
				    + serviceMethodDescription.getMethod()
				    + "_";
			    resetArgumentPvs();
			    resetResultPvs();
			    updateUI();

			} else {
			    description = "Unknown Service/Method "
				    + text_method.getText();
			    serviceMethodDescription = createServiceMethodDescription();
			    argumentPrefix = "";
			    resultPrefix = "";
			    resetArgumentPvs();
			    resetResultPvs();
			    updateUI();
			}
		    } else {
			lblServiceMethodDescription
				.setText("Invalid Service/Method name");
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
		    resetArgumentPvs();
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
	FormData fd_text_result_prefix = new FormData();
	fd_text_result_prefix.bottom = new FormAttachment(resultPvTableViewerComposite, -5);
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
		    resetResultPvs();
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
    }

    private void updateUI() {
	if (serviceMethodDescription != null) {
	    text_method.setText(serviceMethodDescription.getService() + "/"
		    + serviceMethodDescription.getMethod());
	    lblServiceMethodDescription.setText(description);
	    text_arg_prefix.setEnabled(true);
	    text_arg_prefix.setText(argumentPrefix);
	    argumentPvTableViewer.setInput(serviceMethodDescription
		    .getArgumentPvs().entrySet());
	    text_result_prefix.setEnabled(true);
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
	getShell().pack();
    }

    /**
     * recreates all the argument pvs using the prefix
     */
    private void resetArgumentPvs() {
	for (String argument : serviceMethodDescription.getArgumentPvs()
		.keySet()) {
	    serviceMethodDescription.setArgumentPv(argument, argumentPrefix
		    + argument);
	}
	updateUI();
    }

    private void resetResultPvs() {
	for (String result : serviceMethodDescription.getResultPvs().keySet()) {
	    serviceMethodDescription.setResultPv(result, resultPrefix + result);
	}
	updateUI();
    }

    /**
     * @return the serviceMethodDescription
     */
    public ServiceMethodDescription getServiceMethodDescription() {
	return serviceMethodDescription;
    }

}
