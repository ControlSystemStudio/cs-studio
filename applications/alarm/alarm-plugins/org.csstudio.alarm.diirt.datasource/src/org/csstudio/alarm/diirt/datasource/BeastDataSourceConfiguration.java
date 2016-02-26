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
 * Configuration for {@link BeastDataSource}
 *
 * @author Kunal Shroff
 *
 */
public class BeastDataSourceConfiguration extends DataSourceConfiguration<BeastDataSource> {

    private String configName = null;

    @Override
    public BeastDataSourceConfiguration read(InputStream input) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xPath = xpathFactory.newXPath();

            String ver = xPath.evaluate("/beast/@version", document);
            if (!ver.equals("1")) {
                throw new IllegalArgumentException("Unsupported version " + ver);
            }

            String configName = xPath.evaluate("/beast/dataSourceOptions/@configName", document);
            if (configName != null && !configName.isEmpty()) {
                this.configName = configName;
            }else{
                Logger.getLogger(BeastDataSourceConfiguration.class.getName()).log(Level.FINEST, "Couldn't load brokerURL from beast file configuration");
            }
        } catch (Exception e) {
            Logger.getLogger(BeastDataSourceConfiguration.class.getName()).log(Level.FINEST, "Couldn't load file configuration", e);
            throw new IllegalArgumentException("Couldn't load file configuration", e);
        }
        return this;
    }

    @Override
    public BeastDataSource create() {
        return new BeastDataSource(this);
    }

    public String getConfigName() {
        return configName;
    }

}
