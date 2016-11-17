package org.csstudio.vtype.pv.ui;
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
import org.eclipse.osgi.util.NLS;

/** Access to the localization message ressources within this plugin.
 *  @author Alexander Will - original in org.csstudio.platform.libs.epics.ui
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    /**
     * The bundle name of the localization messages ressources.
     */
    private static final String BUNDLE_NAME = "org.csstudio.vtype.pv.ui.messages"; //$NON-NLS-1$

    public static String EpicsPreferencePage_RESTART_MESSAGE;
    public static String EpicsPreferencePage_CONTEXT;
    public static String EpicsPreferencePage_CONTEXT_CAJ;
    public static String EpicsPreferencePage_CONTEXT_JNI;
    public static String EpicsPreferencePage_MONITOR;
    public static String EpicsPreferencePage_MONITOR_VALUE;
    public static String EpicsPreferencePage_MONITOR_ARCHIVE;
    public static String EpicsPreferencePage_MONITOR_ALARM;
    public static String EpicsPreferencePage_DBE_PROPERTY_SUPPORTED;
    public static String EpicsPreferencePage_VAR_ARRAY_SUPPORT;
    public static String EpicsPreferencePage_VAR_ARRAY_SUPPORT_AUTO;
    public static String EpicsPreferencePage_VAR_ARRAY_SUPPORT_ENABLED;
    public static String EpicsPreferencePage_VAR_ARRAY_SUPPORT_DISABLED;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * This constructor is private since this class only provides static
     * methods.
     */
    private Messages()
    {
        // Prevent instantiation
    }
}
