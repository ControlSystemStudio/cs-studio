/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.sns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.csstudio.diag.pvfields.model.PVFieldsAPI;
import org.csstudio.diag.pvfields.model.PVInfo;
import org.csstudio.platform.utility.rdb.RDBUtil;

/**
 * Implementation of PVFieldsAPI for SNS RDB and EPICS PVs
 *
 * @author Dave Purcell
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVFieldsData implements PVFieldsAPI
{
    final private RDBUtil rdbutil;
    private ArrayList<String> extraFields = new ArrayList<String>();

    public SNSPVFieldsData() throws Exception
    {
		rdbutil = RDBUtil.connect(Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(), true);
    }

	@Override
    public PVInfo [] getPVInfo(String pv_name, String field) throws Exception {

		final Connection connection = rdbutil.getConnection();
        final ArrayList<PVInfo> pvList = new ArrayList<PVInfo>();

        /** Setup first portion of select statement */
        String pvSelect = "select distinct sgnl_id,rec_type_id, fld_id, dbd_type_id, fld_val, ioc_nm," +
        		" to_char(boot_dte,'Month DD, YYYY \"at\" HH:MI am') as boot_dte, file_nm ";
        String pvFrom	= "from epics.sgnl_fld_v ";

        /** Determine type of where clause for PV */
        String pvPVWhere = "where sgnl_id = ? ";
        if (pv_name.contains("%")) pvPVWhere = "where sgnl_id like ? ";

        /** Determine type of where clause for the Field(s) */
        String pvFieldWhere = "";
        String unionClause = "";
        String orderByClause = " order by fld_id,sgnl_id";

        // Useful field selector?
        if (field != null)
        {
            field = field.trim();
            if (field.length() <= 0   ||   "%".equals(field))
                field = null;
        }
        // Limit selected fields
        if (field != null)
        {
            if (field.contains("%") && !field.contains(",")) // Use as single pattern
                pvFieldWhere = "and fld_id like '" + field + "' ";
            else
            {   // List of field names. Split at comma, allowing spaces.
                final String fields[] = field.split("\\s*,\\s*");
                if (fields.length >= 1)
                {

                	if (fields[0].contains("%")) pvFieldWhere = "and (fld_id like '" + fields[0] + "' ";
                	else {
                		pvFieldWhere = "and (fld_id = '" + fields[0] + "' ";
                		extraFields.add(fields[0]);
                	}

                	for (int i=1; i<fields.length; ++i) {
                		if (fields[i].contains("%")) pvFieldWhere += "or fld_id like '" + fields[i] + "' ";
                		else {
                			pvFieldWhere += "or fld_id = '" + fields[i] + "' ";
                			extraFields.add(fields[i]);
                		}
                	}

                	pvFieldWhere += ") ";
                }
            }

            if (field.contains("VAL"))
            {
            	unionClause = " union select distinct sgnl_id,rec_type_id, 'VAL', '', '', ioc_nm, " +
    			"to_char(boot_dte,'Month DD, YYYY \"at\" HH:MI am') as boot_dte, file_nm " +
    			"from epics.sgnl_fld_v ";
            	if (pv_name.contains("%")) unionClause += "where sgnl_id like '"+ pv_name +"' ";
            	else unionClause += "where sgnl_id = '"+ pv_name +"' ";
            }
        }

        /** Query selected is dependent on the process variable passed */
        String pvStatement = pvSelect + pvFrom + pvPVWhere + pvFieldWhere + unionClause + orderByClause;
        final PreparedStatement select = connection.prepareStatement(pvStatement);
        select.setString(1, pv_name);

        PVInfo pv = null;
        try
        {

            final ResultSet rset = select.executeQuery();

            while (rset.next())
            {
                if (pvList.size()==0 && !pv_name.contains("%") && field==null) {
                	       	pv =  new SNSPVField(pv_name,
                            rset.getString(2),
                            rset.getString(6),
                            rset.getString(7),
                            rset.getString(8),
    	                    "VAL",
    	                    "",
    	                    "");
            				pvList.add(pv);
                }

            		pv =  new SNSPVField(rset.getString(1),
                            rset.getString(2),
                            rset.getString(6),
                            rset.getString(7),
                            rset.getString(8),
		                    rset.getString(3),
		                    rset.getString(4),
		                    rset.getString(5));

            		pvList.add(pv);

             	  if (extraFields.contains(rset.getString(3))) {
             		 extraFields.remove(rset.getString(3));
             	  }

            }
            if (extraFields.size() > 0 || (pv_name.contains("%") && field==null ) )
            {
            	if (pv_name.contains("%")) {
            		String pvListSelect = "select distinct sgnl_id from epics.sgnl_fld_v where sgnl_id like ? ";
            		final PreparedStatement listSelect = connection.prepareStatement(pvListSelect,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            		listSelect.setString(1, pv_name);
            		final ResultSet pvListSet = listSelect.executeQuery();

            		if (field==null)
            		{
            			while (pvListSet.next())
                		{
            				pv = new SNSPVField(pvListSet.getString(1), "", "", "", "","VAL","","");
		                	pvList.add(pv);
                		}
            		}
            		else
            		{
            			for (int i=0; i<extraFields.size(); ++i) {
            				while (pvListSet.next())
            				{
			            		pv = new SNSPVField(pvListSet.getString(1), "", "", "", "",extraFields.get(i),"","Not Originally Set");
			                	pvList.add(pv);
			                }
            				pvListSet.beforeFirst();
	            		}
            		}
            		pvListSet.close();
            	}
            	else
            	{
		            	for (int i=0; i<extraFields.size(); ++i) {
		            		pv = new SNSPVField(pv_name, "", "", "", "",extraFields.get(i),"","Not Originally Set");
		                	pvList.add(pv);
		                	}

	            }
            }
            rset.close();

        }
        finally
        {
            select.close();
            extraFields.clear();
        }
        if (pvList.size() == 0 )
        {   // Nothing found, create dummy PV for message
            pv = new PVInfo("Not found", "", "", "", "","No Results Found","","");
        	pvList.add(pv);
        }
        final PVInfo[] pvInfoArray = new PVInfo[pvList.size()];
        return pvList.toArray(pvInfoArray);
	}
}
