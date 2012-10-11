package org.csstudio.logbook.olog.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

class ContextPropertyWidget extends AbstractPropertyWidget {
	private Button btnRestore;
	private Text textFileDescription;
	private Text textFileName;
	private Label lblNewLabel_1;
	private Label lblNewLabel_2;
	private Button btnCurrentContext;

	public ContextPropertyWidget(Composite parent, int style,
			String propertyName) {
		super(parent, style, propertyName);
		setLayout(new FormLayout());

		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(100, -10);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Context Files:");

		btnRestore = new Button(this, SWT.NONE);
		btnRestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isEditable()) {
					// Apply Action

				} else {
					// Restore Action
					
				}
			}
		});
		FormData fd_btnRestore = new FormData();
		fd_btnRestore.bottom = new FormAttachment(100, -10);
		fd_btnRestore.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		btnRestore.setLayoutData(fd_btnRestore);
		btnRestore.setText("Restore");

		btnCurrentContext = new Button(this, SWT.NONE);
		btnCurrentContext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IEditorInput input = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor().getEditorInput();
				// IFile file = (IFile) input.getAdapter(IFile.class);
				// logEntryBuilder.attach(new File(file.getLocationURI()));
				// PropertyBuilder propertyBuilder = PropertyBuilder
				// .property("css-context");
				// propertyBuilder.attribute("context-file", file.getName());
				// logEntryBuilder.addProperty(propertyBuilder);
			}
		});
		FormData fd_btnCurrentContext = new FormData();
		fd_btnCurrentContext.top = new FormAttachment(btnRestore, 0, SWT.TOP);
		fd_btnCurrentContext.left = new FormAttachment(0, 10);
		btnCurrentContext.setLayoutData(fd_btnCurrentContext);
		btnCurrentContext.setText("Add Current Context");

		lblNewLabel_1 = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 6);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("File Description:");

		textFileName = new Text(this, SWT.BORDER);
		FormData fd_textFileName = new FormData();
		fd_textFileName.bottom = new FormAttachment(btnRestore, -6);
		fd_textFileName.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_textFileName.left = new FormAttachment(0, 10);
		textFileName.setLayoutData(fd_textFileName);

		lblNewLabel_2 = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.bottom = new FormAttachment(textFileName, -6);
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("FileName:");

		textFileDescription = new Text(this, SWT.BORDER);
		FormData fd_textFileDescription = new FormData();
		fd_textFileDescription.bottom = new FormAttachment(lblNewLabel_2, -6);
		fd_textFileDescription.left = new FormAttachment(0, 10);
		fd_textFileDescription.top = new FormAttachment(lblNewLabel_1, 6);
		fd_textFileDescription.right = new FormAttachment(100, -10);
		textFileDescription.setLayoutData(fd_textFileDescription);

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch (evt.getPropertyName()) {
				case "editable":
					updateWidget();
					break;
				case "property":
					updateWidget();
					break;
				case "propertyBuilder":
					break;
				default:
					break;
				}
			}
		});

	}

	private void updateWidget() {
		textFileName.setEditable(isEditable());
		textFileDescription.setEditable(isEditable());
		btnCurrentContext.setVisible(isEditable());
		if (isEditable()) {
			btnRestore.setText("Apply");
		} else {
			btnRestore.setText("Restore");
		}
	}

	@Override
	public void afterCreate(LogEntry logEntry) {

	}
}