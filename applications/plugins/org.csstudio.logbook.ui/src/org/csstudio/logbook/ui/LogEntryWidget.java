/**
 * 
 */
package org.csstudio.logbook.ui;

import static org.csstudio.logbook.LogEntryBuilder.logEntry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.apputil.ui.swt.Screenshot;
import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.TagBuilder;
import org.csstudio.logbook.ui.util.IFileUtil;
import org.csstudio.logbook.util.LogEntryUtil;
import org.csstudio.ui.util.dialogs.StringListSelectionDialog;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.ImageStackWidget;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wb.swt.ResourceManager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 * 
 */
public class LogEntryWidget extends Composite {

    private boolean editable;
    // SWT.DOWN is collapsed which SWT.UP is expanded
    private boolean expanded = false;

    // Model
    private LogEntryChangeset logEntryChangeset = new LogEntryChangeset();
    // private LogEntry logEntry;

    private LogbookClient logbookClient;
    // List of all the possible logbooks and tags which may be added to a
    // logEntry.
    private List<String> logbookNames = Collections.emptyList();
    private List<String> tagNames = Collections.emptyList();
    private List<String> levels = Collections.emptyList();

    // TODO
    private java.util.Map<String, PropertyWidgetFactory> propertyWidgetFactories;
    // private LogEntry logEntry;

    // UI components
    private Text text;
    private Text textDate;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);
    private Button btnAddLogbook;
    private Button btnAddTags;

    private CTabFolder tabFolder;
    private Composite tbtmImgAttachmentsComposite;
    private CTabItem tbtmAttachments;
    private Composite tbtmFileAttachmentsComposite;
    private CTabItem tbtmFileAttachments;
    private Composite tbtmPropertyTreeComposite;
    private CTabItem tbtmPropertyTree;

    private ImageStackWidget imageStackWidget;
    private Button btnAddImage;
    private Button btnAddScreenshot;
    private Button btnCSSWindow;
    private Label lblTags;
    private Composite composite;
    private ErrorBar errorBar;
    private final boolean newWindow;

    private String imageToSelect;

    private final String[] supportedImageTypes = new String[] { "*.png",
	    "*.jpg", "*.jpeg", "*.tiff", "*.gif" };
    private Label lblNewLabel;
    private MultipleSelectionCombo<String> multiSelectionComboLogbook;
    private MultipleSelectionCombo<String> multiSelectionComboTag;
    private Button showDetailsButton;
    private Label label;
    private LinkTable linkTable;
    private Button removeSelectedButton;
    private Button addFileButton;
    private Button btnCurrentContext;
    private PropertyTree propertyTree;
    private Combo level;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.removePropertyChangeListener(listener);
    }

    public LogEntryWidget(final Composite parent, int style,
	    final boolean newWindow, boolean editable) {
	super(parent, style);
	this.newWindow = newWindow;
	this.editable = editable;
	GridLayout gridLayout = new GridLayout(1, false);
	gridLayout.verticalSpacing = 2;
	gridLayout.marginWidth = 2;
	gridLayout.marginHeight = 2;
	gridLayout.horizontalSpacing = 2;
	setLayout(gridLayout);

	errorBar = new ErrorBar(this, SWT.NONE);

	composite = new Composite(this, SWT.NONE | SWT.DOUBLE_BUFFERED);
	GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	gd_composite.heightHint = 638;
	composite.setLayoutData(gd_composite);
	composite.setLayout(new FormLayout());

	label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
	FormData fd_label = new FormData();
	fd_label.left = new FormAttachment(0, 1);
	fd_label.right = new FormAttachment(100, -1);
	if (expanded) {
	    fd_label.top = new FormAttachment(60, -28);
	} else {
	    fd_label.top = new FormAttachment(100, -28);
	}
	label.setLayoutData(fd_label);
	label.addMouseMoveListener(new MouseMoveListener() {
	    // TODO add upper and lower bounds
	    public void mouseMove(MouseEvent e) {
		FormData fd = (FormData) label.getLayoutData();
		int calNumerator = (int) (fd.top.numerator + (e.y * 100)
			/ e.display.getActiveShell().getClientArea().height);
		fd.top = new FormAttachment(calNumerator <= 100 ? calNumerator
			: 100, fd.top.offset);
		label.setLayoutData(fd);
		label.getParent().layout();
	    }
	});
	label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));

	Label lblDate = new Label(composite, SWT.NONE);
	FormData fd_lblDate = new FormData();
	fd_lblDate.left = new FormAttachment(0, 4);
	lblDate.setLayoutData(fd_lblDate);
	lblDate.setText("Date:");

	textDate = new Text(composite, SWT.NONE);
	textDate.setEditable(false);
	FormData fd_textDate = new FormData();
	fd_textDate.left = new FormAttachment(lblDate, 6);
	textDate.setLayoutData(fd_textDate);

	text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP
		| SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
	text.setEditable(editable);
	text.addFocusListener(new FocusAdapter() {
	    @Override
	    public void focusLost(FocusEvent e) {

		try {
		    LogEntryBuilder logEntryBuilder = logEntry(
			    logEntryChangeset.getLogEntry()).setText(
			    text.getText());
		    logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		} catch (IOException e1) {
		    setLastException(e1);
		}
	    }
	});
	text.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CR) {
		    text.getParent().layout();
		}
	    }
	});
	FormData fd_text = new FormData();
	fd_text.right = new FormAttachment(100, -5);
	fd_text.left = new FormAttachment(0, 5);
	text.setLayoutData(fd_text);

	Label lblLogbooks = new Label(composite, SWT.NONE);
	FormData fd_lblLogbooks = new FormData();
	fd_lblLogbooks.left = new FormAttachment(0, 5);
	lblLogbooks.setLayoutData(fd_lblLogbooks);
	lblLogbooks.setText("Logbooks:");

	btnAddLogbook = new Button(composite, SWT.NONE);
	btnAddLogbook.setEnabled(editable);
	fd_text.bottom = new FormAttachment(btnAddLogbook, -4);
	fd_lblLogbooks.top = new FormAttachment(btnAddLogbook, 5, SWT.TOP);
	btnAddLogbook.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// Open a dialog which allows users to select logbooks
		StringListSelectionDialog dialog = new StringListSelectionDialog(
			parent.getShell(), logbookNames,
			multiSelectionComboLogbook.getSelection(),
			"Add Logbooks");
		if (dialog.open() == IDialogConstants.OK_ID) {
		    try {
			LogEntryBuilder logEntryBuilder = LogEntryBuilder
				.logEntry(logEntryChangeset.getLogEntry());
			Collection<LogbookBuilder> newLogbooks = new ArrayList<LogbookBuilder>();
			for (String logbookName : dialog.getSelectedValues()) {
			    newLogbooks.add(LogbookBuilder.logbook(logbookName));
			}
			logEntryBuilder.setLogbooks(newLogbooks);
			logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		    } catch (IOException e1) {
			setLastException(e1);
		    }
		}
	    }
	});
	btnAddLogbook.setImage(ResourceManager.getPluginImage(
		"org.csstudio.logbook.ui", "icons/logbook-add-16.png"));
	FormData fd_btnAddLogbook = new FormData();
	fd_btnAddLogbook.bottom = new FormAttachment(label, -46);
	fd_btnAddLogbook.left = new FormAttachment(100, -40);
	fd_btnAddLogbook.right = new FormAttachment(100, -5);
	btnAddLogbook.setLayoutData(fd_btnAddLogbook);

	lblTags = new Label(composite, SWT.NONE);
	FormData fd_lblTags = new FormData();
	fd_lblTags.left = new FormAttachment(0, 5);
	lblTags.setLayoutData(fd_lblTags);
	lblTags.setText("Tags:");

	btnAddTags = new Button(composite, SWT.NONE);
	btnAddTags.setImage(ResourceManager.getPluginImage(
		"org.csstudio.logbook.ui", "icons/add_tag.png"));
	btnAddTags.setEnabled(editable);
	fd_lblTags.top = new FormAttachment(btnAddTags, 5, SWT.TOP);
	btnAddTags.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// Open a dialog which allows users to select tags
		StringListSelectionDialog dialog = new StringListSelectionDialog(
			parent.getShell(), tagNames, multiSelectionComboTag
				.getSelection(), "Add Tags");
		if (dialog.open() == IDialogConstants.OK_ID) {
		    try {
			LogEntryBuilder logEntryBuilder = LogEntryBuilder
				.logEntry(logEntryChangeset.getLogEntry());
			Collection<TagBuilder> newTags = new ArrayList<TagBuilder>();
			for (String tagName : dialog.getSelectedValues()) {
			    newTags.add(TagBuilder.tag(tagName));
			}
			logEntryBuilder.setTags(newTags);
			logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		    } catch (IOException e1) {
			setLastException(e1);
		    }
		}
	    }
	});
	FormData fd_btnAddTags = new FormData();
	fd_btnAddTags.bottom = new FormAttachment(label, -6);
	fd_btnAddTags.right = new FormAttachment(100, -5);
	fd_btnAddTags.left = new FormAttachment(100, -40);
	btnAddTags.setLayoutData(fd_btnAddTags);

	level = new Combo(composite, SWT.NONE);
	fd_text.top = new FormAttachment(level, 6);
	fd_lblDate.top = new FormAttachment(level, 4, SWT.TOP);
	fd_textDate.top = new FormAttachment(level, 4, SWT.TOP);
	FormData fd_combo = new FormData();
	fd_combo.top = new FormAttachment(0, 5);
	fd_combo.right = new FormAttachment(100, -5);
	level.setLayoutData(fd_combo);
	level.addSelectionListener(new SelectionAdapter() {
	    
	    @Override
	    public void widgetSelected(SelectionEvent e) {

		try {
		    LogEntryBuilder logEntryBuilder = logEntry(
			    logEntryChangeset.getLogEntry()).setLevel(level.getItem(level.getSelectionIndex()));
		    logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		} catch (IOException e1) {
		    setLastException(e1);
		}
	    }
	});

	lblNewLabel = new Label(composite, SWT.NONE);
	FormData fd_lblNewLabel = new FormData();
	fd_lblNewLabel.top = new FormAttachment(level, 4, SWT.TOP);
	fd_lblNewLabel.right = new FormAttachment(level, -5);
	lblNewLabel.setLayoutData(fd_lblNewLabel);
	lblNewLabel.setText("Level:");

	multiSelectionComboLogbook = new MultipleSelectionCombo<String>(
		composite, SWT.NONE);
	FormData fd_multiSelectionCombo = new FormData();
	fd_multiSelectionCombo.top = new FormAttachment(text, 4);
	fd_multiSelectionCombo.right = new FormAttachment(btnAddLogbook, -5);
	fd_multiSelectionCombo.left = new FormAttachment(lblLogbooks, 6);
	multiSelectionComboLogbook.setLayoutData(fd_multiSelectionCombo);
	multiSelectionComboLogbook
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("selection")) {
			    try {
				LogEntryBuilder logEntryBuilder = LogEntryBuilder
					.logEntry(logEntryChangeset
						.getLogEntry());
				Collection<LogbookBuilder> newLogbooks = new ArrayList<LogbookBuilder>();
				for (String logbookName : multiSelectionComboLogbook
					.getSelection()) {
				    newLogbooks.add(LogbookBuilder
					    .logbook(logbookName));
				}
				logEntryBuilder.setLogbooks(newLogbooks);
				logEntryChangeset
					.setLogEntryBuilder(logEntryBuilder);
			    } catch (IOException e1) {
				setLastException(e1);
			    }
			}
		    }
		});

	multiSelectionComboTag = new MultipleSelectionCombo<String>(composite, SWT.NONE);
	FormData fd_multiSelectionCombo_1 = new FormData();
	fd_multiSelectionCombo_1.top = new FormAttachment(btnAddTags, -3, SWT.TOP);
	fd_multiSelectionCombo_1.right = new FormAttachment(
		multiSelectionComboLogbook, 0, SWT.RIGHT);
	fd_multiSelectionCombo_1.left = new FormAttachment(
		multiSelectionComboLogbook, 0, SWT.LEFT);
	multiSelectionComboTag.setLayoutData(fd_multiSelectionCombo_1);
	multiSelectionComboTag
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("selection")) {
			    try {
				LogEntryBuilder logEntryBuilder = LogEntryBuilder
					.logEntry(logEntryChangeset
						.getLogEntry());
				Collection<TagBuilder> newTags = new ArrayList<TagBuilder>();
				for (String tagName : multiSelectionComboTag
					.getSelection()) {
				    newTags.add(TagBuilder.tag(tagName));
				}
				logEntryBuilder.setTags(newTags);
				logEntryChangeset
					.setLogEntryBuilder(logEntryBuilder);
			    } catch (IOException e1) {
				setLastException(e1);
			    }
			}

		    }
		});

	showDetailsButton = new Button(composite, SWT.FLAT | SWT.LEFT);
	showDetailsButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// Toggle the expand/collapse
		setExpanded(!isExpanded());
	    }
	});
	FormData fd_btnNewButton = new FormData();
	fd_btnNewButton.left = new FormAttachment(label, 0, SWT.LEFT);
	fd_btnNewButton.right = new FormAttachment(100, -2);
	fd_btnNewButton.top = new FormAttachment(label, 0);
	fd_btnNewButton.bottom = new FormAttachment(label, 24, SWT.BOTTOM);
	showDetailsButton.setLayoutData(fd_btnNewButton);
	showDetailsButton.setText("Details");

	tabFolder = new CTabFolder(composite, SWT.BORDER | SWT.DOUBLE_BUFFERED);
	FormData fd_tabFolder = new FormData();
	fd_tabFolder.bottom = new FormAttachment(100, -2);
	fd_tabFolder.right = new FormAttachment(100, -2);
	fd_tabFolder.left = new FormAttachment(0, 2);
	fd_tabFolder.top = new FormAttachment(showDetailsButton, 2);
	tabFolder.setLayoutData(fd_tabFolder);
	tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
		SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

	tbtmAttachments = new CTabItem(tabFolder, SWT.NONE);
	tbtmAttachments.setText("Images");
	tabFolder.setSelection(tbtmAttachments);

	tbtmImgAttachmentsComposite = new Composite(tabFolder, SWT.NONE);
	tbtmAttachments.setControl(tbtmImgAttachmentsComposite);
	tbtmImgAttachmentsComposite.setLayout(new FormLayout());

	btnAddImage = new Button(tbtmImgAttachmentsComposite, SWT.NONE);
	btnAddImage.setVisible(editable);
	btnAddImage.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		final FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
		dlg.setFilterExtensions(supportedImageTypes); //$NON-NLS-1$
		final String filename = dlg.open();
		if (filename != null) {
		    try {
			File imgFile = new File(filename);
			LogEntryBuilder logEntryBuilder = LogEntryBuilder
				.logEntry(logEntryChangeset.getLogEntry())
				.attach(AttachmentBuilder.attachment(
					imgFile.getName()).inputStream(
					new FileInputStream(imgFile)));
			imageToSelect = imgFile.getName();
			logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		    } catch (Exception e1) {
			setLastException(e1);
		    }
		}
	    }
	});
	FormData fd_btnAddImage = new FormData();
	fd_btnAddImage.left = new FormAttachment(1);
	fd_btnAddImage.bottom = new FormAttachment(100, -2);
	fd_btnAddImage.right = new FormAttachment(32);
	btnAddImage.setLayoutData(fd_btnAddImage);
	btnAddImage.setText("Add Image");

	btnAddScreenshot = new Button(tbtmImgAttachmentsComposite, SWT.NONE);
	btnAddScreenshot.setVisible(editable);
	btnAddScreenshot.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		try {
		    LogEntryBuilder logEntryBuilder = logEntry(
			    logEntryChangeset.getLogEntry()).attach(
			    addScreenshot(true, newWindow));
		    logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		} catch (IOException e1) {
		    setLastException(e1);
		}
	    }
	});
	FormData fd_btnAddScreenshot = new FormData();
	fd_btnAddScreenshot.left = new FormAttachment(33);
	fd_btnAddScreenshot.bottom = new FormAttachment(100, -2);
	fd_btnAddScreenshot.right = new FormAttachment(65);
	btnAddScreenshot.setLayoutData(fd_btnAddScreenshot);
	btnAddScreenshot.setText("Screenshot");

	btnCSSWindow = new Button(tbtmImgAttachmentsComposite, SWT.NONE);
	btnCSSWindow.setVisible(editable);
	btnCSSWindow.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		try {
		    LogEntryBuilder logEntryBuilder = logEntry(
			    logEntryChangeset.getLogEntry()).attach(
			    addScreenshot(false, newWindow));
		    logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		} catch (IOException e1) {
		    setLastException(e1);
		}
	    }
	});
	FormData fd_btnCSSWindow = new FormData();
	fd_btnCSSWindow.left = new FormAttachment(66);
	fd_btnCSSWindow.bottom = new FormAttachment(100, -2);
	fd_btnCSSWindow.right = new FormAttachment(99);
	btnCSSWindow.setLayoutData(fd_btnCSSWindow);
	btnCSSWindow.setText("CSS Window");

	imageStackWidget = new ImageStackWidget(tbtmImgAttachmentsComposite,
		SWT.NONE);
	imageStackWidget.setEditable(editable);
	FormData fd_imageStackWidget = new FormData();
	fd_imageStackWidget.bottom = new FormAttachment(btnAddImage, -2);
	fd_imageStackWidget.right = new FormAttachment(100, -2);
	fd_imageStackWidget.top = new FormAttachment(0, 2);
	fd_imageStackWidget.left = new FormAttachment(0, 2);
	imageStackWidget.setLayoutData(fd_imageStackWidget);
	imageStackWidget
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @SuppressWarnings("unchecked")
		    @Override
		    public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName()
				.equals("imageInputStreamsMap")) {
			    Collection<String> oldImages = ((Map<String, byte[]>) evt
				    .getOldValue()).keySet();
			    Collection<String> newImages = (((Map<String, byte[]>) evt
				    .getNewValue()).keySet());
			    if (!oldImages.equals(newImages)) {
				Collection<String> removedImages = new ArrayList<String>(
					oldImages);
				removedImages.removeAll(newImages);
				Collection<String> addedImages = new ArrayList<String>(
					newImages);
				addedImages.removeAll(oldImages);

				try {
				    LogEntryBuilder logEntryBuilder = LogEntryBuilder
					    .logEntry(logEntryChangeset
						    .getLogEntry());
				    for (String removedImage : removedImages) {
					logEntryBuilder
						.removeAttachment(removedImage);
				    }
				    logEntryChangeset
					    .setLogEntryBuilder(logEntryBuilder);
				} catch (IOException e) {
				    setLastException(e);
				}
			    }
			}
		    }
		});

	tbtmFileAttachments = new CTabItem(tabFolder, SWT.NONE);
	tbtmFileAttachments.setText("Files");
	tabFolder.setSelection(tbtmFileAttachments);

	tbtmFileAttachmentsComposite = new Composite(tabFolder, SWT.NONE);
	tbtmFileAttachments.setControl(tbtmFileAttachmentsComposite);
	tbtmFileAttachmentsComposite.setLayout(new FormLayout());

	errorBar = new ErrorBar(tbtmFileAttachmentsComposite, SWT.NONE);
	FormData fd_errorBar = new FormData();
	fd_errorBar.right = new FormAttachment(100, -2);
	fd_errorBar.top = new FormAttachment(0, 2);
	fd_errorBar.left = new FormAttachment(0, 2);
	errorBar.setLayoutData(fd_errorBar);

	btnCurrentContext = new Button(tbtmFileAttachmentsComposite, SWT.NONE);
	btnCurrentContext.setVisible(editable);
	btnCurrentContext.setText("Attach context");
	btnCurrentContext.setToolTipText("Attach the in focus CS-Studio file");

	FormData fd_btnCurrentContext = new FormData();
	fd_btnCurrentContext.left = new FormAttachment(0, 5);
	fd_btnCurrentContext.bottom = new FormAttachment(100, -5);
	fd_btnCurrentContext.right = new FormAttachment(32);
	btnCurrentContext.setLayoutData(fd_btnCurrentContext);

	btnCurrentContext.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		IEditorInput input = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage()
			.getActiveEditor().getEditorInput();
		IFile editorFile = (IFile) input.getAdapter(IFile.class);
		if (editorFile != null) {
		    try {
			File file = new File(editorFile.getLocationURI());
			if (file != null) {
			    LogEntryBuilder logEntryBuilder = LogEntryBuilder
				    .logEntry(logEntryChangeset.getLogEntry())
				    .attach(AttachmentBuilder.attachment(
					    file.getName()).inputStream(
					    new FileInputStream(file)));
			    logEntryChangeset
				    .setLogEntryBuilder(logEntryBuilder);
			}
		    } catch (IOException e1) {
			setLastException(e1);
		    }
		}
	    }
	});

	addFileButton = new Button(tbtmFileAttachmentsComposite, SWT.NONE);
	addFileButton.setVisible(editable);
	addFileButton.setText("Attach file");
	addFileButton.setToolTipText("Add a file attachment");
	FormData fd_btnAddFileButtonButton = new FormData();
	fd_btnAddFileButtonButton.left = new FormAttachment(33);
	fd_btnAddFileButtonButton.bottom = new FormAttachment(100, -5);
	fd_btnAddFileButtonButton.right = new FormAttachment(65);
	addFileButton.setLayoutData(fd_btnAddFileButtonButton);
	addFileButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		final FileDialog fileDialog = new FileDialog(getShell(),
			SWT.SAVE);
		final String filename = fileDialog.open();
		if (filename != null) {
		    try {
			File file = new File(filename);
			LogEntryBuilder logEntryBuilder = LogEntryBuilder
				.logEntry(logEntryChangeset.getLogEntry())
				.attach(AttachmentBuilder.attachment(
					file.getName()).inputStream(
					new FileInputStream(file)));
			logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		    } catch (IOException e1) {
			setLastException(e1);
		    }
		}

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	removeSelectedButton = new Button(tbtmFileAttachmentsComposite,
		SWT.NONE);
	removeSelectedButton.setVisible(editable);
	removeSelectedButton.setText("Remove selected");
	removeSelectedButton.setToolTipText("Remove the selected attachments");
	FormData fd_btnRemoveSelectedButton = new FormData();
	fd_btnRemoveSelectedButton.left = new FormAttachment(66);
	fd_btnRemoveSelectedButton.bottom = new FormAttachment(100, -5);
	fd_btnRemoveSelectedButton.right = new FormAttachment(100, -5);
	removeSelectedButton.setLayoutData(fd_btnRemoveSelectedButton);
	removeSelectedButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// Button behaviour various based on if the logEntry is being
		// displayed in view mode or edit mode
		try {
		    LogEntryBuilder logEntryBuilder = LogEntryBuilder
			    .logEntry(getLogEntry());

		    if (isEditable()) {
			// remove the selected attachments
			for (Attachment attachment : linkTable.getSelection()) {
			    logEntryBuilder.removeAttachment(attachment
				    .getFileName());
			}
			logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
		    } else {
			// Open the selected files

		    }
		} catch (IOException e1) {
		    errorBar.setException(e1);
		}

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	linkTable = new LinkTable(tbtmFileAttachmentsComposite, SWT.NONE) {

	    @Override
	    public void linkAction(Attachment attachment) {

		try {
		    IWorkbenchPage page = PlatformUI.getWorkbench()
			    .getActiveWorkbenchWindow().getActivePage();
		    IFile ifile = IFileUtil.getInstance().createFileResource(
			    attachment.getFileName(),
			    attachment.getInputStream());
		    IEditorDescriptor desc = PlatformUI.getWorkbench()
			    .getEditorRegistry()
			    .getDefaultEditor(attachment.getFileName());
		    if (desc != null) {
			IEditorPart part = page.openEditor(new FileEditorInput(
				ifile), desc.getId());
			IFileUtil.getInstance().registerPart(part, ifile);
		    } else {
			super.linkAction(attachment);
		    }
		} catch (IOException | PartInitException e) {
		    errorBar.setException(e);
		}
	    }
	};
	FormData fd_linkTable = new FormData();
	fd_linkTable.top = new FormAttachment(errorBar, 2);
	fd_linkTable.bottom = new FormAttachment(removeSelectedButton);
	fd_linkTable.right = new FormAttachment(100, -2);
	fd_linkTable.left = new FormAttachment(0, 2);
	linkTable.setLayoutData(fd_linkTable);

	tbtmPropertyTree = new CTabItem(tabFolder, SWT.NONE);
	tbtmPropertyTree.setText("Properties");
	tabFolder.setSelection(tbtmPropertyTree);

	tbtmPropertyTreeComposite = new Composite(tabFolder, SWT.NONE);
	tbtmPropertyTree.setControl(tbtmPropertyTreeComposite);
	tbtmPropertyTreeComposite.setLayout(new GridLayout());

	propertyTree = new PropertyTree(tbtmPropertyTreeComposite, SWT.NONE);
	propertyTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	tabFolder.showItem(tbtmAttachments);

	final AtomicReference<PropertyChangeEvent> eventRef = new AtomicReference<PropertyChangeEvent>();
	
	this.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		eventRef.set(evt);
		switch (evt.getPropertyName()) {
		case "expand":
		    FormData fd = ((FormData) label.getLayoutData());
		    if (expanded) {
			fd.top = new FormAttachment(60, -28);
			showDetailsButton.setText("Hide details");
		    } else {
			fd.top = new FormAttachment(100, -28);
			showDetailsButton.setText("Show Details");
		    }
		    label.setLayoutData(fd);
		    label.getParent().layout();
		    break;
		case "logEntry":
		    getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
			    if (eventRef.getAndSet(null) == null) {
				return;
			    } else {
				init();
			    }
			}
		    });
		    break;
		case "logEntryBuilder":
		    updateUI();
		    break;
		default:
		    break;
		}
	    }
	});

	try {
	    logbookClient = LogbookClientManager.getLogbookClientFactory()
		    .getClient();
	} catch (Exception e1) {
	    setLastException(e1);
	}
	// Attachment buttons need to be enabled/disabled
	if (!editable) {
	    // Attachment Tab Layout
	    FormData fd = ((FormData) imageStackWidget.getLayoutData());
	    fd.bottom = new FormAttachment(100, -2);
	    imageStackWidget.setLayoutData(fd);
	} else {
	    // Attachment Tab Layout
	    FormData fd = ((FormData) imageStackWidget.getLayoutData());
	    fd.bottom = new FormAttachment(btnAddImage, -2);
	    imageStackWidget.setLayoutData(fd);
	}
	Runnable initialize = new Runnable() {

	    @Override
	    public void run() {
		if (logbookClient != null) {
		    try {
			logbookNames = Lists.transform(new ArrayList<Logbook>(
				logbookClient.listLogbooks()),
				new Function<Logbook, String>() {
				    public String apply(Logbook input) {
					return input.getName();
				    };
				});
			tagNames = Lists.transform(new ArrayList<Tag>(
				logbookClient.listTags()),
				new Function<Tag, String>() {
				    public String apply(Tag input) {
					return input.getName();
				    };
				});
			levels = logbookClient.listLevels();
			getDisplay().asyncExec(new Runnable() {

			    @Override
			    public void run() {
				updateUI();
			    }
			});
		    } catch (final Exception e) {
			setLastException(e);
		    }
		}
	    }
	};
	Executors.newCachedThreadPool().execute(initialize);
	try {
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
	} catch (Exception e) {
	    propertyWidgetFactories = Collections.emptyMap();
	    setLastException(e);
	}
    }

    private void init() {
	try {
	    final LogEntry logEntry = this.logEntryChangeset.getLogEntry();
	    // logEntryChangeset = new LogEntryChangeset();
	    logEntryChangeset
		    .addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
			    getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
				    updateUI();
				}
			    });
			}
		    });
	    if (logEntry != null) {
		LogEntryBuilder logEntryBuilder = LogEntryBuilder
			.logEntry(logEntry);
		// TODO temporary fix, in future releases the attachments will
		// be listed with the logEntry itself
		if (logEntry.getId() != null && logbookClient != null) {
		    Runnable retriveAttachments = new Runnable() {
			@Override
			public void run() {
			    try {
				LogEntryBuilder logEntryBuilder = LogEntryBuilder
					.logEntry(logEntryChangeset
						.getLogEntry());
				Collection<AttachmentBuilder> attachments = new ArrayList<AttachmentBuilder>();
				for (Attachment attachment : logbookClient
					.listAttachments(logEntry.getId())) {
				    attachments.add(AttachmentBuilder
					    .attachment(attachment));
				}
				logEntryBuilder.setAttachments(attachments);
				logEntryChangeset
					.setLogEntryBuilder(logEntryBuilder);
			    } catch (Exception ex) {

			    }
			}
		    };
		    Executors.newCachedThreadPool().execute(retriveAttachments);
		}
		this.logEntryChangeset.setLogEntryBuilder(logEntryBuilder);
	    }
	} catch (Exception ex) {
	    // Failed to get a client to the logbook
	    // Display exception and disable editing.
	    setLastException(ex);
	}
    }

    private void updateUI() {
    	if (isDisposed()) {
    		return;
    	}
	// Dispose the contributed tabs, only keep the default attachments tab
	for (CTabItem cTabItem : tabFolder.getItems()) {
	    if (!cTabItem.equals(tbtmAttachments)
		    && !cTabItem.equals(tbtmFileAttachments)
		    && !cTabItem.equals(tbtmPropertyTree)) {
		cTabItem.dispose();
	    }
	}
	LogEntry logEntry = null;
	try {
	    logEntry = this.logEntryChangeset.getLogEntry();
	} catch (IOException e1) {
	    setLastException(e1);
	}
	if (logEntry != null) {
	    // Show the logEntry
	    text.setText(logEntry.getText());	    
	    if(!level.getItems().equals(levels)){
		level.setItems(levels.toArray(new String[levels.size()]));
	    }
	    if(levels.contains(logEntry.getLevel())){
		level.select(levels.indexOf(logEntry.getLevel()));
	    }
	    textDate.setText(DateFormat.getDateInstance().format(
		    logEntry.getCreateDate() == null ? System
			    .currentTimeMillis() : logEntry.getCreateDate()));
	    if (!multiSelectionComboLogbook.getItems().equals(logbookNames)) {
		multiSelectionComboLogbook.setItems(logbookNames);
	    }
	    multiSelectionComboLogbook.setSelection(LogEntryUtil.getLogbookNames(logEntry));
	    if (!multiSelectionComboTag.getItems().equals(tagNames)) {
		multiSelectionComboTag.setItems(tagNames);
	    }
	    multiSelectionComboTag.setSelection(LogEntryUtil.getTagNames(logEntry));

	    if (logEntry.getAttachment().size() > 0) {
		setExpanded(true);
		tabFolder.setSelection(tbtmAttachments);
	    } else if (logEntry.getProperties().size() > 0) {
		setExpanded(true);
		tabFolder.setSelection(tbtmPropertyTree);
	    } else {
		setExpanded(false);
	    }
	    Map<String, InputStream> imageInputStreamsMap = new HashMap<String, InputStream>();
	    for (Attachment attachment : logEntry.getAttachment()) {
		if (Arrays.asList(supportedImageTypes).contains(
			"*"
				+ attachment.getFileName().substring(
					attachment.getFileName().lastIndexOf(
						"."),
					attachment.getFileName().length()))) {
		    try {
			if (attachment.getInputStream().available() > 0) {
			    imageInputStreamsMap.put(attachment.getFileName(),
				    attachment.getInputStream());
			}
		    } catch (IOException e) {
			setLastException(e);
		    }
		}
	    }
	    try {
		imageStackWidget.setImageInputStreamsMap(imageInputStreamsMap);
		if (imageToSelect != null) {
		    imageStackWidget.setSelectedImageName(imageToSelect);
		    imageToSelect = null;
		}
	    } catch (IOException e) {
		setLastException(e);
	    }
	    linkTable.setFiles(Collections.<Attachment> emptyList());
	    linkTable.setFiles(new ArrayList<Attachment>(logEntry.getAttachment()));
	    propertyTree.setProperties(new ArrayList<Property>(logEntry.getProperties()));
	} else {
	    text.setText("");
	    level.setItems(new String[0]);
	    multiSelectionComboLogbook.setItems(Collections.<String> emptyList());
	    multiSelectionComboTag.setItems(Collections.<String> emptyList());
	    imageStackWidget.setSelectedImageName(null);
	    linkTable.setFiles(Collections.<Attachment> emptyList());
	}
	if (propertyWidgetFactories != null) {
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
	}
	composite.layout();
    }

    @SuppressWarnings("nls")
    private AttachmentBuilder addScreenshot(final boolean full,
	    final boolean newWindow) {
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
	    imageToSelect = screenshot_file.getName();
	    return AttachmentBuilder
		    .attachment(screenshot_file.getName())
		    .inputStream(new FileInputStream(screenshot_file.getPath()));
	} catch (Exception ex) {
	    setLastException(ex);
	}
	return null;
    }

    public void setLastException(final Exception exception) {
	getDisplay().asyncExec(new Runnable() {

	    @Override
	    public void run() {
	    	if (!errorBar.isDisposed()) {
	    		errorBar.setException(exception);
	    	}
	    }
	});

    }

    public boolean isEditable() {
	return editable;
    }

    public void setEditable(boolean editable) {
	boolean oldValue = this.editable;
	this.editable = editable;
	changeSupport.firePropertyChange("editable", oldValue, this.editable);
    }

    public LogEntry getLogEntry() throws IOException {
	return this.logEntryChangeset.getLogEntry();
    }

    public void setLogEntry(LogEntry logEntry) {

	try {
	    LogEntry oldValue = this.logEntryChangeset.getLogEntry();
	    this.logEntryChangeset = new LogEntryChangeset(logEntry);
	    changeSupport.firePropertyChange("logEntry", oldValue,
		    this.logEntryChangeset.getLogEntry());
	} catch (IOException e) {
	    setLastException(e);
	}

    }

    public java.util.List<String> getLogbookNames() {
	return logbookNames;
    }

    public void setLogbookNames(java.util.List<String> logbookNames) {
	java.util.List<String> oldValue = this.logbookNames;
	this.logbookNames = logbookNames;
	changeSupport.firePropertyChange("logbookNames", oldValue,
		this.logbookNames);
    }

    public java.util.List<String> getTagNames() {
	return tagNames;
    }

    public void setTagNames(java.util.List<String> tagNames) {
	java.util.List<String> oldValue = this.tagNames;
	this.tagNames = tagNames;
	changeSupport.firePropertyChange("tagNames", oldValue, this.tagNames);
    }

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
	return expanded;
    }

    /**
     * @param expanded
     *            the expanded to set
     */
    public void setExpanded(boolean expanded) {
	boolean oldValue = this.expanded;
	this.expanded = expanded;
	changeSupport.firePropertyChange("expand", oldValue, this.expanded);
    }
}
