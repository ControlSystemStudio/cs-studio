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
package org.csstudio.domain.common.junit;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The run condition that evaluates whether current operating system belongs to a specific os group.
 *
 * @author bknerr
 * @since 30.05.2011
 */
public class OsCondition implements IRunCondition {

    /**
     * Used in {@link org.csstudio.domain.desy.junit.RunIf} annotation as 'constant expression'.
     * Unfortunately, enums can only be used indirectly
     * ({@link OsGroup}.WIN.name() != 'constant expression').
     *
     * Attention, hence the values of the final strings must match the names of the
     * OsGroup enums.
     */
    public static final String WIN = "WIN";
    public static final String LINUX = "LINUX";
    public static final String MAC = "MAC_OS";
    public static final String SUN = "SUN";
    public static final String HP = "HP_UX";
    public static final String FREE_BSD = "FREE_BSD";


    private final ImmutableSet<OsGroup> _oss;

    /**
     * Constructor.
     */
    public OsCondition(@Nonnull final String... oss) {
        final Set<OsGroup> set = Sets.newHashSet();
        for (final String os : oss) {
            set.add(OsGroup.valueOf(os));

        }
        _oss = ImmutableSet.copyOf(set);
    }
    /**
     * Constructor.
     */
    public OsCondition(@Nonnull final String os) {
        _oss = ImmutableSet.<OsGroup>builder().add(OsGroup.valueOf(os)).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shallBeRun() {
        final String osType = System.getProperty("os.name");

        for (final OsGroup os : _oss) {
            if (os.contains(osType)) {
                return true;
            }
        }
        return false;
    }

}
