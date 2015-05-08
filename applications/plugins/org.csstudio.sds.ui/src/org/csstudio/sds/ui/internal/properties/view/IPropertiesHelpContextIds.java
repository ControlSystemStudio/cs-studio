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
 package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.ui.PlatformUI;

/**
 * Help context ids for the properties view.
 * <p>
 * This interface contains constants only; it is not intended to be implemented
 * or extended.
 * </p>
 *
 * @author Sven Wende
 *
 */
interface IPropertiesHelpContextIds {
    /**
     * Prefix for all ids.
     */
    String PREFIX = PlatformUI.PLUGIN_ID + "."; //$NON-NLS-1$

    /**
     * Help context id for the categories action.
     */
    String CATEGORIES_ACTION = PREFIX + "properties_categories_action_context"; //$NON-NLS-1$

    /**
     * Help context id for the defaults action.
     */
    String DEFAULTS_ACTION = PREFIX + "properties_defaults_action_context"; //$NON-NLS-1$

    /**
     * Help context id for the filter action.
     */
    String FILTER_ACTION = PREFIX + "properties_filter_action_context"; //$NON-NLS-1$

    /**
     * Help context id for the filter action.
     */
    String ALIAS_ACTION = PREFIX + "properties_alias_action_context"; //$NON-NLS-1$

    /**
     * Help context id for the copy action.
     */
    String COPY_PROPERTY_ACTION = PREFIX + "properties_copy_action_context"; //$NON-NLS-1$

    /**
     * Help context id for the property sheet view.
     */
    String PROPERTY_SHEET_VIEW = PREFIX + "property_sheet_view_context"; //$NON-NLS-1$
}
