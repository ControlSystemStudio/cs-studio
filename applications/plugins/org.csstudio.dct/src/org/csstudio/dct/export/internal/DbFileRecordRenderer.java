package org.csstudio.dct.export.internal;

import java.util.Map;

import org.csstudio.dct.export.IRecordRenderer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.RecordUtil;

public class DbFileRecordRenderer implements IRecordRenderer {
	private static final String NEWLINE = "\r\n";
	public String render(IRecord record) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("record(");
		sb.append(record.getType());
		sb.append(", \"");
		sb.append(RecordUtil.getResolvedName(record));
		
		sb.append("\") {");
		sb.append(NEWLINE);
		
		Map<String, String> fields = RecordUtil.getResolvedFields(record);
		for(String key : fields.keySet()) {
			sb.append("   field(");
			sb.append(key);
			sb.append(", \"");
			sb.append(fields.get(key));
			sb.append("\")");
			sb.append(NEWLINE);
		}
		
		sb.append("}");
		
		return sb.toString();
	}

}
