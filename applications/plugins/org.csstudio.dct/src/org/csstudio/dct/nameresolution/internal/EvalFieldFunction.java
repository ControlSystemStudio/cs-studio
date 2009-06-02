package org.csstudio.dct.nameresolution.internal;

import java.util.Collections;
import java.util.List;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.RecordFinder;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.jface.fieldassist.IContentProposal;

import bsh.Interpreter;

/**
 * Implementation for the forwardlink() function.
 * 
 * @author Sven Wende
 * 
 */
public final class EvalFieldFunction  implements IFieldFunction {

	/**
	 *{@inheritDoc}
	 */
	public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
		IRecord r = RecordFinder.findRecordByPath(parameters[0], record.getContainer());

		String result = null;

		if (r != null) {
			result = AliasResolutionUtil.getEpicsNameFromHierarchy(r);
		} else {
			result = "No Record found";
		}


		Interpreter i = new Interpreter();  // Construct an interpreter

		i.eval("bar = "+parameters[0]);             
		return ""+i.get("bar");
	}

	public List<IContentProposal> getParameterProposal(int parameter, IRecord record) {
		return Collections.EMPTY_LIST;
	}
	
}
