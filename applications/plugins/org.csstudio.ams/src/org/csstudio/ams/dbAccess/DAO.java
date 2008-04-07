/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.ams.dbAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;

public class DAO implements AmsConstants
{
  public static java.util.Date getUtilDate(ResultSet rs, int idx) throws SQLException
  {
	  //TimeZone tz = TimeZone.getTimeZone("UTC");
	  //Calendar cal = Calendar.getInstance(tz);
	  //Timestamp ts = rs.getTimestamp(idx, cal);
	  //if (ts == null)
	  //  return null;
	  
	  //return new java.util.Date(ts.getTime());

      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      long lDate = rs.getLong(idx);
	  if (lDate == 0)
	    return null;

	  try
	  {
		  return sdf.parse("" + lDate);
	  }
	  catch (ParseException e)
	  {
		  Log.log(Log.WARN, e);
		  return null;
	  }
  }
  
  public static void setUtilDate(PreparedStatement pst, int idx, java.util.Date date) throws SQLException
  {
	  //pst.setTimestamp(idx, 
	  //	  new Timestamp(date == null ? 0 : date.getTime()),
	  //	  Calendar.getInstance(TimeZone.getTimeZone("UTC")));
  
	  if (date == null)
	  {
		  pst.setLong(idx, 0);
		  return;
	  }

	  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String sdate = sdf.format(date);
	  pst.setLong(idx, Long.parseLong(sdate));
  }

  public static void close(Statement st, ResultSet rs)
  {
    try
    {
      if(rs != null)
        rs.close();
    }
    catch(Exception ex)
    {
		Log.log(Log.WARN, ex);
    }

    try
    {
      if(st != null)
        st.close();
    }
    catch(Exception ex)
    {
		Log.log(Log.WARN, ex);
    }
  }

  protected static int getNewID(Connection con, String field, String table) throws SQLException
  {
	  String query = "SELECT MAX(" + field + ") FROM " + table;
	  
	  Statement st = null;
	  ResultSet rs = null;
	  
	  try
	  {
		  st = con.createStatement();
		  rs = st.executeQuery(query);
		  
		  if(rs.next())
			  return rs.getInt(1) + 1;
		  return 1;
	  }
	  catch(SQLException ex)
	  {
		  Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
		  throw ex;
	  }
	  finally
	  {
		  close(st,rs);
	  }
  }
}