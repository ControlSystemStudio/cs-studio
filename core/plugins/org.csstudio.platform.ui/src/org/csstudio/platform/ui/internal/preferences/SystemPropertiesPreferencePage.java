package org.csstudio.platform.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.SystemPropertyPreferenceEntry;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Preference page for setting up defaults for system properties.
 * 
 * @author Joerg Rathlev
 */
public final class SystemPropertiesPreferencePage extends PreferencePage
        implements IWorkbenchPreferencePage {
    
	/**
	 * Column property name for the key column.
	 */
	private static final String KEY = "key"; //$NON-NLS-1$
	
	/**
	 * Column property name for the value column.
	 */
	private static final String VALUE = "value"; //$NON-NLS-1$
	
	/**
	 * Model representing the preference entries.
	 */
    private PropertiesModel _properties;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite parent) {
        // We don't want Reset and Apply buttons on this preference page.
        // Apply does not work anyway (system properties are set during startup)
        // and Reset is not supported currently.
        noDefaultAndApplyButton();
        
        Composite contents = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        contents.setLayout(layout);
        contents.setFont(parent.getFont());
        
        Label about = new Label(contents, SWT.WRAP);
        about.setText(Messages.SystemPropertiesPreferencePage_ABOUT_TEXT);
        GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
        data.widthHint = 300;
        about.setLayoutData(data);
        
        final TableViewer viewer = new TableViewer(contents,
                SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        initializeTableViewer(viewer);
        viewer.setContentProvider(new PropertiesContentProvider());
        viewer.setLabelProvider(new PropertiesLabelProvider());
        
        Button addButton = new Button(contents, SWT.PUSH);
        addButton.setText(Messages.SystemPropertiesPreferencePage_ADD_BUTTON);
        data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        addButton.setLayoutData(data);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                Shell shell = e.widget.getDisplay().getActiveShell();
                SystemPropertyDialog dlg = new SystemPropertyDialog(shell);
                if (dlg.open() == Window.OK) {
                    SystemPropertyPreferenceEntry entry =
                        new SystemPropertyPreferenceEntry(dlg.getKey(), dlg.getValue());
                    _properties.add(entry);
                }
            }
        });
        
        final Button removeButton = new Button(contents, SWT.PUSH);
        removeButton.setText(Messages.SystemPropertiesPreferencePage_REMOVE_BUTTON);
        data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        removeButton.setLayoutData(data);
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                IStructuredSelection selection =
                    (IStructuredSelection) viewer.getSelection();
                for (Iterator i = selection.iterator(); i.hasNext(); ) {
                    SystemPropertyPreferenceEntry entry =
                        (SystemPropertyPreferenceEntry) i.next();
                    _properties.remove(entry);
                }
            }
        });

        // Add a selection listener to the viewer that will enable the
        // "Remove" button when at least one item is selected in the table.
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				removeButton.setEnabled(!event.getSelection().isEmpty());
			}
        });
        
        _properties = new PropertiesModel();
        _properties.loadFromPreferences();
        viewer.setInput(_properties);
        
        return contents;
    }
    
    /**
     * Stores the entries in the preferences.
     * @return <code>true</code>.
     */
    @Override
    public boolean performOk() {
        _properties.storeToPreferences();
        return true;
    }
    
    /**
     * Initializes the table control.
     * @param viewer the viewer.
     */
    private void initializeTableViewer(final TableViewer viewer) {
        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        viewer.setColumnProperties(new String[] {KEY, VALUE});
        TableColumn col;
        // first column: key
        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(200);
        col.setText(Messages.SystemPropertiesPreferencePage_KEY_COLUMN_LABEL);
        // second column: value
        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(150);
        col.setText(Messages.SystemPropertiesPreferencePage_VALUE_COLUMN_LABEL);
        
        CellEditor[] editors = new CellEditor[2];
        editors[0] = new TextCellEditor(table);
        editors[1] = new TextCellEditor(table);
        viewer.setCellEditors(editors);
        
        // Add a cell modifier so values can be edited inline in the table
        viewer.setCellModifier(new ICellModifier() {
            public boolean canModify(final Object element, final String property) {
                return property.equals(KEY) || property.equals(VALUE);
            }

            public Object getValue(final Object element, final String property) {
                SystemPropertyPreferenceEntry entry =
                    (SystemPropertyPreferenceEntry) element;
                if (KEY.equals(property)) {
                    return entry.getKey();
                } else if (VALUE.equals(property)) {
                    return entry.getValue();
                } else {
                    return null;
                }
            }

            public void modify(Object element, final String property, final Object value) {
                // this is to handle the case when we get an SWT item
                if (element instanceof Item) {
                    element = ((Item) element).getData();
                }
                
                SystemPropertyPreferenceEntry entry =
                    (SystemPropertyPreferenceEntry) element;
                if (KEY.equals(property)) {
                    entry.setKey((String) value);
                } else if (VALUE.equals(property)) {
                    entry.setValue((String) value);
                }
                viewer.update(entry, null);
            }
        });
    }
    

    /**
     * {@inheritDoc}
     */
    public void init(final IWorkbench workbench) {
        // nothing to do
    }
    
    
    /**
     * Model representing the preference entries. An instance of this class is
     * used as the input element for the content provider. It will notify the
     * content provider when the entries in the model are changed.
     */
    private static class PropertiesModel {
    	/**
    	 * The entries.
    	 */
        private Collection<SystemPropertyPreferenceEntry> _entries =
            new ArrayList<SystemPropertyPreferenceEntry>();
        
        /**
         * Content provider that gets notified when the entries are updated.
         */
        private PropertiesContentProvider _listener;
        
        /**
         * Sets the content provider that will be notified when the entries
         * are updated.
         * @param provider the content provider.
         */
        private void setListener(final PropertiesContentProvider provider) {
            _listener = provider;
        }
        
        /**
         * Loads the entries from the preferences.
         */
        private void loadFromPreferences() {
            _entries = SystemPropertyPreferenceEntry.loadFromPreferences();
        }

        /**
         * Stores the entries in the preferences.
         */
        private void storeToPreferences() {
        	SystemPropertyPreferenceEntry.storeToPreferences(_entries);
        }

        /**
         * Adds an entry to this model.
         * @param entry the entry.
         */
        private void add(final SystemPropertyPreferenceEntry entry) {
            _entries.add(entry);
            if (_listener != null) {
                _listener.add(entry);
            }
        }
        
        /**
         * Removes an entry from this model.
         * @param entry the entry.
         */
        private void remove(final SystemPropertyPreferenceEntry entry) {
            _entries.remove(entry);
            if (_listener != null) {
                _listener.remove(entry);
            }
        }
    }

    /**
     * Content provider for the table.
     */
    private static class PropertiesContentProvider implements IStructuredContentProvider {

        /**
         * The table viewer to which this provider provides content.
         */
    	private TableViewer _viewer;
        
        /**
         * {@inheritDoc}
         */
    	public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof PropertiesModel) {
                return ((PropertiesModel) inputElement)._entries.toArray();
            } else {
                return new Object[0];
            }
        }

        /**
         * Notifies this content provider that an entry was removed from the
         * underlying model.
         * @param entry the entry that was removed.
         */
    	private void remove(final SystemPropertyPreferenceEntry entry) {
            if (_viewer != null) {
                _viewer.remove(entry);
            }
        }

    	/**
    	 * Notifies this content provider that an entry was added to the
    	 * underlying model.
    	 * @param entry the entry that was added.
    	 */
        private void add(final SystemPropertyPreferenceEntry entry) {
            if (_viewer != null) {
                _viewer.add(entry);
            }
        }

        /**
         * Disposes of this content provider.
         */
        public void dispose() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         */
        public void inputChanged(final Viewer viewer, final Object oldInput,
        		final Object newInput) {
            if (viewer instanceof TableViewer) {
                _viewer = (TableViewer) viewer;
            }
            if (oldInput instanceof PropertiesModel) {
                ((PropertiesModel) oldInput).setListener(null);
            }
            if (newInput instanceof PropertiesModel) {
                ((PropertiesModel) newInput).setListener(this);
            }
        }
    }

    /**
     * Label provider for the table.
     */
    private static class PropertiesLabelProvider extends LabelProvider
            implements ITableLabelProvider {

        /**
         * {@inheritDoc}
         */
    	public Image getColumnImage(final Object element, final int columnIndex) {
            return null;  // no images
        }

        /**
         * {@inheritDoc}
         */
    	public String getColumnText(final Object element, final int columnIndex) {
            if (element instanceof SystemPropertyPreferenceEntry) {
                SystemPropertyPreferenceEntry entry =
                    (SystemPropertyPreferenceEntry) element;
                switch (columnIndex) {
                case 0:
                    return entry.getKey();
                case 1:
                    return entry.getValue();
                default:
                    return null;
                }
            } else {
                return null;
            }
        }
    }

}
