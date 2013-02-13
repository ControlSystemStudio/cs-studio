/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Helper for parsing the rec type/field settings.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FieldParser
{
	/** Parse preference string like
	 *   "ai(INP,FLNK) ; ao (DOL, SIML , FLNK )"
	 *  into list of fields to follow for each record type.
	 *  
	 * @param field_configuration Format "record_type (field1, field2) ; record_type (...)"
	 * @return HashMap od record types to list of field names
	 * @throws Exception on parse error
	 */
    public static HashMap<String, List<String>> parse(final String field_configuration) throws Exception
	{
		final HashMap<String, List<String>> rec_fields =
			new HashMap<String, List<String>>();
		// Split record configs on ';'
		final String[] rec_configs = field_configuration.split("\\s*;\\s*");
		for (String rec_config : rec_configs)
		{
			// Get record type
			final int i1 = rec_config.indexOf('(');
			if (i1 < 0)
				throw new Exception("Missing start of field list in '" + rec_config + "'"); 
			final String rec_type = rec_config.substring(0, i1).trim();
			if (rec_type.length() <= 0)
				throw new Exception("Missing record type in '" + rec_config + "'"); 
			final int i2 = rec_config.indexOf(')', i1);
			if (i2 < 0)
				throw new Exception("Missing end of field list in '" + rec_config + "'");
			// Get fields for that type
			final String[] field_configs = rec_config.substring(i1+1, i2).split("\\s*,\\s*");
			final ArrayList<String> fields = new ArrayList<String>();
			for (String field : field_configs)
			{
				final String field_spec = field.trim();
				// Plain 'FIELD', or 'FIELDA-L'?
				final int range_sep = field_spec.indexOf('-');
				if (range_sep > 0)
				{
				    if (field_spec.length() != range_sep + 2)
				        throw new Exception("Can only handle field ranges with single-character XYZA-L, not for example XYZAA-LL with 2-character ranges");
				    // 'FIELD'
				    final String base = field_spec.substring(0,  range_sep-1);
				    // 'A'
				    char first = field_spec.charAt(range_sep-1);
				    // 'L'
                    char last = field_spec.charAt(range_sep+1);
                    for (char c = first; c<=last; ++c)
                        fields.add(base + c);
				}
				else
				    fields.add(field_spec);
			}
			// Put into hash
			rec_fields.put(rec_type, fields);
		}
		return rec_fields;
	}
}
