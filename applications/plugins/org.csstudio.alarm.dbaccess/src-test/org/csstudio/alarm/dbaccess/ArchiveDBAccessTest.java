package org.csstudio.alarm.dbaccess;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Class that builds prepared SQL statements for settings in the expert search
 * in the alarm archive log table.
 *
 * @author jhatje
 *
 */
public class ArchiveDBAccessTest {

	private static final List<FilterItem> twoSimpleFilterItems = new ArrayList<FilterItem>();
	private static final List<FilterItem> twoSubqueryFilterItems = new ArrayList<FilterItem>();
	private static final List<FilterItem> fourFilterItemsBothTypes = new ArrayList<FilterItem>();
	private static final List<FilterItem> testFilterItemList = new ArrayList<FilterItem>();

	@BeforeClass
	public static void setUp() {
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

		hostItem = new FilterItem("host", "berndTest", "and");
		severityItem = new FilterItem("type", "event", "and");
		typeItem = new FilterItem("severity", "MAJOR", "end");
		facilityItem = new FilterItem("facility", "TEST", "and");
		fourFilterItemsBothTypes.add(hostItem);
		fourFilterItemsBothTypes.add(severityItem);
		fourFilterItemsBothTypes.add(facilityItem);
		fourFilterItemsBothTypes.add(typeItem);

		hostItem = new FilterItem("host", "berndTest", "and");
		severityItem = new FilterItem("type", "event", "and");
		typeItem = new FilterItem("severity", "MAJOR", "end");
		facilityItem = new FilterItem("facility", "TEST", "and");
		testFilterItemList.add(hostItem);
		testFilterItemList.add(severityItem);
		testFilterItemList.add(facilityItem);
		testFilterItemList.add(typeItem);

	}

	private int currentFirstColumn;
    protected String currentSecondColumn;
    protected String currentThirdColumn;

	@Test
	public void testProcessResult() throws Throwable {
	    final List<ResultSet> dbdaten = new ArrayList<ResultSet>();

	    // Prepare ResultSet-Mock:
	    final ResultSet resultSet = Mockito.mock(ResultSet.class);

	    Mockito.doAnswer(new Answer<Boolean>() {
            public Boolean answer(final InvocationOnMock invocation) throws Throwable {
                currentFirstColumn = 23;
                currentSecondColumn = "property1";
                currentThirdColumn = "value1";
                return true;
            }
	    }).doAnswer(new Answer<Boolean>() {
            public Boolean answer(final InvocationOnMock invocation) throws Throwable {
                currentFirstColumn = 42;
                currentSecondColumn = "property2";
                currentThirdColumn = "value2";
                return true;
            }
        }).doAnswer(new Answer<Boolean>() {
            public Boolean answer(final InvocationOnMock invocation) throws Throwable {
                return false;
            }
        }).when(resultSet).next();

	    Mockito.doAnswer(new Answer<Integer>() {
            public Integer answer(final InvocationOnMock invocation) throws Throwable {
                return currentFirstColumn;
            }
	    }).when(resultSet).getInt(1);

	    Mockito.doAnswer(new Answer<String>() {
	        public String answer(final InvocationOnMock invocation) throws Throwable {
	            return ""+ currentFirstColumn;
	        }
	    }).when(resultSet).getString(1);

	    Mockito.doAnswer(new Answer<String>() {
            public String answer(final InvocationOnMock invocation) throws Throwable {
                return currentSecondColumn;
            }
        }).when(resultSet).getString(2);

	    Mockito.doAnswer(new Answer<String>() {
            public String answer(final InvocationOnMock invocation) throws Throwable {
                return currentThirdColumn;
            }
        }).when(resultSet).getString(3);

	    // Prepare param-list:
	    dbdaten.add(resultSet);

	    // Prepare ArchiveDB-Access-Tool:
//////	    SQLBuilder sqlBuilder = new SQLBuilder();
//////	    sqlBuilder.setRownum("100");
//////	    ArchiveDBAccess out = new ArchiveDBAccess(sqlBuilder) {
//////	        @Override
//////	        protected Map<String, String> getIDPropertyMapping() {
//////	            Map<String, String> result = new HashMap<String, String>();
//////	            result.put("property1", "mappedProperty1");
//////	            result.put("property2", "mappedProperty2");
//////	            return result;
//////	        }
//////	    };
////
////	    // Run method under test
////	    List<HashMap<String, String>> result = out.processResult(dbdaten);
//
//	    // check result:
//	    assertNotNull(result);
//	    assertEquals(2, result.size());
//
//	    Map<String,String> firstRow = result.get(0);
//	    assertEquals("value1", firstRow.get("mappedProperty1"));
//
//	    Map<String,String> secondRow = result.get(1);
//	    assertEquals("value2", secondRow.get("mappedProperty2"));
	}

	@Ignore("This is an integration test and should be moved to special test case.")
	@Test
	public void testGetLogMessages() {
		final Calendar from = Calendar.getInstance();
		from.set(2008, Calendar.DECEMBER, 1, 5, 5, 0);
		final Calendar to = Calendar.getInstance();
		to.set(2008, Calendar.DECEMBER, 8, 6, 9, 0);

//		 ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
//				.getLogMessages(from, to, null, fourFilterItemsBothTypes, 100);
//		ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
//				.getLogMessages(from, to, testFilterItemList, 500);
//		int erg1 = ArchiveDBAccess.getInstance()
//				.countMessagesToDelete(fourFilterItemsBothTypes, from, to);


//		for (HashMap<String, String> hashMap : erg) {
//			Set<Entry<String, String>> entries = hashMap.entrySet();
//			for (Entry<String, String> entry : entries) {
//				System.out
//						.print(entry.getKey() + "-" + entry.getValue() + "; ");
//			}
//			System.out.println();
//		}
	}
}
