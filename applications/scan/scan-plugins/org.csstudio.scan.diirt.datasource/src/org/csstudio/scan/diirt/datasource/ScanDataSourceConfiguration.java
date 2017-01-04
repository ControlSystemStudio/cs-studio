/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.scan.diirt.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.diirt.datasource.DataSourceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

/**
 * Configuration for {@link ScanDataSource}
 *
 * @author Eric Berryman
 *
 */
public class ScanDataSourceConfiguration extends DataSourceConfiguration<ScanDataSource> {

    Map<String, URL> connections;
    int pollInterval;
    int timeOut;

    @Override
    public ScanDataSourceConfiguration read(InputStream input) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xPath = xpathFactory.newXPath();

            String ver = xPath.evaluate("/scan/@version", document);
            if (!ver.equals("1")) {
                throw new IllegalArgumentException("Unsupported version " + ver);
            }

            String pollIntervalSetting = xPath.evaluate("/scan/servers/@pollInterval", document);
            pollInterval = Integer.parseInt(pollIntervalSetting);

            String timeOutSetting = xPath.evaluate("/scan/servers/@timeout", document);
            timeOut = Integer.parseInt(timeOutSetting);

            Map<String, URL> newConnections = new HashMap<>();
            NodeList xmlChannels = (NodeList) xPath.evaluate("/scan/servers/server", document, XPathConstants.NODESET);
            for (int j = 0; j < xmlChannels.getLength(); j++) {
                Node xmlChannel = xmlChannels.item(j);
                String channelName =  xPath.evaluate("@name", xmlChannel);
                String url =  xPath.evaluate("@url", xmlChannel);
                newConnections.put(channelName,new URL(url));
            }

            connections = newConnections;
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
            Logger.getLogger(ScanDataSourceConfiguration.class.getName()).log(Level.FINEST, "Couldn't load scan configuration", ex);
            throw new IllegalArgumentException("Couldn't load scan configuration", ex);
        }
        return this;
    }

    @Override
    public ScanDataSource create() {
        return new ScanDataSource(this);
    }

}
