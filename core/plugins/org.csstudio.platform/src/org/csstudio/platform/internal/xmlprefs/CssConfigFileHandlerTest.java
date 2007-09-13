package org.csstudio.platform.internal.xmlprefs;

import java.io.File;

import junit.framework.TestCase;


public class CssConfigFileHandlerTest
		extends TestCase
{
	// --------------------------------------------------------------------------------------------
	
	public void testGetConfigFile()
	{
		File configFile = CssConfigFileHandler.getInstance().getConfigFile();
		assertNotNull(configFile);
	}

}
