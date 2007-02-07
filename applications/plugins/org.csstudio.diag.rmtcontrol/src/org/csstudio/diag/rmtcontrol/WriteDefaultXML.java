package org.csstudio.diag.rmtcontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

public final class WriteDefaultXML {

	static void writeDefault(File file){
		if(file.isFile()){
			SAXBuilder b = new  SAXBuilder(false);
			try {
				StringReader sr = new StringReader(getDefaultXML());
				Document xml = b.build(sr);
				FileOutputStream out =  new  FileOutputStream(file);
				XMLOutputter serializer = new XMLOutputter();
				serializer.output(xml,out);
				out.flush();
				out.close();
			} catch (JDOMException e) {
				Activator.logException(Messages.getString("WriteDefaultXML.ExceptionJDOM"), e); //$NON-NLS-1$
			} catch (IOException e) {
				Activator.logException(Messages.getString("WriteDefaultXML.ExceptionIO")+file, e);			} //$NON-NLS-1$
		}

	}

	private static String getDefaultXML() {
		String def =
			"<RMT version=\"0.0.1\">\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Config\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4711\">\r\n"+ //$NON-NLS-1$
				   	"\t<Command command=\"0\" filename=\"110.conf\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Start\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4711\">\r\n"+ //$NON-NLS-1$
		   			"\t<Command command=\"1\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Stop\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4711\">\r\n"+ //$NON-NLS-1$
		   			"\t<Command command=\"2\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Shutdown\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4711\">\r\n"+ //$NON-NLS-1$
			   		"\t<Command command=\"3\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"TestIO\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4712\">\r\n"+ //$NON-NLS-1$
			   		"\t<Command command=\"4\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Failover\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4712\">\r\n"+ //$NON-NLS-1$
					"\t<Command command=\"5\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Failoverwitch ExecTime\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4712\">\r\n"+ //$NON-NLS-1$
					"\t<Command command=\"5\" exectime=\"2006-12-31 12:59:59\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"ChangeUpdateMode\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4712\">\r\n"+ //$NON-NLS-1$
			   		"\t<Command command=\"6\" updatemode=\"-1\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Small Diagnose\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4714\">\r\n"+ //$NON-NLS-1$
					"\t<Command command=\"7\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Diagnose\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4714\">\r\n"+ //$NON-NLS-1$
			   		"\t<Command command=\"8\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Report\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4712\">\r\n"+ //$NON-NLS-1$
					"\t<Command command=\"9\" destination=\"RMT\"/>\r\n"+ //$NON-NLS-1$
				"\t</Root>\r\n"+ //$NON-NLS-1$
			"\t</Befehl>\r\n"+ //$NON-NLS-1$
			"\t<Befehl name=\"Driver\">\r\n"+ //$NON-NLS-1$
				"\t<Root version=\"1.0.0\" invokeid=\"4713\">\r\n"+ //$NON-NLS-1$
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
