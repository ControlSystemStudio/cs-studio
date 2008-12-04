package org.csstudio.dct.model.internal;

import java.util.UUID;

import org.csstudio.dct.metamodel.Factory;
import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.internal.DatabaseDefinition;
import org.csstudio.dct.model.IRecord;

public class RecordFactory {

	/**
	 * Creates a new record.
	 * 
	 * @param type
	 *            the type of the record (mandatory)
	 * @param name
	 *            the name of the record (mandatory)
	 * @return
	 */
	public static IRecord createRecord(String type, String name) {

		DatabaseDefinition dbd = Factory.createSampleDatabaseDefinition();
		IRecord base = new Record("", type, null);
		
		for (IFieldDefinition fd : dbd.getRecordDefinitions().get(0).getFieldDefinitions()) {
			base.addField(fd.getName(), "");
		}

		Record result = new Record(name, type);
		result.setParentRecord(base);

		return result;
	}

	public static IRecord createRecord(String type, String name, UUID id) {
		DatabaseDefinition dbd = Factory.createSampleDatabaseDefinition();
		IRecord base = new Record("", type, null);

		for (IFieldDefinition fd : dbd.getRecordDefinitions().get(0).getFieldDefinitions()) {
			base.addField(fd.getName(), "");
		}

		Record result = new Record(name, type, id);
		result.setParentRecord(base);

		return result;
	}
	
}