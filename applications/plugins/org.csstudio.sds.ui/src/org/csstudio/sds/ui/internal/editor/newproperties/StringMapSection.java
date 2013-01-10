package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.internal.model.StringMapProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ColumnConfig;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ConvenienceTableWrapper;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ITableRow;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section implementation for {@link StringMapProperty}.
 * 
 * @author Sven Wende
 * 
 */
public class StringMapSection extends AbstractBaseSection<StringMapProperty> {
	private TableViewer tableViewer;
	private List<TableEditor> tableEditors = new ArrayList<TableEditor>();

	public StringMapSection(String propertyId) {
		super(propertyId);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public int getMinimumHeight() {
		return 150;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doCreateControls(final Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		parent.setLayout(new FormLayout());
		
		FormData fd;
		
		// .. table for viewing and editing the entries
		Composite tableComposite = new Composite(parent, SWT.NONE);
		
		fd = new FormData();
		fd.left = new FormAttachment(0,0);
		fd.right = new FormAttachment(100,-10);
		fd.top = new FormAttachment(0,0);
		fd.bottom = new FormAttachment(100,-50);
		tableComposite.setLayoutData(fd);

		tableComposite.setLayout(new TableColumnLayout());
		tableComposite.setLayoutData(fd);

		Table table = getWidgetFactory().createTable(tableComposite, SWT.FULL_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);

		tableViewer = ConvenienceTableWrapper.equip(table, new ColumnConfig[] { new ColumnConfig("key", "Alias Name", 100, 10, true),
				new ColumnConfig("value", "Value", 150, 30, false), new ColumnConfig("remove", "Alias Name", 30, -1, true) });

		// .. button to add new entries to the table
		Hyperlink addHyperLink = getWidgetFactory().createHyperlink(parent, "Add Entry", SWT.NONE);
		addHyperLink.setUnderlined(false);
		addHyperLink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				WidgetProperty property = getMainWidgetProperty();

				if (property != null) {
					Map<String, String> map = property.getPropertyValue();

					if (map != null) {
						int i = 0;

						while (map.containsKey("new" + i)) {
							i++;
						}

						Map<String, String> newMap = new LinkedHashMap<String, String>(map);
						newMap.put("new" + i, "value");
						applyPropertyChange(newMap);
					}
				}
			}
		});
		
		fd = new FormData();
		fd.left = new FormAttachment(0,0);
		fd.right = new FormAttachment(100,0);
		fd.top = new FormAttachment(tableComposite, 5);
		fd.bottom = new FormAttachment(100,0);
		addHyperLink.setLayoutData(fd);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doRefreshControls(StringMapProperty widgetProperty) {
		// .. (re)create the table editors used for removing single lines of the
		// table
		if (tableEditors != null) {
			// .. dispose existing editors
			for (TableEditor editor : tableEditors) {
				if (editor.getEditor() != null) {
					editor.getEditor().dispose();
				}
				editor.dispose();

			}

			tableEditors.clear();
		}

		// .. create new editors
		if (widgetProperty != null) {
			Map<String, String> map = widgetProperty.getPropertyValue();
			List<ITableRow> rows = new ArrayList<ITableRow>();

			AbstractWidgetModel widgetModel = widgetProperty.getWidgetModel();
			Map<String, String> allAliases = widgetModel.getAllInheritedAliases();
			Map<String, String> inheritedAliases = new LinkedHashMap<String, String>(allAliases);
			for (String key : map.keySet()) {
				inheritedAliases.remove(key);
			}

			for (String key : map.keySet()) {
				rows.add(new MapEntryTableRowAdapter(new LinkedHashMap<String, String>(map), key, createValueProposals(inheritedAliases)));
			}
			tableViewer.setInput(rows);

			TableItem[] items = tableViewer.getTable().getItems();
			for (int i = 0; i < items.length; i++) {
				final TableItem item = items[i];

				TableEditor tableEditor = new TableEditor(tableViewer.getTable());
				Button button = new Button(tableViewer.getTable(), SWT.FLAT);
				button.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/delete.gif"));
				button.pack();
				tableEditor.minimumWidth = button.getSize().x;
				tableEditor.horizontalAlignment = SWT.LEFT;
				tableEditor.setEditor(button, item, 2);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						MapEntryTableRowAdapter data = (MapEntryTableRowAdapter) item.getData();
						data.setValue(2, "true");
					}
				});

				tableEditors.add(tableEditor);
			}
		}
	}

	@SuppressWarnings("unused")
	private IContentProposal[] createKeyProposals(Map<String, String> inheritedAliases) {
		List<IContentProposal> result = new ArrayList<IContentProposal>();

		for (String alias : inheritedAliases.keySet()) {
			result.add(new AliasContentProposal(alias, alias + " -> " + inheritedAliases.get(alias)));
		}

		return result.toArray(new IContentProposal[result.size()]);
	}

	private IContentProposal[] createValueProposals(Map<String, String> inheritedAliases) {
		List<IContentProposal> result = new ArrayList<IContentProposal>();

		for (String k : inheritedAliases.keySet()) {
			result.add(new AliasContentProposal(k, inheritedAliases.get(k)));
		}

		return result.toArray(new IContentProposal[result.size()]);
	}

	/**
	 * Content proposal for aliases.
	 * 
	 * @author Sven Wende
	 * 
	 */
	private static class AliasContentProposal implements IContentProposal {
		private String content;
		private String label;

		public AliasContentProposal(String content, String label) {
			super();
			this.content = content;
			this.label = label;
		}

		public String getContent() {
			return "$" + content + "$";
		}

		public int getCursorPosition() {
			return 0;
		}

		public String getDescription() {
			return label;
		}

		public String getLabel() {
			return "$" + content + "$";
		}

	}

	/**
	 * Row adapter for the table entries.
	 * 
	 * @author Sven Wende
	 * 
	 */
	private class MapEntryTableRowAdapter implements ITableRow {
		private final Map<String, String> map;
		private final String key;
		private final IContentProposal[] valueProposals;

		public MapEntryTableRowAdapter(Map<String, String> map, String key, IContentProposal[] valueProposals) {
			this.map = map;
			this.key = key;
			this.valueProposals = valueProposals;
		}

		public boolean canModify(int column) {
			switch (column) {
			case 0:
			case 1:
				return true;
			default:
				break;
			}
			return true;
		}

		public RGB getBackgroundColor(int column) {
			return null;
		}

		public CellEditor getCellEditor(final int column, Composite parent) {
			switch (column) {
			case 0:
			case 1:
				TextCellEditor editor = new TextCellEditor(parent);
				editor.getControl().setBackground(COLOR_CONTROL_ACTIVE);
				IContentProposalProvider proposalProvider = new IContentProposalProvider() {
					public IContentProposal[] getProposals(String contents, int position) {
						if (column == 1) {
							return valueProposals;
						}
						return null;
					}
				};
				ContentProposalAdapter adapter = new ContentProposalAdapter(editor.getControl(), new TextContentAdapter(), proposalProvider,
						getContentProposalActivationKeystroke(), getContentProposalActivationCharacters());
				adapter.setPropagateKeys(true);
				adapter.setPopupSize(new Point(400, 300));
				return editor;
			default:
				return null;
			}
		}

		/**
		 * Returns the characters which activate the content proposal popup
		 * menu. Default is "$" Subclasses may override.
		 * 
		 * @return the characters which activate the content proposal popup menu
		 */
		private char[] getContentProposalActivationCharacters() {
			return new char[] { '$' };
		}

		/**
		 * Returns the keystroke which activates the content proposal popup
		 * menu. Default is CTRL+Space Subclasses may override.
		 * 
		 * @return the keystroke which activates the content proposal popup menu
		 */
		private KeyStroke getContentProposalActivationKeystroke() {
			KeyStroke keyStroke;
			try {
				keyStroke = KeyStroke.getInstance("Ctrl+Space");
			} catch (ParseException e1) {
				keyStroke = null;
			}

			return keyStroke;
		}

		public String getDisplayValue(int column) {
			switch (column) {
			case 0:
				return key;
			case 1:
				return map.get(key);
			default:
				return null;
			}
		}

		public String getEditingValue(int column) {
			switch (column) {
			case 0:
				return key;
			case 1:
				return map.get(key);
			default:
				return null;
			}
		}

		public Font getFont(int column) {
			switch (column) {
			case 0:
				return CustomMediaFactory.getInstance().getFont("Arial", 9, SWT.BOLD);
			case 1:
				return CustomMediaFactory.getInstance().getFont("Arial", 9, SWT.NONE);
			default:
				return null;
			}
		}

		public RGB getForegroundColor(int column) {
			switch (column) {
			case 0:
				return CustomMediaFactory.COLOR_DARK_GRAY;
			case 1:
				return CustomMediaFactory.COLOR_BLUE;
			default:
				return null;
			}
		}

		public Image getImage(int column) {
			return null;
		}

		public String getTooltip() {
			return null;
		}

		public void setValue(int column, Object value) {
			switch (column) {
			case 0:
				String val = map.get(key);
				map.remove(key);
				map.put(value.toString(), val);
				applyPropertyChange(map);
				break;
			case 1:
				map.put(key, value.toString());
				applyPropertyChange(map);
				break;
			case 2:
				map.remove(key);
				applyPropertyChange(map);
				break;
			default:
				break;
			}
		}

		public int compareTo(ITableRow o) {
			return 0;
		}

	}

}
