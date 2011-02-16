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
package org.csstudio.domain.desy.epics.types;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.CssValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.util.StringUtil;

/**
 * IEnumeratedValue conversion support.
 *
 * @author bknerr
 * @since 15.12.2010
 */
final class IEnumeratedValueConversionTypeSupport extends
        AbstractIValueConversionTypeSupport<IEnumeratedValue> {

    public IEnumeratedValueConversionTypeSupport() {
        super(IEnumeratedValue.class);
    }

    /**
     * {@inheritDoc}
     *
     * Kay's enumerated values have to have only a single value element corresponding to the set
     * enumerated value string. We want to archive the string, which yields the information, not the
     * index which doesn't speak for itself or might be changed in the system later on.
     *
     * CHECKSTYLE OFF: CyclomaticComplexity (accepted here as we'll get rid of I*Values anyway)
     */
    @Override
    @CheckForNull
    protected EpicsSystemVariable<EpicsEnumTriple> convertToSystemVariable(@Nonnull final String name,
                                                                           @Nonnull final IEnumeratedValue value) throws TypeSupportException {
        // This is a nice example for what happens when physicists 'design' programs.
        // (...and I already got rid of an unsafe cast)
        final int[] values = value.getValues();
        if (values == null || values.length <= 0) {
            LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue hasn't any values!");
            return null;
        }
        final IEnumeratedMetaData metaData = value.getMetaData();
        if (metaData == null) {
            LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue hasn't any metadata!");
            return null;
        }
        final String[] states = metaData.getStates();
        final int index = values[0];
        if (states == null || index < 0 || index >= states.length) {
            LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue's index cannot be linked to a state!");
            return null;
        }
        final String state = states[index];
        if (StringUtil.isBlank(state)) {
            LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue's state is null or empty string!");
            return null;
        }

        // Now I know that IEnumeratedValue has been concisely filled, yeah.
        // (And I already got rid of some boilerplate...)
        // TODO (bknerr) : where's the raw value from epics... couldn't find it in EnumeratedValue
        final EpicsEnumTriple eVal = EpicsEnumTriple.createInstance(index, state, null);

        final EpicsAlarm alarm = EpicsIValueTypeSupport.toEpicsAlarm(value.getSeverity(), value.getStatus());
        final TimeInstant timestamp = BaseTypeConversionSupport.toTimeInstant(value.getTime());

        return new EpicsSystemVariable<EpicsEnumTriple>(name,
                                                        new CssValueType<EpicsEnumTriple>(eVal),
                                                        ControlSystem.EPICS_DEFAULT,
                                                        timestamp,
                                                        alarm);
    }
}
