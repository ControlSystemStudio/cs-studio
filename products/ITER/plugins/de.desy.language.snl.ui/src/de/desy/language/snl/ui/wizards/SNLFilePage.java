package de.desy.language.snl.ui.wizards;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

/**
 * This class takes care of the initialization and creation of the user's
 * desired C file. It determines whether a source (*.st) or header (*.sth) file
 * should be created, and initializes it with a basic main() function.
 */
public class SNLFilePage extends WizardNewFileCreationPage {
	private final IWorkbench workbench;

	private Button sectionCheckbox;

	private Button subsectionCheckbox;

	private String defaultSuffix = new String(".st");
	private String[] availableSuffixes = {".st", ".stt"};

	private static int nameCounter = 1;

	public SNLFilePage(final IWorkbench workbench,
			final IStructuredSelection selection) {
		super("File Creation Page", selection);
		this.setTitle("Create a new SNL file");
		this.workbench = workbench;
	}

	/**
	 * Creates the overall visual aspect of the page.
	 * 
	 * @param parent
	 *            The <code>Composite</code> containing the page
	 */
	@Override
	public void createControl(final Composite parent) {
		// inherit default container and name specification widgets
		super.createControl(parent);
//		final Composite composite = (Composite) this.getControl();
//		final Button[] radios = new Button[2];
//		final String[] fileTypes = new String[] { ".st", ".sth" };
//
//		final SelectionListener RadioListener = new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				for (int i = 0; i < radios.length; i++) {
//					if (radios[i].getSelection()) {
//						SNLFilePage.this.suffix = fileTypes[i];
//						for (int j = 0; j < i; j++) {
//							radios[j].setSelection(false);
//						}
//						for (int j = i + 1; j < radios.length; j++) {
//							radios[j].setSelection(false);
//						}
//					}
//				}
//			}
//		};

		// sample section generation group
//		new Label(composite, SWT.NONE).setText("");
//		final Group group = new Group(composite, SWT.NONE);
//		group.setLayout(new GridLayout());
//		group.setText("Choose the desired type:");
//		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
//				| GridData.HORIZONTAL_ALIGN_FILL));
//
//		radios[0] = new Button(group, SWT.RADIO);
//		radios[0].setSelection(true);
//		radios[0].setText("Source file (*.st)");
//		radios[0].pack();
//
//		radios[1] = new Button(group, SWT.RADIO);
//		radios[1].setText("Header file (*.sth)");
//		radios[1].pack();
//
//		for (final Button element : radios) {
//			element.pack();
//			element.addSelectionListener(RadioListener);
//		}
		this.setPageComplete(this.validatePage());
	}

	/**
	 * Creates a new file resource as requested by the user. If everything is OK
	 * then answer true. If not, false will cause the dialog to stay open.
	 * 
	 * @return whether creation was successful
	 */
	public boolean finish() {
		// create the new file resource
		String name = this.getFileName();
		final int index = name.lastIndexOf(".");
		if (index != -1) {
			boolean acceptable = false;
			for (String suffix : availableSuffixes) {
				if (suffix.equals(name.substring(index))) {
					acceptable = true;
					break;
				}
			}

			if (!acceptable)
				name = name.substring(0, index) + this.defaultSuffix;
		} else {
			name = name + this.defaultSuffix;
		}
		this.setFileName(name);

		final IFile newFile = this.createNewFile();

		// Since the file resource was created, open it for editing
		// if requested by the user
		try {
			final IWorkbenchWindow dwindow = this.workbench
					.getActiveWorkbenchWindow();
			final IWorkbenchPage page = dwindow.getActivePage();
			if (page != null) {
				IDE.openEditor(page, newFile, true);
			}
		} catch (final PartInitException e) {
			e.printStackTrace();
			return false;
		}
		SNLFilePage.nameCounter++;
		return true;
	}

	/**
	 * The implementation of this <code>WizardNewFileCreationPage</code>
	 * method generates sample headings for sections and subsections in the
	 * newly-created SNL file according to the selections of checkbox widgets
	 */
	@Override
	protected InputStream getInitialContents() {
		return null;
	}

	@Override
	protected String getNewFileLabel() {
		return "New File Label"; //$NON-NLS-1$
	}

	/**
	 * (non-Javadoc) Method declared on WizardNewFileCreationPage.
	 */
	@Override
	public void handleEvent(final Event e) {
		final Widget source = e.widget;

		if (source == this.sectionCheckbox) {
			if (!this.sectionCheckbox.getSelection()) {
				this.subsectionCheckbox.setSelection(false);
			}
			this.subsectionCheckbox.setEnabled(this.sectionCheckbox
					.getSelection());
		}

		super.handleEvent(e);
	}
}
