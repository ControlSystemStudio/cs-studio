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
package org.csstudio.domain.desy.preferences;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Abstract class to wrap the value retrieval by strategy for a non null preference service.
 *
 * @author bknerr
 * @since 29.04.2011
 * @param <T> the type of the preference value
 */
abstract class AbstractPrefStrategy<T> implements IPrefStrategy<T> {
    private static final Logger LOG =
            CentralLogger.getInstance().getLogger(AbstractPrefStrategy.class);
    /**
     * Constructor.
     */
    public AbstractPrefStrategy() {
        // Empty
    }

    @Override
    @Nonnull
    public T getResult(@Nonnull final String context,
                       @Nonnull final String key,
                       @Nonnull final T defaultValue) {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            LOG.warn("Preference service unavailable, fall back to default preference.");
            return defaultValue;
        }
        return getResultByTypeStrategy(prefs, context, key, defaultValue);
    }
    @Nonnull
    protected abstract T getResultByTypeStrategy(@Nonnull final IPreferencesService prefs,
                                                 @Nonnull final String context,
                                                 @Nonnull final String key,
                                                 @Nonnull final T defaultValue);
}
