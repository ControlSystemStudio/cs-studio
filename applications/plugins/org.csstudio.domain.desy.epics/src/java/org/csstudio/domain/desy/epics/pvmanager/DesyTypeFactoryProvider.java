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
import java.util.Set;

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
import com.google.common.collect.Sets;

/**
 * Factory provider class.
 *
 * @author bknerr
 * @since 31.08.2011
 */
public final class DesyTypeFactoryProvider {

    @SuppressWarnings("rawtypes")
    private static Map<DBRType, DesyScalarJCATypeFactory> SCALAR_FACTORY_MAP =
        Maps.newConcurrentMap();
    @SuppressWarnings("rawtypes")
    private static Map<DBRType, DesyMultiScalarJCATypeFactory> MULTISCALAR_FACTORY_MAP =
        Maps.newConcurrentMap();

    static {
        // Add all SCALARs and MULTISCALARs
        // DBR_TIME_Float -> EpicsSystemVariable<Float>
        SCALAR_FACTORY_MAP.put(DBR_Float.TYPE,
                               new DesyScalarJCATypeFactory<Float, DBR_TIME_Float, DBR_CTRL_Float>(Float.class,
                                       DBR_TIME_Float.TYPE,
                                       DBR_CTRL_Float.TYPE,
                                       DBR_Float.TYPE){
                                   @Override
                                   @Nonnull
                                   public Float toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Float eMeta, final int index) {
                                       return Float.valueOf(((DBR_TIME_Float) eVal).getFloatValue()[index]);
                                   }
                               });
        MULTISCALAR_FACTORY_MAP.put(DBR_Float.TYPE,
                                    new DesyMultiScalarJCATypeFactory<Float, DBR_TIME_Float, DBR_CTRL_Float>(Float.class,
                                                                                                             DBR_TIME_Float.TYPE,
                                                                                                             DBR_CTRL_Float.TYPE,
                                                                                                             DBR_Float.TYPE));
        // DBR_CTRL_Double -> EpicsSystemVariable<Double>
        SCALAR_FACTORY_MAP.put(DBR_Double.TYPE,
                               new DesyScalarJCATypeFactory<Double, DBR_TIME_Double, DBR_CTRL_Double>(Double.class,
                                       DBR_TIME_Double.TYPE,
                                       DBR_CTRL_Double.TYPE,
                                       DBR_Double.TYPE){
                                   @Override
                                   @Nonnull
                                   public Double toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Double eMeta, final int index) {
                                       return Double.valueOf(((DBR_TIME_Double) eVal).getDoubleValue()[index]);
                                   }
                               });
        MULTISCALAR_FACTORY_MAP.put(DBR_Double.TYPE,
                                    new DesyMultiScalarJCATypeFactory<Double, DBR_TIME_Double, DBR_CTRL_Double>(Double.class,
                                                                                                                DBR_TIME_Double.TYPE,
                                                                                                                DBR_CTRL_Double.TYPE,
                                                                                                                DBR_Double.TYPE));
        // DBR_CTRL_Byte -> EpicsSystemVariable<Byte>
        SCALAR_FACTORY_MAP.put(DBR_Byte.TYPE,
                               new DesyScalarJCATypeFactory<Byte, DBR_TIME_Byte, DBR_CTRL_Byte>(Byte.class,
                                       DBR_TIME_Byte.TYPE,
                                       DBR_CTRL_Byte.TYPE,
                                       DBR_Byte.TYPE){
                                   @Override
                                   @Nonnull
                                   public Byte toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Byte eMeta, final int index) {
                                       return Byte.valueOf(((DBR_TIME_Byte) eVal).getByteValue()[index]);
                                   }
                               });
        MULTISCALAR_FACTORY_MAP.put(DBR_Byte.TYPE,
                                    new DesyMultiScalarJCATypeFactory<Byte, DBR_TIME_Byte, DBR_CTRL_Byte>(Byte.class,
                                                                                                          DBR_TIME_Byte.TYPE,
                                                                                                          DBR_CTRL_Byte.TYPE,
                                                                                                          DBR_Byte.TYPE));
        // DBR_CTRL_Short -> EpicsSystemVariable<Short>
        SCALAR_FACTORY_MAP.put(DBR_Short.TYPE,
                               new DesyScalarJCATypeFactory<Short, DBR_TIME_Short, DBR_CTRL_Short>(Short.class,
                                       DBR_TIME_Short.TYPE,
                                       DBR_CTRL_Short.TYPE,
                                       DBR_Short.TYPE){
                                   @Override
                                   @Nonnull
                                   public Short toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Short eMeta, final int index) {
                                       return Short.valueOf(((DBR_TIME_Short) eVal).getShortValue()[index]);
                                   }
                               });
        MULTISCALAR_FACTORY_MAP.put(DBR_Short.TYPE,
                                    new DesyMultiScalarJCATypeFactory<Short, DBR_TIME_Short, DBR_CTRL_Short>(Short.class,
                                                                                                          DBR_TIME_Short.TYPE,
                                                                                                          DBR_CTRL_Short.TYPE,
                                                                                                          DBR_Short.TYPE));
        // DBR_CTRL_Int -> EpicsSystemVariable<Integer>
        SCALAR_FACTORY_MAP.put(DBR_Int.TYPE,
                               new DesyScalarJCATypeFactory<Integer, DBR_TIME_Int, DBR_CTRL_Int>(Integer.class,
                                       DBR_TIME_Int.TYPE,
                                       DBR_CTRL_Int.TYPE,
                                       DBR_Int.TYPE){
                                   @Override
                                   @Nonnull
                                   public Integer toScalarData(@Nonnull final DBR eVal, @CheckForNull final DBR_CTRL_Int eMeta, final int index) {
                                       return Integer.valueOf(((DBR_TIME_Int) eVal).getIntValue()[index]);
                                   }
                               });
        MULTISCALAR_FACTORY_MAP.put(DBR_Int.TYPE,
                                    new DesyMultiScalarJCATypeFactory<Integer, DBR_TIME_Int, DBR_CTRL_Int>(Integer.class,
                                            DBR_TIME_Int.TYPE,
                                            DBR_CTRL_Int.TYPE,
                                            DBR_Int.TYPE));
        // DBR_TIME_String -> EpicsSystemVariable<String>
        SCALAR_FACTORY_MAP.put(DBR_String.TYPE,
                               new DesyScalarJCATypeFactory<String, DBR_TIME_String, DBR_STS_String>(String.class,
                                       DBR_TIME_String.TYPE,
                                       DBR_STS_String.TYPE,
                                       DBR_String.TYPE){
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
        MULTISCALAR_FACTORY_MAP.put(DBR_String.TYPE,
                                    new DesyMultiScalarJCATypeFactory<String, DBR_TIME_String, DBR_STS_String>(String.class,
                                            DBR_TIME_String.TYPE,
                                            DBR_STS_String.TYPE,
                                            DBR_String.TYPE));
        // DBR_TIME_Enum -> EpicsSystemVariable<EpicsEnum>
        SCALAR_FACTORY_MAP.put(DBR_Enum.TYPE,
                               new DesyScalarJCATypeFactory<EpicsEnum, DBR_TIME_Enum, DBR_LABELS_Enum>(EpicsEnum.class,
                                       DBR_TIME_Enum.TYPE,
                                       DBR_LABELS_Enum.TYPE,
                                       DBR_Enum.TYPE){
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
        MULTISCALAR_FACTORY_MAP.put(DBR_Enum.TYPE,
                                    new DesyMultiScalarJCATypeFactory<EpicsEnum, DBR_TIME_Enum, DBR_LABELS_Enum>(EpicsEnum.class,
                                            DBR_TIME_Enum.TYPE,
                                            DBR_LABELS_Enum.TYPE,
                                            DBR_Enum.TYPE));
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
        final AbstractDesyJCATypeFactory fac;
        if (channel.getElementCount() > 1) {
            fac = MULTISCALAR_FACTORY_MAP.get(channel.getFieldType());
        } else {
            fac = SCALAR_FACTORY_MAP.get(channel.getFieldType());
        }
        if (fac == null) {
            throw new IllegalArgumentException("The dbrType " + channel.getFieldType() + " is not supported for channel " + channel, null);
        }
        return fac;
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    static Map<DBRType, DesyScalarJCATypeFactory> getScalarMap() {
        return SCALAR_FACTORY_MAP;
    }

    @Nonnull
    public static Set<Class<?>> getInstalledTargetTypes() {
        final Set<Class<?>> targetTypes = Sets.newHashSet();
        for (@SuppressWarnings("rawtypes") final DesyScalarJCATypeFactory fac : getScalarMap().values()) {
            targetTypes.add(fac.getValueType());
        }
        return targetTypes;
    }
}
