package org.csstudio.dct.export.internal;

import java.util.Map;

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
        sb.append(String.format("%-30s\t%-20s\t%s%n", "Naming rule", "Record name","Description"));
        for(IRecord r : project.getFinalRecords()) {
            String name = AliasResolutionUtil.getEpicsNameFromHierarchy(r);
            String resolvedName;
            try {
                resolvedName = ResolutionUtil.resolve(name, r);
            } catch (AliasResolutionException e) {
                resolvedName = e.getMessage();
            }
            Map<String, String> resolveFields = ResolutionUtil.resolveFields(r);
            String desc = resolveFields.get("DESC");
            if(desc==null) {
                desc="";
            }
            sb.append(String.format("%-30s\t%-20s\t%s%n", name, resolvedName,desc ));
        }
        return sb.toString();
    }

}
