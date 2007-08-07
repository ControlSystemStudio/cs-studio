package org.csstudio.platform.ui.internal.preferences;

import org.csstudio.platform.OnsiteSubnetPreferences;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for the onsite subnets.
 * 
 * @author Joerg Rathlev
 */
public class OnsiteSubnetPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Default constructor.
	 */
	public OnsiteSubnetPreferencePage() {
		super(SWT.NULL);
		setMessage("Onsite Subnets");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void createFieldEditors() {
		addField(new ListEditor(OnsiteSubnetPreferences.PREFERENCE_KEY,
				"Subnets: ", getFieldEditorParent()){
			
			public String[] parseString(String stringList){
				return stringList.split(",");
			}
			
			public String getNewInputObject(){
				AddSubnetDialog dialog = new AddSubnetDialog(getShell());
				dialog.open();
				return null;
			}
			
			public String createList(String[] items){
				StringBuilder temp = new StringBuilder();
				for(int i = 0; i < items.length; i++) {
					temp.append(items[i]);
					temp.append(",");
				}
				return temp.toString();
			}
			
			
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IPreferenceStore doGetPreferenceStore() {
		return CSSPlatformUiPlugin.getCorePreferenceStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench)
    {
        // nothing to do
	}

}
