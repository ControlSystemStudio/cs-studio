package org.csstudio.diag.pvutil.sns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.csstudio.diag.pvutil.model.FEC;
import org.csstudio.diag.pvutil.model.PV;
import org.csstudio.diag.pvutil.model.PVUtilDataAPI;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.rdb.RDBUtil;

public class SNSPVUtilData implements PVUtilDataAPI
{
   final private RDBUtil rdbutil;   
   final private static String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdb1.sns.ornl.gov/prod";
   //private static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdev3.sns.ornl.gov:1521/devl";

   
  /** Connection to the SNS RDB. 
   * Dictated by the string variable URL
   * 
   * @throws Exception
   */
   public SNSPVUtilData() throws Exception
	{ rdbutil = RDBUtil.connect(URL);
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.PVUtilDataAPI#getFECs(java.lang.String)
	 */
	public FEC[] getFECs(String filter) throws Exception {		

		filter = "%" + filter + "%";
		final ArrayList<FEC> fecs = new ArrayList<FEC>();
		final Connection connection = rdbutil.getConnection();
        
		// This looks for beam line devices and FECs.  
		// Other device types will have to be added if appropriate.
        final String deviceSelect = " select distinct dvc_id from epics.dvc " +
        "where upper(dvc_id) like upper(?)" +
        "and ( bl_dvc_ind = 'Y'" +
        "	or dvc_type_id in ('IOC') )" +
        "order by dvc_id";
         
        final PreparedStatement select = 
            connection.prepareStatement
            (deviceSelect,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        try
        {
	        select.setString(1, filter);
	        final ResultSet rset = select.executeQuery();
	        while (rset.next())
	        	fecs.add(new FEC(rset.getString(1)));
	        rset.close();
        }
        finally
        {
        	select.close();
        }

        connection.close();

        // Convert to plain java array
        final FEC[] fecArray = new FEC[fecs.size()];
		return fecs.toArray(fecArray);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.PVUtilDataAPI#getPVs(java.lang.String, java.lang.String)
	 */
	public PV[] getPVs(String deviceID, String filterPV)throws Exception {

		final ArrayList<PV> pvs = new ArrayList<PV>();

        Connection connection = rdbutil.getConnection();
        String WILDCARD = "%";
        final PreparedStatement select;
        final String iocNetNm;
        final String message;

        if (deviceID == "") deviceID = iocNetNm = WILDCARD;
        else iocNetNm = deviceID.replaceAll("([_:])", "-").toLowerCase();
        
        if (filterPV == "")  filterPV = WILDCARD;

        String likeClause;
        if (filterPV.contains("%")) likeClause = "like ?";
        else likeClause = "= ?";
        

		Logger logger = CentralLogger.getInstance().getLogger(this);
		/** Query selected is dependent on the filter values passed */
		if (deviceID == WILDCARD  && filterPV != WILDCARD)
        {
            String pvSelect = "select distinct sgnl_id,rec_type_id ||' record associated with: ' || ioc_nm as info " +
            		"from epics.sgnl_fld_v where sgnl_id ";
            pvSelect += likeClause;
            pvSelect += " union ";
            pvSelect += "select distinct sgnl_id,rec_type_id ||' record associated with: ' || dvc_id as info from epics.sgnl_rec where sgnl_id ";
            pvSelect += likeClause;
            logger.debug("1: PV Filter - No Device");
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
            logger.debug("2: Device Filter - No PV");
            select = connection.prepareStatement(pvSelect);
            select.setString(1, iocNetNm);
            select.setString(2, deviceID);
        }
        else if (filterPV != WILDCARD && deviceID != WILDCARD )
        {
            String pvSelect = "select distinct sgnl_id,rec_type_id ||' record associated with: ' || ioc_nm as info " +
            		"from epics.sgnl_fld_v where ioc_nm = ? and sgnl_id ";
            pvSelect += likeClause;
            pvSelect += " union ";
            pvSelect += "select distinct sgnl_id,rec_type_id ||' record associated with: ' || dvc_id as info from epics.sgnl_rec where dvc_id = ? and sgnl_id ";
            pvSelect += likeClause;
            logger.debug("3: PV Filter and Device Filter ioc " + iocNetNm + "dvc " + deviceID + "rec " + filterPV);
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
            logger.debug("4: Setting up.");
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
        
        connection.close();
        
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
	public String getStartDeviceID() {
		return ":IOC";
		//return "LLRF";
		//return "%";
		//return "";
	}

}
