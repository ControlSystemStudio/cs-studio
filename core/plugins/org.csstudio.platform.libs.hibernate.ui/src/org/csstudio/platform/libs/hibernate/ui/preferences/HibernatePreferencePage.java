package org.csstudio.platform.libs.hibernate.ui.preferences;

import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DDB_TIMEOUT;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DIALECT;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.HIBERNATE_CONNECTION_URL;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.SHOW_SQL;

import org.csstudio.platform.libs.hibernate.Activator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 24.04.2009
 */
public class HibernatePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    @SuppressWarnings("unused")
	private final class ListEditorExtension extends ListEditor {
        private final String _titel;
        private final String _desc;

        private ListEditorExtension(String name, String labelText, Composite parent, String titel, String desc) {
            super(name, labelText, parent);
            _titel = titel;
            _desc = desc;
        }

        @Override
        protected String[] parseString(String stringList) {
            return stringList.split(",");
        }

        @Override
        protected String getNewInputObject() {
            InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), 
                    _titel, 
                    _desc, 
                    "", 
                    null);
            if (inputDialog.open() == Window.OK) 
            {
                return inputDialog.getValue();
            }
            return null;            }

        @Override
        protected String createList(String[] items) {
            String temp = "";
            for(int i = 0; i < items.length;i++)
                temp = temp + items[i] + ",";
            return temp;            }
    }

    public HibernatePreferencePage() {
        super(GRID);
        ScopedPreferenceStore prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, 
                Activator.PLUGIN_ID);
        setPreferenceStore(prefStore);
        setDescription("Settings for the IO Configurator.");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
     * manipulate various types of preferences. Each field editor knows how to save and restore
     * itself.
     */
    public void createFieldEditors() {
        TabFolder tabs = new TabFolder(getFieldEditorParent(), SWT.NONE);
        tabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        makeHibernateDBTab(tabs);
    }
    
    private void makeHibernateDBTab(TabFolder tabs) {
        // database settings
        TabItem tabDb = new TabItem(tabs, SWT.NONE);
        tabDb.setText("Database Settings");
        Composite dbComposite = new Composite(tabs, SWT.NONE);
        dbComposite.setLayout(new GridLayout(1, true));
        dbComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tabDb.setControl(dbComposite);
        
        new Label(dbComposite, SWT.NONE);
        Label descLabel = new Label(dbComposite, SWT.NONE);
        descLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        descLabel.setText("Hibernate Settings for the IO Configurator to the Device Database");
        new Label(dbComposite, SWT.NONE);
        addField(new StringFieldEditor(DDB_USER_NAME, "DDB User &Name:", dbComposite));
        StringFieldEditor editor = new StringFieldEditor(DDB_PASSWORD, "DDB &Password:", dbComposite);
        addField(new IntegerFieldEditor(DDB_TIMEOUT, "DDB &Timeout:", dbComposite,3));
        editor.getTextControl(dbComposite).setEchoChar('*');
        addField(editor);
        addField(new StringFieldEditor(HIBERNATE_CONNECTION_DRIVER_CLASS,
                "Hibernate &connection driver:", dbComposite));
        addField(new StringFieldEditor(DIALECT, "&Dialect:", dbComposite));
        editor = new MultiLineStringFieldEditor(HIBERNATE_CONNECTION_URL, "&URL:", dbComposite);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        layoutData.widthHint = 360;
        editor.getTextControl(dbComposite).setLayoutData(layoutData);
        addField(editor);
        addField(new BooleanFieldEditor(SHOW_SQL, "&Show SQL", dbComposite));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

}
