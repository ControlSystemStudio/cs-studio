package org.csstudio.platform.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.SystemPropertyPreferenceEntry;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
        
        final TableViewer viewer = new TableViewer(contents,
                SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        initializeTableViewer(viewer);
        viewer.setContentProvider(new PropertiesContentProvider());
        viewer.setLabelProvider(new PropertiesLabelProvider());
        
        Button addButton = new Button(contents, SWT.PUSH);
        addButton.setText("Add ...");
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = e.widget.getDisplay().getActiveShell();
                SystemPropertyDialog dlg = new SystemPropertyDialog(shell);
                if (dlg.open() == Window.OK) {
                    SystemPropertyPreferenceEntry entry =
                        new SystemPropertyPreferenceEntry(dlg._key, dlg._value);
                    _properties.add(entry);
                }
            }
        });
        
        Button removeButton = new Button(contents, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection =
                    (IStructuredSelection) viewer.getSelection();
                for (Iterator i = selection.iterator(); i.hasNext(); ) {
                    SystemPropertyPreferenceEntry entry =
                        (SystemPropertyPreferenceEntry) i.next();
                    _properties.remove(entry);
                }
            }
        });
        
        _properties = new PropertiesModel();
        _properties.loadFromPreferences();
        viewer.setInput(_properties);
        
        return contents;
    }
    
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
        
        viewer.setColumnProperties(new String[] {"key", "value"});
        TableColumn col;
        // first column: key
        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(200);
        col.setText("Key");
        // second column: value
        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(150);
        col.setText("Value");
        
        CellEditor[] editors = new CellEditor[2];
        editors[0] = new TextCellEditor(table);
        editors[1] = new TextCellEditor(table);
        viewer.setCellEditors(editors);
        
        viewer.setCellModifier(new ICellModifier() {
            public boolean canModify(Object element, String property) {
                return (property.equals("key") || property.equals("value"));
            }

            public Object getValue(Object element, String property) {
                SystemPropertyPreferenceEntry entry =
                    (SystemPropertyPreferenceEntry) element;
                if ("key".equals(property)) {
                    return entry.getKey();
                } else if ("value".equals(property)) {
                    return entry.getValue();
                } else {
                    return null;
                }
            }

            public void modify(Object element, String property, Object value) {
                // this is to handle the case when we get an SWT item
                if (element instanceof Item) {
                    element = ((Item) element).getData();
                }
                
                SystemPropertyPreferenceEntry entry =
                    (SystemPropertyPreferenceEntry) element;
                if ("key".equals(property)) {
                    entry.setKey((String) value);
                } else if ("value".equals(property)) {
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
    
    private static class SystemPropertyDialog extends Dialog {
    	
    	private String _key = "";
    	private String _value = "";

        protected SystemPropertyDialog(Shell parentShell) {
            super(parentShell);
        }
        
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setText("System Property");
        }
        
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite parentComposite = (Composite) super.createDialogArea(parent);
            
            Composite contents = new Composite(parentComposite, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            layout.numColumns = 2;
            contents.setLayout(layout);
            contents.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
            contents.setFont(parentComposite.getFont());
            
            Label keyLabel = new Label(contents, SWT.NULL);
            keyLabel.setText("Key:");
            final Text keyText = new Text(contents, SWT.SINGLE | SWT.BORDER);
            GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            layoutData.widthHint = 250;
            keyText.setLayoutData(layoutData);
            keyText.setText(_key);
            keyText.addModifyListener(new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
            		if (e.widget == keyText) {
            			_key = keyText.getText();
            		}
            	}
            });
            
            Label valueLabel = new Label(contents, SWT.NULL);
            valueLabel.setText("Value:");
            final Text valueText = new Text(contents, SWT.SINGLE | SWT.BORDER);
            valueText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            valueText.setText(_value);
            valueText.addModifyListener(new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
            		if (e.widget == valueText) {
            			_value = valueText.getText();
            		}
            	}
            });

            return contents;
        }
        
        void setKey(String key) {
        	_key = key;
        }
        
        void setValue(String value) {
        	_value = value;
        }
    }
    
    private static class PropertiesModel {
        private List<SystemPropertyPreferenceEntry> _entries =
            new ArrayList<SystemPropertyPreferenceEntry>();
        private PropertiesContentProvider _listener;
        
        private void setListener(PropertiesContentProvider provider) {
            _listener = provider;
        }
        
        private void loadFromPreferences() {
            IEclipsePreferences platformPrefs = getPlatformPreferences();
            Preferences systemPropertyPrefs =
                platformPrefs.node("systemProperties");
            try {
                String[] keys = systemPropertyPrefs.keys();
                for (String key : keys) {
                    String value = systemPropertyPrefs.get(key, "");
                    SystemPropertyPreferenceEntry entry =
                        new SystemPropertyPreferenceEntry(key, value);
                    add(entry);
                }
            } catch (BackingStoreException e) {
                // TODO: do something about it?
            }
        }

        private IEclipsePreferences getPlatformPreferences() {
            return new InstanceScope().getNode(
                    CSSPlatformPlugin.getDefault().getBundle().getSymbolicName());
        }
        
        private void storeToPreferences() {
            IEclipsePreferences platformPrefs = getPlatformPreferences();
            Preferences systemPropertyPrefs =
                platformPrefs.node("systemProperties");
            // first, remove all of the existing entries
            try {
                systemPropertyPrefs.clear();
            } catch (BackingStoreException e) {
                // TODO Auto-generated catch block
            }
            // now write the new values into the node
            for (SystemPropertyPreferenceEntry entry : _entries) {
                systemPropertyPrefs.put(entry.getKey(), entry.getValue());
            }
        }

        private void add(SystemPropertyPreferenceEntry entry) {
            _entries.add(entry);
            if (_listener != null) {
                _listener.add(entry);
            }
        }
        
        private void remove(SystemPropertyPreferenceEntry entry) {
            _entries.remove(entry);
            if (_listener != null) {
                _listener.remove(entry);
            }
        }
    }

    private static class PropertiesContentProvider implements IStructuredContentProvider {

        private TableViewer _viewer;
        
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof PropertiesModel) {
                return ((PropertiesModel) inputElement)._entries.toArray();
            } else {
                return new Object[0];
            }
        }

        private void remove(SystemPropertyPreferenceEntry entry) {
            if (_viewer != null) {
                _viewer.remove(entry);
            }
        }

        private void add(SystemPropertyPreferenceEntry entry) {
            if (_viewer != null) {
                _viewer.add(entry);
            }
        }

        public void dispose() {
            // nothing to do
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
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

    private static class PropertiesLabelProvider extends LabelProvider
            implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;  // no images
        }

        public String getColumnText(Object element, int columnIndex) {
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
