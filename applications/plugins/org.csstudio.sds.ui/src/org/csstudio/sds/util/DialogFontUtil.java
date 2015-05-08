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
 package org.csstudio.sds.util;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Control;

/**
 * A util class to set the dialog font, if this is set in the preference page.
 * Use only temporarily (24.10.07)
 * @author Kai Meyer
 *
 * @deprecated because the default dialog font should be used (23.11.2007)
 */
public final class DialogFontUtil {

    /**
     * A boolean, representing if the dialog font should be used.
     */
    private static boolean _useDialogFont = false;
    /**
     * A boolean, representing if the preference listener is initialized.
     */
    private static boolean _listenerIsInitialized = false;
    /**
     * The listener for the preference page.
     */
    private static IPropertyChangeListener _listener;

    /**
     * Sets the default dialog font on the given control and its children, only
     * when the preference-page allows this.
     * @param control The control on which the font should be set
     */
    public static void setDialogFont(final Control control) {
        if (!_listenerIsInitialized) {
            initializeListener();
        }
        if (_useDialogFont) {
//            Dialog.applyDialogFont(control);
        }
    }

    /**
     * Initializes the preference listener.
     */
    private static void initializeListener() {
        _listener = new IPropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
//                if (event.getProperty().equals(PreferenceConstants.PROP_USE_DIALOG_FONT)) {
//                    _useDialogFont = (Boolean) event.getNewValue();
//                }
            }
        };
        SdsUiPlugin.getCorePreferenceStore().addPropertyChangeListener(_listener);
//        _useDialogFont = SdsUiPlugin.getCorePreferenceStore().getBoolean(PreferenceConstants.PROP_USE_DIALOG_FONT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        SdsUiPlugin.getCorePreferenceStore().removePropertyChangeListener(_listener);
        super.finalize();
    }

}
