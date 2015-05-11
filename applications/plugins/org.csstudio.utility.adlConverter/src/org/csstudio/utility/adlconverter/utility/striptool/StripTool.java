/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility.striptool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.graphics.RGB;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 08.09.2009
 */
public class StripTool {

    private static final Logger LOG = LoggerFactory.getLogger(StripTool.class);
    private final Document _document;
    private final Element _root;
    private Element _pvList;
    private final NumberFormat _formatter;

    public StripTool() {
        _root = new Element("databrowser");
        _document = new Document(_root);
        DecimalFormatSymbols instance = DecimalFormatSymbols.getInstance();
        instance.setDecimalSeparator('.');
        _formatter = new DecimalFormat("#.####",instance);
    }


    /**
     *
     * @param path
     *            The target File Path.
     * @throws IOException
     */
    public final void getXmlFile(final File path) throws IOException {
        FileWriter writer = new FileWriter(path);
        Format format = Format.getPrettyFormat();
        format.setEncoding("ISO-8859-1");
        XMLOutputter out = new XMLOutputter(format);
        out.output(_document, writer);
        LOG.info("Write File: {}", path.getAbsolutePath());
        writer.close();
    }

    public InputStream getXmlFileInputStream() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Format format = Format.getPrettyFormat();
        format.setEncoding("ISO-8859-1");
        XMLOutputter out = new XMLOutputter(format);
        out.output(_document, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        return is;
    }

    public void setTimespan(double timespan) {
        Element start = new Element("start");
        start.setText(String.format(Locale.ENGLISH, "-0 hours %1$f seconds", timespan));
        _root.addContent(start);
    }

    public void setNumSamples(int numSamples) {
        Element start = new Element("ring_size");
        start.setText(String.format(Locale.ENGLISH, "%1$d", numSamples));
        _root.addContent(start);

    }


    public void setRefreshInterval(double refreshInterval) {
        Element start = new Element("scan_period");
        start.setText(_formatter.format(refreshInterval));
        _root.addContent(start);
    }

    public void setSampleInterval(double sampleInterval) {
        Element start = new Element("update_period");
        start.setText(_formatter.format(sampleInterval));
        _root.addContent(start);
    }


    public void setBackground(RGB background) {
        Element start = makeColorElement("background", background);
        _root.addContent(start);
    }

    public void setForeground(RGB foreground) {
        Element start = makeColorElement("foreground", foreground);
        _root.addContent(start);
    }

    public void setGridColor(RGB gridColor) {
        Element start = makeColorElement("grid_color", gridColor);
        _root.addContent(start);
    }


    private static Element makeColorElement(String tagName, RGB color) {
        Element colorElement = new Element(tagName);
        Element red = new Element("red");
        red.setText(String.format("%1$d", color.red));
        Element green = new Element("green");
        green.setText(String.format("%1$d", color.green));
        Element blue = new Element("blue");
        blue.setText(String.format("%1$d", color.blue));

        colorElement.addContent(red);
        colorElement.addContent(green);
        colorElement.addContent(blue);
        return colorElement;
    }


    public void addPV(String pvName, int axis, int graphLineWidth, Double min, Double max,
            boolean axisVisible, RGB rgb) {
        if(_pvList == null) {
            _pvList = new Element("pvlist");
            _root.addContent(_pvList);
        }
        Element pv = new Element("pv");

        // Name
        Element nameElement = new Element("name");
        nameElement.setText(pvName);
        pv.addContent(nameElement);

        // Axis
        Element axisElement = new Element("axis");
        axisElement.setText(Integer.toString(axis));
        pv.addContent(axisElement);

        // Min
        Element minElement = new Element("min");
        minElement.setText(Double.toString(min));
        pv.addContent(minElement);

        // Max
        Element maxElement = new Element("max");
        maxElement.setText(Double.toString(max));
        pv.addContent(maxElement);

        // Visible
        Element visibleElement = new Element("visible");
        visibleElement.setText("true");
        pv.addContent(visibleElement);

        // Axis visible
        Element axisVisibleElement = new Element("axis_visible");
        System.out.println("axis_visible: "+Boolean.toString(axisVisible));
        // TODO: ist das der richtige Wert hierfür?
//        axisVisibleElement.setText(Boolean.toString(axisVisible));
        axisVisibleElement.setText("true");
        pv.addContent(axisVisibleElement);

        // Autoscale
        Element autoscale = new Element("autoscale");
        autoscale.setText("false");
        pv.addContent(autoscale);

        // Color
        Element color = makeColorElement("color", rgb);
        pv.addContent(color);

        // log_scale
        Element logScale = new Element("log_scale");
        logScale.setText("false");
        pv.addContent(logScale);

        // trace type
        Element traceType = new Element("trace_type");
        traceType.setText("Area");
        pv.addContent(traceType);

        // Request
        Element request = new Element("request");
        request.setText("1");
        pv.addContent(request);

        // Archive
        Element archive = new Element("archive");
        Element archiveName = new Element("name");
        archiveName.setText("AAPI");
        Element archiveUrl = new Element("url");
        archiveUrl.setText("aapi://krynfs.desy.de:4055");
        Element archiveKey = new Element("key");
        archiveKey.setText("0");
        archive.addContent(archiveName);
        archive.addContent(archiveUrl);
        archive.addContent(archiveKey);
        pv.addContent(archive);

        _pvList.addContent(pv);
    }

}
