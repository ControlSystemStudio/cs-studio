package org.csstudio.logbook.olog.property.shift;

import gov.bnl.shiftClient.Shift;
import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.util.LogEntryUtil;
import org.csstudio.shift.ShiftClientManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * The widget associated with the shift property 
 *
 * TODO The attach is called in the update since the editable is set after the widget is created.
 * 
 * @author shroffk
 *
 */
class ShiftPropertyWidget extends AbstractPropertyWidget {
    
    private final IPreferencesService service = Platform.getPreferencesService();
    
    public static final String propertyName = "Shift";
    public static final String attrTypeName = "Type";
    public static final String attrIdName = "Id";
    public static final String attrURLName = "URL";
    
    private static final String inactiveShift = "No Active Shift";
    
    private static PropertyBuilder widgetProperty;

    private Text textId;
    private Text textURL;
    private Button btnAttach;
    private Link link;
    private Label lblAttached;
    private Combo comboType;
    private Composite container;
    
    private List<String> types = Collections.emptyList();
    private ShiftClient shiftClient;
    private Shift shift;

    private String defaultType ;

    private Button btnRemove;

    private static boolean autoAttach = true;

    public ShiftPropertyWidget(Composite parent, int style,
	    LogEntryChangeset logEntryChangeset) {
	super(parent, style, logEntryChangeset);
	setLayout(new FormLayout());	
	
	container = new Composite(this, style);
	FormData layoutData = new FormData();
	layoutData.top = new FormAttachment(0, 5);
	layoutData.left = new FormAttachment(0, 5);
	layoutData.bottom = new FormAttachment(100, -5);
	layoutData.right = new FormAttachment(100, -5);
	
	container.setLayoutData(layoutData);
	
	GridLayout gridLayout = new GridLayout(2, false);
	gridLayout.verticalSpacing = 1;
	gridLayout.horizontalSpacing = 1;
	gridLayout.marginHeight = 1;
	gridLayout.marginWidth = 1;
	container.setLayout(gridLayout);	
		
	Label lblPropertyName = new Label(container, SWT.NONE);
	lblPropertyName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	lblPropertyName.setText(propertyName+":");
	
	lblAttached = new Label(container, SWT.NONE);
	lblAttached.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
	lblAttached.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblAttached.setText("attached");
	
	Label lblType = new Label(container, SWT.NONE);
	lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	lblType.setText(attrTypeName+": ");
	
	comboType = new Combo(container, SWT.BORDER);
	comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	comboType.addSelectionListener(new SelectionAdapter() {
	    
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		String selectedType = comboType.getItem(comboType.getSelectionIndex());
		shift = shiftClient.getShiftByType(selectedType);
		if (shift.getStatus().equals("Active")) {
		    widgetProperty = PropertyBuilder
			    .property(propertyName)
			    .attribute(attrTypeName, selectedType)
			    .attribute(attrIdName, shift.getId().toString())
			    .attribute(
				    attrURLName,
				    service.getString(
					    "org.csstudio.utility.shift",
					    "shift_url",
					    "https://localhost:8181/Shift/resources",
					    null)
					    + "/shift/"
					    + selectedType
					    + "/"
					    + shift.getId().toString());
		    attachProperty();
		} else {
		    widgetProperty = null;
		}
	    }
	});
	
	Label lblId = new Label(container, SWT.NONE);
	lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	lblId.setText(attrIdName+": ");

	textId = new Text(container, SWT.BORDER);
	textId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	
	Label lblURL = new Label(container, SWT.NONE);
	lblURL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	lblURL.setText(attrURLName+": ");

	textURL = new Text(container, SWT.BORDER);
	textURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	
	link = new Link(container, SWT.NONE);
	link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	link.addSelectionListener(new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			String url = link.getText();
			url = url.substring("<a>".length(), url.length() - "</a>".length());
			Program.launch(url);
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
	});
	
	btnRemove = new Button(container, SWT.CHECK);
	btnRemove.setSelection(autoAttach);
	btnRemove.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		autoAttach = btnRemove.getSelection();
		if(autoAttach){
		    attachProperty();		    
		}else{
		    removeProperty();
		}
	    }
	});
	btnRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
	btnRemove.setText("Auto Attach");
	
	btnAttach = new Button(container, SWT.NONE);
	btnAttach.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// store the property
		attachProperty();
	    }
	});
	btnAttach.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
	btnAttach.setText("Attach");
	init();
    }

    public void init(){
	try {
	    if(shiftClient == null){
		shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
	    }
	    widgetProperty = PropertyBuilder.property(propertyName);	    
	    types = new ArrayList<String>();
	    for (Type type : shiftClient.listTypes()) {
		types.add(type.getName());
	    }
	    comboType.setItems(types.toArray(new String[types.size()]));
	    defaultType = service.getString("org.csstudio.shift.ui", "Default.type", "", null);	   
	    shift = shiftClient.getShiftByType(defaultType);
	    if (types.contains(defaultType) && shift.getStatus().equals("Active")) {
		widgetProperty.attribute(attrTypeName, defaultType);
		widgetProperty.attribute(attrIdName, shift.getId().toString());
		widgetProperty.attribute(attrURLName, service
			    .getString("org.csstudio.utility.shift", "shift_url",
				    "https://localhost:8181/Shift/resources", null)
			    + "/shift/" + defaultType + "/" + shift.getId().toString());
	    } else {
		widgetProperty = null;
	    }    
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
    }
    
    private void attachProperty(){
	LogEntryBuilder logEntryBuilder;
	try {
	    if (widgetProperty != null) {
		Property oldProperty = LogEntryUtil.getProperty(
			getLogEntryChangeset().getLogEntry(), propertyName);
		Property newProperty = widgetProperty.build();
		if (oldProperty == null
			|| !newProperty.getName()
				.equals(oldProperty.getName())
			|| !newProperty.getAttributes()
				.equals(oldProperty.getAttributes())) {
		    logEntryBuilder = LogEntryBuilder
			    .logEntry(getLogEntryChangeset().getLogEntry());
		    logEntryBuilder.addProperty(widgetProperty);
		    getLogEntryChangeset().setLogEntryBuilder(logEntryBuilder);
		}
	    }
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }
    
    private void removeProperty(){
   	LogEntryBuilder logEntryBuilder;
   	try {
   		Property oldProperty = LogEntryUtil.getProperty(
   			getLogEntryChangeset().getLogEntry(), propertyName);
   		if (oldProperty != null) {
   		    logEntryBuilder = LogEntryBuilder.logEntry(getLogEntryChangeset().getLogEntry());
   		    logEntryBuilder.removeProperty(propertyName);
   		    getLogEntryChangeset().setLogEntryBuilder(logEntryBuilder);
   		}
   	} catch (IOException e1) {
   	    // TODO Auto-generated catch block
   	    e1.printStackTrace();
   	}
       }
    
    @Override
    public void updateUI() {
	this.lblAttached.setVisible(!isEditable());
	this.comboType.setEnabled(isEditable());
	this.textId.setEditable(isEditable());	
//	this.textURL.setVisible(isEditable());
	GridData textURLGridData= (GridData) this.textURL.getLayoutData();
	textURLGridData.exclude = !isEditable();
	this.textURL.setLayoutData(textURLGridData);
	this.link.setVisible(!isEditable());	
	GridData linkGridData = (GridData) this.link.getLayoutData();	    
	linkGridData.exclude = isEditable();
	this.link.setLayoutData(linkGridData);	
	this.btnAttach.setVisible(isEditable());
	if(isEditable() && autoAttach){
	    attachProperty();
	}
	Property property = null;
	try {
	    property = LogEntryUtil.getProperty(getLogEntryChangeset().getLogEntry(), propertyName);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (property != null) {
	    this.comboType.setText(property.getAttributeValue(attrTypeName) == null ? ""
		    : property.getAttributeValue(attrTypeName));
	    this.textId
		    .setText(property.getAttributeValue(attrIdName) == null ? ""
			    : property.getAttributeValue(attrIdName));
	    this.textURL
		    .setText(property.getAttributeValue(attrURLName) == null ? ""
			    : property.getAttributeValue(attrURLName));
	    String shiftURL = property.getAttributeValue(attrURLName) == null ? ""		    
		    : property.getAttributeValue(attrURLName);
	    this.link.setText("<a>" + shiftURL + "</a>");
	}
    }
}