/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.nameSpaceSearch;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Messages extends NLS {

    private static final Logger LOG = LoggerFactory.getLogger(Messages.class);

    private static final String BUNDLE_NAME = "org.csstudio.utility.nameSpaceSearch.messages"; //$NON-NLS-1$

    public static String MainView_Controller;

    public static String MainView_ecom;

    public static String MainView_facility;

    public static String MainView_Record;

    public static String MainView_searchButton;

    public static String MainView_SearchButtonFont;

    public static String MainView_ToolTip;

    public static String MainView_ToolTip_Sort;

    public static String PreferencePage_DN;

    public static String PreferencePage_LDAP;

    public static String PreferencePage_PASS;

    public static String PreferencePage_URL;

    public static String PreferencePage_RECORD_ATTRIBUTE;

    public static String PreferencePage_SEARCH_ROOT;

    public static String PreferencePage_SEARCH_ROOT_TOOL_TIP;

    /**
     * The localzation messages ressource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        // EMPTY
    }

    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            LOG.error("Key '{}' Error", key,e);
            return '!' + key + '!';
        }
    }
}
