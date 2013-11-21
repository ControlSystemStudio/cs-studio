package org.csstudio.logbook.olog.property.shift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.util.LogEntryUtil;
import org.csstudio.shift.Shift;
import org.csstudio.shift.ShiftClient;
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
 * @author shroffk
 *
 */
class ShiftPropertyWidget extends AbstractPropertyWidget {
    
    private final IPreferencesService service = Platform.getPreferencesService();
    
    private static final String propertyName = "Shift";
    private static final String attrTypeName = "Type";
    private static final String attrIdName = "Id";
    private static final String attrURLName = "URL";
    
    private static final Property widgetProperty = PropertyBuilder
	    .property(propertyName)
	    .attribute(attrTypeName)
	    .attribute(attrIdName)
	    .attribute(attrURLName)
	    .build();

    private Text textId;
    private Text textURL;
    private Button btnAttach;
    private Link link;
    private Label lblAttached;
    private Combo comboType;
    private Composite container;
    
    private List<String> levels = Collections.emptyList();
    private ShiftClient shiftClient;
    private Collection<Shift> shifts;

    private String defaultType ;

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
		for (Shift shift : shifts) {
		    if(shift.getType().equalsIgnoreCase(comboType.getItem(comboType.getSelectionIndex()))){
			textId.setText(shift.getId().toString());
		    }
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
	btnAttach = new Button(container, SWT.NONE);
	btnAttach.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// store the property
		attachProperty();
	    }
	});
	btnAttach.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 2, 1));
	btnAttach.setText("Attach");
	init();
    }

    public void init(){
	
	try {
	    if(shiftClient == null){
		shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
	    }
	    levels = new ArrayList<String>(shiftClient.listTypes());
	    shifts = shiftClient.listShifts();
	    if (!comboType.getItems().equals(levels)) {
		comboType.setItems(levels.toArray(new String[levels.size()]));
	    }
	    try {
		defaultType = service.getString("org.csstudio.shift.ui", "Default.type", "", null);		
		comboType.setText(defaultType);
		comboType.setSelection(new Point(levels.indexOf(defaultType), levels.indexOf(defaultType)));
		for (Shift shift : shifts) {
		    if(shift.getType().equalsIgnoreCase(defaultType)){
			textId.setText(shift.getId().toString());
		    }
		}
		textURL.setText(service.getString("org.csstudio.utility.shift",
			"shift_url", "https://localhost:8181/Shift/resources",
			null)
			+ "/shift/" + comboType.getText() + "/" + textId.getText());
		attachProperty();
	    } catch (Exception ex) {
		// TODO
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
    }
    
    private void attachProperty(){
	LogEntryBuilder logEntryBuilder;
	try {
	    Property oldProperty = LogEntryUtil.getProperty(getLogEntryChangeset()
		    .getLogEntry(), ShiftPropertyWidget.widgetProperty
		    .getName());
	    PropertyBuilder newProperty = PropertyBuilder
		    .property(widgetProperty)
		    .attribute(attrTypeName, comboType.getText())
		    .attribute(attrIdName, textId.getText())
		    .attribute(attrURLName, textURL.getText());
	    if (oldProperty == null ||
		    !newProperty.build().getName().equals(oldProperty.getName()) ||
		    !newProperty.build().getAttributes().equals(oldProperty.getAttributes())) {
		logEntryBuilder = LogEntryBuilder.logEntry(getLogEntryChangeset().getLogEntry());
		logEntryBuilder.addProperty(newProperty);
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
	
	this.textURL.setVisible(isEditable());
	GridData textURLGridData= (GridData) this.textURL.getLayoutData();
	textURLGridData.exclude = !isEditable();
	this.textURL.setLayoutData(textURLGridData);
	this.link.setVisible(!isEditable());	
	GridData linkGridData = (GridData) this.link.getLayoutData();	    
	linkGridData.exclude = isEditable();
	this.link.setLayoutData(linkGridData);
	
	this.btnAttach.setVisible(isEditable());
	Property property = null;
	try {
	    property = LogEntryUtil.getProperty(getLogEntryChangeset()
	    	.getLogEntry(), ShiftPropertyWidget.widgetProperty.getName());
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
	    String ticketURL = property.getAttributeValue(attrURLName) == null ? ""
		    : property.getAttributeValue(attrURLName);
	    this.link.setText("<a>" + ticketURL + "</a>");
	}
    }
}