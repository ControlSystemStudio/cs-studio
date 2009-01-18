package org.csstudio.dct.export.internal;

import java.util.Map;

import org.csstudio.dct.export.IRecordRenderer;
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
public final class DbFileRecordRenderer implements IRecordRenderer {
	private static final String NEWLINE = "\r\n";
	private boolean renderEmptyFields = false;

	/**
	 * Constructor.
	 * 
	 * @param renderEmptyFields
	 *            flag that indicates whether empty fields should be rendered
	 */
	public DbFileRecordRenderer(boolean renderEmptyFields) {
		this.renderEmptyFields = renderEmptyFields;
	}

	/**
	 *{@inheritDoc}
	 */
	public String render(IRecord record) {
	
		StringBuffer sb = new StringBuffer();
		sb.append("record(");
		sb.append(record.getType());
		sb.append(", \"");
		try {
			sb.append(ResolutionUtil.resolve(AliasResolutionUtil.getNameFromHierarchy(record), record));
		} catch (AliasResolutionException e) {
			sb.append("<" + e.getMessage() + ">");
		}
	
		sb.append("\") {");
		sb.append(NEWLINE);
	
		Map<String, Object> fields = record.getFinalFields();
	
		for (String key : fields.keySet()) {
			String v = fields.get(key) != null ? fields.get(key).toString() : "";
	
			if (("".equals(v) && renderEmptyFields) || !"".equals(v)) {
				sb.append("   field(");
				sb.append(key);
				sb.append(", \"");
	
				String fieldValue = "";
				try {
					sb.append(ResolutionUtil.resolve(v, record));
				} catch (AliasResolutionException e) {
					fieldValue = "<" + e.getMessage() + ">";
				}
				sb.append(fieldValue);
	
				sb.append("\")");
				sb.append(NEWLINE);
			}
		}
	
		sb.append("}");
	
		return sb.toString();
	}

}
