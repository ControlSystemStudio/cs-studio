/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.sns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.csstudio.diag.pvutil.model.FEC;
import org.csstudio.diag.pvutil.model.PV;
import org.csstudio.diag.pvutil.model.PVUtilDataAPI;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Implementation of the PVUtilDataAPI for the SNS RDB
 *  @author Dave Purcell
 */
@SuppressWarnings("nls")
public class SNSPVUtilData implements PVUtilDataAPI
{
   final private RDBUtil rdbutil;

  /** Connection to the SNS RDB.
   * Dictated by the string variable URL
   *
   * @throws Exception
   */
   public SNSPVUtilData() throws Exception
   {
       rdbutil = RDBUtil.connect(Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(), true);
    }

	/* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.PVUtilDataAPI#getFECs(java.lang.String)
	 */
	@Override
    public FEC[] getFECs(String filter) throws Exception {

		filter = filter.trim();

		//filter = "%" + filter + "%";
		final ArrayList<FEC> fecs = new ArrayList<FEC>();
		final Connection connection = rdbutil.getConnection();
		// This looks for beam line devices and FECs.
		// Other device types will have to be added if appropriate.
        String deviceSelect = " select 'bad sql happening' as dvc from dual";
        if (filter.length() <= 0) {
        	deviceSelect = "select distinct dvc_id from epics.dvc " +
        			"where bl_dvc_ind = 'Y' " +
        			"union " +
        			"select distinct dvc_id from  epics.ics_netreg_dvc_v " +
        			"order by dvc_id";
        }
        else {
        	deviceSelect = "select distinct dvc_id from epics.dvc  " +
        			"     where upper(dvc_id) like upper('%"+filter+"%')" +
        			"       and ( bl_dvc_ind = 'Y'" +
        			"            or dvc_type_id in (select distinct a.dvc_type_id from epics.dvc a, epics.ioc_dvc b" +
        			"                                where a.dvc_id = b.dvc_id union" +
        			"                                select distinct a.dvc_type_id from epics.dvc a, epics.ics_comm_dvc b" +
        			"                                where a.dvc_id = b.dvc_id) )" +
        			"        order by dvc_id";
        }

        final PreparedStatement select =
            connection.prepareStatement
            (deviceSelect,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        try
        {
	        //select.setString(1, filter);
	        final ResultSet rset = select.executeQuery();
	        while (rset.next())
	        	fecs.add(new FEC(rset.getString(1)));
	        rset.close();
        }
        finally
        {
        	select.close();
        }

        // Convert to plain java array
        final FEC[] fecArray = new FEC[fecs.size()];
		return fecs.toArray(fecArray);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.PVUtilDataAPI#getPVs(java.lang.String, java.lang.String)
	 */
	@Override
    public PV[] getPVs(String deviceID, String filterPV)throws Exception {

		final ArrayList<PV> pvs = new ArrayList<PV>();

        Connection connection = rdbutil.getConnection();
        String WILDCARD = "%";
        final PreparedStatement select;
        final String iocNetNm;
        final String message;

        if (deviceID == "") deviceID = iocNetNm = WILDCARD;
        else iocNetNm = deviceID.replaceAll("([_:])", "-").toLowerCase();

        if (filterPV.length() <= 0)  filterPV = WILDCARD;


        String likeClause;
        if (filterPV.contains("%")) likeClause = "like upper(?)";
        else likeClause = "= upper(?)";


		/** Query selected is dependent on the filter values passed */
		if (deviceID == WILDCARD  && filterPV != WILDCARD)
        {
            String pvSelect = "select distinct sgnl_id,rec_type_id ||' record associated with: ' || ioc_nm as info " +
            		"from epics.sgnl_fld_v where upper(sgnl_id) ";
            pvSelect += likeClause;
            pvSelect += " union ";
            pvSelect += "select distinct sgnl_id,rec_type_id ||' record associated with: ' || dvc_id as info from epics.sgnl_rec where upper(sgnl_id) ";
            pvSelect += likeClause;

            Activator.getLogger().fine("1: PV Filter - No Device");
            select = connection.prepareStatement(pvSelect);
            select.setString(1, filterPV);
            select.setString(2, filterPV);
        }
        else if (filterPV == WILDCARD && deviceID != WILDCARD)
        {
            String pvSelect = "select distinct sgnl_id,rec_type_id ||' record associated with: ' || ioc_nm as info " +
            		"from epics.sgnl_fld_v where ioc_nm = ? " +
            		"union " +
            		"select distinct sgnl_id,rec_type_id ||' record associated with: ' || dvc_id as info  " +
            		"from epics.sgnl_rec where dvc_id = ?";
            Activator.getLogger().fine("2: Device Filter - No PV");
            select = connection.prepareStatement(pvSelect);
            select.setString(1, iocNetNm);
            select.setString(2, deviceID);
        }
        else if (filterPV != WILDCARD && deviceID != WILDCARD )
        {
            String pvSelect = "select distinct sgnl_id,rec_type_id ||' record associated with: ' || ioc_nm as info " +
            		"from epics.sgnl_fld_v where ioc_nm = ? and upper(sgnl_id) ";
            pvSelect += likeClause;
            pvSelect += " union ";
            pvSelect += "select distinct sgnl_id,rec_type_id ||' record associated with: ' || dvc_id as info from epics.sgnl_rec where dvc_id = ? and upper(sgnl_id) ";
            pvSelect += likeClause;
            Activator.getLogger().fine("3: PV Filter and Device Filter ioc " + iocNetNm + "dvc " + deviceID + "rec " + filterPV);
            select = connection.prepareStatement(pvSelect);
            select.setString(1, iocNetNm);
            select.setString(2, filterPV);
            select.setString(3, deviceID);
            select.setString(4, filterPV);
        }
        else
        {
        	/** Dummy query for default set up.  */
        	String pvSelect = "select distinct rec_nm,'info' as info from irmisbase.ioc_record_v where 1=2";
        	Activator.getLogger().fine("4: Setting up.");
            select = connection.prepareStatement(pvSelect);
        }
        try
        {
            final ResultSet rset = select.executeQuery();
            pvs.clear();
            while (rset.next())
            {
                PV device = new PV(rset.getString(1), rset.getString(2));
                synchronized (pvs)
                {
                    pvs.add(device);
                }
            }
            rset.close();
            if (pvs.size() == 0)
            {
                //if-then to determine Info returned when no records are returned.
                if (deviceID == "%" && filterPV != "%") {
                	message = "No PVs found like '" + filterPV +"'";
                }
                else if (deviceID != "%S" && filterPV == "%") {
                	message = "No PVs associated with '" + deviceID +"'";
                }
                else{
                	message = "When combining '" + deviceID + "' and '" + filterPV +"'";
                }

                PV device = new PV("No PVs Found", message);

                synchronized (pvs)
                {
                    pvs.add(device);
                }
            }
        }
        finally
        {
            select.close();
        }

        // Convert to plain java array
        final PV[] pvArray = new PV[pvs.size()];
		return pvs.toArray(pvArray);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.PVUtilDataAPI#getStartDeviceID()
	 *
	 * This returns the filter to get the FEC list reduced upon initialization
	 * of the plugin.  Wildcards should be included as
	 * approriate for implementation.
	 * Null can not be returned.
	 */
	@Override
    public String getStartDeviceID()
	{
		return ":IOC";
		//return "LLRF";
		//return "%";
		//return "";
	}
}
