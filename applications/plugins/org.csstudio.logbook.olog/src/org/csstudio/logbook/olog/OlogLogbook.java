/**
 * 
 */
package org.csstudio.logbook.olog;

import static edu.msu.nscl.olog.api.LogbookBuilder.logbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import org.csstudio.logbook.ILogbook;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.utility.olog.Activator;
import org.csstudio.utility.olog.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.util.NLS;

import edu.msu.nscl.olog.api.Log;
import edu.msu.nscl.olog.api.LogBuilder;
import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;
import edu.msu.nscl.olog.api.OlogException;

/**
 * @author Delphy Nypaver Armstrong
 * @author Kay Kasemir
 * @author Eric Berryman
 * @author shroffk
 * 
 */
public class OlogLogbook implements ILogbook {

	final private int MAX_TEXT_SIZE;
	final private OlogClient client;

	private String LogbookName;

	private static final Logger log = Logger.getLogger(OlogLogbook.class
			.getCanonicalName());

	public OlogLogbook(String logbookName, String user, String password)
			throws Exception {
		if (user.isEmpty()) {
			client = Olog.getClient();
		} else {
			final IPreferencesService prefs = Platform.getPreferencesService();
			String url = prefs.getString(
					org.csstudio.utility.olog.Activator.PLUGIN_ID,
					PreferenceConstants.Olog_URL,
					"https://localhost:8181/Olog/resources", null);
			String jcrURI = prefs.getString(
					org.csstudio.utility.olog.Activator.PLUGIN_ID,
					PreferenceConstants.Olog_jcr_URL,
					"http://localhost:8080/Olog/repository/olog", null);
			client = OlogClientBuilder.serviceURL(url).jcrURI(jcrURI)
					.withHTTPAuthentication(true).username(user)
					.password(password).create();
		}
		LogbookName = logbookName;
		/* The maximum allowed size of logbook text entry MEDIUMTEXT */
		MAX_TEXT_SIZE = 16777215;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.logbook.ILogbook#createEntry(java.lang.String,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public void createEntry(String title, String text, String... file_names)
			throws Exception {
		final String[] captions = new String[file_names.length];
		for (int i = 0; i < captions.length; ++i)
			captions[i] = new File(file_names[i]).getName();
		createEntry(LogbookName, text, file_names, captions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.logbook.ILogbook#createEntry(java.lang.String,
	 * java.lang.String, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void createEntry(String title, String text, String[] filenames,
			String[] captions) throws Exception {
		int entry_id;

		if (text.length() < MAX_TEXT_SIZE) {
			// Short text goes into entry
			entry_id = createBasicEntry(LogbookName, text);
		} else {

			// If text is made into an attachment due to size restraints,
			// replace the
			// text that is now an attachment with text to explain why there is
			// an
			// attachment
			final String info = "Input text exceeds " + MAX_TEXT_SIZE
					+ " characters, see attachment.";
			entry_id = createBasicEntry(LogbookName, info);

			// Get a name for the created text file
			final File tmp_file = File.createTempFile("Logbook", ".txt");
			final String fname = tmp_file.getAbsolutePath();

			// Store the oversized text entry in the text file
			setContents(tmp_file, text);
			// Add the text attachment to the elog
			addFileToElog(fname, "A", entry_id, "Full Text");
			tmp_file.deleteOnExit();
		}

		// Attach remaining files
		for (int i = 0; i < filenames.length; ++i) {
			final String caption = i < captions.length ? captions[i]
					: filenames[i];
			addFileToElog(filenames[i], "I", entry_id, caption);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.logbook.ILogbook#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	/**
	 * Create basic ELog entry in one or more logbooks with text and no tags,
	 * obtaining entry ID which would allow addition of attachments.
	 * 
	 * @param String
	 *            [] logbooks list of logbooks that elog entry belongs to
	 * @param String
	 *            text text for the elog entry
	 * @throws Exception
	 *             on error
	 */
	private int createBasicEntry(final String LogbookName, final String text)
			throws Exception {
		Log returnLog = null;
		LogBuilder lb = LogBuilder.log().description(text).level("Info")
				.appendToLogbook(logbook(LogbookName));
		returnLog = client.set(lb);
		return returnLog.getId().intValue();
	}

	/**
	 * Determine the type of attachment, based on file extension, and add it to
	 * the elog entry with the entry_id.
	 * 
	 * @param fname
	 *            input filename, either an image or a text file
	 * @param fileType
	 *            "I" for image file, "A" for text file
	 * @param entry_id
	 *            ID of entry to which to add this file
	 * @param caption
	 *            Caption or 'title' for the attachment
	 * @throws Exception
	 */
	private void addFileToElog(final String fname, String fileType,
			final int entry_id, final String caption) throws Exception {
		try {
			client.add(new File(fname), (long) entry_id);
		} catch (Exception e1) {
			log.severe("Could not upload " + fname + " Error: " + e1);
			return;
		}

	}

	/**
	 * Change the contents of text file in its entirety, overwriting any
	 * existing text.
	 * 
	 * This style of implementation throws all exceptions to the caller.
	 * 
	 * @param aFile
	 *            is an existing file which can be written to.
	 * @throws IllegalArgumentException
	 *             if param does not comply.
	 * @throws FileNotFoundException
	 *             if the file does not exist.
	 * @throws IOException
	 *             if problem encountered during write.
	 */
	static public void setContents(final File aFile, final String aContents)
			throws FileNotFoundException, IOException {
		if (aFile == null) {
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!aFile.exists()) {
			throw new FileNotFoundException("File does not exist: " + aFile);
		}
		if (!aFile.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: "
					+ aFile);
		}
		if (!aFile.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: "
					+ aFile);
		}

		// use buffering
		final Writer output = new BufferedWriter(new FileWriter(aFile));
		try {
			// FileWriter always assumes default encoding is OK!
			output.write(aContents);
		} finally {
			output.close();
		}
	}

}
