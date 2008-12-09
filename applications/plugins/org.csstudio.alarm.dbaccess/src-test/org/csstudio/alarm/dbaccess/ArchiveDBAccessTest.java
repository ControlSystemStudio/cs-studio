package org.csstudio.alarm.dbaccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that builds prepared SQL statements for settings in the expert search
 * in the alarm archive log table.
 * 
 * @author jhatje
 * 
 */
public class ArchiveDBAccessTest {

	private ArrayList<FilterItem> noFilterItems = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> twoSimpleFilterItems = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> twoSubqueryFilterItems = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> fourFilterItemsBothTypes = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> testFilterItemList = new ArrayList<FilterItem>();

	@Before
	public void setUp() {
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

	@Test
	public void testGetLogMessages() {
		Calendar from = Calendar.getInstance();
		from.set(2008, Calendar.DECEMBER, 1, 5, 5, 0);
		Calendar to = Calendar.getInstance();
		to.set(2008, Calendar.DECEMBER, 8, 6, 9, 0);

//		 ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
//				.getLogMessages(from, to, null, fourFilterItemsBothTypes, 100);
		ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
				.getLogMessages(from, to, testFilterItemList, 500);
		int erg1 = ArchiveDBAccess.getInstance()
				.countMessagesToDelete(fourFilterItemsBothTypes, from, to);

		System.out.println("number: " + erg1);

		for (HashMap<String, String> hashMap : erg) {
			Set<Entry<String, String>> entries = hashMap.entrySet();
			for (Entry<String, String> entry : entries) {
				System.out
						.print(entry.getKey() + "-" + entry.getValue() + "; ");
			}
			System.out.println();
		}
	}
}
