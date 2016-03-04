package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;

/**
 * Represents a project. A project is the root of the hierarchy.
 *
 * @author Sven Wende
 */
public final class Project extends Folder implements IProject {
    private transient Map<String, BaseRecord> baseRecords;
    private transient IDatabaseDefinition databaseDefinition;

    private String path;
    private String ioc;

    /**
     * Constructor.
     * @param name the name
     * @param id the id
     */
    protected Project(String name, UUID id) {
        super(name, id);
        baseRecords = new HashMap<String, BaseRecord>();
        databaseDefinition = null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IDatabaseDefinition getDatabaseDefinition() {
        return databaseDefinition;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setDatabaseDefinition(IDatabaseDefinition databaseDefinition) {
        this.databaseDefinition = databaseDefinition;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public BaseRecord getBaseRecord(String type) {
        if(!baseRecords.containsKey(type)) {
            baseRecords.put(type, new BaseRecord(null));
        }

        return baseRecords.get(type);
    }


    /**
     *{@inheritDoc}
     */
    @Override
    public Map<String, BaseRecord> getBaseRecords() {
        return baseRecords;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setBaseRecords(Map<String, BaseRecord> baseRecords) {
        this.baseRecords = baseRecords;
    }



    /**
     *{@inheritDoc}
     */
    @Override
    public String getDbdPath() {
        return path;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setDbdPath(String path) {
        this.path = path;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getIoc() {
        return ioc;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setIoc(String ioc) {
        this.ioc = ioc;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public List<IRecord> getFinalRecords() {
        return getFinalRecords(this);
    }

    private List<IRecord> getFinalRecords(IFolder folder) {
        List<IRecord> result = new ArrayList<IRecord>();

        for(IFolderMember m : folder.getMembers()) {
            if(m instanceof IRecord) {
                Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(m, "disabled");

                if(disabled!=null && !disabled) {
                    result.add((IRecord) m);
                }
            } else if(m instanceof IInstance) {
                result.addAll(getFinalRecords((IInstance)m));
            } else if(m instanceof IFolder) {
                result.addAll(getFinalRecords((IFolder)m));
            }
        }

        return result;
    }

    private List<IRecord> getFinalRecords(IInstance instance) {
        List<IRecord> result = new ArrayList<IRecord>();

        for(IRecord r : instance.getRecords()) {
            Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(r, "disabled");

            if(disabled!=null && !disabled) {
                result.add(r);
            }
        }

        for(IInstance i : instance.getInstances()) {
            result.addAll(getFinalRecords(i));
        }

        return result;
    }
}
