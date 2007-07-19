/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.diag.snlDebugger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

public final class WriteDefaultXML {

	public static void writeDefault(final File file){
		if(file.isFile()){
			final SAXBuilder b = new  SAXBuilder(false);
			try {
				final StringReader sr = new StringReader(getDefaultXML());
				final Document xml = b.build(sr);
				final FileOutputStream out =  new  FileOutputStream(file);
				final XMLOutputter serializer = new XMLOutputter();
				serializer.output(xml,out);
				System.out.println("write");
				out.flush();
				out.close();
			} catch (final JDOMException e) {
				Activator.logException(Messages.getString("WriteDefaultXML.ExceptionJDOM"), e); //$NON-NLS-1$
			} catch (final IOException e) {
				Activator.logException(Messages.getString("WriteDefaultXML.ExceptionIO")+file, e);			} //$NON-NLS-1$
		}

	}

	private static String getDefaultXML() {
		/*
		 * Ab Version 0.0.2 werden so genannte Count Vartiablen unterstützt.
		 * -- Variablenformt: $(varname)
		 * -- Typ die zahl wird als Integer interprätiert
		 * -- Wird jetzt eine Abfrage ausgewählt wird der Verariable an die entsprechnende Stelle
		 * 	  eingesetzt und um ein erhöht. So das für die nächste aus Auswahl der neue Wert zur
		 * 	  verfügung steht.
		 */
		final String def =
			"<RMT version=\"0.0.2\">\r\n"+ //$NON-NLS-1$
				"\t<var name=\"$(invokeid)\" typ=\"count\">"+
					"\t\t1"+
				"\t</var>"+
				"\t<Befehl name=\"Config\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
					   	"\t<Command command=\"0\" filename=\"110.conf\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Start\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
			   			"\t<Command command=\"1\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Stop\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
			   			"\t<Command command=\"2\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Shutdown\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
				   		"\t<Command command=\"3\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"TestIO\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
				   		"\t<Command command=\"4\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Failover\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
						"\t<Command command=\"5\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Failoverwitch ExecTime\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
						"\t<Command command=\"5\" exectime=\"2006-12-31 12:59:59\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"ChangeUpdateMode\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
				   		"\t<Command command=\"6\" updatemode=\"-1\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Small Diagnose\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
						"\t<Command command=\"7\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Diagnose\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
				   		"\t<Command command=\"8\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Report\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
						"\t<Command command=\"9\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
				"\t<Befehl name=\"Driver\">\r\n"+ //$NON-NLS-1$
					"\t<Root version=\"1.0.0\" invokeid=\"$(invokeid)\">\r\n"+ //$NON-NLS-1$
			   			"\t<Command destination=\"CAN1\" property1=\"sample\" property2=\"sample\">\r\n"+ //$NON-NLS-1$
			      			"\t<element1>sample</element1>\r\n"+ //$NON-NLS-1$
			      			"\t<element2 attrib1=\"sample\" attrib2=\"sample\">sample</element2>\r\n"+ //$NON-NLS-1$
			   			"\t</Command>\r\n"+ //$NON-NLS-1$
					"\t</Root>\r\n"+ //$NON-NLS-1$
				"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t</RMT>"; //$NON-NLS-1$
		return def;
	}
}
