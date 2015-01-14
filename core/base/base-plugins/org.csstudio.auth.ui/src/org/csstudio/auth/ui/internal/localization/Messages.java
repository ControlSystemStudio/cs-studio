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
package org.csstudio.auth.ui.internal.localization;

import org.eclipse.osgi.util.NLS;

/**
 * Access to the localization message ressources within this
 * plugin.
 *
 *
 * TODO Messages don't match what's in messages.properties
 *
 * @author Alexander Will
 * @author Kay Kasemir
 */
public final class Messages extends NLS {
	/**
	 * The bundle name of the localization messages ressources.
	 */
	private static final String BUNDLE_NAME = "org.csstudio.auth.ui.internal.localization.messages"; //$NON-NLS-1$

	public static String AuthenticationPreferencePage_LOGIN_ON_STARTUP_OFFSITE;
	public static String AuthenticationPreferencePage_PAGE_TITLE;
	public static String AuthenticationPreferencePage_LOGIN_ON_STARTUP;

	public static String LoginInformationToolbar_ButtonText;
	public static String LoginInformationToolbar_CSS;
	public static String LoginInformationToolbar_System;
	public static String LoginInformationToolbar_Teaser;
	public static String LoginInformationToolbar_Title;
	public static String LoginInformationToolbar_Xmpp;

	public static String SystemPropertiesPreferencePage_ABOUT_TEXT;
	public static String SystemPropertiesPreferencePage_ADD_BUTTON;
	public static String SystemPropertiesPreferencePage_KEY_COLUMN_LABEL;
	public static String SystemPropertiesPreferencePage_REMOVE_BUTTON;
	public static String SystemPropertiesPreferencePage_VALUE_COLUMN_LABEL;

	public static String SystemPropertyDialog_KEY_LABEL;
	public static String SystemPropertyDialog_TITLE;
	public static String SystemPropertyDialog_VALUE_LABEL;

	public static String LoginDialog_LoginAnonymous;
	public static String LoginDialog_Password;
	public static String LoginDialog_UserName;

    public static String NotLoggedIn;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
