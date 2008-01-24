package org.csstudio.sds.ui.internal.runmode;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunModeBoxInputTest {
	private RunModeBoxInput _runModeBoxInput;

	@Before
	public void setUp() throws Exception {
		Map<String, String> aliases = new HashMap<String, String>();
		aliases.put("aliasKey1", "aliasValue1");
		aliases.put("aliasKey2", "aliasValue2");
		aliases.put("aliasKey3", "aliasValue3");
		_runModeBoxInput = new RunModeBoxInput(new Path("abc"), aliases,
				RunModeType.VIEW);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSerialization() {
		ByteArrayOutputStream bot = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bot);
			oos.writeObject(_runModeBoxInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetFilePath() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAliases() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetType() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

}
