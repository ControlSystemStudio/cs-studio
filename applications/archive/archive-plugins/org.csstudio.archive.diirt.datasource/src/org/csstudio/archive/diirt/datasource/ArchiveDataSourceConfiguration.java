/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.archive.diirt.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.diirt.datasource.DataSourceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * <code>ArchiveDataSourceConfiguration</code> loads the configuration file for the archive data source and generates
 * the sources set which defined where to fetch the data from. The configuration is expected to be in the
 * diirt/datasources/archive/archive.xml file, where diirt is the root of the diirt configuration.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ArchiveDataSourceConfiguration extends DataSourceConfiguration<ArchiveDataSource> {

    private Set<ArchiveSource> sources = new HashSet<>();
    private int binCount = 1000;

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSourceConfiguration#read(java.io.InputStream)
     */
    @Override
    public DataSourceConfiguration<ArchiveDataSource> read(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);

            NodeList sourcesElement = document.getElementsByTagName("archivesources");
            if (sourcesElement != null && sourcesElement.getLength() == 1) {
                Node node = sourcesElement.item(0);
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node source = list.item(i);
                    if ("source".equals(source.getNodeName())) {
                        NamedNodeMap attributes = source.getAttributes();
                        String name = attributes.getNamedItem("name").getNodeValue();
                        String url = attributes.getNamedItem("url").getNodeValue();
                        String key = attributes.getNamedItem("key").getNodeValue();
                        if (name == null || url == null || name.isEmpty() || url.isEmpty()) {
                            Logger.getLogger(ArchiveDataSourceConfiguration.class.getName()).log(Level.FINEST,
                                "Name and url of archive diirt configuration cannot be null or empty.");
                            continue;
                        }
                        sources.add(new ArchiveSource(name, key, url));
                    } else if ("binCount".equals(source.getNodeName())) {
                        binCount = Integer.parseInt(source.getTextContent());
                    }
                }
            }
        } catch (RuntimeException | ParserConfigurationException | SAXException | IOException e) {
            Logger.getLogger(ArchiveDataSourceConfiguration.class.getName()).log(Level.FINEST,
                "Could not load file configuration", e);
            throw new IllegalArgumentException("Could not load file configuration", e);
        }
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSourceConfiguration#create()
     */
    @Override
    public ArchiveDataSource create() {
        if (sources.isEmpty()) {
            throw new IllegalStateException(
                "There aer no archive sources configured for the diirt archive datasource.");
        }
        return new ArchiveDataSource(this);
    }

    /**
     * Returns the list of all registered archive sources.
     *
     * @return the list of all registered sources
     */
    ArchiveSource[] getSources() {
        return sources.toArray(new ArchiveSource[sources.size()]);
    }

    /**
     * Returns the archive source whose name or url matches the given parameter.
     *
     * @param nameOrUrl the name or url to match
     * @return the archive source
     */
    ArchiveSource getSource(String nameOrUrl) {
        for (ArchiveSource s : sources) {
            if (s.name.equals(nameOrUrl) || s.url.matches(nameOrUrl)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns the number of bins used for optimised data retrieval.
     *
     * @return number of bins for optimised data retrieval
     */
    int getBinCount() {
        return binCount;
    }
}
