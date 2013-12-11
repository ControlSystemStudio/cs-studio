package org.csstudio.utility.olog.ui;

import org.csstudio.security.ui.PasswordFieldEditor;
import org.csstudio.utility.olog.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class LogbookPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor urlField;
	private BooleanFieldEditor useAuthenticationField;
	private StringFieldEditor usernameField;
	private PasswordFieldEditor passwordField;
	
	private final IPreferencesService service = Platform.getPreferencesService();
	
	private ScopedPreferenceStore store;
	private Button promptForAuthentication;
	private IEclipsePreferences prefs;
	private Text pageSize;
	private Text defaultLogbook;

	@Override
	public void init(IWorkbench workbench) {
	    store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
			org.csstudio.utility.olog.Activator.PLUGIN_ID);
	    prefs = InstanceScope.INSTANCE.getNode("org.csstudio.logbook.ui");
	}
	
	@Override
	protected Control createContents(Composite parent) {
	    final Composite container = new Composite(parent, SWT.LEFT);
	    container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    container.setLayout(new GridLayout());
	    
	    urlField = new StringFieldEditor(PreferenceConstants.Olog_URL,
			"Olog Service URL:", container);
	    urlField.setPage(this);
	    urlField.setPreferenceStore(store);
	    urlField.load();
	    
	    useAuthenticationField = new BooleanFieldEditor(
			PreferenceConstants.Use_authentication, "use authentication",
			container);
	    useAuthenticationField.fillIntoGrid(container, 2);
	    useAuthenticationField
		.setPropertyChangeListener(new IPropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
			usernameField.setEnabled(useAuthenticationField.getBooleanValue(), container);
			passwordField.setEnabled(useAuthenticationField.getBooleanValue(), container);
		    }
		});
	    useAuthenticationField.setPage(this);
	    useAuthenticationField.setPreferenceStore(store);
	    useAuthenticationField.load();
	    	    
	    usernameField = new StringFieldEditor(PreferenceConstants.Username,
			"username:", container);
	    usernameField.setEnabled(useAuthenticationField.getBooleanValue(), container);
	    usernameField.setPage(this);
	    usernameField.setPreferenceStore(store);
	    usernameField.load();
	    
	    passwordField = new PasswordFieldEditor(org.csstudio.utility.olog.Activator.PLUGIN_ID,
			PreferenceConstants.Password,
			"user password:", container);
	    passwordField.setEnabled(useAuthenticationField.getBooleanValue(), container);
	    passwordField.setPage(this);
	    passwordField.setPreferenceStore(store);
	    passwordField.load();
	    
	    promptForAuthentication = new Button(container, SWT.CHECK);
	    promptForAuthentication.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
	    promptForAuthentication.setText("Prompt User for authentication with each log entry creation");
	    promptForAuthentication.setSelection(service.getBoolean("org.csstudio.logbook.ui","Autenticate.user", false, null));
	    	
	    Label labelDefaultLogbook = new Label(container, SWT.NONE);
	    labelDefaultLogbook.setText("Default logbook:");
	    
	    defaultLogbook = new Text(container, SWT.BORDER);
	    defaultLogbook.setText(service.getString("org.csstudio.logbook.ui","Default.logbook", "", null));
	    defaultLogbook.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    Label labelPageSize = new Label(container, SWT.NONE);
	    labelPageSize.setText("Page Size:");
	    
	    pageSize = new Text(container, SWT.BORDER);
	    pageSize.setText(String.valueOf(service.getInt("org.csstudio.logbook.ui","Result.size", 100, null)));
	    pageSize.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    return container;	    
	}
	
        @Override
        public boolean performOk() {
            urlField.store();
            useAuthenticationField.store();
            usernameField.store();
            passwordField.store();		   
            try {
        	prefs.put("Autenticate.user", promptForAuthentication.getSelection()? "true":"false");
        	prefs.put("Default.logbook", defaultLogbook.getText());
        	prefs.putInt("Result.size", Integer.valueOf(pageSize.getText()));
		prefs.flush();
	    } catch (BackingStoreException e) {
		e.printStackTrace();
	    }
    	    return super.performOk();    
    	    
        }
	
	@Override
	protected void performDefaults() {
	    urlField.loadDefault();
            useAuthenticationField.loadDefault();
            usernameField.loadDefault();
            passwordField.loadDefault();
            IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode("org.csstudio.logbook.ui");
            promptForAuthentication.setSelection(defaultPrefs.getBoolean("Autenticate.user", false));
            defaultLogbook.setText(defaultPrefs.get("Default.logbook", ""));
            pageSize.setText(String.valueOf(defaultPrefs.getInt("Result.size", 100)));
	    super.performDefaults();
	}

}
