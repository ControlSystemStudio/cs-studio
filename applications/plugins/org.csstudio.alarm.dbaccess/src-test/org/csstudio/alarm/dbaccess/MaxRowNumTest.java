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
 * Class test if the flag maxSize in class ArchiveDBAccess is set if
 * the sum of rows in all result sets is equal to rownum in sql statement.
 * 
 * @author jhatje
 * 
 */
public class MaxRowNumTest {


	@Test
	public void testGetLogMessages() {
		Calendar from = Calendar.getInstance();
		from.set(2008, Calendar.JULY, 1, 16, 8, 0);
		Calendar to = Calendar.getInstance();
		to.set(2008, Calendar.NOVEMBER, 27, 16, 12, 0);

		ArrayList<HashMap<String, String>> erg = ArchiveDBAccess.getInstance()
				.getLogMessages(from, to, null, 100);

		Assert.assertTrue(ArchiveDBAccess.getInstance().is_maxSize());
		
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
