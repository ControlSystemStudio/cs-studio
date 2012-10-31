/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.csstudio.apputil.ui.swt.Screenshot;
import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.TagBuilder;
import org.csstudio.logbook.ui.util.IFileUtil;
import org.csstudio.logbook.util.LogEntryUtil;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ResourceManager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 * 
 */
public class LogEntryWidget extends Composite {

	private boolean editable;
	// This serves as the model for the complete logEntry that is simply being
	// displayed.
	// This serves as the model behind the new entry being created to be written
	// to the logbook service
	private LogEntryChangeset logEntryChangeset;
	private LogEntryChangeset savedLogEntryChangeset;

	private LogbookClient logbookClient;
	// List of all the possible logbooks and tags which may be added to a
	// logEntry.
	private java.util.List<String> logbookNames;
	private java.util.List<String> tagNames;

	// TODO
	private java.util.Map<String, PropertyWidgetFactory> propertyWidgetFactories;
	private LogEntry logEntry;
	// UI components
	private Text text;
	private Text textDate;
	private Text textOwner;
	private Button btnEnableEdit;
	private List logbookList;
	private List tagList;

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private Button btnSubmit;
	private Button btnSave;
	private Button btnAddLogbook;
	private Button btnAddTags;
	final private FormData empty;
	private Label label_vertical;
	private CTabItem tbtmAttachments;
	private CTabFolder tabFolder;
	private Composite tbtmAttachmentsComposite;
	private ImageStackWidget imageStackWidget;
	private Button btnAddImage;
	private Button btnAddScreenshot;
	private Button btnCSSWindow;
	private Label lblTags;
	private Label label_horizontal;
	private Composite composite;
	private ErrorBar errorBar;
	private final boolean newWindow;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public LogEntryWidget(final Composite parent, int style,
			final boolean newWindow) {
		super(parent, style);
		this.newWindow = newWindow;
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);

		errorBar = new ErrorBar(this, SWT.NONE);

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		Label lblDate = new Label(composite, SWT.NONE);
		FormData fd_lblDate = new FormData();
		fd_lblDate.top = new FormAttachment(0, 5);
		fd_lblDate.left = new FormAttachment(0, 5);
		lblDate.setLayoutData(fd_lblDate);
		lblDate.setText("Date:");

		textDate = new Text(composite, SWT.NONE);
		textDate.setEditable(false);
		FormData fd_textDate = new FormData();
		fd_textDate.top = new FormAttachment(0, 5);
		fd_textDate.left = new FormAttachment(lblDate, 5);
		textDate.setLayoutData(fd_textDate);

		label_vertical = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		label_vertical.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				FormData fd = (FormData) label_vertical.getLayoutData();
				long calNumerator = fd.left.numerator + (e.x * 100)
						/ e.display.getActiveShell().getClientArea().width;
				fd.left = new FormAttachment((int) calNumerator);
				label_vertical.setLayoutData(fd);
				label_vertical.getParent().layout();
			}
		});
		label_vertical.setCursor(Display.getCurrent().getSystemCursor(
				SWT.CURSOR_SIZEWE));
		FormData fd_label_vertical = new FormData();
		fd_label_vertical.top = new FormAttachment(0, 2);
		fd_label_vertical.bottom = new FormAttachment(100, -2);
		fd_label_vertical.left = new FormAttachment(70);
		label_vertical.setLayoutData(fd_label_vertical);

		label_horizontal = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_horizontal.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				FormData fd = (FormData) label_horizontal.getLayoutData();
				long calNumerator = fd.top.numerator + (e.y * 100)
						/ e.display.getActiveShell().getClientArea().height;
				fd.top = new FormAttachment((int) calNumerator);
				label_horizontal.setLayoutData(fd);
				label_horizontal.getParent().layout();
			}
		});
		label_horizontal.setCursor(Display.getCurrent().getSystemCursor(
				SWT.CURSOR_SIZENS));
		FormData fd_label_horizontal = new FormData();
		fd_label_horizontal.top = new FormAttachment(20);
		fd_label_horizontal.right = new FormAttachment(label_vertical, 2);
		fd_label_horizontal.left = new FormAttachment(0, 5);
		label_horizontal.setLayoutData(fd_label_horizontal);

		text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					text.getParent().layout();
				}
			}
		});
		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(label_horizontal, -2);
		fd_text.right = new FormAttachment(label_vertical, -2);
		fd_text.top = new FormAttachment(lblDate, 10, SWT.BOTTOM);
		fd_text.left = new FormAttachment(0, 5);
		text.setLayoutData(fd_text);

		Label lblOwner = new Label(composite, SWT.NONE);
		FormData fd_lblOwner = new FormData();
		fd_lblOwner.left = new FormAttachment(label_vertical, 2);
		fd_lblOwner.top = new FormAttachment(0, 5);
		lblOwner.setLayoutData(fd_lblOwner);
		lblOwner.setText("Owner:");

		textOwner = new Text(composite, SWT.BORDER);
		FormData fd_textOwner = new FormData();
		fd_textOwner.top = new FormAttachment(0, 5);
		fd_textOwner.right = new FormAttachment(100, -5);
		fd_textOwner.left = new FormAttachment(lblOwner, 2);
		textOwner.setLayoutData(fd_textOwner);

		btnSubmit = new Button(composite, SWT.NONE);
		FormData fd_btnSubmit = new FormData();
		fd_btnSubmit.left = new FormAttachment(label_vertical, 2);
		fd_btnSubmit.right = new FormAttachment(100, -5);
		fd_btnSubmit.bottom = new FormAttachment(100, -5);
		btnSubmit.setLayoutData(fd_btnSubmit);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO if owner not the same as the preference one create a new
				// client
				try {
					LogbookClient logbookClient = LogbookClientManager
							.getLogbookClientFactory().getClient();
					saveLogEntryChangeset();
					LogEntry logEntry = logbookClient
							.createLogEntry(logEntryChangeset.getLogEntry());
					setEditable(false);
					setLogEntry(logEntry);
				} catch (Exception ex) {
					setLastException(ex);
				}

			}
		});
		btnSubmit.setEnabled(false);
		btnSubmit.setText("Submit");
		btnSubmit.setEnabled(true);

		btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					saveLogEntryChangeset();
				} catch (Exception ex) {
					setLastException(ex);
				}
			}
		});
		FormData fd_btnSave = new FormData();
		fd_btnSave.left = new FormAttachment(label_vertical, 2);
		fd_btnSave.right = new FormAttachment(100, -5);
		fd_btnSave.bottom = new FormAttachment(btnSubmit, -2);
		btnSave.setLayoutData(fd_btnSave);
		btnSave.setText("Save");

		btnEnableEdit = new Button(composite, SWT.CHECK);
		FormData fd_btnEnableEdit = new FormData();
		fd_btnEnableEdit.bottom = new FormAttachment(btnSave, -2);
		fd_btnEnableEdit.left = new FormAttachment(label_vertical, 2);
		fd_btnEnableEdit.right = new FormAttachment(100, -5);
		btnEnableEdit.setLayoutData(fd_btnEnableEdit);
		btnEnableEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEditable(btnEnableEdit.getSelection());
			}
		});
		btnEnableEdit.setText("Edit Entry");
		btnEnableEdit.setSelection(editable);

		Label lblLogbooks = new Label(composite, SWT.NONE);
		FormData fd_lblLogbooks = new FormData();
		fd_lblLogbooks.left = new FormAttachment(label_vertical, 2);
		fd_lblLogbooks.top = new FormAttachment(lblDate, 10, SWT.BOTTOM);
		lblLogbooks.setLayoutData(fd_lblLogbooks);
		lblLogbooks.setText("Logbooks:");

		logbookList = new List(composite, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_logbookList = new FormData();
		fd_logbookList.left = new FormAttachment(label_vertical, 2);
		fd_logbookList.right = new FormAttachment(100, -5);
		fd_logbookList.top = new FormAttachment(lblLogbooks, 2, SWT.BOTTOM);
		logbookList.setLayoutData(fd_logbookList);

		btnAddLogbook = new Button(composite, SWT.NONE);
		btnAddLogbook.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Open a dialog which allows users to select logbooks
				StringListSelectionDialog dialog = new StringListSelectionDialog(
						parent.getShell(), logbookNames, LogEntryUtil
								.getLogbookNames(logEntryChangeset
										.getLogEntry()), "Add Logbooks");
				if (dialog.open() == IDialogConstants.OK_ID) {
					Collection<LogbookBuilder> newLogbooks = new ArrayList<LogbookBuilder>();
					for (String logbookName : dialog.getSelectedValues()) {
						newLogbooks.add(LogbookBuilder.logbook(logbookName));
					}
					logbookList.setItems(dialog.getSelectedValues().toArray(
							new String[dialog.getSelectedValues().size()]));
					logbookList.getParent().layout();
				}
			}
		});
		btnAddLogbook.setImage(ResourceManager.getPluginImage(
				"org.csstudio.logbook.ui", "icons/logbook-16.png"));
		FormData fd_btnAddLogbook = new FormData();
		fd_btnAddLogbook.left = new FormAttachment(label_vertical, 2);
		fd_btnAddLogbook.top = new FormAttachment(logbookList, 5);
		fd_btnAddLogbook.right = new FormAttachment(100, -5);
		btnAddLogbook.setLayoutData(fd_btnAddLogbook);
		btnAddLogbook.setText("Add Logbook");

		lblTags = new Label(composite, SWT.NONE);
		FormData fd_lblTags = new FormData();
		fd_lblTags.left = new FormAttachment(label_vertical, 2);
		fd_lblTags.top = new FormAttachment(btnAddLogbook, 5);
		lblTags.setLayoutData(fd_lblTags);
		lblTags.setText("Tags:");

		tagList = new List(composite, SWT.BORDER);
		FormData fd_tagList = new FormData();
		fd_tagList.left = new FormAttachment(label_vertical, 2);
		fd_tagList.top = new FormAttachment(lblTags, 2);
		fd_tagList.right = new FormAttachment(100, -5);
		tagList.setLayoutData(fd_tagList);

		btnAddTags = new Button(composite, SWT.NONE);
		btnAddTags.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Open a dialog which allows users to select tags
				StringListSelectionDialog dialog = new StringListSelectionDialog(
						parent.getShell(), tagNames, LogEntryUtil
								.getTagNames(logEntryChangeset.getLogEntry()),
						"Add Tags");
				if (dialog.open() == IDialogConstants.OK_ID) {
					Collection<TagBuilder> newTags = new ArrayList<TagBuilder>();
					for (String tagName : dialog.getSelectedValues()) {
						newTags.add(TagBuilder.tag(tagName));
					}
					tagList.setItems(dialog.getSelectedValues().toArray(
							new String[dialog.getSelectedValues().size()]));
					tagList.getParent().layout();
				}
			}
		});
		btnAddTags.setText("Add Tags");
		FormData fd_btnAddTags = new FormData();
		fd_btnAddTags.left = new FormAttachment(label_vertical, 2);
		fd_btnAddTags.top = new FormAttachment(tagList, 5);
		fd_btnAddTags.right = new FormAttachment(100, -5);
		btnAddTags.setLayoutData(fd_btnAddTags);

		tabFolder = new CTabFolder(composite, SWT.BORDER);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(label_horizontal, 2);
		fd_tabFolder.right = new FormAttachment(label_vertical, -2);
		fd_tabFolder.left = new FormAttachment(0, 5);
		fd_tabFolder.bottom = new FormAttachment(100, -5);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		tbtmAttachments = new CTabItem(tabFolder, SWT.NONE);
		tbtmAttachments.setText("Attachments");
		tabFolder.setSelection(tbtmAttachments);

		tbtmAttachmentsComposite = new Composite(tabFolder, SWT.NONE);
		tbtmAttachments.setControl(tbtmAttachmentsComposite);
		tbtmAttachmentsComposite.setLayout(new FormLayout());

		btnAddImage = new Button(tbtmAttachmentsComposite, SWT.NONE);
		btnAddImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.png" }); //$NON-NLS-1$
				dlg.setFilterNames(new String[] { "PNG Image" }); //$NON-NLS-1$
				final String filename = dlg.open();
				if (filename != null) {
					imageStackWidget.addImageFilename(filename);
				}
			}
		});
		FormData fd_btnAddImage = new FormData();
		fd_btnAddImage.left = new FormAttachment(1);
		fd_btnAddImage.bottom = new FormAttachment(100, -2);
		fd_btnAddImage.right = new FormAttachment(32);
		btnAddImage.setLayoutData(fd_btnAddImage);
		btnAddImage.setText("Add Image");
		btnAddScreenshot = new Button(tbtmAttachmentsComposite, SWT.NONE);
		btnAddScreenshot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addScreenshot(true, newWindow);
			}
		});
		FormData fd_btnAddScreenshot = new FormData();
		fd_btnAddScreenshot.left = new FormAttachment(33);
		fd_btnAddScreenshot.bottom = new FormAttachment(100, -2);
		fd_btnAddScreenshot.right = new FormAttachment(65);
		btnAddScreenshot.setLayoutData(fd_btnAddScreenshot);
		btnAddScreenshot.setText("Screenshot");

		btnCSSWindow = new Button(tbtmAttachmentsComposite, SWT.NONE);
		btnCSSWindow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addScreenshot(false, newWindow);
			}
		});
		FormData fd_btnCSSWindow = new FormData();
		fd_btnCSSWindow.left = new FormAttachment(66);
		fd_btnCSSWindow.bottom = new FormAttachment(100, -2);
		fd_btnCSSWindow.right = new FormAttachment(99);
		btnCSSWindow.setLayoutData(fd_btnCSSWindow);
		btnCSSWindow.setText("CSS Window");

		imageStackWidget = new ImageStackWidget(tbtmAttachmentsComposite,
				SWT.NONE);
		FormData fd_imageStackWidget = new FormData();
		fd_imageStackWidget.bottom = new FormAttachment(btnAddImage, -2);
		fd_imageStackWidget.right = new FormAttachment(100, -2);
		fd_imageStackWidget.top = new FormAttachment(0, 2);
		fd_imageStackWidget.left = new FormAttachment(0, 2);
		imageStackWidget.setLayoutData(fd_imageStackWidget);

		empty = new FormData();
		empty.top = new FormAttachment(0);
		empty.bottom = new FormAttachment(0);
		empty.left = new FormAttachment(0);
		empty.right = new FormAttachment(0);

		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch (evt.getPropertyName()) {
				case "editable":
					if (isEditable()) {
						getLogEntryChangeset().setLogEntryBuilder(
								LogEntryBuilder.logEntry(savedLogEntryChangeset
										.getLogEntry()));
					} else {
						// Save the current changes
						savedLogEntryChangeset
								.setLogEntryBuilder(LogEntryBuilder
										.logEntry(getLogEntryChangeset()
												.getLogEntry()));
						getLogEntryChangeset().setLogEntryBuilder(
								LogEntryBuilder.logEntry(getLogEntry()));
					}
					break;
				case "logEntry":
					init();
					updateUI();
					break;
				case "logEntryBuilder":
					updateUI();
					break;
				default:
					// updateUI();
					break;
				}
			}
		});

		init();
		// updateUI();
	}

	private void init() {
		try {
			if (getLogEntry() == null) {
				logEntryChangeset = new LogEntryChangeset();
			} else {
				logEntryChangeset = new LogEntryChangeset();
				LogEntryBuilder logEntryBuilder = LogEntryBuilder
						.logEntry(getLogEntry());
				// TODO temporary fix, in future releases the attachments will
				// be listed with the logEntry itself
				if (logEntry.getId() != null) {
					Collection<Attachment> attachments = logbookClient
							.listAttachments(logEntry.getId());
					for (Attachment attachment : attachments) {
						IFile f = null;
						try {
							f = IFileUtil.getInstance().createFileResource(
									attachment.getFileName(),
									attachment.getInputStream());
						} catch (IOException ex) {
							setLastException(ex);
						}
						IFileUtil.getInstance().registerPart(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()
										.getPartService().getActivePart(), f);
						logEntryBuilder.attach(AttachmentBuilder.attachment(f
								.getLocationURI().getPath()));
					}
				}
				logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
			}
			logEntry = logEntryChangeset.getLogEntry();
			savedLogEntryChangeset = logEntryChangeset;
			logEntryChangeset
					.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							updateUI();
						}
					});
			if (logbookClient == null) {
				logbookClient = LogbookClientManager.getLogbookClientFactory()
						.getClient();
				logbookNames = Lists.transform(new ArrayList<Logbook>(
						logbookClient.listLogbooks()),
						new Function<Logbook, String>() {
							public String apply(Logbook input) {
								return input.getName();
							};
						});
				tagNames = Lists.transform(
						new ArrayList<Tag>(logbookClient.listTags()),
						new Function<Tag, String>() {
							public String apply(Tag input) {
								return input.getName();
							};
						});
			}
			// get the list of properties and extensions to handle these
			// properties.
			IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.csstudio.logbook.ui.propertywidget");
			if (config.length > 0) {
				propertyWidgetFactories = new HashMap<String, PropertyWidgetFactory>();
				for (IConfigurationElement iConfigurationElement : config) {
					propertyWidgetFactories
							.put(iConfigurationElement
									.getAttribute("propertyName"),
									(PropertyWidgetFactory) iConfigurationElement
											.createExecutableExtension("propertywidgetfactory"));
				}
			} else {
				propertyWidgetFactories = Collections.emptyMap();
			}
		} catch (Exception ex) {
			// Failed to get a client to the logbook
			// Display exception and disable editing.
			setLastException(new Exception(ex.getCause()));
		}

	}

	private void saveLogEntryChangeset() throws Exception {
		LogEntryBuilder logEntryBuilder = LogEntryBuilder
				.logEntry(getLogEntryChangeset().getLogEntry())
				.setText(text.getText()).owner(textOwner.getText());
		Collection<TagBuilder> newTags = new ArrayList<TagBuilder>();
		for (String tagName : tagList.getItems()) {
			newTags.add(TagBuilder.tag(tagName));
		}
		logEntryBuilder.setTags(newTags);
		Collection<LogbookBuilder> newLogbooks = new ArrayList<LogbookBuilder>();
		for (String logbookName : logbookList.getItems()) {
			newLogbooks.add(LogbookBuilder.logbook(logbookName));
		}
		logEntryBuilder.setLogbooks(newLogbooks);
		Collection<AttachmentBuilder> newAttachments = new ArrayList<AttachmentBuilder>();
		for (Attachment attachment : getLogEntryChangeset().getLogEntry()
				.getAttachment()) {
			if (!imageStackWidget.getImageFilenames().contains(
					attachment.getFileName()))
				newAttachments.add(AttachmentBuilder.attachment(attachment));
		}
		for (String attachment : imageStackWidget.getImageFilenames()) {
			newAttachments.add(AttachmentBuilder.attachment(attachment)
					.inputStream(new FileInputStream(attachment)));
		}
		logEntryBuilder.setAttachments(newAttachments);
		getLogEntryChangeset().setLogEntryBuilder(logEntryBuilder);
	}

	private void updateUI() {
		// Dispose the contributed tabs, only keep the default attachments tab
		for (CTabItem cTabItem : tabFolder.getItems()) {
			if (!cTabItem.equals(tbtmAttachments)) {
				cTabItem.dispose();
			}
		}
		btnEnableEdit.setSelection(editable);
		text.setEditable(editable);
		textOwner.setEditable(editable);
		btnSave.setEnabled(editable);
		btnSubmit.setEnabled(editable);
		// logbookList.setEnabled(editable);
		// tagList.setEnabled(editable);
		btnAddLogbook.setVisible(editable);
		btnAddTags.setVisible(editable);
		// Attachment buttons need to be enabled/disabled
		btnAddImage.setVisible(editable);
		btnAddScreenshot.setVisible(editable);
		btnCSSWindow.setVisible(editable);
		if (!editable) {
			btnAddLogbook.setSize(btnAddLogbook.getSize().x, 0);
			btnAddTags.setSize(btnAddTags.getSize().x, 0);
			FormData fd_lblTags = ((FormData) lblTags.getLayoutData());
			fd_lblTags.top = new FormAttachment(logbookList, 5);
			lblTags.setLayoutData(fd_lblTags);
			// Attachment Tab Layout
			FormData fd = ((FormData) imageStackWidget.getLayoutData());
			fd.bottom = new FormAttachment(100, -2);
			imageStackWidget.setLayoutData(fd);
		} else {
			btnAddLogbook.setSize(btnAddLogbook.getSize().x, SWT.DEFAULT);
			btnAddTags.setSize(btnAddTags.getSize().x, SWT.DEFAULT);
			FormData fd_lblTags = ((FormData) lblTags.getLayoutData());
			fd_lblTags.top = new FormAttachment(btnAddLogbook, 5);
			lblTags.setLayoutData(fd_lblTags);
			// Attachment Tab Layout
			FormData fd = ((FormData) imageStackWidget.getLayoutData());
			fd.bottom = new FormAttachment(btnAddImage, -2);
			imageStackWidget.setLayoutData(fd);
		}
		btnEnableEdit.getParent().layout();
		imageStackWidget.setImageFilenames(Collections.<String> emptyList());
		LogEntry logEntry = logEntryChangeset.getLogEntry();
		if (logEntry != null) {
			// Show the logEntry
			text.setText(logEntry.getText());
			// textDate.setText(DateFormat.getDateInstance().format(
			// logEntry.getCreateDate()));
			textOwner.setText(logEntry.getOwner() == null ? "" : logEntry
					.getOwner());
			java.util.List<String> logbookNames = LogEntryUtil
					.getLogbookNames(logEntry);
			logbookList.setItems(logbookNames.toArray(new String[logbookNames
					.size()]));
			java.util.List<String> tagNames = LogEntryUtil
					.getTagNames(logEntry);
			tagList.setItems(tagNames.toArray(new String[tagNames.size()]));
			java.util.List<String> imageFileNames = new ArrayList<String>();
			for (Attachment attachment : logEntry.getAttachment()) {
				if (attachment.getFileName().endsWith(".png"))
					imageFileNames.add(attachment.getFileName());
			}
			imageStackWidget.setImageFilenames(imageFileNames);
		} else {
			text.setText("");
			textOwner.setText("");
			logbookList.setItems(new String[0]);
			tagList.setItems(new String[0]);
			imageStackWidget.setSelectedImageFile(null);
		}
		for (Entry<String, PropertyWidgetFactory> propertyFactoryEntry : propertyWidgetFactories
				.entrySet()) {
			if (editable
					|| LogEntryUtil.getPropertyNames(logEntry).contains(
							propertyFactoryEntry.getKey())) {
				CTabItem tbtmProperty = new CTabItem(tabFolder, SWT.NONE);
				tbtmProperty.setText(propertyFactoryEntry.getKey());
				AbstractPropertyWidget abstractPropertyWidget = propertyFactoryEntry
						.getValue().create(tabFolder, SWT.NONE,
								logEntryChangeset);
				tbtmProperty.setControl(abstractPropertyWidget);
				abstractPropertyWidget.setEditable(editable);
			}
		}
		this.layout();
	}

	@SuppressWarnings("nls")
	private void addScreenshot(final boolean full, final boolean newWindow) {
		// Hide the shell that displays the dialog
		// to keep the dialog itself out of the screenshot
		if (newWindow)
			getShell().setVisible(false);

		// Take the screen shot
		final Image image = full ? Screenshot.getFullScreenshot() : Screenshot
				.getApplicationScreenshot();

		// Show the dialog again
		if (newWindow)
			getShell().setVisible(true);

		// Write to file
		try {
			final File screenshot_file = File.createTempFile("screenshot",
					".png");
			screenshot_file.deleteOnExit();

			final ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };
			image.dispose();
			// Save
			loader.save(screenshot_file.getPath(), SWT.IMAGE_PNG);

			imageStackWidget.addImageFilename(screenshot_file.getPath());
		} catch (Exception ex) {
			// MessageDialog.openError(getShell(), "Error", ex.getMessage());
			setLastException(ex);
		}
	}

	private void setLastException(Exception exception) {
		errorBar.setException(exception);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		boolean oldValue = this.editable;
		this.editable = editable;
		changeSupport.firePropertyChange("editable", oldValue, this.editable);
	}

	private LogEntry getLogEntry() {
		return this.logEntry;
	}

	public void setLogEntry(LogEntry logEntry) {
		LogEntry oldValue = this.logEntry;
		this.logEntry = logEntry;
		changeSupport.firePropertyChange("logEntry", oldValue, this.logEntry);
	}

	public LogEntryChangeset getLogEntryChangeset() {
		return this.logEntryChangeset;
	}
}
