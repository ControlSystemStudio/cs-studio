
package org.csstudio.websuite.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.regex.Pattern;

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
public class Wetter extends HttpServlet {

    /** Generated serial version id */
    private static final long serialVersionUID = 1420715721544267590L;

    /** Precompiled Pattern object for checking the precision */
    private Pattern numberPattern;
    
    /** Class that reads the value from the control system */
    private ValueReader valueReader;

    /** Private logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger(Wetter.class);
    
    /** The URL of the EPICS web application */
    private String epicsWebApp;
    
    /** The URL of the AAPI web application */
    private String aapiWebApp;

    // private final static String SERVLET_DIRECTORY = "/MeasuredData/";
    // private final static String IMAGES_DIRECTORY = SERVLET_DIRECTORY + "images";

    /**
     * 
     */
    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        numberPattern = Pattern.compile("\\d+");
        valueReader = new ValueReader();
        
        IPreferencesService ps = Platform.getPreferencesService();
        epicsWebApp = ps.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.EPICS_WEB_APP, "", null);
        aapiWebApp = ps.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.AAPI_WEB_APP, "", null);
    }

    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse  response)
    throws ServletException, IOException {
        
        // set content type and other response header fields first
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        //ErrorMessage.out("TopWindowStatistic.doGet: start processing");
        //
        // create html header
        //
        out.println("<HTML><HEAD>");
        // ... already set above
        out.println("<meta http-equiv=\"content-type\" content=\"text/html;charset=charset=\"iso-8859-1\">");
        out.println("<META HTTP-EQUIV=\"expires\" CONTENT=\"30\">");
        out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
        out.println("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"10\">");
        out.println("<TITLE>DESY Wetterstation</TITLE>");
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
        
        // body
        
        //out.println("<body text=\"black\" bgcolor=\"#ffff99\" background=\"../images/strich.gif\">");
        out.println("<body  bgcolor=\"#ffff99\" >");
        //out.println("        <ul>");
        
        String epicsCaUrl = epicsWebApp + "?DTYPE=13&MONITOR_UPDATE_TIME=2&GET=";
        
        // Start table
        out.println("        <table border=\"0\" height=\"40\">");
        
        // Header
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td align=\"center\"><img src=\"images/desy-logo.gif\"></td>");
        out.println("                <td><center><h2>DESY</h2></center></td>");
        out.println("                <td align=\"center\"><img src=\"images/kryk-logo.gif\"></td>");
        out.println("            </tr>");
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td>    </td>");
        out.println("                <td><center><h2>Wetterstation</h2></center></td>");
        out.println("                <td>    </td>");
        out.println("            </tr>");
        
        // Current time on Utility IOC
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td>    </td>");
        out.println("                <td><center>" + valueReader.getValueAsString("utilityIOC:time_si") + "</center></td>");
        out.println("                <td>    </td>");
        out.println("            </tr>");
        
        // Get the local host name
        String localHostName = null;
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            localHostName = localhost.getHostName();
        } catch (Exception ex) {
            LOG.error("Error reading local host name \n",  ex);
            localHostName = "could not be defined";
        }
        
        // print result
        out.println("            <tr bgcolor=\"#ccffff\"/>");
        out.println("            <tr bgcolor=\"#ffee00\"> <td colspan= \"3\">");
        // out.println ("<center>eLogbook Version: <font color=\"#3333FF\"> " + property.VERSION_NUMBER + "</font> on: <font color=\"#3333FF\">" + localHostName +"</font></center>");
        out.println ("<center><font color=\"#3333FF\"> " + LocalProperty.VERSION + "</font> on: <font color=\"#3333FF\">" + localHostName +"</font></center>");
        
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td><center>Channel</center></td>");
        out.println("                <td><center>actual Value</center></td>");
        out.println("                <td><center> EGU </center></td>");
        // out.println("                <td> </td>");
        out.println("            </tr>");
        out.println("            <tr bgcolor=\"#ccffff\">");
        out.println("                <td><center> - </center></td>");
        out.println("                <td><center>(click: for history)</center></td>");
        out.println("                <td><center>(click: for value)</center></td>");
        // out.println("                <td> </td>");
        out.println("            </tr>");
        
        
        //
        // MKS-2 Rufbereitschaft
        //
//        out.println("            <tr bgcolor=\"#ccffff\"/>");
//        out.println("            <tr bgcolor=\"#ccff00\">");
//        out.println("                <td><center>Rufbereitschaft</center></td>");
//        out.println("                <td><center>" + epicsCa.get( "Rufbereitschaft") + "</center></td>");
//        out.println("                <td>   </td>");
//        out.println("            </tr>");
//        
//        out.println("            <tr bgcolor=\"#ccff00\"/>");
//        out.println("            <tr bgcolor=\"#ccffff\"/>");
        
        
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
        out.println("<a href=\"javascript:openWindowTopNoTool(\'"  +
                epicsCaUrl + "MTII700_ai');\">" + "_" +
                epicsCa.get( "MTII700_ai.EGU") +  "</a></center></td>");
        out.println("                <td>   </td>");
        out.println("            </tr>");
        */
        
        addRow(epicsCaUrl, out, "krykWeather:Temp_ai");
        addRow(epicsCaUrl, out, "krykWeather:vWind_ai");
        addRow(epicsCaUrl, out, "krykWeather:vWindBoe_ai");
        addRow(epicsCaUrl, out, "krykWeather:angWind360_ai");
        addRow(epicsCaUrl, out, "krykWeather:PLuft_ai");
        addRow(epicsCaUrl, out, "krykWeather:relF_ai");
        //addRow(epicsCa, epicsCaUrl, out, "krykWeather:Td_ai");

        // tail
        out.println("            </tr>");
        out.println("        </table>");
        //
        // additional debugging information
        //
//        if ( !thisRequest.getEpicsEnableCaPut())  {
//            out.println("<center><font color=\"##ff0000\">CA_PUT disabled</font></center>");
//        }
        out.println("</BODY></HTML>");
        out.close();
    }

    public void addRow(String epicsCaUrl, PrintWriter out, String record) {
        
        String prec = null;
        int precision = 0; // Default value
        
        out.println("            <tr bgcolor=\"#ccff00\">");
        out.println("                <td>" + record + "</td>");
        
        // Check the value for the precision
        prec = valueReader.getValueAsString(record + ".PREC");
        if(prec != null) {
            
            // Do we have a number?
            if(numberPattern.matcher(prec).matches()) {
                precision = Integer.parseInt(prec);
            }
        }
        
        // Internal Archiver
        out.println("                <td align=\"right\">" + 
                "<a href=\"javascript:openWindowTop(\'"  +
                aapiWebApp + "&METHOD=GET&NAMES=" + record + "');\">" +
                Utility.precision(valueReader.getValueAsString(record), precision) + "</a></td>");

        /* 
         * Public Archiver
         * 
        out.println("                <td align=\"right\">" + 
                "<a href=\"javascript:openWindowTop(\'"  +
                property.EPICS_CHANNEL_ARCHIVER_MKS_PUBLIC + "&NAMES=" + record + "');\">" +
                Utility.precision(epicsCa.get(record), precision) + "</a></td>");
        */
        
        out.println("<td><a href=\"javascript:openWindowTopNoTool(\'" +
                epicsCaUrl + record + "');\">" + valueReader.getValueAsString(record + ".EGU") +
                "</a></center></td>");
      
        // out.println("                <td>   </td>");
        out.println("            </tr>");    
    }

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        doGet(request, response);
    }
}
