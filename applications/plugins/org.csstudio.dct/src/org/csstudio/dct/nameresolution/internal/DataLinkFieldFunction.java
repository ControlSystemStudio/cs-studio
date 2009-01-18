package org.csstudio.dct.nameresolution.internal;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.RecordFinder;

/**
 * Implementation for the datalink() function.
 * 
 * @author Sven Wende
 * 
 */
public final class DataLinkFieldFunction implements IFieldFunction {

	/**
	 *{@inheritDoc}
	 */
	public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
		IRecord r = RecordFinder.findRecordByPath(parameters[0], record.getContainer());

		String result = null;

		if (r != null) {
			result = r.getEpicsNameFromHierarchy();
		} else {
			result = "No Record found";
		}

		return result;
	}

}
