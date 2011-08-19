package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Class that builds prepared SQL statements for settings in the expert search
 * in the alarm archive log table.
 *
 * @author jhatje
 *
 */
public class SQLBuilderTest {

    private static final String COMMON_ROWNUM_LT_CLAUSE = " and ROWNUM <50000";

    private static final String COMMON_STMT_RESULT_PREFIX =
	    "select mc.message_id, mc.msg_property_type_id,  mc.value";

    private static final String COMMON_FROM_WHERE_CLAUSE =
        " from  message m, message_content mc where m.id = mc.MESSAGE_ID";

    private static final String COMMON_TODATE_INTERVAL_CLAUSE =
        " and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and" +
        " to_date(? , 'YYYY-MM-DD HH24:MI:SS')";

    private static final String COMMON_SUBQUERY_CLAUSE =
        " and mc.message_id in (select mc.MESSAGE_ID from message_content mc"+
        " where mc.msg_property_type_id = ? and upper(mc.VALUE) LIKE upper(?))";

    private static final String COMMON_ORDER_BY_CLAUSE =
        " order by mc.MESSAGE_ID desc";


	private DBConnectionHandler HANDLER_MOCK;

    private ResultSetMetaData _metaMock;

    final FilterItem _severityItem = new FilterItem("severity", "high", "and");
    final FilterItem _typeItem = new FilterItem("type", "event", "and");
    final FilterItem _hostItem = new FilterItem("host", "berndTest", "and");
    final FilterItem _facilityItem = new FilterItem("facility", "test", "and");

    @Before
	public void setUp() throws Exception {

	    HANDLER_MOCK = Mockito.mock(DBConnectionHandler.class);
	    final Connection connMock = Mockito.mock(Connection.class);
	    Mockito.when(HANDLER_MOCK.getConnection()).thenReturn(connMock);

	    final Statement stmtMock = Mockito.mock(Statement.class);
	    Mockito.when(connMock.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	                                         ResultSet.CONCUR_READ_ONLY))
	           .thenReturn(stmtMock);

	    final ResultSet resultSetMock = Mockito.mock(ResultSet.class);
	    Mockito.when(stmtMock.executeQuery("SELECT * FROM message WHERE id = 1"))
	           .thenReturn(resultSetMock);

	    _metaMock = Mockito.mock(ResultSetMetaData.class);
	    Mockito.when(resultSetMock.getMetaData()).thenReturn(_metaMock);
	}


	@Test
	public void testComposeSQLStmtForNoFilter() throws SQLException {
	    final List<FilterItem> noFilterItems = Collections.emptyList();

	    final SQLBuilder b = new SQLBuilder(HANDLER_MOCK);

	    Mockito.when(_metaMock.getColumnCount()).thenReturn(Integer.valueOf(1));
	    Mockito.when(_metaMock.getColumnName(1)).thenReturn("fuup");

	    String erg = b.composeSQLStmtForFilter(noFilterItems);

        final String NO_FILTER_ITEMS =
            COMMON_STMT_RESULT_PREFIX  +
            COMMON_FROM_WHERE_CLAUSE +
            COMMON_TODATE_INTERVAL_CLAUSE +
            COMMON_ROWNUM_LT_CLAUSE +
            COMMON_ORDER_BY_CLAUSE;

        Assert.assertEquals(NO_FILTER_ITEMS, erg);

        b.setMaxRowNum(-1);
        erg = b.composeSQLStmtForFilter(noFilterItems);

        final String NO_FILTER_ITEMS_NO_ROW =
            COMMON_STMT_RESULT_PREFIX +
            COMMON_FROM_WHERE_CLAUSE +
            COMMON_TODATE_INTERVAL_CLAUSE +
            COMMON_ORDER_BY_CLAUSE;
        Assert.assertEquals(NO_FILTER_ITEMS_NO_ROW, erg);
	}

	@Test
	public void testComposeSQLStmtFor2FilterItems() throws SQLException {
	    final List<FilterItem> twoSimpleFilterItems = new ArrayList<FilterItem>();
	    twoSimpleFilterItems.add(new FilterItem(_severityItem));
	    twoSimpleFilterItems.add(new FilterItem(_typeItem));

	    final SQLBuilder b = new SQLBuilder(HANDLER_MOCK);
	    Mockito.when(_metaMock.getColumnCount()).thenReturn(Integer.valueOf(2));
	    Mockito.when(_metaMock.getColumnName(1)).thenReturn("SEVERITY");
	    Mockito.when(_metaMock.getColumnName(2)).thenReturn("TYPE");

	    String erg = b.composeSQLStmtForFilter(twoSimpleFilterItems);

	    final String TWO_SIMPLE_FILTER_ITEMS =
	        COMMON_STMT_RESULT_PREFIX +
	        COMMON_FROM_WHERE_CLAUSE+
            " and upper(m.SEVERITY) LIKE upper(?)" +
            " and upper(m.TYPE) LIKE upper(?)" +
            COMMON_TODATE_INTERVAL_CLAUSE +
            COMMON_ROWNUM_LT_CLAUSE +
            COMMON_ORDER_BY_CLAUSE;
	    Assert.assertEquals(TWO_SIMPLE_FILTER_ITEMS, erg);

	    b.setMaxRowNum(-1);
	    twoSimpleFilterItems.clear();
	    twoSimpleFilterItems.add(new FilterItem(_severityItem));
	    twoSimpleFilterItems.add(new FilterItem(_typeItem));

	    erg = b.composeSQLStmtForFilter(twoSimpleFilterItems);


	    final String TWO_SIMPLE_FILTER_ITEMS_NO_ROW =
	        COMMON_STMT_RESULT_PREFIX +
	        COMMON_FROM_WHERE_CLAUSE +
	        " and upper(m.SEVERITY) LIKE upper(?)" +
	        " and upper(m.TYPE) LIKE upper(?)" +
	        COMMON_TODATE_INTERVAL_CLAUSE +
	        COMMON_ORDER_BY_CLAUSE;
	    Assert.assertEquals(TWO_SIMPLE_FILTER_ITEMS_NO_ROW, erg);
	}
	@Test
	public void testComposeSQLStmtFor2SubQueryFilterItems() throws SQLException {
	    final List<FilterItem> twoSubqueryFilterItems = new ArrayList<FilterItem>();

	    twoSubqueryFilterItems.add(new FilterItem(_hostItem));
	    twoSubqueryFilterItems.add(new FilterItem(_facilityItem));

	    final SQLBuilder b = new SQLBuilder(HANDLER_MOCK);
	    Mockito.when(_metaMock.getColumnCount()).thenReturn(Integer.valueOf(0));

	    String erg = b.composeSQLStmtForFilter(twoSubqueryFilterItems);

	    final String TWO_SUBQUERY_FILTER_ITEMS =
	        COMMON_STMT_RESULT_PREFIX + COMMON_FROM_WHERE_CLAUSE +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_TODATE_INTERVAL_CLAUSE +
	        COMMON_ROWNUM_LT_CLAUSE +
            COMMON_ORDER_BY_CLAUSE;

	    Assert.assertEquals(TWO_SUBQUERY_FILTER_ITEMS, erg);

	    b.setMaxRowNum(-1);
	    erg = b.composeSQLStmtForFilter(twoSubqueryFilterItems);

	    final String TWO_SUBQUERY_FILTER_ITEMS_NO_ROW =
            COMMON_STMT_RESULT_PREFIX +
            COMMON_FROM_WHERE_CLAUSE +
            COMMON_SUBQUERY_CLAUSE +
            COMMON_SUBQUERY_CLAUSE +
            COMMON_TODATE_INTERVAL_CLAUSE +
            COMMON_ORDER_BY_CLAUSE;

	    Assert.assertEquals(TWO_SUBQUERY_FILTER_ITEMS_NO_ROW, erg);
	}

	@Test
	public void testComposeSQLStmtFor4FilterItemsBothTypes() throws SQLException {
	    final List<FilterItem> fourFilterItemsBothTypes = new ArrayList<FilterItem>();
	    fourFilterItemsBothTypes.add(new FilterItem(_hostItem));
	    fourFilterItemsBothTypes.add(new FilterItem(_severityItem));
	    fourFilterItemsBothTypes.add(new FilterItem(_facilityItem));
	    fourFilterItemsBothTypes.add(new FilterItem(_typeItem));

	    final SQLBuilder b = new SQLBuilder(HANDLER_MOCK);
	    Mockito.when(_metaMock.getColumnCount()).thenReturn(Integer.valueOf(2));
	    Mockito.when(_metaMock.getColumnName(1)).thenReturn("SEVERITY");
	    Mockito.when(_metaMock.getColumnName(2)).thenReturn("TYPE");

	    final String FOUR_FILTER_ITEMS_BOTH_TYPES =
	        COMMON_STMT_RESULT_PREFIX +
	        COMMON_FROM_WHERE_CLAUSE +
	        " and upper(m.SEVERITY) LIKE upper(?)" +
	        " and upper(m.TYPE) LIKE upper(?)" +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_TODATE_INTERVAL_CLAUSE +
	        COMMON_ROWNUM_LT_CLAUSE +
	        COMMON_ORDER_BY_CLAUSE;

	    String erg = b.composeSQLStmtForFilter(fourFilterItemsBothTypes);

	    Assert.assertEquals(FOUR_FILTER_ITEMS_BOTH_TYPES, erg);

	    b.setMaxRowNum(-1);
	    fourFilterItemsBothTypes.clear();
	    fourFilterItemsBothTypes.add(new FilterItem(_hostItem));
	    fourFilterItemsBothTypes.add(new FilterItem(_severityItem));
	    fourFilterItemsBothTypes.add(new FilterItem(_facilityItem));
	    fourFilterItemsBothTypes.add(new FilterItem(_typeItem));
	    erg = b.composeSQLStmtForFilter(fourFilterItemsBothTypes);

	    final String FOUR_FILTER_ITEMS_BOTH_TYPES_NO_ROW =
	        COMMON_STMT_RESULT_PREFIX +
	        COMMON_FROM_WHERE_CLAUSE +
	        " and upper(m.SEVERITY) LIKE upper(?)" +
	        " and upper(m.TYPE) LIKE upper(?)" +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_TODATE_INTERVAL_CLAUSE +
	        COMMON_ORDER_BY_CLAUSE;
        Assert.assertEquals(FOUR_FILTER_ITEMS_BOTH_TYPES_NO_ROW, erg);
	}



	@Test
	public void testComposeSQLStmtFor4FilterItemsBothTypesCount() throws SQLException {
	    final List<FilterItem> fourFilterItemsBothTypesCount = new ArrayList<FilterItem>();
	    fourFilterItemsBothTypesCount.add(new FilterItem(_hostItem));
	    fourFilterItemsBothTypesCount.add(new FilterItem(_severityItem));
	    fourFilterItemsBothTypesCount.add(new FilterItem(_facilityItem));
	    fourFilterItemsBothTypesCount.add(new FilterItem(_typeItem));

	    final SQLBuilder b = new SQLBuilder(HANDLER_MOCK);
	    Mockito.when(_metaMock.getColumnCount()).thenReturn(Integer.valueOf(2));
	    Mockito.when(_metaMock.getColumnName(1)).thenReturn("SEVERITY");
	    Mockito.when(_metaMock.getColumnName(2)).thenReturn("TYPE");

	    final String erg = b.composeSQLStmtForCount(fourFilterItemsBothTypesCount);

	    final String COUNT_FOUR_FILTER_ITEMS =
	        "select count(m.id)"  +
	        COMMON_FROM_WHERE_CLAUSE +
	        " and upper(m.SEVERITY) LIKE upper(?)" +
	        " and upper(m.TYPE) LIKE upper(?)" +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_SUBQUERY_CLAUSE +
	        COMMON_TODATE_INTERVAL_CLAUSE;

	    Assert.assertEquals(COUNT_FOUR_FILTER_ITEMS, erg);
	}
}
