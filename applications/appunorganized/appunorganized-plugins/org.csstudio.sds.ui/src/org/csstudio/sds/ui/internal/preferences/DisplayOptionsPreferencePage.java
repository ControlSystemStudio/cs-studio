/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.ui.internal.preferences;

import java.util.List;

import org.csstudio.sds.internal.preferences.CategorizationType;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.newproperties.colorservice.NamedStyle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A preference page to set the options for the display editors.
 *
 * @author Sven Wende & Kai Meyer
 */
public final class DisplayOptionsPreferencePage extends
        FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /**
     * Constructor.
     */
    public DisplayOptionsPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setMessage("Set Display Options"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFieldEditors() {
        FieldEditor editor;

        editor = new IntegerFieldEditor(PreferenceConstants.PROP_GRID_SPACING,
                "Grid Spacing", getFieldEditorParent());
        addField(editor);

        editor = new BooleanFieldEditor(PreferenceConstants.PROP_ANTIALIASING,
                "Antialiasing", getFieldEditorParent());
        addField(editor);

        editor = new BooleanFieldEditor(
                PreferenceConstants.PROP_CLOSE_PARENT_DISPLAY,
                "Close Parent Display", getFieldEditorParent());
        addField(editor);

        String[][] labelAndValues = new String[][] {
                { "No categorization", CategorizationType.NONE.getId() },
                { "Collapsible groups", CategorizationType.DRAWER.getId() },
                { "Stack", CategorizationType.STACK.getId() } };
        editor = new RadioGroupFieldEditor(
                PreferenceConstants.PROP_WIDGET_CATEGORIZATION,
                "Categorization of SDS widgets", 1, labelAndValues,
                getFieldEditorParent(), true);
        addField(editor);

        editor = new BooleanFieldEditor(
                PreferenceConstants.PROP_WRITE_ACCESS_DENIED,
                "Deny Write Access for all widgets", getFieldEditorParent());
        addField(editor);

        Group styleComposite = new Group(getFieldEditorParent(), SWT.NONE);
        styleComposite.setText("Styles");
        GridLayout layout = new GridLayout(2,false);
        styleComposite.setLayout(layout);
        styleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        String[][] cafLabelAndValues = createLabelsAndValues();
        if (cafLabelAndValues.length == 0) {
            Label notFound = new Label(styleComposite, SWT.WRAP);
            notFound.setText("No styles found");
            notFound.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        } else {
            addField(new RadioGroupFieldEditor(PreferenceConstants.PROP_SELECTED_COLOR_AND_FONT_STYLE,
                "Select the style for colors and fonts", 1, cafLabelAndValues, styleComposite));
        }
        Label hint = new Label(styleComposite, SWT.WRAP);
        hint.setText("The styles are taken from file '/Settings/settings.xml'");
        hint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    }

    private String[][] createLabelsAndValues() {
        List<NamedStyle> styles = SdsUiPlugin.getDefault().getColorAndFontService().getStyles();
        String[][] result = new String[styles.size()][2];
        for (int i = 0; i < styles.size(); i++) {
            result[i] = new String[] {styles.get(i).getDescription() + " ("+ styles.get(i).getName() +")", styles.get(i).getName()};
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return SdsUiPlugin.getCorePreferenceStore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
    }

}
