package org.csstudio.logbook.sns;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.csstudio.logbook.ILogbook;
import org.junit.Test;

/** JUnit test of the Attachment to Elog.
 *  <p>
 *  Ask for a UID, password and a file to attach
 *  
 *  @author Delphy Nypaver Armstrong
 */
public class AttachmentTest {
	  /**
    * @param args
    */
   private static final String URL =
      "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";

  private static final String LOGBOOK = "Scratch Pad";
  
	/** Sends the input file to the Elog as an attachment
	 * @throws Exception
	 */
	@Test
	public void testAttachment() throws Exception
	{
		
final BufferedReader command_line = new BufferedReader(new InputStreamReader(System.in));

System.out.print("User      : ");
final String uid = command_line.readLine();

System.out.print("Password  : ");
final String passwd = command_line.readLine();

System.out.print("Attachment: ");
final String fname = command_line.readLine();
final String short_text = "This is a test entry";

     final ILogbook logbook =
          new SNSLogbookFactory().connect(URL, LOGBOOK, uid, passwd);
         String title = "MPS Bypass Report Test";
         logbook.createEntry(title, short_text, fname);
         logbook.close();
   }
}
