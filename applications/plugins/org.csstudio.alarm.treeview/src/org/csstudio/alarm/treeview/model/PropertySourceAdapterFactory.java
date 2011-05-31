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

package org.csstudio.alarm.treeview.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;

/**
 * Adapter factory that creates adapts alarm tree nodes to property sources.
 *
 * @author Joerg Rathlev
 */
public final class PropertySourceAdapterFactory implements IAdapterFactory {

    /**
     * Global cache for the property source of most recently selected node.
     */
    private static AlarmTreeNodePropertySource PROP_SOURCE_CACHE;

    private static PropertySourceAdapterFactory INSTANCE;

    /**
     * Constructor. May only be called by the framework via extension points!
     */
    public PropertySourceAdapterFactory() {
        if (INSTANCE != null) {
            throw new IllegalStateException("ERROR:" + PropertySourceAdapterFactory.class.getName() +
                                            " may only be instantiated once.");
        }
        INSTANCE = this;
    }


	/**
	 * {@inheritDoc}
	 */
	@Override
	@CheckForNull
    public Object getAdapter(@Nonnull final Object adaptableObject,
	                         @Nullable @SuppressWarnings("rawtypes") final Class adapterType) {

	    if (adaptableObject instanceof AbstractAlarmTreeNode &&
	        adapterType == IPropertySource.class) {

	        final AbstractAlarmTreeNode node = (AbstractAlarmTreeNode) adaptableObject;
	        if (PROP_SOURCE_CACHE == null || !PROP_SOURCE_CACHE.getNode().getLdapName().equals(node.getLdapName())) {
	            PROP_SOURCE_CACHE = new AlarmTreeNodePropertySource((AbstractAlarmTreeNode) adaptableObject);
	        }
	        return PROP_SOURCE_CACHE;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Nonnull
    @SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] {IPropertySource2.class};
	}

	/**
	 * Flags the cached propertySource to be dirty.
	 * Hence, it is reloaded from LDAP the next time.
	 */
	public static void dirty() {
	    PROP_SOURCE_CACHE = null;
	}

}
