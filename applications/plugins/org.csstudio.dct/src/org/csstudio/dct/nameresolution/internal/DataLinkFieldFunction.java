package org.csstudio.dct.nameresolution.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.FieldFunctionContentProposal;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.RecordFinder;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.fieldassist.IContentProposal;

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
			StringBuffer sb = new StringBuffer();
			sb.append(AliasResolutionUtil.getEpicsNameFromHierarchy(r));

			String field = parameters[1];

			if (field != null && !field.trim().equals("")) {
				sb.append(".");
				sb.append(field);
			}

			sb.append(" ");
			sb.append(parameters[2]);
			sb.append(" ");
			sb.append(parameters[3]);
			result = sb.toString();
		} else {
			result = "No Record found";
		}

		return result;
	}

	public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knowParameters, IRecord record) {
		List<IContentProposal> result = new ArrayList<IContentProposal>();

		switch (parameterIndex) {
		case 0:
			for (IRecord r : record.getContainer().getRecords()) {
				String name = AliasResolutionUtil.getNameFromHierarchy(r);
				result.add(new FieldFunctionContentProposal(name, name, "Reference to record [" + name + "]", name.length()));
			}
			break;
		case 1:
			if (knowParameters.length > 0) {
				IRecord r = RecordFinder.findRecordByPath(knowParameters[0], record.getContainer());
				if (r != null) {
					for (String f : r.getFinalFields().keySet()) {
						result.add(new FieldFunctionContentProposal(f, f, f + " field", 3));
					}
				}
			}
			break;
		case 2:
			result.add(new FieldFunctionContentProposal("NMS", "NMS", "NMS Description", 3));
			result.add(new FieldFunctionContentProposal("PP", "PP", "PP Description", 2));
			result.add(new FieldFunctionContentProposal("CNPP", "CNPP", "CNPP Description", 4));
			result.add(new FieldFunctionContentProposal("CPP", "CPP", "CPP Description", 3));
			break;
		case 3:
			result.add(new FieldFunctionContentProposal("NMS", "NMS", "NMS Description", 3));
			result.add(new FieldFunctionContentProposal("MS", "MS", "MS Description", 2));
			break;
		default:
			break;
		}
		return result;
	}

}
