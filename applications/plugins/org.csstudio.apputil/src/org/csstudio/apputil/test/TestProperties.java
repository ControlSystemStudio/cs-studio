package org.csstudio.apputil.test;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

/** Helper for getting properties that are needed to execute a test.
 *  These include for example site-specific URLs or PV names,
 *  and also passwords that you would rather not include in the source code.
 *
 *  The Test Properties are fundamentally just Java {@link Properties},
 *  and test code can use them however you want.
 *  They do not depend on the Eclipse runtime or plugin mechanisms to allow
 *  use from basic JUnit tests.
 *  They are loaded from a file that should reside outside of the source
 *  code directory tree.
 *  The full pathname of the file can be provided in several ways:
 *  <ul>
 *  <li>System property "test_properties".
 *  <li>Environment variable "test_properties".
 *  </ul>
 *  
 *  The system property is conveniently set via Eclipse
 *  "Preferences", "Java", "Installed JREs", select the one you use, "Edit",
 *  add "Default VM Arguments" like
 *  "-Dtest_properties=/Eclipse/CustomizationFiles/tests.ini"
 *  
 *  Test code should be written such that it handles missing properties.
 *  
 *  @see TestPropertiesUnitTest
 *  
 *  @author Kay Kasemir
 */
public class TestProperties
{
	private static final String TEST_PROPERTIES = "test_properties"; //$NON-NLS-1$
	final private Properties settings = new Properties();

	/** Initialize test properties
	 *  @throws Exception on error accessing the file.
	 *          NOT having configured a test properties file is NOT an error!
	 */
	public TestProperties() throws Exception
	{
        // Check system property for file name
		String filename = System.getProperty(TEST_PROPERTIES);
		// Fall back to environment variable
		if (filename == null)
			filename = System.getenv(TEST_PROPERTIES);
		// _IF_ configured, load the settings
		if (filename != null)
		{
	    	final FileInputStream stream = new FileInputStream(filename);
			settings.load(stream);
			stream.close();
		}
	}

	/** Get test property as string
	 *  @param key Property name
	 *  @return String value or <code>null</code> if not set
	 */
	public String getString(final String key)
    {
		return settings.getProperty(key);
    }

	/** Get test property as string
	 *  @param key Property name
	 *  @param default_value Default value to use if no setting found
	 *  @return String value or <code>default_value</code> if not set
	 */
	public String getString(final String key, final String default_value)
    {
		return settings.getProperty(key, default_value);
    }
	
	/** Get test property as Integer
	 *  @param key Property name
	 *  @return Integer value or <code>null</code> if not set
	 *  @exception NumberFormatException if the value is set but cannot be parsed as an integer
	 */
	public Integer getInteger(final String key)
    {
		final String text = getString(key);
		if (text == null)
			return null;
		return Integer.valueOf(text);
    }

	/** Get test property as Integer
	 *  @param key Property name
	 *  @param default_value Default value to use if no setting found
	 *  @return Integer value or <code>default_value</code> if not set
	 *  @exception NumberFormatException if the value is set but cannot be parsed as an integer
	 */
	public Integer getInteger(final String key, final int default_value)
    {
		final String text = getString(key);
		if (text == null)
			return default_value;
		return Integer.valueOf(text);
    }

	
	@SuppressWarnings("unchecked")
    public Enumeration<String> getKeys()
    {
	    return (Enumeration<String>)settings.propertyNames();
    }
}
