package org.csstudio.alarm.dbaccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

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
public class ArchiveDBAccessTest {

	private ArrayList<FilterItem> noFilterItems = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> twoSimpleFilterItems = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> twoSubqueryFilterItems = new ArrayList<FilterItem>();
	private ArrayList<FilterItem> fourFilterItemsBothTypes = new ArrayList<FilterItem>();

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

		hostItem = new FilterItem("host", "xmtsKryoPump", "and");
		severityItem = new FilterItem("severity", "INVALID", "and");
		typeItem = new FilterItem("name", "XMTSTTP1262B_ai", "end");
		facilityItem = new FilterItem("facility", "MTH", "and");
		fourFilterItemsBothTypes.add(hostItem);
		fourFilterItemsBothTypes.add(severityItem);
		fourFilterItemsBothTypes.add(facilityItem);
		fourFilterItemsBothTypes.add(typeItem);
	}

	@Test
	public void testGetLogMessages() {
		Calendar from = Calendar.getInstance();
		from.set(2008, Calendar.NOVEMBER, 27, 16, 8, 0);
		Calendar to = Calendar.getInstance();
		to.set(2008, Calendar.NOVEMBER, 27, 16, 12, 0);

//		 ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
//				.getLogMessages(from, to, null, fourFilterItemsBothTypes, 100);
		ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
				.getLogMessages(from, to, 100);

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
