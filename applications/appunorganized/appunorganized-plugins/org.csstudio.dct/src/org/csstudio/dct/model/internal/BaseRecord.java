package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.metamodel.PromptGroup;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;

/**
 * Represents an implicit base record that contains all standard field
 * definitions for a record definition. Each real record in a model will inherit
 * from a base record.
 *
 * @author Sven Wende
 *
 */
public final class BaseRecord implements IRecord {
    private IRecordDefinition recordDefinition;
    private List<IRecord> inheritingRecords = new ArrayList<IRecord>();
    private Map<String, String> fields;

    /**
     * Constructor.
     *
     * @param recordDefinition
     *            the record definition
     */
    public BaseRecord(IRecordDefinition recordDefinition) {
        setRecordDefinition(recordDefinition);
    }

    /**
     * Sets the record definition.
     *
     * @param recordDefinition
     *            the record definition
     */
    public void setRecordDefinition(IRecordDefinition recordDefinition) {
        this.recordDefinition = recordDefinition;

        fields = new LinkedHashMap<String, String>();

        if (recordDefinition != null) {
            for (IFieldDefinition fd : recordDefinition.getFieldDefinitions()) {

                if (fd.getPromptGroup() != null && PromptGroup.UNDEFINED != fd.getPromptGroup()) {
                    // determine default value
                    String defaultValue = "";

                    if (fd.getInitial() != null && fd.getInitial().length() > 0) {
                        defaultValue = fd.getInitial();
                    } else {
                        if (fd.getMenu() != null && fd.getMenu().getChoices() != null && fd.getMenu().getChoices().size() > 0) {
                            defaultValue = fd.getMenu().getChoices().get(0).getDescription();
                        }
                    }

                    fields.put(fd.getName(), defaultValue);
                }
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void addField(String name, String value) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void addProperty(String name, String value) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IContainer getContainer() {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getField(String name) {
        return fields.get(name);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Map<String, String> getDefaultFields() {
        return getFields();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Map<String, String> getFinalFields() {
        return getFields();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getFinalProperties() {
        return Collections.EMPTY_MAP;
    }

    /**
     *{@inheritDoc}
     */
    public String getNameFromHierarchy() {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getEpicsName() {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getEpicsNameFromHierarchy() {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setEpicsName(String epicsName) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IRecord getParentRecord() {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getProperties() {
        return Collections.EMPTY_MAP;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getProperty(String name) {
        throw null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getType() {
        return recordDefinition.getType();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isInherited() {
        return false;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void removeField(String name) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void removeProperty(String name) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setContainer(IContainer container) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public UUID getId() {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getName() {
        return recordDefinition != null ? recordDefinition.getType() : "??";
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setName(String name) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDependentRecord(IRecord record) {
        assert record != null;
        assert record.getParentRecord() == this : "Record must inherit from here.";
        inheritingRecords.add(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRecord> getDependentRecords() {
        return inheritingRecords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeDependentRecord(IRecord record) {
        assert record != null;
        assert record.getParentRecord() == this : "Record must inherit from here.";
        inheritingRecords.remove(record);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IRecordDefinition getRecordDefinition() {
        return recordDefinition;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean hasProperty(String name) {
        return false;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void accept(IVisitor visitor) {

    }

    /**
     *{@inheritDoc}
     */
    public Map<String, String> getFinalParameterValues() {
        return new HashMap<String, String>();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isAbstract() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getDisabled() {
        return false;
    }
}
