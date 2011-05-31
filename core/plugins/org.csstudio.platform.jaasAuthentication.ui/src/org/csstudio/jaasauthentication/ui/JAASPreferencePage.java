/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.jaasauthentication.ui;

import java.util.Arrays;

import org.csstudio.platform.internal.jassauthentication.preference.JAASPreferenceModel;
import org.csstudio.platform.internal.jassauthentication.preference.PreferencesHelper;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for the JAAS authentication
 *
 * @author Xihui Chen
 */
public class JAASPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final String PREFERENCE_PAGE_TITLE = Messages.JAASPreferencePage_title;
	private static final String RESTART_NOTICE =
		Messages.JAASPreferencePage_restartNotice;
	private Combo sourceCombo;
	private Text configFileEntryText;
	private ModuleTableEditor moduleTableEditor;

	@Override
	protected Control createContents(Composite parent) {
		Composite composite_sourceField = createComposite(parent, 2);

		//source select combo
		createLabel(composite_sourceField, Messages.JAASPreferencePage_source);
		sourceCombo = new Combo(composite_sourceField, SWT.DROP_DOWN | SWT.READ_ONLY);
		sourceCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sourceCombo.setItems(JAASPreferenceModel.CONFIG_SOURCES);
		sourceCombo.addModifyListener(new ModifyListener() {
			@Override
            public void modifyText(ModifyEvent e) {
				setMessage(RESTART_NOTICE);
				final boolean fileSource =
					sourceCombo.getText().equals(JAASPreferenceModel.SOURCE_FILE);
				configFileEntryText.setEnabled(fileSource);
				moduleTableEditor.setEnabled(!fileSource);
			}
		});

		//configuration file entry text
		createLabel(composite_sourceField, Messages.JAASPreferencePage_fileEntry);
		configFileEntryText = new Text(composite_sourceField, SWT.SINGLE | SWT.BORDER);
		configFileEntryText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		configFileEntryText.addModifyListener(new ModifyListener() {
			@Override
            public void modifyText(ModifyEvent e) {
				setMessage(RESTART_NOTICE);
			}
		});

		//create module table editor
		Composite composite_configField = createComposite(parent, 1);
		moduleTableEditor = new ModuleTableEditor(composite_configField);

		initializeValues();

		setMessage(PREFERENCE_PAGE_TITLE);
		return new Composite(parent, SWT.NULL);

	}

	@Override
    public void init(IWorkbench workbench)
	{
	    // NOP
	}

	/**
     * Initializes states of the controls from the preference store.
     */
    private void initializeValues() {
    	sourceCombo.setText(PreferencesHelper.getConfigSource());
    	configFileEntryText.setText(PreferencesHelper.getConfigFileEntry());
    	JAASPreferenceModel.configurationEntryList.clear();
    	JAASPreferenceModel.configurationEntryList.addAll(
    			Arrays.asList(PreferencesHelper.getJAASConfigurationEntries(false)));
    	moduleTableEditor.refresh();
		final boolean fileSource =
			sourceCombo.getText().equals(JAASPreferenceModel.SOURCE_FILE);
		configFileEntryText.setEnabled(fileSource);
		moduleTableEditor.setEnabled(!fileSource);
    }

	 /**
     * Initializes states of the controls using default values
     * in the preference store.
     */
    private void initializeDefaults() {
    	sourceCombo.setText(PreferencesHelper.getDefaultConfigSource());
    	configFileEntryText.setText(PreferencesHelper.getDefaultConfigFileEntry());
    	JAASPreferenceModel.configurationEntryList.clear();
    	JAASPreferenceModel.configurationEntryList.addAll(
    			Arrays.asList(PreferencesHelper.getJAASConfigurationEntries(true)));
    	moduleTableEditor.refresh();
		final boolean fileSource =
			sourceCombo.getText().equals(JAASPreferenceModel.SOURCE_FILE);
		configFileEntryText.setEnabled(fileSource);
		moduleTableEditor.setEnabled(!fileSource);

    }

    @Override
    protected void performDefaults() {
    	super.performDefaults();
    	initializeDefaults();
    }

    @Override
    public boolean performOk() {
    	PreferencesHelper.storeValues(sourceCombo.getText(), configFileEntryText.getText());
    	return true;
    }


    /**
     * Creates composite control and sets the default layout data.
     *
     * @param parent  the parent of the new composite
     * @param numColumns  the number of columns for the new composite
     * @return the newly-created composite
     */
    private Composite createComposite(Composite parent, int numColumns) {
        Composite composite = new Composite(parent, SWT.NULL);

        //GridLayout
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        composite.setLayout(layout);

        //GridData
        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        composite.setLayoutData(data);
        return composite;
    }

    /**
     * Utility method that creates a label instance
     * and sets the default layout data.
     *
     * @param parent  the parent for the new label
     * @param text  the text for the new label
     * @return the new label
     */
    private Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.LEFT);
        label.setText(text);
        GridData data = new GridData();
        data.horizontalSpan = 1;
        data.horizontalAlignment = GridData.FILL;
        label.setLayoutData(data);
        return label;
    }


}
