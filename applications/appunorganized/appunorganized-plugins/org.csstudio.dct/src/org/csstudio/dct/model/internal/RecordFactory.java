package org.csstudio.dct.model.internal;

import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.ExtensionPointUtil;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;

/**
 * Factory that creates records.
 *
 * @author Sven Wende
 *
 */
public final class RecordFactory {
    private RecordFactory() {
    }

    /**
     * Creates a record. The record is equipped with all fields that are known
     * for the type of record.
     *
     * @param project
     *            the project
     * @param type
     *            the record type
     * @param name
     *            the record name
     * @param id
     *            the id for the new record
     * @return the record
     */
    public static Record createRecord(IProject project, String type, String name, UUID id) {
        assert project != null;
        assert type != null;
        assert id != null;

        IRecord base = project.getBaseRecord(type);

        if (base == null) {
            throw new IllegalArgumentException("Cannot create record of type " + type + ".");
        }

        Record result = new Record(name, type, id);

        // link to record definition
        result.setParentRecord(base);

        // add properties needed for record functions
        Map<String, String> properties = ExtensionPointUtil.getRecordAttributes();

        for(String key : properties.keySet()) {
            result.addProperty(key, properties.get(key));
        }

        return result;
    }

    public static Record cloneRecord(IProject project, IRecord original) {
        assert project != null;
        assert original != null;

        String type  = original.getType();
        String name = original.getName();

        IRecord base = project.getBaseRecord(type);

        if (base == null) {
            throw new IllegalArgumentException("Cannot create record of type " + type + ".");
        }

        Record clone = new Record(name, type, UUID.randomUUID());

        // link to record definition
        clone.setParentRecord(base);

        // add properties needed for record functions
        Map<String, String> properties = ExtensionPointUtil.getRecordAttributes();

        for(String key : properties.keySet()) {
            clone.addProperty(key, properties.get(key));
        }

        // copy field values from original
        for(String key : original.getFields().keySet()) {
            clone.addField(key, original.getField(key));
        }

        return clone;
    }


}
