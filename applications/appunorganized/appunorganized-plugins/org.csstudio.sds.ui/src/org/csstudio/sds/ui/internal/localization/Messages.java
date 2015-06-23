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
 package org.csstudio.sds.ui.internal.localization;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle class for the SDS UI Plugin.
 *
 * @author Sven Wende
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.sds.ui.internal.localization.messages";//$NON-NLS-1$

    /**
     * The resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);


    public static String SetPropertyValueCommand_label;

    public static String SetDynamicsDescriptorCommand_label;

    public static String SetAliasDescriptorsCommand_label;

    // ==============================================================================
    // Properties View
    // ==============================================================================
    public static String Alias_text;

    public static String Alias_toolTip;

    public static String Categories_text;

    public static String Categories_toolTip;

    public static String CopyProperty_text;

    public static String Defaults_text;

    public static String Defaults_toolTip;

    public static String Filter_text;

    public static String Filter_toolTip;

    public static String PropertyViewer_property;

    public static String PropertyViewer_value;

    public static String PropertyViewer_misc;

    public static String CopyToClipboardProblemDialog_title;

    public static String CopyToClipboardProblemDialog_message;

    public static String SaveAsDialog_QUESTION;

    public static String SaveAsDialog_OVERWRITE_QUESTION;

    public static String SaveAsDialog_FILE_LABEL;

    public static String SaveAsDialog_FILE;

    public static String SaveAsDialog_TITLE;

    public static String SaveAsDialog_MESSAGE;

    public static String CSSApplicationsPreferencePage_MESSAGE;

    /**
     * Static constructor.
     */
    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * Gets the String for the specified key in its localized version according
     * to the current locale.
     *
     * @param key
     *            the key
     * @return the localized String
     */
    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}