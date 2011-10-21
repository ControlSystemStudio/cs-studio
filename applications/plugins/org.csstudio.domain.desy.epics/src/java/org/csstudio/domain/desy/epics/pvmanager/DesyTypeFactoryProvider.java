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
package org.csstudio.domain.desy.epics.pvmanager;

import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.dbr.DBR_CTRL_Byte;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_CTRL_Float;
import gov.aps.jca.dbr.DBR_CTRL_Int;
import gov.aps.jca.dbr.DBR_CTRL_Short;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Enum;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_STS_String;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_Byte;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import gov.aps.jca.dbr.DBR_TIME_Float;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.dbr.DBR_TIME_String;
import gov.aps.jca.dbr.LABELS;
import gov.aps.jca.dbr.STS;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.epics.pvmanager.jca.TypeFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Factory provider class.
 *
 * @author bknerr
 * @since 31.08.2011
 */
public final class DesyTypeFactoryProvider {

    @SuppressWarnings("rawtypes")
    private static Map<DBRType, DesyJCATypeFactory> FACTORY_MAP = Maps.newConcurrentMap();
    static {
        // Add all SCALARs
        // DBR_TIME_Float -> EpicsSystemVariable<Float>
        FACTORY_MAP.put(DBR_Float.TYPE,
                        new DesyJCATypeFactory<Float, DBR_TIME_Float, DBR_CTRL_Float>(Float.class,
                                                                                   DBR_TIME_Float.TYPE,
                                                                                   DBR_CTRL_Float.TYPE){
                            @Override
                            @Nonnull
                            public Float toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Float eMeta, final int index) {
                                return Float.valueOf(((DBR_TIME_Float) eVal).getFloatValue()[index]);
                            }
                        });
        // DBR_CTRL_Double -> EpicsSystemVariable<Double>
        FACTORY_MAP.put(DBR_Double.TYPE,
                        new DesyJCATypeFactory<Double, DBR_TIME_Double, DBR_CTRL_Double>(Double.class,
                                                                                      DBR_TIME_Double.TYPE,
                                                                                      DBR_CTRL_Double.TYPE){
                            @Override
                            @Nonnull
                            public Double toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Double eMeta, final int index) {
                                return Double.valueOf(((DBR_TIME_Double) eVal).getDoubleValue()[index]);
                            }
                        });
        // DBR_CTRL_Byte -> EpicsSystemVariable<Byte>
        FACTORY_MAP.put(DBR_Byte.TYPE,
                        new DesyJCATypeFactory<Byte, DBR_TIME_Byte, DBR_CTRL_Byte>(Byte.class,
                                                                                DBR_TIME_Byte.TYPE,
                                                                                DBR_CTRL_Byte.TYPE){
                            @Override
                            @Nonnull
                            public Byte toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Byte eMeta, final int index) {
                                return Byte.valueOf(((DBR_TIME_Byte) eVal).getByteValue()[index]);
                            }
                        });
        // DBR_CTRL_Short -> EpicsSystemVariable<Short>
        FACTORY_MAP.put(DBR_Short.TYPE,
                        new DesyJCATypeFactory<Short, DBR_TIME_Short, DBR_CTRL_Short>(Short.class,
                                                                                   DBR_TIME_Short.TYPE,
                                                                                   DBR_CTRL_Short.TYPE){
                            @Override
                            @Nonnull
                            public Short toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Short eMeta, final int index) {
                                return Short.valueOf(((DBR_TIME_Short) eVal).getShortValue()[index]);
                            }
                        });
        // DBR_CTRL_Int -> EpicsSystemVariable<Integer>
        FACTORY_MAP.put(DBR_Int.TYPE,
                        new DesyJCATypeFactory<Integer, DBR_TIME_Int, DBR_CTRL_Int>(Integer.class,
                                                                                 DBR_TIME_Int.TYPE,
                                                                                 DBR_CTRL_Int.TYPE){
                            @Override
                            @Nonnull
                            public Integer toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Int eMeta, final int index) {
                                return Integer.valueOf(((DBR_TIME_Int) eVal).getIntValue()[index]);
                            }
                        });
        // DBR_TIME_String -> EpicsSystemVariable<String>
        FACTORY_MAP.put(DBR_String.TYPE,
                        new DesyJCATypeFactory<String, DBR_TIME_String, DBR_STS_String>(String.class,
                                                                                     DBR_TIME_String.TYPE,
                                                                                     DBR_STS_String.TYPE){
                            @Override
                            @Nonnull
                            public String toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_STS_String eMeta, final int index) {
                                return ((DBR_TIME_String) eVal).getStringValue()[index];
                            }

                            @Override
                            @CheckForNull
                            public EpicsMetaData createMetaData(@Nonnull final STS eMeta) {
                                return EpicsMetaData.create(new EpicsAlarm(EpicsAlarmSeverity.valueOf(eMeta.getSeverity()),
                                                                           EpicsAlarmStatus.valueOf(eMeta.getStatus())),
                                                            null, null, null);
                            }
                        });
        // DBR_TIME_Enum -> EpicsSystemVariable<EpicsEnum>
        FACTORY_MAP.put(DBR_Enum.TYPE,
                        new DesyJCATypeFactory<EpicsEnum, DBR_TIME_Enum, DBR_LABELS_Enum>(EpicsEnum.class,
                                                                                       DBR_TIME_Enum.TYPE,
                                                                                       DBR_LABELS_Enum.TYPE){
                            @Override
                            @Nonnull
                            public EpicsEnum toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_LABELS_Enum eMeta, final int index) {
                                final short i = ((DBR_TIME_Enum) eVal).getEnumValue()[index];
                                final String[] labels = eMeta!= null ? eMeta.getLabels() : null;
                                if (labels != null && i >=0 && i < labels.length && !Strings.isNullOrEmpty(labels[i])) {
                                    return EpicsEnum.createFromState(labels[i], (int) i);
                                }
                                return EpicsEnum.createFromRaw(Integer.valueOf(i));
                            }
                            @Override
                            @Nonnull
                            public EpicsMetaData createMetaData(@Nonnull final STS eMeta) {
                                return EpicsMetaData.create(((LABELS) eMeta).getLabels());
                            }
                        });
    }

    /**
     * Constructor.
     */
    private DesyTypeFactoryProvider() {
        // Dont instantiate
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    public static TypeFactory matchFor(@Nonnull final Channel channel) {
        final DesyJCATypeFactory fac = FACTORY_MAP.get(channel.getFieldType());
        if (fac == null) {
            throw new IllegalArgumentException("The dbrType type is not supported: " + channel, null);
        }
        return fac;
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    static Map<DBRType, DesyJCATypeFactory> getMap() {
        return FACTORY_MAP;
    }
}
