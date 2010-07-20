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
 package org.csstudio.alarm.table.preferences;

import java.io.File;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.service.IAlarmSoundService;
import org.csstudio.alarm.table.utility.Functions;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;


/**
 * Handling of preferences for Severity: Names, Colors, Sounds
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 20.07.2010
 */
public class JmsLogPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private CustomMediaFactory customMediaFactory;



	public JmsLogPreferencePage() {
		super(GRID);
		customMediaFactory = CustomMediaFactory.getInstance();
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.JmsLogPreferencePage_severityKeys);
	}

	@Override
	public void createFieldEditors() {
		makeKeyWord();
		adjustGridLayout();
	}

	public void init(@Nonnull final IWorkbench workbench) {
        // Nothing to do
    }

    private void makeKeyWord() {
		Group g1 = new Group(getFieldEditorParent(),SWT.NONE);
		g1.setLayout(new GridLayout(5,false));
		g1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		g1.setText("SEVERITY"); //$NON-NLS-1$
        String[] keys = defineKeys();
        String[] values = defineValues();
        String[] colors = defineColors();
        String[] sounds = defineSounds();

		Composite c1 = new Composite(g1,SWT.NONE);
		c1.setLayout(new GridLayout(1,false));
		c1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		new Label(c1,SWT.NONE).setText(Messages.JmsLogPreferencePage_key);

		Composite c2 = new Composite(g1,SWT.NONE);
		c2.setLayout(new GridLayout(1,false));
		c2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		new Label(c2,SWT.NONE).setText(Messages.JmsLogPreferencePage_value);

		Composite c3 = new Composite(g1,SWT.NONE);
		c3.setLayout(new GridLayout(1,false));
		c3.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		new Label(c3,SWT.NONE).setText(Messages.JmsLogPreferencePage_color);

		Composite c4 = new Composite(g1,SWT.NONE);
		c4.setLayout(new GridLayout(1,false));
		c4.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		new Label(c4,SWT.NONE).setText(Messages.JmsLogPreferencePage_sound);

		Composite c5 = new Composite(g1,SWT.NONE);
		c5.setLayout(new GridLayout(1,false));
		c5.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		new Label(c5,SWT.NONE).setText("");
		for(int i= 0;i<keys.length;i++){
			newRow(g1, keys[i], values[i], colors[i], sounds[i]);
		}
	}

    @Nonnull
    private String[] defineKeys() {
        String[] keys = {JmsLogPreferenceConstants.KEY0, JmsLogPreferenceConstants.KEY1,
                JmsLogPreferenceConstants.KEY2, JmsLogPreferenceConstants.KEY3,
                JmsLogPreferenceConstants.KEY4, JmsLogPreferenceConstants.KEY5,
                JmsLogPreferenceConstants.KEY6, JmsLogPreferenceConstants.KEY7,
                JmsLogPreferenceConstants.KEY8, JmsLogPreferenceConstants.KEY9};
        return keys;
    }

    @Nonnull
    private String[] defineValues() {
        String[] values = {JmsLogPreferenceConstants.VALUE0, JmsLogPreferenceConstants.VALUE1,
                JmsLogPreferenceConstants.VALUE2, JmsLogPreferenceConstants.VALUE3,
                JmsLogPreferenceConstants.VALUE4, JmsLogPreferenceConstants.VALUE5,
                JmsLogPreferenceConstants.VALUE6, JmsLogPreferenceConstants.VALUE7,
                JmsLogPreferenceConstants.VALUE8, JmsLogPreferenceConstants.VALUE9};
        return values;
    }

    @Nonnull
    private String[] defineColors() {
        String[] colors = {JmsLogPreferenceConstants.COLOR0, JmsLogPreferenceConstants.COLOR1,
                JmsLogPreferenceConstants.COLOR2, JmsLogPreferenceConstants.COLOR3,
                JmsLogPreferenceConstants.COLOR4, JmsLogPreferenceConstants.COLOR5,
                JmsLogPreferenceConstants.COLOR6, JmsLogPreferenceConstants.COLOR7,
                JmsLogPreferenceConstants.COLOR8, JmsLogPreferenceConstants.COLOR9};
        return colors;
    }

    @Nonnull
    private String[] defineSounds() {
        String[] sounds = {JmsLogPreferenceConstants.SOUND0, JmsLogPreferenceConstants.SOUND1,
                JmsLogPreferenceConstants.SOUND2, JmsLogPreferenceConstants.SOUND3,
                JmsLogPreferenceConstants.SOUND4, JmsLogPreferenceConstants.SOUND5,
                JmsLogPreferenceConstants.SOUND6, JmsLogPreferenceConstants.SOUND7,
                JmsLogPreferenceConstants.SOUND8, JmsLogPreferenceConstants.SOUND9};
        return sounds;
    }

    @Nonnull
    private Composite newRow(@Nonnull final Group parent,
                             @Nonnull final String key,
                             @Nonnull final String value,
                             @Nonnull final String color,
                             @Nonnull final String sounds) {

	    makeNameColumn(parent, key);
		makeColorColumns(parent, value, color);
		final FileFieldEditor fileEditor = makeFilebrowserColumn(parent, sounds);
        makePlaybuttonColumn(parent, fileEditor);

        return parent;
	}

    private void makeColorColumns(@Nonnull final Group parent, @Nonnull final String value, @Nonnull final String color) {
        final Composite c2 = new Composite(parent,SWT.NONE);
		c2.setLayout(new GridLayout(1,false));
		c2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		final StringFieldEditor sfeValue = new StringFieldEditor(value, "",20, c2); //$NON-NLS-1$
		StringTokenizer st = new StringTokenizer(getPreferenceStore().getString(color),","); //$NON-NLS-1$
        sfeValue.getTextControl(c2)
                .setBackground(new Color(getFieldEditorParent().getDisplay(), new RGB(Integer
                        .parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer
                        .parseInt(st.nextToken()))));
		addField(sfeValue);

		final Composite c3 = new Composite(parent,SWT.NONE);
		c3.setLayout(new GridLayout(1,false));
		c3.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		final ColorFieldEditor sfeColor = new ColorFieldEditor(color, "", c3); //$NON-NLS-1$
		sfeColor.getColorSelector().addListener(new IPropertyChangeListener(){
			public void propertyChange(@Nonnull PropertyChangeEvent event) {
				sfeColor.getColorSelector().setColorValue((RGB) event.getNewValue());
				sfeValue.getTextControl(c2).setBackground(new Color(getFieldEditorParent().getDisplay(),(RGB) event.getNewValue()));
			}
		});
		addField(sfeColor);
    }

    private void makeNameColumn(@Nonnull final Group parent, @Nonnull final String key) {
        Composite c1 = new Composite(parent,SWT.NONE);
		c1.setLayout(new GridLayout(1,false));
		c1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		StringFieldEditor sfeKey = new StringFieldEditor(key, "",20, c1); //$NON-NLS-1$
		addField(sfeKey);
    }

    @Nonnull
    private FileFieldEditor makeFilebrowserColumn(@Nonnull final Group parent, @Nonnull final String sounds) {
        final Composite c4 = new Composite(parent,SWT.NONE);
		c4.setLayout(new GridLayout(1,false));
		c4.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));
        final FileFieldEditor fileEditor = new MyFileFieldEditor(
                sounds, "", c4);
        fileEditor.setFileExtensions(new String[] {"*.mp3"});
        addField(fileEditor);
        return fileEditor;
    }

    private void makePlaybuttonColumn(@Nonnull final Group parent, @Nonnull final FileFieldEditor fileEditor) {
        final Composite c5 = new Composite(parent,SWT.NONE);
        c5.setLayout(new GridLayout(1,false));
        c5.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, true, 1, 1));
        Button button = new Button(c5, SWT.PUSH);
//        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        button.setImage(customMediaFactory.getImageFromPlugin(JmsLogsPlugin.PLUGIN_ID, "icons/run_tool.gif"));
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                IAlarmSoundService alarmSoundService = JmsLogsPlugin.getDefault().getAlarmSoundService();
                if (alarmSoundService.existsResource(fileEditor.getStringValue())) {
                    alarmSoundService.playAlarmSoundFromResource(fileEditor.getStringValue());
                }
                // TODO (jpenning) remove Functions
//                Functions.playMp3(fileEditor.getStringValue());
            }
        });
    }



	/**
     * Overrides file field editor to handle check for existing resource relative in bundle or absolute in file system
     */
    private static class MyFileFieldEditor extends FileFieldEditor {

        public MyFileFieldEditor(@Nonnull final String name, @Nonnull final String labelText, @Nonnull final Composite parent) {
            super(name, labelText, false, FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent);
        }

        @Override
        protected boolean checkState() {
            boolean result = false;

            final String text = getTextControl().getText();
            result = text.isEmpty() || JmsLogsPlugin.getDefault().getAlarmSoundService().existsResource(text);

            handleErrorMessage(result);
            return result;
        }

        private void handleErrorMessage(final boolean isOk) {
            if (isOk) {
                clearErrorMessage();
            } else {
                showErrorMessage(getErrorMessage());
            }
        }
    }


}
