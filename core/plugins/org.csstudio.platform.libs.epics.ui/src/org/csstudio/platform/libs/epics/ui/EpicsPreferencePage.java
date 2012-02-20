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
package org.csstudio.platform.libs.epics.ui;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.csstudio.platform.libs.epics.EpicsPlugin.MonitorMask;
import org.csstudio.platform.libs.epics.PreferenceConstants;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference page for the 'EPICS' configuration, i.e. ChannelAccess client.
 *  <p>
 *  Original wizard-created info:<br>
 *  This class represents a preference page that
 *  is contributed to the Preferences dialog. By
 *  subclassing <samp>FieldEditorPreferencePage</samp>, we
 *  can use the field support built into JFace that allows
 *  us to create a page that is small and knows how to
 *  save, restore and apply itself.
 *  <p>
 *  This page is used to modify preferences only. They
 *  are stored in the preference store that belongs to
 *  the main plug-in class. That way, preferences can
 *  be accessed directly via the preference store.
 *  <p>
 *  @author Original author unknown
 *  @author Kay Kasemir
 */
public class EpicsPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{
	public EpicsPreferencePage()
    {
		super(GRID);

		final IPreferenceStore pref_store =
		    new ScopedPreferenceStore(InstanceScope.INSTANCE, EpicsPlugin.ID);
		setPreferenceStore(pref_store);
	}

	/** Creates the field editors.
	 *  Each field editor knows how to save and restore itself.
	 */
	@Override
    public void createFieldEditors()
    {
        final String sep = ":";  //$NON-NLS-1$
        final String context_types[][] =
        {
            { Messages.EpicsPreferencePage_CONTEXT_CAJ, Boolean.TRUE.toString() },
            { Messages.EpicsPreferencePage_CONTEXT_JNI, Boolean.FALSE.toString() }
        };
        final Composite parent = getFieldEditorParent();
        addField(new RadioGroupFieldEditor(PreferenceConstants.PURE_JAVA,
                        Messages.EpicsPreferencePage_CONTEXT,
                        context_types.length,
                        context_types,
                        parent));

        final String subscription_types[][] =
        {
            { Messages.EpicsPreferencePage_MONITOR_VALUE, MonitorMask.VALUE.name()  },
            { Messages.EpicsPreferencePage_MONITOR_ARCHIVE, MonitorMask.ARCHIVE.name()  },
            { Messages.EpicsPreferencePage_MONITOR_ALARM, MonitorMask.ALARM.name()  }
        };
        addField(new RadioGroupFieldEditor(PreferenceConstants.MONITOR,
                Messages.EpicsPreferencePage_MONITOR,
                subscription_types.length,
                subscription_types,
                parent));

        addField(new StringFieldEditor(PreferenceConstants.ADDR_LIST,
                        PreferenceConstants.ADDR_LIST + sep, parent));
        addField(new BooleanFieldEditor(PreferenceConstants.AUTO_ADDR_LIST,
                        PreferenceConstants.AUTO_ADDR_LIST + sep, parent));
        addField(new StringFieldEditor(PreferenceConstants.TIMEOUT,
                        PreferenceConstants.TIMEOUT + sep, parent));
        addField(new StringFieldEditor(PreferenceConstants.BEACON_PERIOD,
                        PreferenceConstants.BEACON_PERIOD + sep, parent));
        addField(new StringFieldEditor(PreferenceConstants.REPEATER_PORT,
                        PreferenceConstants.REPEATER_PORT + sep, parent));
        addField(new StringFieldEditor(PreferenceConstants.SERVER_PORT,
                        PreferenceConstants.SERVER_PORT + sep, parent));
        addField(new StringFieldEditor(PreferenceConstants.MAX_ARRAY_BYTES,
                        PreferenceConstants.MAX_ARRAY_BYTES + sep, parent));
    }

	@Override
    public boolean performOk()
    {
		boolean ret = super.performOk();
		EpicsPlugin.getDefault().installPreferences();
		return ret;
	}

    /** {@inheritDoc} */
	@Override
    public void init(IWorkbench workbench)
    { /* NOP */ }

	/** {@inheritDoc} */
	@Override
	public final void propertyChange(final PropertyChangeEvent event)
    {
		setMessage(Messages.EpicsPreferencePage_RESTART_MESSAGE,
		           IMessageProvider.INFORMATION);
		super.propertyChange(event);
	}
}