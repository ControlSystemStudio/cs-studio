package org.csstudio.dct.util;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dct.metamodel.Factory;
import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.metamodel.internal.DatabaseDefinition;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Record;

public class RecordUtil {
	
	public static String getResolvedName(IRecord record) {
		String name = record.getNameFromHierarchy();

		// resolve parameters
		if(record.getContainer() instanceof IContainer) {
			Map<String, String>  parameters = ((IContainer) record.getContainer()).getFinalParameterValues();
			try {
				name = ReplaceAliasesUtil.createCanonicalName(name, parameters);
			} catch (Exception e) {
				// ignore
			}
		}
		
		return name;
	}
	
	public static Map<String, String> getResolvedFields(IRecord record) {
		Map<String, String> result = new HashMap<String, String>();
		
		Map<String, Object> finalFields = record.getFinalFields();

		// resolve parameters
		if(record.getContainer() instanceof IContainer) {
			Map<String, String>  parameters = ((IContainer) record.getContainer()).getFinalParameterValues();
			try {
				for(String key : finalFields.keySet()) {
					String resolvedValue = ReplaceAliasesUtil.createCanonicalName(finalFields.get(key).toString(), parameters); 
					result.put(key, resolvedValue);
				}
			} catch (Exception e) {
				// ignore
			}
		} else {
			for(String key : finalFields.keySet()) {
				result.put(key, finalFields.get(key).toString());
			}
		}
		
		return result;
	}
}
