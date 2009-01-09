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
import org.csstudio.dct.model.IRecord;

/**
 * Represents a project. A project is the root of the hierarchy.
 * 
 * TODO: Extract Interface
 * 
 * @author Sven Wende
 */
public class Project extends Folder {
	private transient Map<String, BaseRecord> baseRecords;
	private transient IDatabaseDefinition databaseDefinition;
	
	private String path;
	private String ioc;

	public Project(String name, UUID id) {
		super(name, id);
		baseRecords = new HashMap<String, BaseRecord>();
		databaseDefinition = null;
	}
	
	public IDatabaseDefinition getDatabaseDefinition() {
		return databaseDefinition;
	}
	
	public void setDatabaseDefinition(IDatabaseDefinition databaseDefinition) {
		this.databaseDefinition = databaseDefinition;
	}

	public BaseRecord getBaseRecord(String type) {
		if(!baseRecords.containsKey(type)) {
			baseRecords.put(type, new BaseRecord(null));
		}
		
		return baseRecords.get(type);
	}

	
	public Map<String, BaseRecord> getBaseRecords() {
		return baseRecords;
	}
	
	public void setBaseRecords(Map<String, BaseRecord> baseRecords) {
		this.baseRecords = baseRecords;
	}
	
	

	public String getDbdPath() {
		return path;
	}

	public void setDbdPath(String path) {
		this.path = path;
	}

	public String getIoc() {
		return ioc;
	}

	public void setIoc(String ioc) {
		this.ioc = ioc;
	}
	
	public List<IRecord> getFinalRecords() {
		return getFinalRecords(this);
	}

	private List<IRecord> getFinalRecords(IFolder folder) {
		List<IRecord> result = new ArrayList<IRecord>();
		
		for(IFolderMember m : folder.getMembers()) {
			if(m instanceof IRecord) {
				result.add((IRecord)m);
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
		
		result.addAll(instance.getRecords());

		for(IInstance i : instance.getInstances()) {
			result.addAll(getFinalRecords(i));
		}
		
		return result;
	}
}
