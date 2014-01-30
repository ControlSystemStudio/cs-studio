package org.csstudio.logbook.olog.properties;

import java.io.IOException;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.util.LogEntryUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

class TicketPropertyWidget extends AbstractPropertyWidget {
    
    private static final String propertyName = "Ticket";
    private static final String attrIdName = "Id";
    private static final String attrURLName = "URL";
    
    private static final Property widgetProperty = PropertyBuilder
	    .property(propertyName).attribute(attrIdName).attribute(attrURLName)
	    .build();

    private Text textId;
    private Text textURL;
    private Button btnAttach;
    private Link link;
    private Label lblAttached;

    public TicketPropertyWidget(Composite parent, int style,
	    LogEntryChangeset logEntryChangeset) {
	super(parent, style, logEntryChangeset);
	setLayout(new FormLayout());

	Label lblNewLabel = new Label(this, SWT.NONE);
	FormData fd_lblNewLabel = new FormData();
	// fd_lblNewLabel.right = new FormAttachment(100, -10);
	fd_lblNewLabel.top = new FormAttachment(0, 10);
	fd_lblNewLabel.left = new FormAttachment(0, 10);
	lblNewLabel.setLayoutData(fd_lblNewLabel);
	lblNewLabel.setText("Tickets:");

	Label lblNewLabel_1 = new Label(this, SWT.NONE);
	FormData fd_lblNewLabel_1 = new FormData();
	fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 6);
	fd_lblNewLabel_1.left = new FormAttachment(0, 10);
	lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
	lblNewLabel_1.setText("Ticket Id: ");

	textId = new Text(this, SWT.BORDER);
	FormData fd_textId = new FormData();
	fd_textId.right = new FormAttachment(100, -10);
	fd_textId.left = new FormAttachment(lblNewLabel_1, 6);
	fd_textId.top = new FormAttachment(lblNewLabel, 6);
	textId.setLayoutData(fd_textId);

	Label lblNewLabel_2 = new Label(this, SWT.NONE);
	FormData fd_lblNewLabel_2 = new FormData();
	fd_lblNewLabel_2.top = new FormAttachment(lblNewLabel_1, 25);
	fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
	lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
	lblNewLabel_2.setText("URL: ");

	textURL = new Text(this, SWT.BORDER);
	FormData fd_textURL = new FormData();
	fd_textURL.top = new FormAttachment(lblNewLabel_2, -3, SWT.TOP);
	fd_textURL.left = new FormAttachment(textId, 0, SWT.LEFT);
	fd_textURL.right = new FormAttachment(100, -10);
	textURL.setLayoutData(fd_textURL);

	btnAttach = new Button(this, SWT.NONE);
	btnAttach.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// store the property
		LogEntryBuilder logEntryBuilder;
		try {
		    logEntryBuilder = LogEntryBuilder
			    .logEntry(getLogEntryChangeset().getLogEntry());
		    logEntryBuilder.addProperty(PropertyBuilder
			    .property(widgetProperty)
			    .attribute(attrIdName, textId.getText())
			    .attribute(attrURLName, textURL.getText()));
		    getLogEntryChangeset().setLogEntryBuilder(logEntryBuilder);
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});
	FormData fd_btnApply = new FormData();
	fd_btnApply.bottom = new FormAttachment(100, -10);
	fd_btnApply.right = new FormAttachment(100, -10);
	btnAttach.setLayoutData(fd_btnApply);
	btnAttach.setText("Attach");

	link = new Link(this, SWT.NONE);
	FormData fd_link = new FormData();
	fd_link.top = new FormAttachment(lblNewLabel_2, -3, SWT.TOP);
	fd_link.left = new FormAttachment(textId, 0, SWT.LEFT);
	fd_link.right = new FormAttachment(100, -10);
	link.setLayoutData(fd_link);
	link.setText("<a>www.google.com</a>");
	link.addSelectionListener(new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			String url = link.getText();
			url = url.substring("<a>".length(),
					url.length() - "</a>".length());
			Program.launch(url);
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
	});

	lblAttached = new Label(this, SWT.NONE);
	lblAttached.setFont(SWTResourceManager.getFont("Segoe UI", 9,
		SWT.ITALIC));
	FormData fd_lblNewLabel_3 = new FormData();
	fd_lblNewLabel_3.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
	fd_lblNewLabel_3.right = new FormAttachment(textId, 0, SWT.RIGHT);
	lblAttached.setLayoutData(fd_lblNewLabel_3);
	lblAttached.setText("attached");
    }

    @Override
    public void updateUI() {
	this.lblAttached.setVisible(!isEditable());
	this.textId.setEditable(isEditable());
	this.textURL.setVisible(isEditable());
	this.link.setVisible(!isEditable());
	this.btnAttach.setVisible(isEditable());
	
	Property property = null;
	try {
	    property = LogEntryUtil.getProperty(getLogEntryChangeset()
	    	.getLogEntry(), TicketPropertyWidget.widgetProperty.getName());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (property != null) {
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