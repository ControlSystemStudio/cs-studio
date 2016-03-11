package org.csstudio.dct.export.internal;

import java.util.Map;

import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;

/**
 * Renders records in DB file syntax (as accepted by an IOC).
 *
 * @author Sven Wende
 *
 */
public final class SimpleDbFileExporter implements IExporter {
    private static final String NEWLINE = "\r\n";


    /**
     *{@inheritDoc}
     */
    public String render(IRecord record) {

        StringBuffer sb = new StringBuffer();
        sb.append("record(");
        sb.append(record.getType());
        sb.append(", \"");
        try {
            sb.append(ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(record), record));
        } catch (AliasResolutionException e) {
            sb.append("<" + e.getMessage() + ">");
        }

        sb.append("\") {");
        sb.append(NEWLINE);

        Map<String, String> fields = ResolutionUtil.resolveFields(record);

        for (String key : fields.keySet()) {
            String v = fields.get(key) != null ? fields.get(key) : "";

            if (!v.equals(record.getDefaultFields().get(key))) {
                sb.append("   field(");
                sb.append(key);
                sb.append(", \"");
                sb.append(v);
                sb.append("\")");
                sb.append(NEWLINE);
            }
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public String export(IProject project) {
        StringBuffer sb = new StringBuffer();
        for (IRecord r :project.getFinalRecords()) {
            sb.append(render(r));
            sb.append("\r\n\r\n");
        }
        return sb.toString();
    }

}
