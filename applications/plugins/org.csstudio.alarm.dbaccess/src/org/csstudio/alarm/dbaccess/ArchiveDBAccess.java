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
 package org.csstudio.alarm.dbaccess;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;



import org.csstudio.alarm.dbaccess.archivedb.Activator;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.08.2007
 */
public final class ArchiveDBAccess implements ILogMessageArchiveAccess {
    static final String url = "jdbc:oracle:thin:@(DESCRIPTION = " +
        "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521)) " +
        "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521)) " +
        "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521)) " +
        "(LOAD_BALANCE = yes) " +
         "(CONNECT_DATA = " +
         "(SERVER = DEDICATED) " +
          "(SERVICE_NAME = desy_db.desy.de) " +
          "(FAILOVER_MODE = " +
            "(TYPE = NONE) " +
            "(METHOD = BASIC) " +
            "(RETRIES = 180) " +
            "(DELAY = 5) " +
          ")" +
        ")" +
      ")";
    static final String user = "KRYKLOGT";
    static final String password = "KRYKLOGT";
	//Text t = null;
	{
		try{
			DriverManager.registerDriver(new OracleDriver());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public ArchiveDBAccess() {
		// TODO Auto-generated constructor stub
	}

	private static ArrayList<HashMap<String, String>> sendSQLStatement( String sqlStatement, int maxAnswerSize, Calendar from, Calendar to) {

		ArrayList<HashMap<String, String>> message = new ArrayList<HashMap<String, String>>();
		Connection con = null;
		int messageCount = 0;
		OracleResultSet rset;
		
		try{
			con = Activator.getDefault().getDatabaseConnection(url, user, password);
			
			OracleStatement stmt = (OracleStatement)con.createStatement();
			
			stmt.execute(
					  sqlStatement
			);

			rset = (OracleResultSet)stmt.getResultSet();
			int id =-1;
			int i = 0;
			HashMap<String, String> hm = null;
			boolean haveMessage = false;
			while(rset.next()){
					if((id!=rset.getNUMBER(1).intValue()) /*&& (i < maxAnserSize)*/ ){
						if(hm!=null){
							message.add(hm);
							i++;
						}
						haveMessage = true;
						hm = new HashMap<String, String>();
					}
					hm.put(rset.getString(4), rset.getString(5));
					id=rset.getNUMBER(1).intValue();
			}
			rset.close(); stmt.close();
			if(haveMessage){
				message.add(hm);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//			try{
//				if(con != null) con.close();
//			}catch(Exception e){}
		}
		return message;
	}
    private String buildSQLStatement(Calendar from, Calendar to, int maxAnswerSize) {
    	/*
    	 * old style
    	 * 
    	"select * from (" +
    		"select * from alarm_archive_messages aam where aam.datum "+
                 "between to_date('"+from.get(GregorianCalendar.YEAR)+
                    "-"+(from.get(GregorianCalendar.MONTH)+1)+
                    "-"+from.get(GregorianCalendar.DAY_OF_MONTH)+
                    " "+from.get(GregorianCalendar.HOUR)+
                    ":"+from.get(GregorianCalendar.MINUTE)+
                    ":"+from.get(GregorianCalendar.SECOND)+
                    "', 'YYYY-MM-DD HH24:MI:SS') "+
                "and to_date('"+to.get(GregorianCalendar.YEAR)+
                    "-"+(to.get(GregorianCalendar.MONTH)+1)+
                    "-"+to.get(GregorianCalendar.DAY_OF_MONTH)+
                    " "+to.get(GregorianCalendar.HOUR)+
                    ":"+to.get(GregorianCalendar.MINUTE)+
                    ":"+to.get(GregorianCalendar.SECOND)+
                    "', 'YYYY-MM-DD HH24:MI:SS') "+
                " order by aam.message_id desc " +
                ") where ROWNUM < " + maxAnswerSize*10; 
                */
    	
        String sql = 
        	"select myTable.* , rownum myRownum from (" +
				"select mc.message_id, mc.id , m.datum, mpt.name,  mc.value " +
		        "from msg_property_type mpt, message m, message_content mc " +
		        "where " +
		        "m.datum "+
		        "between to_date('"+from.get(GregorianCalendar.YEAR)+
		           "-"+(from.get(GregorianCalendar.MONTH)+1)+
		           "-"+from.get(GregorianCalendar.DAY_OF_MONTH)+
		           " "+from.get(GregorianCalendar.HOUR_OF_DAY)+
		           ":"+from.get(GregorianCalendar.MINUTE)+
		           ":"+from.get(GregorianCalendar.SECOND)+
		           "', 'YYYY-MM-DD HH24:MI:SS') "+
		       "and to_date('"+to.get(GregorianCalendar.YEAR)+
		           "-"+(to.get(GregorianCalendar.MONTH)+1)+
		           "-"+to.get(GregorianCalendar.DAY_OF_MONTH)+
		           " "+to.get(GregorianCalendar.HOUR_OF_DAY)+
		           ":"+to.get(GregorianCalendar.MINUTE)+
		           ":"+to.get(GregorianCalendar.SECOND)+
		           "', 'YYYY-MM-DD HH24:MI:SS') "+
		       "and mc.message_id = m.id " +
		       "and mpt.id = mc.msg_property_type_id " +
		         " order by mc.message_id desc" +
	         " ) myTable where rownum < " + maxAnswerSize*10   + 
	         "  order by myRownum "  ;
        return sql;
    }

    private String buildSQLStatement(Calendar from, Calendar to, String filter, int maxAnswerSize) {
	    /*
	     * TODO change statement accordingly like the upper one
	     * for now it's still the old one - so the FILTER will not break
	     */
		String sql = /* "select * from (" + */
	    		"select aam2.* from alarm_archive_messages aam2 ,(select aam.MESSAGE_ID from alarm_archive_messages aam where aam.datum "+ 
					 "between to_date('"+from.get(GregorianCalendar.YEAR)+
						"-"+(from.get(GregorianCalendar.MONTH)+1)+
						"-"+from.get(GregorianCalendar.DAY_OF_MONTH)+
						" "+from.get(GregorianCalendar.HOUR_OF_DAY)+
						":"+from.get(GregorianCalendar.MINUTE)+
						":"+from.get(GregorianCalendar.SECOND)+
						"', 'YYYY-MM-DD HH24:MI:SS') "+
					 "and to_date('"+to.get(GregorianCalendar.YEAR)+
							"-"+(to.get(GregorianCalendar.MONTH)+1)+
							"-"+to.get(GregorianCalendar.DAY_OF_MONTH)+
							" "+to.get(GregorianCalendar.HOUR_OF_DAY)+
							":"+to.get(GregorianCalendar.MINUTE)+
							":"+to.get(GregorianCalendar.SECOND)+
							"', 'YYYY-MM-DD HH24:MI:SS') "+
					 filter+
					 " order by aam.message_id desc) typeid where aam2.message_id = typeid.MESSAGE_ID " +
					 /* " ) where*/ " and ROWNUM < " + maxAnswerSize*10 + " order by aam2.message_id desc"; 
	    return sql;
//	    String sql = 
//            "select myTable.* , rownum myRownum from (" +
//                "select mc.message_id, mc.id , m.datum, mpt.name,  mc.value "+ 
//                    "from msg_property_type mpt, message m, message_content mc,("+
//                        "select  mc.message_id "+
//                            "from msg_property_type mpt, message m, message_content mc "+ 
//                            "where m.datum "+
//                            "between to_date('"+from.get(GregorianCalendar.YEAR)+
//                               "-"+(from.get(GregorianCalendar.MONTH)+1)+
//                               "-"+from.get(GregorianCalendar.DAY_OF_MONTH)+
//                               " "+from.get(GregorianCalendar.HOUR)+
//                               ":"+from.get(GregorianCalendar.MINUTE)+
//                               ":"+from.get(GregorianCalendar.SECOND)+
//                               "', 'YYYY-MM-DD HH24:MI:SS') "+
//                            "and to_date('"+to.get(GregorianCalendar.YEAR)+
//                               "-"+(to.get(GregorianCalendar.MONTH)+1)+
//                               "-"+to.get(GregorianCalendar.DAY_OF_MONTH)+
//                               " "+to.get(GregorianCalendar.HOUR)+
//                               ":"+to.get(GregorianCalendar.MINUTE)+
//                               ":"+to.get(GregorianCalendar.SECOND)+
//                               "', 'YYYY-MM-DD HH24:MI:SS') "+
//                           "and mc.message_id = m.id " +
//                           "and mpt.id = mc.msg_property_type_id " +
//                           filter+
//                           " order by mc.message_id desc"+
//                    ")" +
//             ") myTable where rownum < " + maxAnswerSize*10   + 
//             "  order by myRownum "  ;
//        return sql;
	}


	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, int maxAnserSize) {
		String sql = buildSQLStatement(from, to, maxAnserSize);
		Activator.logInfo(sql);
		ArrayList<HashMap<String, String>> ergebniss = sendSQLStatement(sql, maxAnserSize, from, to);
		return ergebniss;
	}

	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, String filter, int maxAnserSize) {
		String sql = buildSQLStatement(from, to, filter, maxAnserSize);
		Activator.logInfo(sql);
		ArrayList<HashMap<String, String>> ergebniss = sendSQLStatement(sql, maxAnserSize, from, to);
		return ergebniss;
	}

    /**
     * @return the Answer [0] is the Id and [1] is the Type.
     * 
     */
    public static String [][] getMsgTypes() {
        Connection con = null;
        String sql="select * from msg_property_type mpt order by id";
        try{
            con = Activator.getDefault().getDatabaseConnection(url, user, password);
            
            OracleStatement stmt = (OracleStatement)con.createStatement();
            
            stmt.execute(
                      sql
            );

            OracleResultSet rset = (OracleResultSet)stmt.getResultSet();
            ArrayList<String[]> ans = new ArrayList<String[]>();
            while(rset.next()){
                String id = rset.getString("ID");
                String name = rset.getString(2);
                ans.add(new String[]{id,name});
            }
            return ans.toArray(new String[0][2]);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
        
        
    }

}
