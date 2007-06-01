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

	private ArrayList<HashMap<String, String>> sendSQLStatement( String sqlStatement) {

		ArrayList<HashMap<String, String>> message = new ArrayList<HashMap<String, String>>();
		Connection con = null;
		try{
//			String url = de.desy.css.record.properties.PropertiesPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.URL);
//			String user = PropertiesPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.USER);
//			String password = PropertiesPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.PASSWORD);
//			String url = "jdbc:oracle:thin:@dbsrv:1527:DESY";
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
			HashMap<String, String> hm = null;
			while(rset.next()){
					if(id!=rset.getNUMBER(1).intValue()){
						if(hm!=null){
							message.add(hm);
						}
						hm = new HashMap<String, String>();
					}
					hm.put(rset.getString(4), rset.getString(5));
					id=rset.getNUMBER(1).intValue();
			}
			rset.close(); stmt.close();
			message.add(hm);
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
//	#############################################################
//	Beispiel einen SQL-Strings mit abfrage Filter
//	#############################################################
//	select mc.message_id, mt.name as Nutzer, m.datum, mpt.name as Property,  mc.value
//	from  msg_type mt
//    join msg_type_property_type mtpt on mtpt.msg_type_id = mt.id
//    join msg_property_type mpt on mtpt.msg_property_type_id = mpt.id
//    join message m on m.msg_type_id = mt.id
//    join message_content mc on m.msg_type_id = mt.id
//    where mpt.id = mc.msg_property_type_id
//	and m.id = mc.message_id
//	and m.datum
//	between to_date('2006-10-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
//	and to_date('2006-11-21 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
//	#############################################################
//	Hier fängt der Filter an
//	#############################################################
//    and mc.message_id = ANY (
//        select mc.message_id
//		  from  msg_type mt
//          join msg_type_property_type mtpt on mtpt.msg_type_id = mt.id
//          join msg_property_type mpt on mtpt.msg_property_type_id = mpt.id
//          join message m on m.msg_type_id = mt.id
//          join message_content mc on m.msg_type_id = mt.id
//          where mpt.id = mc.msg_property_type_id
//   		  and ((mpt.name like 'TYPE' AND mc.value like 'alarm') or mpt.name like 'TEXT' )
//   		  )
//	order by mc.message_id;

	private String buildSQLStatement(Calendar from, Calendar to) {
/*	Alter SQL_String
 * 	Macht Probleme bei mehr als zwei AND abfragen
 * 	(Methode private String buildSQLStatement(GregorianCalendar from, GregorianCalendar to, String filter))
 *
		String sql = "select mc.message_id, mt.name as Nutzer, m.datum, mpt.name as Property,  mc.value "+
			"from  msg_type mt, msg_type_property_type mtpt, msg_property_type mpt, message m, message_content mc "+
			"where mtpt.msg_type_id = mt.id "+
			"and mtpt.msg_property_type_id = mpt.id "+
			"and m.msg_type_id = mt.id "+
			"and mpt.id = mc.msg_property_type_id "+
			"and m.id = mc.message_id "+
			"and m.datum "+
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
			"order by mc.message_id";
*/
/*
 * Neue SQL-String unter hilfe nahme einer View
 */
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
					"order by aam.message_id";

		return sql;
	}

	private String buildSQLStatement(Calendar from, Calendar to, String filter) {
		/*	Alter SQL_String
		 * 	Macht Probleme bei mehr als zwei AND abfragen
		 * 	(Methode private String buildSQLStatement(GregorianCalendar from, GregorianCalendar to, String filter))
		 *

		String sql =
			"select mc.message_id, mt.name as Nutzer, m.datum, mpt.name as Property,  mc.value "+
			"from  msg_type mt "+
		    "join msg_type_property_type mtpt on mtpt.msg_type_id = mt.id "+
		    "join msg_property_type mpt on mtpt.msg_property_type_id = mpt.id "+
		    "join message m on m.msg_type_id = mt.id "+
		    "join message_content mc on m.msg_type_id = mt.id "+
		    "where mpt.id = mc.msg_property_type_id "+
			"and m.id = mc.message_id "+
			"and m.datum "+
			"between to_date('"+from.get(GregorianCalendar.YEAR)+
			"-"+(from.get(GregorianCalendar.MONTH)+1)+
			"-"+from.get(GregorianCalendar.DAY_OF_MONTH)+
			" "+from.get(GregorianCalendar.HOUR)+
			":"+from.get(GregorianCalendar.MINUTE)+
			":"+from.get(GregorianCalendar.SECOND)+
			"', 'YYYY-MM-DD HH24:MI:SS') "+
			"AND to_date('"+to.get(GregorianCalendar.YEAR)+
			"-"+(to.get(GregorianCalendar.MONTH)+1)+
			"-"+to.get(GregorianCalendar.DAY_OF_MONTH)+
			" "+to.get(GregorianCalendar.HOUR)+
			":"+to.get(GregorianCalendar.MINUTE)+
			":"+to.get(GregorianCalendar.SECOND)+
			"', 'YYYY-MM-DD HH24:MI:SS') "+
			filter+
			"order by mc.message_id";
*/
/*
 * Neue SQL-String unter hilfe nahme einer View
 */
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
							filter+
							"order by aam.message_id";
		return sql;
	}


	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to) {
		String sql = buildSQLStatement(from, to);
		System.out.println(sql);
		ArrayList<HashMap<String, String>> ergebniss = sendSQLStatement(sql);
		return ergebniss;
	}

	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, String filter) {
		String sql = buildSQLStatement(from, to, filter);
		System.out.println(sql);
		ArrayList<HashMap<String, String>> ergebniss = sendSQLStatement(sql);
		return ergebniss;
	}

}
