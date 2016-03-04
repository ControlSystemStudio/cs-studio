package org.csstudio.dct.metamodel.internal;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;


/**
 * Standard implementation of {@link IRecordDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class RecordDefinition implements IRecordDefinition, Serializable {
    private String type;
    private Map<String, IFieldDefinition> fieldDefinitions;

    /**
     * Constructor.
     *
     * @param type the record type
     */
    public RecordDefinition(String type) {
        assert type != null;
        this.type = type;
        fieldDefinitions = new LinkedHashMap<String, IFieldDefinition>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFieldDefinition getFieldDefinitions(String fieldName) {
        return fieldDefinitions.get(fieldName);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IFieldDefinition> getFieldDefinitions() {
        return fieldDefinitions.values();
    }

    public void setFieldDefinitions(Map<String, IFieldDefinition> fieldDefinitions) {
        this.fieldDefinitions = fieldDefinitions;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addFieldDefinition(IFieldDefinition fieldDefinition) {
        assert fieldDefinition != null;
        fieldDefinitions.put(fieldDefinition.getName(), fieldDefinition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFieldDefinition(IFieldDefinition fieldDefinition) {
        assert fieldDefinition != null;
        fieldDefinitions.remove(fieldDefinition.getName());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
