package org.csstudio.dct.metamodel.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;

/**
 * Standard implementation of {@link IDatabaseDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class DatabaseDefinition implements IDatabaseDefinition {
    private Map<String, IRecordDefinition> recordDefinitions;
    private String dbdVersion;

    /**
     * Constructor.
     *
     * @param dbdVersion
     *            the dbd version
     */
    public DatabaseDefinition(String dbdVersion) {
        assert dbdVersion != null;
        this.dbdVersion = dbdVersion;
        recordDefinitions = new LinkedHashMap<String, IRecordDefinition>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRecordDefinition(IRecordDefinition recordDefinition) {
        recordDefinitions.put(recordDefinition.getType(), recordDefinition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecordDefinition getRecordDefinition(String recordType) {
        return recordDefinitions.get(recordType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRecordDefinition> getRecordDefinitions() {
        return new ArrayList<IRecordDefinition>(recordDefinitions.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRecordDefinition(IRecordDefinition recordDefinition) {
        recordDefinitions.remove(recordDefinition.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDbdVersion() {
        return dbdVersion;
    }
}
