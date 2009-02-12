package org.csstudio.dct.export.internal;

import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;

public class RecordNamesExporter implements IExporter {

	public RecordNamesExporter() {
	}
	public String export(IProject project) {
		StringBuffer sb = new StringBuffer();
		
		for(IRecord r : project.getFinalRecords()) {
			String name = AliasResolutionUtil.getEpicsNameFromHierarchy(r);
			String resolvedName;
			try {
				resolvedName = ResolutionUtil.resolve(name, r);
			} catch (AliasResolutionException e) {
				resolvedName = e.getMessage();
			}
			
			sb.append(name + "-> "+ resolvedName + "\r\n");
		}
		return sb.toString();
	}

}
