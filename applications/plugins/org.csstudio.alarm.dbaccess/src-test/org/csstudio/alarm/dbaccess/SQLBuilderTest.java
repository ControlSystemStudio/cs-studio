package org.csstudio.alarm.dbaccess;

import java.util.ArrayList;

import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that builds prepared SQL statements for settings in the expert search
 * in the alarm archive log table.
 * 
 * @author jhatje
 * 
 */
public class SQLBuilderTest {

	private String NO_FILTER_ITEMS = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ "50000 order by mc.MESSAGE_ID desc ";

	private String TWO_SIMPLE_FILTER_ITEMS = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.SEVERITY = ? "
			+ "and m.TYPE = ? "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ "50000 order by mc.MESSAGE_ID desc ";

	private String TWO_SUBQUERY_FILTER_ITEMS = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ "50000 order by mc.MESSAGE_ID desc ";

	private String FOUR_FILTER_ITEMS_BOTH_TYPES = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.SEVERITY = ? "
			+ "and m.TYPE = ? "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ "50000 order by mc.MESSAGE_ID desc ";

	private String NO_FILTER_ITEMS_NO_ROW = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ " order by mc.MESSAGE_ID desc ";

	private String TWO_SIMPLE_FILTER_ITEMS_NO_ROW = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.SEVERITY = ? "
			+ "and m.TYPE = ? "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ " order by mc.MESSAGE_ID desc ";

	private String TWO_SUBQUERY_FILTER_ITEMS_NO_ROW = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ " order by mc.MESSAGE_ID desc ";

	private String FOUR_FILTER_ITEMS_BOTH_TYPES_NO_ROW = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.SEVERITY = ? "
			+ "and m.TYPE = ? "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ " order by mc.MESSAGE_ID desc ";

	private String COUNT_FOUR_FILTER_ITEMS = "select count(m.id) "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID "
			+ "and m.SEVERITY = ? "
			+ "and m.TYPE = ? "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
			+ "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? " + ") "
			+ "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') ";

	@Before
	public void setUp() {
		// The Filter Items will be manipulated by ArchiveDBAccess and therefore
		// they must set up for each test.
	}

	/**
	 * Create some prepared statement and compare them with the estimated ones.
	 */
	@Test
	public void testGenerateSQL() {
		ArrayList<FilterItem> noFilterItems = new ArrayList<FilterItem>();
		ArrayList<FilterItem> twoSimpleFilterItems = new ArrayList<FilterItem>();
		ArrayList<FilterItem> twoSubqueryFilterItems = new ArrayList<FilterItem>();
		ArrayList<FilterItem> fourFilterItemsBothTypes = new ArrayList<FilterItem>();
		ArrayList<FilterItem> fourFilterItemsBothTypesCount = new ArrayList<FilterItem>();

		FilterItem severityItem = new FilterItem("severity", "high", "and");
		FilterItem typeItem = new FilterItem("type", "event", "and");
		FilterItem hostItem = new FilterItem("host", "berndTest", "and");
		FilterItem facilityItem = new FilterItem("facility", "test", "and");
		twoSimpleFilterItems.add(severityItem);
		twoSimpleFilterItems.add(typeItem);

		hostItem = new FilterItem("host", "berndTest", "and");
		facilityItem = new FilterItem("facility", "test", "and");
		twoSubqueryFilterItems.add(hostItem);
		twoSubqueryFilterItems.add(facilityItem);

		severityItem = new FilterItem("severity", "high", "and");
		typeItem = new FilterItem("type", "event", "and");
		hostItem = new FilterItem("host", "berndTest", "and");
		facilityItem = new FilterItem("facility", "test", "and");
		fourFilterItemsBothTypes.add(hostItem);
		fourFilterItemsBothTypes.add(severityItem);
		fourFilterItemsBothTypes.add(facilityItem);
		fourFilterItemsBothTypes.add(typeItem);

		severityItem = new FilterItem("severity", "high", "and");
		typeItem = new FilterItem("type", "event", "and");
		hostItem = new FilterItem("host", "berndTest", "and");
		facilityItem = new FilterItem("facility", "test", "and");
		fourFilterItemsBothTypesCount.add(hostItem);
		fourFilterItemsBothTypesCount.add(severityItem);
		fourFilterItemsBothTypesCount.add(facilityItem);
		fourFilterItemsBothTypesCount.add(typeItem);

		SQLBuilder b = new SQLBuilder();
		String erg = b.generateSQL(noFilterItems);
		System.out.println("------ROWNUM------");
		System.out.println(erg);
		System.out.println(NO_FILTER_ITEMS);
		Assert.assertEquals(NO_FILTER_ITEMS, erg);

		erg = b.generateSQL(twoSimpleFilterItems);
		System.out.println(erg);
		System.out.println(TWO_SIMPLE_FILTER_ITEMS);
		Assert.assertEquals(TWO_SIMPLE_FILTER_ITEMS, erg);

		erg = b.generateSQL(twoSubqueryFilterItems);
		System.out.println(erg);
		System.out.println(TWO_SUBQUERY_FILTER_ITEMS);
		Assert.assertEquals(TWO_SUBQUERY_FILTER_ITEMS, erg);

		erg = b.generateSQL(fourFilterItemsBothTypes);
		System.out.println(erg);
		System.out.println(FOUR_FILTER_ITEMS_BOTH_TYPES);
		Assert.assertEquals(FOUR_FILTER_ITEMS_BOTH_TYPES, erg);

		erg = b.generateSQLCount(fourFilterItemsBothTypesCount);
		System.out.println(erg);
		System.out.println(COUNT_FOUR_FILTER_ITEMS);
		Assert.assertEquals(COUNT_FOUR_FILTER_ITEMS, erg);
		System.out.println("------------------");
	}

	/**
	 * Create some prepared statement and compare them with the estimated ones
	 * without the ROWNUM condition.
	 */
	@Test
	public void testGenerateSQLNoRows() {
		ArrayList<FilterItem> noFilterItems = new ArrayList<FilterItem>();
		ArrayList<FilterItem> twoSimpleFilterItems = new ArrayList<FilterItem>();
		ArrayList<FilterItem> twoSubqueryFilterItems = new ArrayList<FilterItem>();
		ArrayList<FilterItem> fourFilterItemsBothTypes = new ArrayList<FilterItem>();

		FilterItem severityItem = new FilterItem("severity", "high", "and");
		FilterItem typeItem = new FilterItem("type", "event", "and");
		FilterItem hostItem = new FilterItem("host", "berndTest", "and");
		FilterItem facilityItem = new FilterItem("facility", "test", "and");
		twoSimpleFilterItems.add(severityItem);
		twoSimpleFilterItems.add(typeItem);

		hostItem = new FilterItem("host", "berndTest", "and");
		facilityItem = new FilterItem("facility", "test", "and");
		twoSubqueryFilterItems.add(hostItem);
		twoSubqueryFilterItems.add(facilityItem);

		severityItem = new FilterItem("severity", "high", "and");
		typeItem = new FilterItem("type", "event", "and");
		hostItem = new FilterItem("host", "berndTest", "and");
		facilityItem = new FilterItem("facility", "test", "and");
		fourFilterItemsBothTypes.add(hostItem);
		fourFilterItemsBothTypes.add(severityItem);
		fourFilterItemsBothTypes.add(facilityItem);
		fourFilterItemsBothTypes.add(typeItem);

		SQLBuilder b = new SQLBuilder();
		b.setRownum("-1");
		String erg = b.generateSQL(noFilterItems);
		System.out.println("---NO-ROWNUM------");
		System.out.println(erg);
		System.out.println(NO_FILTER_ITEMS_NO_ROW);
		Assert.assertEquals(NO_FILTER_ITEMS_NO_ROW, erg);

		erg = b.generateSQL(twoSimpleFilterItems);
		System.out.println(erg);
		System.out.println(TWO_SIMPLE_FILTER_ITEMS_NO_ROW);
		Assert.assertEquals(TWO_SIMPLE_FILTER_ITEMS_NO_ROW, erg);

		erg = b.generateSQL(twoSubqueryFilterItems);
		System.out.println(erg);
		System.out.println(TWO_SUBQUERY_FILTER_ITEMS_NO_ROW);
		Assert.assertEquals(TWO_SUBQUERY_FILTER_ITEMS_NO_ROW, erg);

		erg = b.generateSQL(fourFilterItemsBothTypes);
		System.out.println(erg);
		System.out.println(FOUR_FILTER_ITEMS_BOTH_TYPES_NO_ROW);
		Assert.assertEquals(FOUR_FILTER_ITEMS_BOTH_TYPES_NO_ROW, erg);
		System.out.println("------------------");
	}
}
