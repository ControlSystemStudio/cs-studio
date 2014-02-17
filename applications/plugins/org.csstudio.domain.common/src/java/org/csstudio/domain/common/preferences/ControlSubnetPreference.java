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
 */
package org.csstudio.domain.common.preferences;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant definitions for control subnets preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author hrickens
 * @since 23.12.2011
 */
public final class ControlSubnetPreference<T> extends AbstractPreference<T> {

    public static final String STRING_LIST_SEPARATOR = ";";

    public static final ControlSubnetPreference<String> CONTROL_SUBNETS = new ControlSubnetPreference<String>("control_subnets",
                                                                                                              "131.169.112.0/255.255.255.0;131.169.113.0/255.255.255.0;131.169.108.0/255.255.255.0");

    private static final Logger LOG = LoggerFactory.getLogger(ControlSubnetPreference.class);

    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    private ControlSubnetPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) ControlSubnetPreference.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getPluginID() {
        return "org.csstudio.domain.desy";
    }

    /**
    * The control subnets are defined in a string like this: "1.2.3.0/255.255.255.0;11.22.33.0/255.255.255.0", i.e. separated without blanks,
    * they are separated properly here.
    *
    * If the preferences contain no control subnets, the name of a default entry (typically 'Test') will be returned.
    *
    * @return an unmodifiable list with the control subnets
    */
    @Nonnull
    public static List<String> getControlSubnets() {
        final String resultString = CONTROL_SUBNETS.getValue();
        String[] result = resultString.split(STRING_LIST_SEPARATOR);
        if (hasNoControlSubnets(result)) {

            LOG.debug("No control subnets in preferences, using default.");
            result = CONTROL_SUBNETS.getDefaultValue().split(STRING_LIST_SEPARATOR);
        }
        final List<String> asList = Arrays.asList(result);
        LOG.debug("getControlSubnets: " + asList);
        return asList;
    }

    private static boolean hasNoControlSubnets(@Nonnull final String[] result) {
        return result.length == 0 || result.length == 1 && result[0].isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return CONTROL_SUBNETS.getValue();
    }

}
