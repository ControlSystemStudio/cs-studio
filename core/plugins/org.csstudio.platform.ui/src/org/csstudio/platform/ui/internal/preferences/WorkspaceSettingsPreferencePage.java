package org.csstudio.platform.ui.internal.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkspaceSettingsPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	/**
	 * Global PLUGIN_ID of the page.
	 */
	public static final String ID = "de.smartpls.postprocessing.preferences.WorkspaceSettingsPreferencePage"; //$NON-NLS-1$
	
	 private Button autoRefreshButton;

	public WorkspaceSettingsPreferencePage() {
		super();
	}

	public WorkspaceSettingsPreferencePage(String title) {
		super(title);
	}

	public WorkspaceSettingsPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite c = createComposite(parent);
		createAutoRefreshControls(c);
		return c;
	}

	public void init(IWorkbench workbench) {

	}
	
	 /**
     * The default button has been pressed.
     */
    @Override
	protected void performDefaults() {
        boolean autoRefresh = ResourcesPlugin.getPlugin()
                .getPluginPreferences().getDefaultBoolean(
                        ResourcesPlugin.PREF_AUTO_REFRESH);
        autoRefreshButton.setSelection(autoRefresh);

        super.performDefaults();
    }

    /**
     * The user has pressed Ok. Store/apply this page's values appropriately.
     */
    @Override
	public boolean performOk() {
        Preferences preferences = ResourcesPlugin.getPlugin()
                .getPluginPreferences();

        boolean autoRefresh = autoRefreshButton.getSelection();
        preferences.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, autoRefresh);
        
        return super.performOk();
    }
	
	/**
     * Creates the composite which will contain all the preference controls for
     * this page.
     * 
     * @param parent
     *            the parent composite
     * @return the composite for this page
     */
    protected Composite createComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        return composite;
    }
    
	/**
     * Create the Refresh controls
     * 
     * @param parent
     */
    private void createAutoRefreshControls(Composite parent) {

        this.autoRefreshButton = new Button(parent, SWT.CHECK);
        this.autoRefreshButton.setText("Auto-Refresh Workspace");

        boolean autoRefresh = ResourcesPlugin.getPlugin()
                .getPluginPreferences().getBoolean(
                        ResourcesPlugin.PREF_AUTO_REFRESH);
        this.autoRefreshButton.setSelection(autoRefresh);
    }

}
