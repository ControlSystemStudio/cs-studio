
package org.csstudio.websuite.servlet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.utils.LocalProperty;
import org.csstudio.websuite.utils.Utility;
import org.csstudio.websuite.utils.ValueReader;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The eLogbook is storing logbook entries as well as images from screen dumps
 * but also any other kind of files in Oracle. 
 * The design is modular in a way that reading and writing to/ from eLog sources is
 * encapsulated in dedicated classes which are extended from the base class 
 * LogBook. 
 * A basic feature of this eLogbook is the ability to read entries from other 
 * logbooks and display internal and external logbook entries in the correct 
 * sequential order.
 * References in the Oracle-eLogbook can point to any other logbook.
 * Icons in the html pages will lead to these entries accordingly.
 * This implementation of the eLogbook is based on the logbook implemented
 * initially for TTF by the MVP group at DESY.
 *
 * (C) DESY Hamburg 2003
 *
 * @author Matthias Clausen DESY/MKS-2
 * @version 1.5.9
 *
 * The TopWindowStatistic servlet
 * is used to display the current statistical information about the Oracle contents
 * Initial implementation by Matthias Clausen DESY/MKS-2 April-2003
 */

public class Halle55 extends HttpServlet {
    
    /** Default serial number */
    private static final long serialVersionUID = 1L;
    
    /** Class that reads the value from the control system */
    private ValueReader valueReader;

    /** Private logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger(Halle55.class);
    
    /** The URL of the EPICS web application */
    private String epicsWebApp;
    
    /** The URL of the AAPI web application */
    private String aapiWebApp;

    /** Path of the data file that can be downloaded */
    private String dataFilePath;
    
    /** Path to the workspace folder */
    private String workspacePath;
    
    /**  */
    private String FILE_SEPARATOR;

    /**
     * 
     */
    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        valueReader = new ValueReader();
        
        FILE_SEPARATOR = System.getProperty("file.separator");

        IPath location = Platform.getLocation();
        workspacePath = location.toOSString();
        if(workspacePath.endsWith(FILE_SEPARATOR) == false) {
            
            workspacePath += FILE_SEPARATOR;
        }

        IPreferencesService ps = Platform.getPreferencesService();
        epicsWebApp = ps.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.EPICS_WEB_APP, "", null);
        aapiWebApp = ps.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.AAPI_WEB_APP, "", null);
        
//        dataFilePath = Platform.getLocation().toPortableString();

        dataFilePath = this.getServletContext().getRealPath("/var");
        if(dataFilePath == null) {
            dataFilePath = "";
        }
        
        if(dataFilePath.endsWith("/") == false) {
            dataFilePath = dataFilePath + "/";
        }
    }

    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        // doGet will create a web page which allows to set a new cookie
        PrintWriter out;

	    // set content type and other response header fields first
        response.setContentType("text/html");
	    out = response.getWriter();
	    
	    boolean ascii = (request.getParameter("ASCII") != null) ? true : false;

	    // create html header
	    out.println("<HTML><HEAD>");
	    
	    // ... already set above
	    out.println("<meta http-equiv=\"content-type\" content=\"text/html;charset=\"iso-8859-1\">");
        out.println("<META HTTP-EQUIV=\"expires\" CONTENT=\"30\">");
        out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
        
        if(!ascii) {
            out.println("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"10\">");
        } else {
            out.println("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"60\">");
        }
        
        out.println("<TITLE>DESY Building 55 Data</TITLE>");
        out.println("<!-- Java Script for Popup Windows-->");
        out.println("<SCRIPT language=\"JavaScript\">");
        out.println("<!-- hide from JavaScript-challenged browsers");
        out.println("function openWindow(url) {");
        out.println("popupWin = window.open(url,'remote','menubar=no,toolbar=yes,location=no,directories=no,status=yes,scrollbars=yes,resizable=yes,width=750,height=570,left=50,top=50')} ");
        out.println("function openWindowTop(url) {");
        out.println("popupWinTop = window.open(url,'remote','menubar=no,toolbar=yes,location=no,directories=no,status=yes,scrollbars=yes,resizable=yes,width=750,height=570,left=50,top=50')} ");
        // do not use - dependent, -
        out.println("function openWindowTopNoTool(url) {");
        out.println("popupWinTop2 = window.open(url,'remote','menubar=no,toolbar=no,location=no,directories=no,status=no,scrollbars=no,resizable=no,width=600,height=400,left=150,top=50')} // done hiding -->");
        out.println("</SCRIPT>");
        out.println("<!-- End Java Script for Popup Windows -->");
        
        out.println("</HEAD>");
        
        String epicsCaUrl = epicsWebApp + "?DTYPE=13&MONITOR_UPDATE_TIME=2&GET=";        
        
        if(!ascii) {
            createNormalBody(epicsCaUrl, out);
        } else {
            createAsciiBody(epicsCaUrl, out);
        }
        
        // additional debugging information
        //
//        if ( !thisRequest.getEpicsEnableCaPut())  {
//            out.println("<center><font color=\"##ff0000\">CA_PUT disabled</font></center>");
//        }

        out.println("</BODY></HTML>");
	    out.close();
    }

    public void createNormalBody(String epicsCaUrl, PrintWriter out) {
        
        // body
        
        //out.println("<body text=\"black\" bgcolor=\"#ffff99\" background=\"../images/strich.gif\">");
        out.println("<BODY  bgcolor=\"#ffff99\" >");
        //out.println("        <ul>");

        /*
         * start table
         */
        
        out.println("        <table border=\"0\" height=\"40\">");
        
        //
        // Header
        //
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td>    </td>");
        out.println("                <td><center><h2>DESY Halle 55</h2></center></td>");
        out.println("                <td>    </td>");
        out.println("            </tr>");
        
        //
        // current time on Utility IOC
        //
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td>    </td>");
        out.println("                <td><center>" + valueReader.getValueAsString("utilityIOC:time_si") + "</center></td>");
        out.println("                <td>    </td>");
        out.println("            </tr>");
        
        //
        // Verion Number and Host Name
        //
        //
        // get the local host name
        //
        String localHostName = null;
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            localHostName = localhost.getHostName();
        } catch (Exception e) {
            LOG.error("Error reading local host name: ", e);
            localHostName = "Could not be defined";
        }
        
        // print result
        out.println("            <tr bgcolor=\"#ccffff\"/>");
        out.println("            <tr bgcolor=\"#ffee00\"> <td colspan= \"3\">");
        // out.println ("<center>eLogbook Version: <font color=\"#3333FF\"> " + property.VERSION_NUMBER + "</font> on: <font color=\"#3333FF\">" + localHostName +"</font></center>");
        out.println ("<center><font color=\"#3333FF\"> " + LocalProperty.VERSION + "</font> on: <font color=\"#3333FF\">" + localHostName +"</font></center>");
        
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td><center>Channel</center></td>");
        out.println("                <td><center>actual Value</center></td>");
        out.println("                <td><center> - </center></td>");
        // out.println("                <td> </td>");
        out.println("            </tr>");
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td><center>(click: history)</center></td>");
        out.println("                <td><center>(click: value)</center></td>");
        out.println("                <td><center> - </center></td>");
        // out.println("                <td> </td>");
        out.println("            </tr>");
        
        
        //
        // MKS-2 Rufbereitschaft
        //
        out.println("            <tr bgcolor=\"#ccffff\"/>");
        out.println("            <tr bgcolor=\"#ccff00\">");
        out.println("                <td><center>Rufbereitschaft</center></td>");
        out.println("                <td><center>" + valueReader.getValueAsString("Rufbereitschaft") + "</center></td>");
        out.println("                <td>   </td>");
        out.println("            </tr>");
        
        out.println("            <tr bgcolor=\"#ccff00\"/>");
        out.println("            <tr bgcolor=\"#ccffff\"/>");
        
        
        /*
         * example
         * 
        out.println("            <tr bgcolor=\"#ccff00\">");
        out.println("                <td><center>HE:PKTR:HPDCCur_ai</center></td>");
        out.println("                <td><center>" + 
                "<a href=\"javascript:openWindowTop(\'"  +
                property.EPICS_CHANNEL_ARCHIVER_MKS + "&NAMES=HE%3APKTR%3AHPDCCur_ai');\">" +
                Utility.precision( epicsCa.get( "HE:PKTR:HPDCCur_ai"), 2) + "</a>");
        out.println("<a href=\"javascript:openWindowTopNoTool(\'"  +
                epicsCaUrl + "HE:PKTR:HPDCCur_ai');\">" +
                "_mA</a></center></td>");
        out.println("                <td>   </td>");
        out.println("            </tr>");
        */
        /*
        Date: Mon,  2 Jul 2007 16:00:16 +0200 (CEST)

        MTII700_ai                        0.0000
        MTTI711_temp                    294.7550
        MTPI711_ai                        0.9601
        MTFI723_calc                      0.0000
        MTTI712_temp                    295.0600
        MTTI710_temp                    293.7070
        MTPI710_ai                        1.0340
        MTTI720_temp                    293.6390
        MTPI007_ai                        0.9613
        MTUI705_ai                        0.0934
        MTFI705_ai                        0.0000
        MTUI704_ai                        0.0000

        out.println("            <tr bgcolor=\"#ccff00\">");
        out.println("                <td><center>MTII700_ai</center></td>");
        
        out.println("                <td><center>" + 
                "<a href=\"javascript:openWindowTop(\'"  +
                property.EPICS_CHANNEL_ARCHIVER_MKS + "&NAMES=MTII700_ai');\">" +
                Utility.precision( epicsCa.get( "MTII700_ai"), 2) + "</a>");
        System.out.println("                <td><center>" + 
                "<a href=\"javascript:openWindowTop(\'"  +
                property.EPICS_CHANNEL_ARCHIVER_MKS + "&NAMES=MTII700_ai');\">" +
                Utility.precision( epicsCa.get( "MTII700_ai"), 2) + "</a>");
        
        out.println("<a href=\"javascript:openWindowTopNoTool(\'"  +
                epicsCaUrl + "MTII700_ai');\">" + "_" +
                epicsCa.get( "MTII700_ai.EGU") +  "</a></center></td>");
        System.out.println("<a href=\"javascript:openWindowTopNoTool(\'"  +
                epicsCaUrl + "MTII700_ai');\">" + "_" +
                epicsCa.get( "MTII700_ai.EGU") +  "</a></center></td>");
        
        out.println("                <td>   </td>");
        out.println("            </tr>");
        */
        
        addRow(epicsCaUrl, out, "MTFI704_ai", true);
        addRow(epicsCaUrl, out, "MTFI705_ai", true);
        addRow(epicsCaUrl, out, "MTFI723_calc", true);
        addEmptyRow(out);
        addRow(epicsCaUrl, out, "MTII700_ai", true);
        addEmptyRow(out);
        addRow(epicsCaUrl, out, "MTPI007_ai", true);
        addRow(epicsCaUrl, out, "MTPI710_ai", true);
        addRow(epicsCaUrl, out, "MTPI711_ai", true);
        addEmptyRow(out);
        addRow(epicsCaUrl, out, "MTTI710_temp", false);
        addRow(epicsCaUrl, out, "MTTI711_temp", false);
        addRow(epicsCaUrl, out, "MTTI712_temp", false);
        addRow(epicsCaUrl, out, "MTTI720_temp", false);
        addEmptyRow(out);
        addRow(epicsCaUrl, out, "MTUI704_ai", true);
        addRow(epicsCaUrl, out, "MTUI705_ai", true);
        
        // tail
        out.println("            </tr>");
        out.println("        </table>");
    }

    public void createAsciiBody(String epicsCaUrl, PrintWriter out) {
        
        String list = createAsciiList(epicsCaUrl);
        boolean file = false;
        
        FileOutputStream dataFile = null;
        try {
            dataFile = new FileOutputStream(workspacePath + "data.txt");
            dataFile.write(list.getBytes());
            file = true;
        } catch(FileNotFoundException fnfe) {
            LOG.warn("Cannot write to the data file: ", fnfe);
            file = false;
        } catch(IOException ioe) {
            LOG.warn("Cannot write to the data file: ", ioe);
            file = false;
        } finally {
            if(dataFile != null) {
                try{dataFile.close();}catch(Exception e) {
                	// Can be ignored
                }
                dataFile = null;
            }
        }

        out.println("<BODY>");
        out.println("<h2>Daten Halle 55 (Reload every 60 seconds)</h2><p>");
        out.println("Date: " + valueReader.getValueAsString("utilityIOC:time_si") + "<p>");

        if(file) {
            out.println("<a href=\"./data.txt\">" + list + "</a>");
            out.println("<p><h4>Right click on the link, then 'Save As...' will create an text file containing the data.</h4>");
        } else {
            out.println(list);
        }
    }

    public void addRow(String epicsCaUrl, PrintWriter out, String record, boolean withEGU) {
        
        out.println("            <tr bgcolor=\"#ccff00\">");
        out.println("                <td>" + record + "</td>");
        out.println("                <td align=\"right\">" + 
                "<a href=\"javascript:openWindowTop(\'" +
                aapiWebApp + "&METHOD=GET&NAMES=" + record + "');\">" +
                Utility.precision(valueReader.getValueAsString(record),
                                  Integer.parseInt(valueReader.getValueAsString(record + ".PREC"))) +
                "</a></td>");

        /*
        out.println("                <td align=\"right\">" + 
                "<a href=\"javascript:openWindowTop(\'"  +
                property.EPICS_CHANNEL_ARCHIVER_MKS_PUBLIC + "&NAMES=" + record + "');\">" +
                Utility.precision( epicsCa.get(record), Integer.parseInt(epicsCa.get(record + ".PREC"))) + "</a></td>");
        */
        
        if(withEGU) {
            out.println("<td><a href=\"javascript:openWindowTopNoTool(\'"  +
                    epicsCaUrl + record + "');\">" + valueReader.getValueAsString(record + ".EGU") +  "</a></center></td>");
        } else {
            out.println("  <td>&nbsp;</td>");
        }
        
        out.println("            </tr>");    
    }
    
    public void addEmptyRow(PrintWriter out) {
        
        out.println("            <tr bgcolor=\"#ccff00\">");
        out.println("                <td colspan=\"3\">&nbsp;</td>");
        out.println("            </tr>");    
    }
    
    public String createAsciiList(String epicsCaUrl) {
        
        StringBuffer list = new StringBuffer();
        
        list.append("MTFI704_ai=" + valueReader.getValueAsString("MTFI704_ai"));
        list.append(";");
        list.append("MTFI705_ai=" + valueReader.getValueAsString("MTFI705_ai"));
        list.append(";");
        list.append("MTFI723_calc=" + valueReader.getValueAsString("MTFI723_calc"));
        list.append(";");
        list.append("MTII700_ai=" + valueReader.getValueAsString("MTII700_ai"));
        list.append(";");
        list.append("MTPI007_ai=" + valueReader.getValueAsString("MTPI007_ai"));
        list.append(";");
        list.append("MTPI710_ai=" + valueReader.getValueAsString("MTPI710_ai"));
        list.append(";");
        list.append("MTPI711_ai=" + valueReader.getValueAsString("MTPI711_ai"));
        list.append(";");
        list.append("MTTI710_temp=" + valueReader.getValueAsString("MTTI710_temp"));
        list.append(";");
        list.append("MTTI711_temp=" + valueReader.getValueAsString("MTTI711_temp"));
        list.append(";");
        list.append("MTTI712_temp=" + valueReader.getValueAsString("MTTI712_temp"));
        list.append(";");
        list.append("MTTI720_temp=" + valueReader.getValueAsString("MTTI720_temp"));
        list.append(";");
        list.append("MTUI704_ai=" + valueReader.getValueAsString("MTUI704_ai"));
        list.append(";");
        list.append("MTUI705_ai=" + valueReader.getValueAsString("MTUI705_ai"));

        return list.toString();
    }
    
    @Override
	public void doPost( HttpServletRequest request, HttpServletResponse	response)
    throws ServletException, IOException {
        
        doGet(request, response);
    }
}
