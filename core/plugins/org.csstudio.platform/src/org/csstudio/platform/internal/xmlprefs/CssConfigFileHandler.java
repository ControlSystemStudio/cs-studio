package org.csstudio.platform.internal.xmlprefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.ResourcesPlugin;


/**
 * This handler reads the name of the file that configures the CSS preferences from another given
 * file (if it exists). There are 2 files involved: The first file name is the name of the file that
 * contains file names. The first file name in that file will be taken to configure the CSS preferences.
 * 
 * The other file name is the name of the config file for CSS preferences.
 * 
 * @author Andre Grunow
 */
public class CssConfigFileHandler
{
	// --------------------------------------------------------------------------------------------

	private final static String CONFIG_FILE_EXTENSION = ".css-ps";
	
	// --------------------------------------------------------------------------------------------

	/**	
	 * This file containts the configuration files. The first filename in that file will
	 * be taken as config file for CSS. 
	 */
	private final static String CONFIG_FILE_NAME = "file-config" + CONFIG_FILE_EXTENSION;
	
	// --------------------------------------------------------------------------------------------

	/**
	 * The base name (without file extension) of the preferences file that is
	 * used if no other file is configured in the <code>CONFIG_FILE_NAME</code>
	 * file.
	 */
	private final static String DEFAULT_FILE_BASENAME = "css-config";
	
	// --------------------------------------------------------------------------------------------

	private final String WORKSPACE_LOCATION = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
	
	// --------------------------------------------------------------------------------------------

	private static CssConfigFileHandler instance;
	
	// --------------------------------------------------------------------------------------------

	private CentralLogger logger;
	
	// --------------------------------------------------------------------------------------------
	
	/**	Specifies to log or not. If this class is under test run, logging may be disabled. */
	private boolean logEnabled = true;
	
	// --------------------------------------------------------------------------------------------

	/**
	 * This is the file that contains all the CSS preferences.
	 */
	private File configFile;
	
	// --------------------------------------------------------------------------------------------

	private CssConfigFileHandler()
	{
		logEnabled = true;// TestConstants.isTest;
		
		if (logEnabled)
			logger = CentralLogger.getInstance();

		configFile = readConfigFile();
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Returns the file that contains all the CSS preferences. If the file is not found, that
	 * configures the name of the config-file, then the default config file for preferences
	 * will be created: css-config.css-ps
	 * 
	 * @return the configuration file for all CSS preferences
	 */
	private File readConfigFile()
	{
		File result = null;
		
		File file = new File(WORKSPACE_LOCATION + "/" + CONFIG_FILE_NAME);

		if (file.exists())
		{
			if (logEnabled && logger != null)
				logger.debug(this, "trying to read the name of the preference file from: " + file.getAbsolutePath());
			
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(file));
				
				String lineRead = null;
				
				while ((lineRead = in.readLine()) != null)
				{
					lineRead = lineRead.trim();
					
					// skip comments that start with "#"
					
					if (lineRead.startsWith("#"))
						continue;
					
					else
					{
						// check, if there is really a file name in that line
						
						if (lineRead.endsWith(CONFIG_FILE_EXTENSION))
						{
							// file is specified with absolute path
							
							if (lineRead.contains("/") || lineRead.contains("\\"))
								result = new File(lineRead);
							
							// just the file name is specified
							
							else
								result = new File(WORKSPACE_LOCATION + "/" + lineRead);
							
							// check if file exists
							
							if (result.exists())
								break;		// stop, because file is found
						}
					}
				}
				
				if (result == null)
					result = getDefaultFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				
				if (logEnabled && logger != null)
					logger.error(this, "Error retreiving css-config file name", e);
			}
		}
		
		else 
		{
			result = getDefaultFile();
		}
		
		return result;
	}
	
	// --------------------------------------------------------------------------------------------

	public File getConfigFile()
	{
		return configFile;
	}
	
	// --------------------------------------------------------------------------------------------

	public static CssConfigFileHandler getInstance()
	{
		return instance == null
				? instance = new CssConfigFileHandler()
				: instance;
	}
	
	// --------------------------------------------------------------------------------------------

	private File getDefaultFile()
	{
		File result = findConfigFileByHostname();
		
		// if the file does not exist, create it
		
		if (! result.exists()) 
		{
			// config file for css config files not found
			if (logEnabled && logger != null)
				logger.debug(this, "no css-config file found. Creating new one with name: " + result);
			
			try
			{
				result.createNewFile();

				BufferedWriter out = new BufferedWriter(new FileWriter(result));

				out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.newLine();
				out.newLine();
				out.write("<csstudio>");
				out.newLine();
				out.write("</csstudio>");
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				
				if (logEnabled && logger != null)
					logger.error(this, "error creating default config file: " + result);
			}
		}
		
		return result;
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * <p>Finds the configuration file for the computer the CSS runs on based on
	 * the computer's qualified hostname. For a computer with hostname
	 * <code>host.example.com</code>, the method will look for the following
	 * configuration files, in that order:</p>
	 * <ul>
	 *   <li><code>css-config_host.example.com.css-ps</code></li>
	 *   <li><code>css-config_example.com.css-ps</code></li>
	 *   <li><code>css-config_com.css-ps</code></li>
	 *   <li><code>css-config.css-ps</code></li>
	 * </ul>
	 * <p>If none of those files exist, this method returns
	 * <code>css-config.css-ps</code>. It is the responsibility of the caller
	 * to check if a file with the returned name actually exists.</p>
	 * 
	 * @return the configuration file to use on this computer.
	 */
	private File findConfigFileByHostname() {
		String hostname = CSSPlatformInfo.getInstance().getQualifiedHostname();
		File file;
		
		while (!hostname.equals("")) {
			String filename = DEFAULT_FILE_BASENAME + "_"
					+ hostname + CONFIG_FILE_EXTENSION; 
			file = new File(WORKSPACE_LOCATION, filename);
			if (file.exists()) {
				return file;
			}
			
			// Remove the first component of the hostname. That is everything
			// up to the first dot in the hostname, or up to the end of the
			// string if there aren't any dots.
			hostname = hostname.replaceFirst("^[\\p{Alnum}-]+(?:\\.|$)", "");
		}
		
		// If we get here, no file was found based on the hostname. Construct
		// and return the default file name.
		file = new File(WORKSPACE_LOCATION,
				DEFAULT_FILE_BASENAME + CONFIG_FILE_EXTENSION);
		return file;
	}
}
