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

/**
 * Holds the SQL Statements for the Prepared Statements. TODO find out if we can
 * use the method Connection.prepareStatement before every query. In this case
 * we can build the statement dynamically and can delete this class with all
 * numbers of AND.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 19.05.2008
 */
public class SQLStatements {

	public static String test = "select * from message_content where value = ?";
	
	public static String MAX_ROWNUM = "50000";

	public static String ARCHIVE_SIMPLE = "select  mc.message_id, mpt.name as Property,  mc.value "
		+ "from  message m, message_content mc, msg_property_type mpt "
		+ "where  mpt.id = mc.msg_property_type_id "
		+ "and  m.id = mc.MESSAGE_ID "
		+ "and  m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
		+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
		+ "and ROWNUM < "
		+ MAX_ROWNUM + " " + "order by mc.MESSAGE_ID desc ";

	public static String ARCHIVE_MESSAGES_1 = "select  mc.message_id, mpt.name as Property,  mc.value "
			+ "from  message m, message_content mc, msg_property_type mpt "
			+ "where  mpt.id = mc.msg_property_type_id "
			+ "and  m.id = mc.MESSAGE_ID "
			+ "and  mc.message_id in (select mc.MESSAGE_ID from message_content mc, msg_property_type mpt "
			+ "where mpt.ID = mc.MSG_PROPERTY_TYPE_ID "
			+ "and (mpt.NAME = ? and mc.VALUE = ?) "
			+ ") "
			+ "and  m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ MAX_ROWNUM + " " + "order by mc.MESSAGE_ID desc ";

	public static String ARCHIVE_MESSAGES_2 = "select  mc.message_id, mpt.name as Property,  mc.value "
			+ "from  message m, message_content mc, msg_property_type mpt "
			+ "where  mpt.id = mc.msg_property_type_id "
			+ "and  m.id = mc.MESSAGE_ID "
			+ "and  mc.message_id in (select mc.MESSAGE_ID from message_content mc, msg_property_type mpt "
			+ "where mpt.ID = mc.MSG_PROPERTY_TYPE_ID "
			+ "and (mpt.NAME = ? and mc.VALUE = ?) "
			+ ") "
			+ "and  mc.message_id in (select mc.MESSAGE_ID from message_content mc, msg_property_type mpt "
			+ "where mpt.ID = mc.MSG_PROPERTY_TYPE_ID "
			+ "and (mpt.NAME = ? and mc.VALUE = ?) "
			+ ") "
			+ "and  m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ MAX_ROWNUM + " " + "order by mc.MESSAGE_ID desc ";

	public static String ARCHIVE_MESSAGES_3 = "select  mc.message_id, mpt.name as Property,  mc.value "
			+ "from  message m, message_content mc, msg_property_type mpt "
			+ "where  mpt.id = mc.msg_property_type_id "
			+ "and  m.id = mc.MESSAGE_ID "
			+ "and  mc.message_id in (select mc.MESSAGE_ID from message_content mc, msg_property_type mpt "
			+ "where mpt.ID = mc.MSG_PROPERTY_TYPE_ID "
			+ "and (mpt.NAME = ? and mc.VALUE = ?) "
			+ ") "
			+ "and  mc.message_id in (select mc.MESSAGE_ID from message_content mc, msg_property_type mpt "
			+ "where mpt.ID = mc.MSG_PROPERTY_TYPE_ID "
			+ "and (mpt.NAME = ? and mc.VALUE = ?) "
			+ ") "
			+ "and  mc.message_id in (select mc.MESSAGE_ID from message_content mc, msg_property_type mpt "
			+ "where mpt.ID = mc.MSG_PROPERTY_TYPE_ID "
			+ "and (mpt.NAME = ? and mc.VALUE = ?) "
			+ ") "
			+ "and  m.DATUM between to_date(?, 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(?, 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ MAX_ROWNUM + " " + "order by mc.MESSAGE_ID desc ";
}
