/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.view.internal.localization;

import org.eclipse.osgi.util.NLS;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 21.07.2011
 */
public final class Messages extends NLS {
    // CHECKSTYLE OFF:Name
    // CHECKSTYLE OFF:V
    public static String ChannelConfigDialog_Count;
    public static String ChannelConfigDialog_AD;
    public static String ChannelConfigDialog_Add;
    public static String ChannelConfigDialog_CantRemove;
    public static String ChannelConfigDialog_CantUpdate;
    public static String ChannelConfigDialog_DB_ErrorMsg;
    public static String ChannelConfigDialog_DB_ErrorTitel;
    public static String ChannelConfigDialog_Documents;
    public static String ChannelConfigDialog_ErrorNoInt;
    public static String ChannelConfigDialog_ErrorNoString;
    public static String ChannelConfigDialog_Input;
    public static String ChannelConfigDialog_Input_;
    public static String ChannelConfigDialog_Module;
    public static String ChannelConfigDialog_Output;
    public static String ChannelConfigDialog_Output_;
    public static String ChannelConfigDialog_Parameter_;
    public static String ChannelConfigDialog_Remove;
    public static String NodeEditor_Msg;
    public static String NodeEditor_Title;
    // CHECKSTYLE ON: V
    // CHECKSTYLE ON: Name
    private static final String BUNDLE_NAME = "org.csstudio.config.ioconfig.view.internal.localization.messages"; //$NON-NLS-1$
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        // Hidden Constructor
    }
}
