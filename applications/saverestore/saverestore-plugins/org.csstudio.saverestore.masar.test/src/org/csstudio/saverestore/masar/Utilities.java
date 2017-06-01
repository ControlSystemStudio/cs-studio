/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.masar;

import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;

/**
 *
 * <code>Utilities</code> defines a set of constants used by the test classes.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class Utilities {

    private Utilities() {

    }

    static final Structure SAVE_SET_VALUE_STRUCTURE = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_CONFIG_INDEX, MasarConstants.P_CONFIG_NAME, MasarConstants.P_CONFIG_DESCRIPTION,
            MasarConstants.P_CONFIG_DATE, MasarConstants.P_CONFIG_VERSION, MasarConstants.P_CONFIG_STATUS },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString), });

    static final Structure STRUCT_SAVE_SET = FieldFactory.getFieldCreate()
        .createStructure(new String[] { MasarConstants.P_STRUCTURE_VALUE }, new Field[] { SAVE_SET_VALUE_STRUCTURE });

    static final Structure SNAPSHOT_VALUE_STRUCTURE = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_EVENT_ID, MasarConstants.P_CONFIG_ID, MasarConstants.P_COMMENT,
            MasarConstants.P_EVENT_TIME, MasarConstants.P_USER },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString), });

    static final Structure STRUCT_SNAPSHOT = FieldFactory.getFieldCreate()
        .createStructure(new String[] { MasarConstants.P_STRUCTURE_VALUE }, new Field[] { SNAPSHOT_VALUE_STRUCTURE });

    static final Structure STRUCT_VSNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] {
            MasarConstants.P_SNAPSHOT_ALARM_MESSAGE,
            MasarConstants.P_SNAPSHOT_SECONDS,
            MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
            MasarConstants.P_SNAPSHOT_DBR_TYPE,
            MasarConstants.P_SNAPSHOT_IS_CONNECTED,
            MasarConstants.P_SNAPSHOT_NANOS,
            MasarConstants.P_SNAPSHOT_USER_TAG,
            MasarConstants.P_SNAPSHOT_ALARM_SEVERITY,
            MasarConstants.P_SNAPSHOT_ALARM_STATUS,
            MasarConstants.P_SNAPSHOT_READONLY,
            MasarConstants.P_SNAPSHOT_GROUP_NAME,
            MasarConstants.P_SNAPSHOT_TAG,
            MasarConstants.P_STRUCTURE_VALUE },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvBoolean),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvBoolean),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate()
                .createUnionArray(FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0])) });

    static final Structure BASE_LEVEL_VALUE_STRUCTURE = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_BASE_LEVEL_NAME },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString) });

    static final Structure STRUCT_CONFIGS = FieldFactory.getFieldCreate()
        .createStructure(new String[] { MasarConstants.P_STRUCTURE_VALUE }, new Field[] { BASE_LEVEL_VALUE_STRUCTURE });

    static final Structure STRUCT_ALARM = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_MESSAGE },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    static final Structure STRUCT_SAVE_SNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_STRUCTURE_VALUE, MasarConstants.P_ALARM },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvBoolean), STRUCT_ALARM });

    static final Structure STRUCT_TIMESTAMP = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_SECONDS, MasarConstants.P_NANOS, MasarConstants.P_USER_TAG },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvInt), });

    static final Structure STRUCT_TAKE_SNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] {
            MasarConstants.P_SNAPSHOT_ALARM_MESSAGE,
            MasarConstants.P_SNAPSHOT_SECONDS,
            MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
            MasarConstants.P_SNAPSHOT_DBR_TYPE,
            MasarConstants.P_SNAPSHOT_IS_CONNECTED,
            MasarConstants.P_SNAPSHOT_NANOS,
            MasarConstants.P_SNAPSHOT_USER_TAG,
            MasarConstants.P_SNAPSHOT_ALARM_SEVERITY,
            MasarConstants.P_SNAPSHOT_ALARM_STATUS,
            MasarConstants.P_STRUCTURE_VALUE,
            MasarConstants.P_TIMESTAMP,
            MasarConstants.P_SNAPSHOT_READONLY,
            MasarConstants.P_SNAPSHOT_GROUP_NAME,
            MasarConstants.P_SNAPSHOT_TAG },
        new Field[] {
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvBoolean),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate()
                .createUnionArray(FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0])),
            STRUCT_TIMESTAMP,
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvBoolean),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString) });

}
