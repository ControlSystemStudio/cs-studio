/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.diirt.datasource.DataSourceConfiguration;
import org.w3c.dom.Document;

/**
 * Configuration for {@link BeastDatasource}
 * 
 * @author Kunal Shroff
 *
 */
public class BeastDataSourceConfiguration extends DataSourceConfiguration<BeastDatasource> {

    private String brokerUrl = "tcp://localhost:61616?jms.prefetchPolicy.all=1000";

    @Override
    public BeastDataSourceConfiguration read(InputStream input) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xPath = xpathFactory.newXPath();
            
            String ver = xPath.evaluate("/jms/@version", document);
            if (!ver.equals("1")) {
                throw new IllegalArgumentException("Unsupported version " + ver);
            }

            String monitorMask = xPath.evaluate("/jms/dataSourceOptions/@brokerURL", document);
            if (monitorMask != null && !monitorMask.isEmpty()) {
                this.brokerUrl = monitorMask;
            }else{
                Logger.getLogger(BeastDataSourceConfiguration.class.getName()).log(Level.FINEST, "Couldn't load brokerURL from jms file configuration");
            }
        } catch (Exception e) {
            Logger.getLogger(BeastDataSourceConfiguration.class.getName()).log(Level.FINEST, "Couldn't load file configuration", e);
            throw new IllegalArgumentException("Couldn't load file configuration", e);
        }
        return this;
    }

    @Override
    public BeastDatasource create() {
        return new BeastDatasource(this);
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

}
