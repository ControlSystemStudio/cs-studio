package org.remotercp.util.preferences;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

public class PreferencesUtilTest {

	@Test
	public void exportPreferencesTest() {
		try {
			final File preferencesFile = File.createTempFile("preferences", ".ini");
			//System.out.println(preferencesFile.getPath());

			final SortedMap<String, String> preferencesMap = new TreeMap<String, String>();
			preferencesMap.put("org.remotercp.color", "blue");
			preferencesMap.put("org.remotercp.font", "helvetica");
			preferencesMap.put("org.remotercp.server", "http://myserver.com");
			preferencesMap.put("org.remotercp.user", "sandra");

			PreferencesUtil.exportPreferencesToFile(preferencesMap,
					preferencesFile.getAbsolutePath());

			final FileReader reader = new FileReader(preferencesFile);
			final BufferedReader bufReader = new BufferedReader(reader);
			String line;
			int count = 0;
			while ((line = bufReader.readLine()) != null) {
				// first line is the date of the export, ignore it
				if (!line.startsWith("#")) {

					/* split keys and values */
					final int keyValueSeparator = line.indexOf("=");
					final String key = line.substring(0, keyValueSeparator);
					final String value = line.substring(keyValueSeparator + 1);

					assertTrue(preferencesMap.containsKey(key));
					assertTrue(preferencesMap.containsValue(value));
				}

				count++;
			}
			bufReader.close();
			reader.close();

		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
