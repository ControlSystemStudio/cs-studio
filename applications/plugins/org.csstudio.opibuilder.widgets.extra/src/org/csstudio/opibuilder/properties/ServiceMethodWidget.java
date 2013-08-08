/**
 * 
 */
package org.csstudio.opibuilder.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceRegistry;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

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
    private ServiceMethodDescription serviceMethodDescription;
    
    private Table argumentPvTable;
    private Table resultPvTable;
    private TableViewer argumentPvTableViewer;
    private TableViewer resultPvTableViewer;

    public ServiceMethodWidget(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(3, false));

	Label lblMethodName = new Label(this, SWT.NONE);
	lblMethodName.setText("Method Name:");
	
	text_method = new Text(this, SWT.BORDER);	
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
		    String service = text_method.getText().split("/")[0];
		    String method = text_method.getText().split("/")[1];
		    ServiceMethod serviceMethod = services.get(service)
			    .getServiceMethods().get(method);
		    if (serviceMethod != null) {
			serviceMethodDescription = ServiceMethodDescription
				.createServiceMethodDescription(service, serviceMethod);
			updateServiceMethodDescription();
			resetArgumentPvs();
			resetResultPvs();
		    }else{
			serviceMethodDescription = null;
			updateServiceMethodDescription();
			resetArgumentPvs();
			resetResultPvs();
		    }
		}
	    }	    
	});
	text_method.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));

	Button btnNewButton = new Button(this, SWT.NONE);
	btnNewButton.setText("Search");

	lblServiceMethodDescription = new Label(this, SWT.NONE);
	lblServiceMethodDescription.setLayoutData(new GridData(SWT.LEFT,
		SWT.CENTER, false, false, 3, 1));

	Label lblNewLabel_1 = new Label(this, SWT.NONE);
	lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		false, 3, 1));
	lblNewLabel_1.setText("Arguments:");

	Label lblNewLabel_2 = new Label(this, SWT.NONE);
	lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblNewLabel_2.setText("Argument Prefix:");

	text_arg_prefix = new Text(this, SWT.BORDER);
	text_arg_prefix.setEnabled(false);
	text_arg_prefix.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		// argument prefix set
		if (e.keyCode == SWT.CR) {
		    resetArgumentPvs();
		}
	    }
	});
	text_arg_prefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 2, 1));
	
	Composite composite = new Composite(this, SWT.NONE);
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
	TableColumnLayout tcl_composite = new TableColumnLayout();
	composite.setLayout(tcl_composite);
	
	argumentPvTableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
	argumentPvTable = argumentPvTableViewer.getTable();
	argumentPvTable.setHeaderVisible(true);
	argumentPvTable.setLinesVisible(true);
	
	TableViewerColumn tableViewerColumn = new TableViewerColumn(argumentPvTableViewer, SWT.NONE);
	tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
		public Image getImage(Object element) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public String getText(Object element) {
		    if(element != null && element instanceof Entry){
			return ((Entry<String, String>)element).getKey();
		    }
		    return "";
		}
	});
	TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
	tcl_composite.setColumnData(tblclmnNewColumn, new ColumnWeightData(50, 100, true) );
	tblclmnNewColumn.setText("argument name");
	
	TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(argumentPvTableViewer, SWT.NONE);
	tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
		public Image getImage(Object element) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public String getText(Object element) {
		    if(element != null && element instanceof Entry){
			return ((Entry<String, String>)element).getValue();
		    }
		    return "";
		}
	});
	TableColumn tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
	tcl_composite.setColumnData(tblclmnNewColumn_1, new ColumnWeightData(50, 100, true));
	tblclmnNewColumn_1.setText("pv/formula");
	argumentPvTableViewer.setContentProvider(new ArrayContentProvider());

	Label lblNewLabel_3 = new Label(this, SWT.NONE);
	lblNewLabel_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		false, 3, 1));
	lblNewLabel_3.setText("Results:");

	Label lblResultPrefix = new Label(this, SWT.NONE);
	lblResultPrefix.setText("Result Prefix:");

	text_result_prefix = new Text(this, SWT.BORDER);
	text_result_prefix.setEnabled(false);
	text_result_prefix.addKeyListener(new KeyAdapter() {
	    
	    @Override
	    public void keyReleased(KeyEvent e) {
		// result prefix
		if (e.keyCode == SWT.CR) {
		    resetResultPvs();
		}
	    }
	});
	text_result_prefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
		true, false, 2, 1));
	
	Composite composite_1 = new Composite(this, SWT.NONE);
	composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
	TableColumnLayout tcl_composite_1 = new TableColumnLayout();
	composite_1.setLayout(tcl_composite_1);
	
	resultPvTableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
	resultPvTable = resultPvTableViewer.getTable();
	resultPvTable.setHeaderVisible(true);
	resultPvTable.setLinesVisible(true);
	
	TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(resultPvTableViewer, SWT.NONE);
	tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
		public Image getImage(Object element) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public String getText(Object element) {
		    if(element != null && element instanceof Entry){
			return ((Entry<String, String>)element).getKey();
		    }
		    return "";
		}
	});
	TableColumn tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
	tcl_composite_1.setColumnData(tblclmnNewColumn_2, new ColumnWeightData(50, 100, true));
	tblclmnNewColumn_2.setText("result");
	
	TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(resultPvTableViewer, SWT.NONE);
	tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
		public Image getImage(Object element) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public String getText(Object element) {
		    if(element != null && element instanceof Entry){
			return ((Entry<String, String>)element).getValue();
		    }
		    return "";
		}
	});
	TableColumn tblclmnNewColumn_3 = tableViewerColumn_3.getColumn();
	tcl_composite_1.setColumnData(tblclmnNewColumn_3, new ColumnWeightData(50, 100, true));
	tblclmnNewColumn_3.setText("pv/formula");
	resultPvTableViewer.setContentProvider(new ArrayContentProvider());
    }
    
    private void updateServiceMethodDescription() {
	if(serviceMethodDescription != null){
//	    lblServiceMethodDescription.setText(serviceMethodDescription
//		    .getMethodDescription().trim());
	    text_arg_prefix.setEnabled(true);
	    text_arg_prefix.setText(DEFAULT_PREFIX + "_"
		    + serviceMethodDescription.getService() + "_"
		    + serviceMethodDescription.getMethod() + "_");
	    text_result_prefix.setEnabled(true);
	    text_result_prefix.setText(DEFAULT_PREFIX + "_"
		    + serviceMethodDescription.getService() + "_"
		    + serviceMethodDescription.getMethod() + "_");
	}else{
	    lblServiceMethodDescription.setText("");
	    text_arg_prefix.setEnabled(false);
	    text_arg_prefix.setText("");
	    text_result_prefix.setEnabled(false);
	    text_result_prefix.setText("");	 
	}
	update();
    }
    
    private void updateUI(){
	if(serviceMethodDescription != null){
	    text_arg_prefix.setEnabled(true);
	    text_arg_prefix.setText(DEFAULT_PREFIX + "_"
		    + serviceMethodDescription.getService() + "_"
		    + serviceMethodDescription.getMethod() + "_");
	    text_result_prefix.setEnabled(true);
	    text_result_prefix.setText(DEFAULT_PREFIX + "_"
		    + serviceMethodDescription.getService() + "_"
		    + serviceMethodDescription.getMethod() + "_");
	}else{
	    lblServiceMethodDescription.setText("");
	    text_arg_prefix.setEnabled(false);
	    text_arg_prefix.setText("");
	    text_result_prefix.setEnabled(false);
	    text_result_prefix.setText("");	 
	}
	update();
    }
    
    /**
     * recreates all the argument pvs using the prefix
     */
    private void resetArgumentPvs(){
	for (String argument : serviceMethodDescription.getArgumentPvs().keySet()) {
	    serviceMethodDescription.setArgumentPv(argument,
		    text_arg_prefix.getText() + argument);
	}
	argumentPvTableViewer.setInput(serviceMethodDescription.getArgumentPvs().entrySet());
	argumentPvTable.getParent().layout();
    }
    
    private void resetResultPvs(){	
	for (String result : serviceMethodDescription.getResultPvs().keySet()) {
	    serviceMethodDescription.setResultPv(result,
		    text_result_prefix.getText() + result);
	}
	resultPvTableViewer.setInput(serviceMethodDescription.getResultPvs().entrySet());
	resultPvTable.getParent().layout();    
    }
    
    /**
     * @return the serviceMethodDescription
     */
    public ServiceMethodDescription getServiceMethodDescription() {
	return serviceMethodDescription;
    }

}
