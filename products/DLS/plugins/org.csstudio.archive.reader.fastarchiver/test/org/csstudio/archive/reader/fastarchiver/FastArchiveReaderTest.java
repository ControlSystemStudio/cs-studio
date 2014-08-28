package org.csstudio.archive.reader.fastarchiver;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class FastArchiveReaderTest {
	// URL may be different at different Facilities
	private static final String URL = "fads://fa-archiver:8888";

	FastArchiveReader faReader;
	String[] patterns = { "BPM", "", "-", "", "something" };
	String[] regEx = { ".*BPM.*", ".*", ".*-.*", ".*something.*" };
	String pvName;

	@Before
	public void setUpBefore() throws Exception {
		faReader = new FastArchiveReader(URL);
		pvName = (String) (new FAInfoRequest(URL).fetchMapping().keySet()
				.toArray()[0]);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetServerName() {
		String name = faReader.getServerName();
		assertNotNull(name);
		assertTrue("Name must have length longer than 0", name.length() != 0);
	}

	@Test
	public void testGetURL() {
		String url = faReader.getURL();
		assertEquals(url, URL);
	}

	@Test
	public void testGetDescription() {
		String description = faReader.getDescription();
		assertNotNull(description);
		assertTrue("Description must have length longer than 0",
				description.length() != 0);
	}

	@Test
	public void testGetVersion() {
		int version = faReader.getVersion();
		assertTrue("Version must be higher than 0", version > 0);
	}

	@Test
	public void testGetArchiveInfo() {
		ArchiveInfo[] ai = faReader.getArchiveInfos();
		assertNotNull(ai);
		for (ArchiveInfo info : ai) {
			assertNotNull(info);
		}

	}

	@Test
	public void testGetNamesByPattern() throws Exception {
		for (String s : patterns) {
			Pattern pattern = Pattern.compile(".*" + s + ".*");
			String[] allNames = faReader.getNamesByPattern(1, s);
			assertNotNull(allNames);
			for (String name : allNames) {
				Matcher matcher = pattern.matcher(name);
				assertTrue("Fails for String \"" + s + "\"", matcher.matches());
			}
		}
	}

	@Test
	public void testGetNamesByRegExp() throws Exception {
		for (String s : regEx) {
			Pattern pattern = Pattern.compile(s);
			String[] allNames = faReader.getNamesByRegExp(1, s);
			assertNotNull(allNames);
			for (String name : allNames) {
				Matcher matcher = pattern.matcher(name);
				assertTrue("Fails for String \"" + s + "\"", matcher.matches());
			}
		}
	}

	@Test
	public void testGetRawValues() throws UnknownChannelException, Exception {
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		ValueIterator vi = faReader.getRawValues(1, pvName, start, end);
		assertNotNull(vi);
	}

	@Test
	public void testGetOptimisedValues() throws UnknownChannelException,
			Exception {
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		int count = 1000000;
		ValueIterator vi = faReader.getOptimizedValues(1, pvName, start, end,
				count);
		assertNotNull(vi);
	}

}
