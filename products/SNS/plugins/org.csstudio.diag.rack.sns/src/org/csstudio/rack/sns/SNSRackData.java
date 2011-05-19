/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rack.sns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.csstudio.diag.rack.model.RackDataAPI;
import org.csstudio.diag.rack.model.RackList;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.rack.sns.SQL;


public class SNSRackData implements RackDataAPI
{
		final private SQL sql = new SQL();
		final private RDBUtil rdbutil;
		private PreparedStatement select;
		private String returnedRack = "Not Set Yet";

	public SNSRackData() throws Exception
		{
        rdbutil = RDBUtil.connect(Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(), true);
		}


	public String[] getRackNames(String filter) throws Exception {

			final ArrayList<String> rackList = new ArrayList<String>();
	    	final Connection connection = rdbutil.getConnection();
	        final Statement stmt = connection.createStatement();

	        String pvSelect = sql.readRacksFromRDB_Select;

	        final PreparedStatement select =
	            connection.prepareStatement
	            (pvSelect,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

	        select.setString(1, "%"+filter+"%");
	        ResultSet rset;
				rset = select.executeQuery();

	        // Fetch unknown number of responses
	        if (rackList.size() == 0)
	        {
	            String rack = "No Rows Selected";
	            synchronized (rackList)
	            {
	                rackList.add(rack);
	            }
	        }

	        synchronized (rackList)
	        {
	            rackList.clear();
	            while (rset.next()) {
	            	rackList.add(rset.getString(1));
	            }
	        }

	        // Convert to plain java array
	        //String iocs_string_array[] = new String[racks.size()];
	        //racks.toArray(iocs_string_array);

	        rset.close();
	        stmt.close();

	        final String[] rackArray = new String[rackList.size()];
		return rackList.toArray(rackArray);
	}


	/* (non-Javadoc)
	 * @see org.csstudio.rack.model.RackDataAPI#getRackListing(java.lang.String, java.lang.String)
	 */
	public RackList[] getRackListing(String rackName, String slotPosInd) throws Exception {

		final ArrayList<RackList> rackDvcList = new ArrayList<RackList>();

		Connection connection = rdbutil.getConnection();

			if (!rackName.contains(":Cab")) rackName = checkParent(rackName);

			    String pvSelect = sql.readRackDvcListFromRDB_Select;

	            select = connection.prepareStatement(pvSelect);
	            select.setString(1, rackName);
	            select.setString(2, slotPosInd);
	        try
	        {
	            final ResultSet rset = select.executeQuery();
	            rackDvcList.clear();
	            while (rset.next())
	            {
	                RackList device = new RackList(rset.getString(1),rset.getInt(2),rset.getInt(3),
	                		rset.getString(4), rset.getString(5));

	                synchronized (rackDvcList)
	                {
	                    rackDvcList.add(device);

	                }
	            }
	            rset.close();
	            returnedRack = rackName;
	            if ( (rackDvcList.size() == 0) && (!"NoParent".equals(rackName.toString())) )
	            {
	                RackList device = new RackList("No Devices Found",0,0, "", "");
	                synchronized (rackDvcList)
	                {
	                    rackDvcList.add(device);
	                }
	            }
	            else if ( (rackDvcList.size() == 0) && ("NoParent".equals(rackName.toString())) )
	            {
	            	RackList device = new RackList("Unknown Rack Parent",0,0, "", "");
	            	 returnedRack = "Unable to Determine Rack";
	                synchronized (rackDvcList)
	                {
	                    rackDvcList.add(device);
	                }
	            }


	        }
	        finally
	        {
	            select.close();
	        }

	    final RackList[] rackListArray = new RackList[rackDvcList.size()];
		return rackDvcList.toArray(rackListArray);
	}


	public String checkParent (String text2Check)throws Exception {
		int i = 0;
        // if the returned parent is a rack or device type "Cab" we're good.
		while ((!text2Check.contains(":Cab")) && (!"NoParent".equals(text2Check)) )  {
				i++;
				text2Check = findParent(text2Check);
			  	if (i>9) break;
		};
		return text2Check;
	}

  public String findParent(String rackName) throws Exception {
	  if (rackName.indexOf(":", rackName.indexOf(":")+1) != -1) {
		  rackName = rackName.substring(0, rackName.indexOf(":", rackName.indexOf(":")+1));
	  }

	    final ArrayList<String> DvcPVParent = new ArrayList<String>();

        final Connection connection = rdbutil.getConnection();
        final Statement stmt = connection.createStatement();

        String pvSelect = sql.readParentFromRDB_Select;

        final PreparedStatement select =
            connection.prepareStatement
            (pvSelect,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        select.setString(1,rackName);

        ResultSet rset;
			rset = select.executeQuery();

		        synchronized (DvcPVParent)
		        {
		      	   	DvcPVParent.clear();

		            while (rset.next()) {
		            	DvcPVParent.add(rset.getString(1));
		            }
		        }

		   if (DvcPVParent.size() == 0  || rset.wasNull() ) rackName = "NoParent";
		   else rackName = DvcPVParent.get(0);

        rset.close();
        stmt.close();

        return rackName;
	}

	public int getRackHeight() {
		// 45 is the SNS standard.  This will be expanded to database calls when height varies.
		return 45;
	}

	public String getRackName() {
		synchronized (returnedRack){
		return returnedRack;
		}
	}

}
