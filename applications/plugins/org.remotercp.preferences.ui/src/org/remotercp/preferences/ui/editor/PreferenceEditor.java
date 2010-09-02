package org.remotercp.preferences.ui.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IFQID;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.preferences.ui.EditableTableItem;
import org.remotercp.preferences.ui.PreferencesUIActivator;
import org.remotercp.preferences.ui.actions.ImportPreferencesAction;
import org.remotercp.preferences.ui.images.ImageKeys;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.preferences.PreferencesUtil;

public class PreferenceEditor extends EditorPart {

	public static final String EDITOR_ID = "org.remotercp.preferences.ui.preferenceEditor";

	private TableViewer preferencesViewer;

	private SortedMap<String, String> preferencesMap;

	private SortedMap<String, String> importedPreferencesMap;

	private final static String configurationScope = "/configuration/";

	private final static String instanceScope = "/instance/";

	private ID userId;

	private enum TableColumns {

		KEY("Key", 0), LOCAL_VALUE("Local value", 1), ARROWS("Transfer", 2), REMOTE_VALUE(
				"Remote value", 3);

		private final String label;
		private int columnIndex;

		TableColumns(String label, final int columnIndex) {
			this.label = label;
			this.columnIndex = columnIndex;
		}

		public String getLabel() {
			return label;
		}

		public int getColumnIndex() {
			return columnIndex;
		}
	}

	public PreferenceEditor() {
		// nothing to do yet
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// do nothing yet
	}

	@Override
	public void doSaveAs() {
		// do nothing yet
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);

		PreferencesEditorInput editorInput = (PreferencesEditorInput) input;
		this.preferencesMap = editorInput.getPreferences();

		userId = editorInput.getUserId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isDirty() {
		List<EditableTableItem> items = (List<EditableTableItem>) this.preferencesViewer
				.getInput();

		for (EditableTableItem item : items) {
			if (item.isChanged()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * This method will be called after the save operation has been performed.
	 * 
	 * XXX: remoteService.get(0).getPreferences is quite dangerous to use. Think
	 * of a different approach
	 */
	public void refresh() {
		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				PreferencesUIActivator.getBundleContext(),
				ISessionService.class);
		try {
			List<IRemotePreferenceService> remoteService = sessionService
					.getRemoteService(IRemotePreferenceService.class,
							new ID[] { this.userId }, null);

			this.preferencesMap = remoteService.get(0).getPreferences(
					new String[] {});
			this.createViewerInput();

			// refresh dirty flag
			firePropertyChange(IEditorPart.PROP_DIRTY);

		} catch (ECFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		Group main = new Group(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
		{
			IFQID resourceID = (IFQID) this.userId.getAdapter(IFQID.class);
			new Label(main, SWT.READ_ONLY).setText("Preferenes for user: "
					+ this.userId.getName() + " (" + resourceID.getResourceName() + ")");

			this.preferencesViewer = new TableViewer(main, SWT.MULTI
					| SWT.FULL_SELECTION);
			this.preferencesViewer
					.setContentProvider(new ArrayContentProvider());
			this.preferencesViewer
					.setLabelProvider(new PreferencesLabelProvider());
			this.preferencesViewer
					.setCellModifier(new PreferencesCellModifier());
			this.preferencesViewer.setColumnProperties(new String[] {
					TableColumns.KEY.getLabel(),
					TableColumns.REMOTE_VALUE.getLabel(),
					TableColumns.ARROWS.getLabel(),
					TableColumns.LOCAL_VALUE.getLabel() });

			Table table = this.preferencesViewer.getTable();

			/*
			 * only remote value is editable. checkbox cell editor is used as a
			 * workaround for arrow-buttons
			 */
			this.preferencesViewer.setCellEditors(new CellEditor[] { null,
					new TextCellEditor(table), new CheckboxCellEditor(table),
					null });

			GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			TableColumn localKey = new TableColumn(table, SWT.LEFT);
			localKey.setText(TableColumns.KEY.getLabel());
			localKey.setWidth(250);

			TableColumn remoteValue = new TableColumn(table, SWT.LEFT);
			remoteValue.setText(TableColumns.REMOTE_VALUE.getLabel());
			remoteValue.setWidth(200);

			TableColumn arrow = new TableColumn(table, SWT.CENTER);
			arrow.setText(TableColumns.ARROWS.getLabel());
			arrow.setWidth(60);

			TableColumn localValue = new TableColumn(table, SWT.LEFT);
			localValue.setText(TableColumns.LOCAL_VALUE.getLabel());
			localValue.setWidth(200);

		}

		// this.initViewer();
		this.createViewerInput();
	}

	// /*
	// * At the beginning onyl remote preferences are loaded therefore the table
	// * does only display remote preferences. Afterwards a user can select a
	// file
	// * with local preferences which will be added to the table.
	// */
	// private void initViewer() {
	// List<EditableTableItem> items = new ArrayList<EditableTableItem>();
	//
	// for (String key : this.preferencesMap.keySet()) {
	// EditableTableItem item = new EditableTableItem();
	// String value = this.preferencesMap.get(key);
	//
	// item.setKey(key);
	// item.setRemoteValue(value);
	// items.add(item);
	// }
	// this.preferencesViewer.setInput(items);
	// }

	/*
	 * Create a mapping between local and remote preferences
	 */
	private void createViewerInput() {
		List<EditableTableItem> items = new ArrayList<EditableTableItem>();
		for (String key : this.preferencesMap.keySet()) {
			EditableTableItem item = new EditableTableItem();
			item.setKey(key);
			item.setRemoteValue(this.preferencesMap.get(key));

			if (this.importedPreferencesMap != null
					&& this.importedPreferencesMap.containsKey(key)) {
				item.setLocalValue(this.importedPreferencesMap.get(key));
			}
			items.add(item);
		}
		this.preferencesViewer.setInput(items);
	}

	@Override
	public void setFocus() {
		this.preferencesViewer.getControl().setFocus();
	}

	@SuppressWarnings("unchecked")
	public List<EditableTableItem> getViewerInput() {
		return (List<EditableTableItem>) this.preferencesViewer.getInput();
	}

	// ##############################
	// Private classes
	// ##############################

	/*
	 * Viewer label provider
	 */
	private class PreferencesLabelProvider extends LabelProvider implements
			ITableLabelProvider, IColorProvider {

		private Color changed = new Color(getEditorSite().getShell()
				.getDisplay(), 255, 250, 205);

		private Color different = new Color(getEditorSite().getShell()
				.getDisplay(), 255, 228, 225);

		private Image arrowLeft = PreferencesUIActivator
				.imageDescriptorFromPlugin(PreferencesUIActivator.PLUGIN_ID,
						ImageKeys.ARROW_LEFT).createImage();

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = null;
			EditableTableItem item = (EditableTableItem) element;
			String key = item.getKey();

			if (columnIndex == TableColumns.ARROWS.columnIndex) {
				if ((key.startsWith(configurationScope) || key
						.startsWith(instanceScope))
						/*
						 * show image-buttons only if local preferences have
						 * been imported
						 */
						&& importedPreferencesMap != null
						&& !importedPreferencesMap.isEmpty()) {
					image = arrowLeft;
				}

			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			EditableTableItem item = (EditableTableItem) element;
			String columnText = null;
			String key = item.getKey();
			switch (columnIndex) {
			case 0:
				/* remove scopes from keys */
				if (key.startsWith(configurationScope)) {
					columnText = key.replaceAll(configurationScope, "");
				} else if (key.startsWith(instanceScope)) {
					columnText = key.replaceAll(instanceScope, "");
				}
				break;
			case 1:
				/* do not display values other then above described scopes */
				if (key.startsWith(configurationScope)
						|| key.startsWith(instanceScope)) {
					columnText = item.getRemoteValue();
				}
				break;
			case 2:
				// do nothing. Image-buttons are displayed here
				break;
			case 3:
				/* do not display values other then above described scopes */
				if (key.startsWith(configurationScope)
						|| key.startsWith(instanceScope)) {
					columnText = item.getLocalValue();
				}
				break;

			default:
				break;
			}
			return columnText;
		}

		public Color getBackground(Object element) {
			Color color = null;
			EditableTableItem item = (EditableTableItem) element;
			if (item.isChanged()) {
				color = changed;
			} else if (item.getLocalValue() != null
					&& !item.getLocalValue().equals(item.getRemoteValue())) {
				color = different;
			}
			return color;
		}

		public Color getForeground(Object element) {
			// do nothing
			return null;
		}
	}

	/*
	 * Viewer cell modifier
	 */
	private class PreferencesCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			EditableTableItem item = (EditableTableItem) element;
			String key = item.getKey();
			if (key.startsWith(configurationScope)
					|| key.startsWith(instanceScope)) {

				if (property.equals(TableColumns.REMOTE_VALUE.getLabel())
						|| property.equals(TableColumns.ARROWS.getLabel())) {
					return true;
				}
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			String value = "";
			EditableTableItem item = (EditableTableItem) element;
			if (property.equals(TableColumns.REMOTE_VALUE.getLabel())) {
				value = item.getRemoteValue();
			}

			// XXX: workaround to use images as buttons in cells
			if (property.equals(TableColumns.ARROWS.getLabel())) {
				return true;
			}
			return value;
		}

		public void modify(Object element, String property, Object value) {
			TableItem tableItem = (TableItem) element;
			EditableTableItem item = (EditableTableItem) tableItem.getData();

			if (property.equals(TableColumns.REMOTE_VALUE.getLabel())) {
				item.setRemoteValue(value.toString());
				// mark preference as changed/not changed
				if (!value.toString().equals(preferencesMap.get(item.getKey()))) {
					item.setChanged(true);
				} else {
					item.setChanged(false);
				}
			}

			// BUTTON has been pushed
			if (property.equals(TableColumns.ARROWS.getLabel())) {
				if (item.getLocalValue() != null) {
					item.setRemoteValue(item.getLocalValue());
					item.setChanged(true);
				}
			}

			preferencesViewer.refresh();
			firePropertyChange(PROP_DIRTY);
		}
	}

	/**
	 * Sets the imported preferences. This method is called from the
	 * {@link ImportPreferencesAction}
	 * 
	 * @param importedPreferences
	 */
	public void setImportedPreferences(File importedPreferences) {
		try {
			this.importedPreferencesMap = PreferencesUtil
					.createPreferencesFromFile(importedPreferences);

			// create viewer input with combined, local and remote preferences
			this.createViewerInput();
		} catch (IOException e) {
			IStatus error = new Status(Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Unable to create preferences from the imported file", e);
			ErrorView.addError(error);
		}
	}
}
