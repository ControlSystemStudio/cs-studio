package org.csstudio.dct.model.internal;

import java.util.UUID;

import org.csstudio.dct.model.IRecord;

public class RecordFactory {
	public static IRecord createRecord(Project project, String type, String name, UUID id) {
		assert project != null;
		assert type != null;
		assert id != null;
		
		IRecord base = project.getBaseRecord(type);

		if (base == null) {
			throw new IllegalArgumentException("Cannot create record of type " + type + ".");
		}
		
		Record result = new Record(name, type, id);
		result.setParentRecord(base);

		return result;
	}

}