package org.csstudio.alarm.dbaccess;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;



import org.csstudio.alarm.dbaccess.archivedb.Activator;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;

public class ArchiveDBAccess implements ILogMessageArchiveAccess {

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

	private ArrayList<HashMap<String, String>> sendSQLStatement( String sqlStatement, int maxAnserSize) {

		ArrayList<HashMap<String, String>> message = new ArrayList<HashMap<String, String>>();
		Connection con = null;
		try{
			String url = "jdbc:oracle:thin:@(DESCRIPTION = " +
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
			String user = "KRYKLOGT";
			String password = "KRYKLOGT";

			con = DriverManager.getConnection(url, user, password);
			OracleStatement stmt = (OracleStatement)con.createStatement();

			stmt.execute(
					sqlStatement
			);

			OracleResultSet rset = (OracleResultSet)stmt.getResultSet();
			int id =-1;
			int i = 0;
			HashMap<String, String> hm = null;
			boolean haveMessage = false;
			while(rset.next()){
					if((id!=rset.getNUMBER(1).intValue()) && (i < maxAnserSize)){
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
		}
		finally{
			try{
				if(con != null) con.close();
			}catch(Exception e){}
		}
		return message;
	}
    private String buildSQLStatement(Calendar from, Calendar to, int maxAnserSize) {
        String sql = "select * from alarm_archive_messages aam where aam.datum "+
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
                    " order by aam.message_id "; 
        return sql;
    }

	private String buildSQLStatement(Calendar from, Calendar to, String filter, int maxAnserSize) {
	    String sql = "select aam2.* from alarm_archive_messages aam2 ,(select aam.MESSAGE_ID from alarm_archive_messages aam where aam.datum "+ 
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
					 filter+
					 " order by aam.message_id) typeid where aam2.message_id = typeid.MESSAGE_ID ";
	    return sql;
	}


	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, int maxAnserSize) {
		String sql = buildSQLStatement(from, to, maxAnserSize);
		Activator.logInfo(sql);
		ArrayList<HashMap<String, String>> ergebniss = sendSQLStatement(sql, maxAnserSize);
		return ergebniss;
	}

	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, String filter, int maxAnserSize) {
		String sql = buildSQLStatement(from, to, filter, maxAnserSize);
		Activator.logInfo(sql);
		ArrayList<HashMap<String, String>> ergebniss = sendSQLStatement(sql, maxAnserSize);
		return ergebniss;
	}

}
