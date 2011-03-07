package org.csstudio.utility.pvmanager.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.epics.pvmanager.DataSource;

/** Preference GUI for Utility.PV
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
 implements IWorkbenchPreferencePage
{
	
	private static final Logger log = Logger.getLogger(PreferencePage.class.getName());
	
    /** Constructor */
    public PreferencePage()
    {
        // This way, preference changes in the GUI end up in a file under
        // {workspace}/.metadata/.plugins/org.eclipse.core.runtime/.settings/,
        // i.e. they are specific to the workspace instance.
        // Note: "org.csstudio.utility.pv" is a common setting between pv, pv.ui, pvmanager and pvmanager.ui
    	//       They need to be kept synchronized.
        final IPreferenceStore store =
            new ScopedPreferenceStore(new InstanceScope(),
            		"org.csstudio.utility.pv");
        setPreferenceStore(store);
        setMessage(Messages.PreferencePage_Message);
    }

    /** {@inheritDoc */
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** Creates the field editors.
     *  Each one knows how to save and restore itself.
     */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        try
        {
        	Set<String> supportedTypes = new HashSet<String>();
        	supportedTypes.addAll(retrieveUtilityPVSupported());
        	supportedTypes.addAll(retrievePVManagerSupported());
        	List<String> sortedList = new ArrayList<String>(supportedTypes);
        	Collections.sort(sortedList);
            final String prefixes[] = sortedList.toArray(new String[sortedList.size()]);
            final String values[][] = new String[prefixes.length][2];
            for (int i = 0; i < prefixes.length; i++)
            {           
                values[i][0] = prefixes[i] + "://";
                values[i][1] = prefixes[i];
            }
            
            // Note: "default_type" is a common setting between pv, pv.ui, pvmanager and pvmanager.ui
        	//       They need to be kept synchronized.
            addField(new ComboFieldEditor("default_type",
                    Messages.PreferencePage_DefaultPV, values, parent));
        }
        catch (Exception ex)
        {
            setMessage("Error: " + ex.getMessage(), ERROR); //$NON-NLS-1$
        }
    }
    
    private Set<String> retrieveUtilityPVSupported() {
		Set<String> items = new HashSet<String>();
		
		try {
			Class<?> clazz = Class.forName("org.csstudio.utility.pv.PVFactory");
			String[] parameters = (String[]) clazz.getMethod("getSupportedPrefixes").invoke(null);
			items.addAll(Arrays.asList(parameters));
			log.config("Loading utility.pv supported types: " + items);
			return items;
		} catch (Exception ex) {
			log.config("utility.pv not found: " + ex.getMessage());
			return Collections.emptySet();
		}
    }
    
    private Set<String> retrievePVManagerSupported() {
		Set<String> items = new HashSet<String>();
		
		try {
			Class<?> clazz = Class.forName("org.csstudio.utility.pvmanager.ConfigurationHelper");
			@SuppressWarnings("unchecked")
			Map<String, DataSource> parameters = (Map<String, DataSource>) clazz.getMethod("configuredDataSources").invoke(null);
			items.addAll(parameters.keySet());
			log.config("Loading PVManager supported types: " + items);
			return items;
		} catch (Exception ex) {
			log.config("PVManager not found: " + ex.getMessage());
			return Collections.emptySet();
		}
    }

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        setMessage(Messages.PreferencePage_RestartInfo, INFORMATION);
        super.propertyChange(event);
    }
}
