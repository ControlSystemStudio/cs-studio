package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.util.CompareUtil;

/**
 * Standard implementation of {@link IRecord}.
 *
 * @author Sven Wende
 */
public final class Record extends AbstractPropertyContainer implements IRecord {
    private static final long serialVersionUID = -909182136862019398L;
    private String type;
    private String epicsName;
    private Map<String, String> fields = new HashMap<String, String>();
    private IRecord parentRecord;
    private transient IContainer container;
    private transient List<IRecord> inheritingRecords = new ArrayList<IRecord>();
    private Boolean disabled;

    public Record() {
    }

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param type
     *            the type
     * @param id
     *            the id
     */
    public Record(String name, String type, UUID id) {
        super(name, id);
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param parentRecord
     *            the parent record
     * @param id
     *            the id
     */
    public Record(IRecord parentRecord, UUID id) {
        super(null, id);
        this.parentRecord = parentRecord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        assert type != null || parentRecord != null : "type!=null || parentRecord!=null";
        return type != null ? type : parentRecord.getType();
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getFinalProperties() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IRecord> stack = getRecordStack();

        // add the field values of the parent hierarchy, values can be overriden
        // by children
        while (!stack.isEmpty()) {
            IRecord top = stack.pop();
            result.putAll(top.getProperties());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addField(String key, String value) {
        fields.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getField(String key) {
        return fields.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeField(String key) {
        fields.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getFinalFields() {
        Map<String, String> result = new LinkedHashMap<String, String>();

        Stack<IRecord> stack = getRecordStack();

        // add the field values of the parent hierarchy, values can be overriden
        // by children
        while (!stack.isEmpty()) {
            IRecord top = stack.pop();
            result.putAll(top.getFields());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDefaultFields() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IRecord> stack = getRecordStack();

        // add the field values of the parent hierarchy, values can be overriden
        // by children
        if (!stack.isEmpty()) {
            IRecord top = stack.pop();

            if (top instanceof BaseRecord) {
                result.putAll(top.getFields());
            }
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getEpicsName() {
        return epicsName;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setEpicsName(String epicsName) {
        this.epicsName = epicsName;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getEpicsNameFromHierarchy() {
        String name = "unknown";

        Stack<IRecord> stack = getRecordStack();

        while (!stack.isEmpty()) {
            IRecord top = stack.pop();

            if (top.getEpicsName() != null && top.getEpicsName().length() > 0) {
                name = top.getEpicsName();
            }
        }

        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecord getParentRecord() {
        return parentRecord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContainer getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentRecord(IRecord parentRecord) {
        this.parentRecord = parentRecord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContainer(IContainer container) {
        this.container = container;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isAbstract() {
        return getRootContainer(getContainer()) instanceof IPrototype;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     *{@inheritDoc}
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * Recursive helper method which determines the root container.
     *
     * @param container
     *            a starting container
     *
     * @return the root container of the specified starting container
     */
    private IContainer getRootContainer(IContainer container) {
        if (container.getContainer() != null) {
            return getRootContainer(container.getContainer());
        } else {
            return container;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInherited() {
        IRecord p = getParentRecord();
        boolean result = p!=null && !(p instanceof BaseRecord);
        return result;
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
     * {@inheritDoc}
     */
    @Override
    public IRecordDefinition getRecordDefinition() {
        IRecord base = getRecordStack().pop();
        return base.getRecordDefinition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof Record) {
            Record record = (Record) obj;

            if (super.equals(obj)) {
                // .. type
                if (CompareUtil.equals(getType(), record.getType())) {
                    // .. fields
                    if (getFields().equals(record.getFields())) {
                        // .. parent record id (we check the id only, to prevent
                        // stack overflows)
                        if (CompareUtil.idsEqual(getParentRecord(), record.getParentRecord())) {
                            // .. container (we check the id only, to prevent
                            // stack overflows)
                            if (CompareUtil.idsEqual(getContainer(), record.getContainer())) {
                                result = true;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Collect all parent records in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     *
     * @return all parent records, including this
     */
    private Stack<IRecord> getRecordStack() {
        Stack<IRecord> stack = new Stack<IRecord>();

        IRecord r = this;

        while (r != null) {
            stack.add(r);
            r = r.getParentRecord();
        }
        return stack;
    }

}
