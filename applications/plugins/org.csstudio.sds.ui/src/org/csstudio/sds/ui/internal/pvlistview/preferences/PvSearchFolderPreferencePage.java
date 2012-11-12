package org.csstudio.sds.ui.internal.pvlistview.preferences;


import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PvSearchFolderPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private final PvSearchFolderPreferenceService pvSearchFolderPreferenceService;
	private Button removeFolderButton;
	private CheckboxTableViewer tableViewer;

	public PvSearchFolderPreferencePage() {
		super();
		pvSearchFolderPreferenceService = SdsUiPlugin.getDefault()
				.getPvSearchFolderPreferenceService();
		setTitle("Process Variable Search Preferences");
		setDescription("Select folders to use for default PV searches");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite mainComposite = new Composite(parent, NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		mainComposite.setLayoutData(gd);

		createListTableViewer(mainComposite);
		createButtons(mainComposite);

		return mainComposite;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		pvSearchFolderPreferenceService
				.saveLibraryFolderPreferenceItems(getAllItems());
		return true;
	}

	protected List<PvSearchFolderPreferenceItem> getAllItems() {
		TableItem[] items = tableViewer.getTable().getItems();
		List<PvSearchFolderPreferenceItem> result = new ArrayList<PvSearchFolderPreferenceItem>(
				items.length);
		for (int i = 0; i < items.length; i++) {
			result.add((PvSearchFolderPreferenceItem) items[i].getData());
		}
		return result;
	}

	private void createListTableViewer(Composite container) {
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		tableViewer.getTable().setFont(container.getFont());
		PvSearchFolderPreferenceLabelProvider libraryFolderPreferenceProvider = new PvSearchFolderPreferenceLabelProvider();
		tableViewer.setLabelProvider(libraryFolderPreferenceProvider);
		tableViewer.setCheckStateProvider(libraryFolderPreferenceProvider);

		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {

			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				List<?> elements = (List<?>) inputElement;
				return elements.toArray();
			}
		});

		tableViewer
				.setInput(pvSearchFolderPreferenceService.loadPvSearchItems());
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				PvSearchFolderPreferenceItem item = (PvSearchFolderPreferenceItem) event
						.getElement();
				item.setChecked(event.getChecked());
			}
		});

		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateButtons();
					}
				});

	}

	private void updateButtons() {
		removeFolderButton.setEnabled(!tableViewer.getSelection().isEmpty());
	}

	private void createButtons(Composite container) {
		initializeDialogUnits(container);
		// button container
		Composite buttonContainer = new Composite(container, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 1;
		buttonLayout.marginHeight = 0;
		buttonLayout.marginWidth = 0;
		buttonContainer.setLayout(buttonLayout);
		// Add folder button
		Button addFolderButton = createPushButton(buttonContainer,
				"Add folder", true);
		addFolderButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String addedFolder = addFolderPressed();
				if (addedFolder != null) {
					PvSearchFolderPreferenceItem newItem = new PvSearchFolderPreferenceItem(
							addedFolder);
					// don't add selected folder if it's already been added
					boolean alreadyContainsItem = false;
					for (PvSearchFolderPreferenceItem item : getAllItems()) {
						if (item.getFolderPath()
								.equals(newItem.getFolderPath())) {
							alreadyContainsItem = true;
							break;
						}
					}
					if (!alreadyContainsItem) {
						tableViewer.add(newItem);
					}
				}
			}
		});
		removeFolderButton = createPushButton(buttonContainer, "Remove folder",
				false);
		removeFolderButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (!tableViewer.getSelection().isEmpty()) {
					tableViewer.getTable().remove(
							tableViewer.getTable().getSelectionIndex());
					updateButtons();
				}
			}
		});
	}

	private Button createPushButton(Composite parent, String label,
			boolean enabled) {
		Button button = new Button(parent, SWT.PUSH);
		button.setEnabled(enabled);
		button.setFont(parent.getFont());
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		button.setLayoutData(gd);
		return button;
	}

	protected String addFolderPressed() {
		ResourceSelectionDialog resourceSelectionDialog = new ResourceSelectionDialog(
				getShell(), "Select a folder which contains rules", null);
		if (resourceSelectionDialog.open() == Window.OK) {
			IPath selectedResource = resourceSelectionDialog
					.getSelectedResource();
			return selectedResource.toString();
		}
		return null;
	}

}
