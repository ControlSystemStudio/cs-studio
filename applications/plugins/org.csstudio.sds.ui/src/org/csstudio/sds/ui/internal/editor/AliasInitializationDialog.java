package org.csstudio.sds.ui.internal.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.ui.util.LayoutUtil;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * This modeless dialog presents the predefined aliases of a newly created
 * widget and lets the user edit the alias values. It is not intended to create
 * or remove aliases. <br>
 * Usage: <br>
 * Pass the aliases of a widget model to the dialog
 * constructor and open the dialog. You can retrieve the modified descriptors
 * after the dialog is closed by calling
 * {@link AliasInitializationDialog#getAliasDescriptors()}.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class AliasInitializationDialog extends Dialog {
	/**
	 * A POJO for a value and his key.
	 * @author Stefan Hofer
	 */
	private final class KeyValuePair {
		/**
		 * The key.
		 */
		private String _key;
		/**
		 * The value.
		 */
		private String _value;

		/**
		 * Constructor.
		 * @param key The key
		 * @param value The value
		 */
		private KeyValuePair(final String key, final String value) {
			super();
			_key = key;
			_value = value;
		}

		/**
		 * Returns the key.
		 * @return The key
		 */
		public String getKey() {
			return _key;
		}

		/**
		 * Sets the key.
		 * @param key The new key
		 */
		public void setKey(final String key) {
			_key = key;
		}

		/**
		 * Returns the value.
		 * @return The value
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Sets the value.
		 * @param value The new value
		 */
		public void setValue(final String value) {
			_value = value;
		}

	}

	/**
	 * Label provider for the alias table.
	 * 
	 * @author Sven Wende
	 * 
	 */
	public final class AliasTableLabelProvider implements ITableLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {

			switch (columnIndex) {
			case 0:
				return CustomMediaFactory.getInstance().getImageFromPlugin(
						SdsUiPlugin.PLUGIN_ID, "icons/parameter.png"); //$NON-NLS-1$
			default:
				return null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			String result = "";
			KeyValuePair keyValuePair = (KeyValuePair) element;

			switch (columnIndex) {
			case 0:
				result = keyValuePair.getKey();
				break;
			case 1:
				result = keyValuePair.getValue();
				break;
			default:
				break;
			}

			assert result != null : "result!=null"; //$NON-NLS-1;

			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		public void addListener(final ILabelProviderListener listener) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void dispose() {
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isLabelProperty(final Object element,
				final String property) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public void removeListener(final ILabelProviderListener listener) {
		}
	}

	/**
	 * A cell modifier that allows only the modification of the "value" column.
	 * 
	 * @author Stefan Hofer
	 * @version $Revision$
	 */
	static final class AliasTableCellModifier implements ICellModifier {

		/**
		 * {@inheritDoc}
		 */
		public boolean canModify(final Object element, final String property) {
			switch (findColumnIndex(property)) {
			case 0:
				return true;
			case 1:
				return true;
			default:
				return false;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValue(final Object element, final String property) {
			Object result = null;

			KeyValuePair keyValuePair = (KeyValuePair) element;

			switch (findColumnIndex(property)) {
			case 0:
				result = keyValuePair.getKey();
				break;
			case 1:
				result = keyValuePair.getValue();
				break;
			default:
				break;
			}
			assert result != null : "result!=null"; //$NON-NLS-1$;

			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		public void modify(final Object element, final String property,
				final Object value) {
			KeyValuePair pair;
			
			// @see ICellModifier#modify(element, property, value)
			if (element instanceof Item) {
				pair = (KeyValuePair) ((Item) element).getData();
			} else {
				pair = (KeyValuePair) element;
			}

			String valueString = (String) value;

			switch (findColumnIndex(property)) {
			case 0:
				pair.setKey(valueString);
				break;
			case 1:
				pair.setValue(valueString);
				break;
			case 2:
				break;
			default:
				break;
			}

			_aliasTableViewer.refresh(pair);
		}

		/**
		 * Gets the colunmn index for the specified property.
		 * 
		 * @param property
		 *            the property
		 * 
		 * @return the column index
		 */
		@SuppressWarnings("synthetic-access")
		private int findColumnIndex(final String property) {
			int result = 0;
			// Find the index of the column
			Object[] columnHeaders = _aliasTableViewer.getColumnProperties();
			for (int i = 0; i < columnHeaders.length; i++) {
				if (columnHeaders[i].equals(property)) {
					result = i;
				}
			}
			return result;
		}

	}

	/**
	 * Use these column names if you want to present aliases in a table.
	 * 
	 * @author Stefan Hofer
	 * @version $Revision$
	 * 
	 */
	static enum Columns {
		/**
		 * First column.
		 */
		first("Name"),

		/**
		 * Second column.
		 */
		second("Value");

		/**
		 * Same as {@link #values()} but as Strings.
		 * 
		 * @return The String representation of the columns.
		 */
		public static String[] toStringArray() {
			final Columns[] values = values();
			String[] result = new String[values.length];

			for (int i = 0; i < result.length; i++) {
				result[i] = values[i].toString();
			}

			return result;
		}

		/**
		 * The name of the column.
		 */
		private String _name;

		/**
		 * Private constructor.
		 * 
		 * @param name
		 *            The name of this column.
		 */
		private Columns(final String name) {
			_name = name;
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return _name;
		}

	}

	/**
	 * A static reference to the current dialog object's TableViewer so that the
	 * CellModifier can refresh it.
	 */
	private static TableViewer _aliasTableViewer;

	/**
	 * The {@link List} of aliases.
	 */
	private List<KeyValuePair> _aliasesList;

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            The parent of the dialog.
	 * @param aliases
	 *            The aliases that should be edited. The dialog works on this
	 *            instance.
	 */
	public AliasInitializationDialog(final Shell parentShell,
			final Map<String, String> aliases) {
		super(parentShell);
		this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
				| SWT.BORDER | SWT.RESIZE);

		_aliasesList = new ArrayList<KeyValuePair>();

		for (String key : aliases.keySet()) {
			_aliasesList.add(new KeyValuePair(key, aliases.get(key)));
		}

	}

	/**
	 * @return The possibly modified alias descriptors.
	 */
	public Map<String, String> getAliasDescriptors() {
		Map<String, String> result = new HashMap<String, String>();

		for (KeyValuePair pair : _aliasesList) {
			if (pair.getKey() != null && pair.getKey().length() > 0) {
				result.put(pair.getKey(), pair.getValue());
			}
		}
		return result;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Fill in alias values");
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composit = (Composite) super.createDialogArea(parent);
		_aliasTableViewer = createAliasesTable(composit);
		return composit;
	}

	/**
	 * Creates a table viewer, which enables the user to modify alias values.
	 * 
	 * @param parent
	 *            the parent composite
	 * 
	 * @return the created viewer
	 */
	private TableViewer createAliasesTable(final Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0));
		group.setText("Aliases");
		group.setLayoutData(LayoutUtil
				.createGridDataForHorizontalFillingCell(100));

		// create table
		final Table table = new Table(group, SWT.FULL_SELECTION
				| SWT.SCROLL_PAGE);
		table.setLinesVisible(true);
		table.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.CENTER, 0);
		column.setText(Columns.first.toString());
		column.setWidth(140);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(Columns.second.toString());
		column.setWidth(140);

		final TableViewer viewer = new TableViewer(table);
		viewer.setColumnProperties(Columns.toStringArray());

		// configure cell editors
		CellEditor[] editors = new CellEditor[Columns.values().length];
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		viewer.setCellEditors(editors);

		// cell modifiers
		viewer.setCellModifier(new AliasTableCellModifier());

		// content and label provider
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new AliasTableLabelProvider());

		viewer.setInput(_aliasesList.toArray());

		viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE,
				new Transfer[] { TextTransfer.getInstance() },
				new DropTargetAdapter() {

					@Override
					public void dragEnter(final DropTargetEvent event) {
						for (TransferData transfer : event.dataTypes) {
							if (TextTransfer.getInstance().isSupportedType(
									transfer)) {
								event.detail = DND.DROP_COPY;
								break;
							}
						}
						super.dragEnter(event);
					}

					@Override
					public void drop(final DropTargetEvent event) {
						if (event.data instanceof String) {
							setAliasValue((String) event.data,
									(TableItem) event.item);
						}
					}
				});

		return viewer;
	}

	/**
	 * Sets the value of the AliasDescriptor, which is the data of the given
	 * item.
	 * 
	 * @param value
	 *            The value for the AliasDescriptor
	 * @param item
	 *            The TableItem, which contains the AliasDescriptor
	 */
	private void setAliasValue(final String value, final TableItem item) {
		if (item != null && item.getData() instanceof KeyValuePair) {
			KeyValuePair keyValuePair = (KeyValuePair) item.getData();
			keyValuePair.setValue(value);
			_aliasTableViewer.refresh();
		}
		_aliasTableViewer.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		_aliasTableViewer.getTable().setFocus();
		// finish last edit
		try {
			for (CellEditor editor : _aliasTableViewer.getCellEditors()) {
				editor.deactivate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.okPressed();
	}

}
