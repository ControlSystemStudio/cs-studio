/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.utility.treemodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.IllegalNameException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.collect.ImmutableSet;


/**
 * Exports content model to different file formats.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 19.05.2010
 */
public final class ContentModelExporter {

    private static final String XML_ENCODING_FORMAT = "ISO-8859-1";
    private static final Logger LOG = Logger.getLogger(ContentModelExporter.class.getName());

    /**
     * Constructor.
     */
    private ContentModelExporter() {
        // Don't instantiate
    }

    /**
     * Exports the given content model to an xml file.
     * @param filePath the filePath to the new xml file.
     * @throws ExportContentModelException
     */
    public static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        void exportContentModelToXmlFile(final String filePath,
                                         final ContentModel<T> model,
                                         final String dtdFilePath) throws ExportContentModelException {


        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

            final Document doc = createDOM(model, dtdFilePath);

            outputter.output(doc, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (final IOException e) {
            throw new ExportContentModelException("Error while trying to write the XML file.", e);
        }
    }

    /**
     * Exports the given content model to String.
     * @throws ExportContentModelException
     */
    public static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        String exportContentModelToXmlString(final ContentModel<T> model,
                                             final String dtdFilePath) throws ExportContentModelException {

            final Format f = Format.getPrettyFormat();
            f.setEncoding(XML_ENCODING_FORMAT);
            final XMLOutputter outputter = new XMLOutputter(f);
            final Document doc = createDOM(model, dtdFilePath);
            return outputter.outputString(doc);
    }

    private static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        Document createDOM(final ContentModel<T> model, final String dtdFilePath) throws ExportContentModelException {

        final ISubtreeNodeComponent<T> virtualRoot = model.getVirtualRoot();

        final Collection<INodeComponent<T>> directChildren = virtualRoot.getDirectChildren();
        if (directChildren.size() > 1) {
            LOG.warning("Content model contains more than one root node. For DOM tree export only first root is chosen.");
        }
        final INodeComponent<T> root = directChildren.iterator().next();
        if (root == null) {
            LOG.warning("Content model is empty. Return empty document.");
            return new Document();
        }

        final Element rootElem = new Element(root.getType().getNodeTypeName());
        rootElem.setAttribute("name", root.getName());

        final Document doc = new Document(rootElem);

        if (dtdFilePath != null) {
            final DocType docType = new DocType(rootElem.getName(), dtdFilePath);
            doc.setDocType(docType);
        }

        if (root instanceof ISubtreeNodeComponent) {
            for (final INodeComponent<T> child : ((ISubtreeNodeComponent<T>) root).getDirectChildren()) {
                createDOMElement(rootElem, child);
            }
        }

        return doc;
    }

    private static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        void createDOMElement(final Element parentElem,
                              final INodeComponent<T> modelNode) throws ExportContentModelException {

        // FIXME (bknerr) : once the deprecated esco, ioc components have vanished, the next lines can be removed
        String typeName = modelNode.getType().getNodeTypeName();
        if ("esco".equals(typeName)) {
            typeName = "ecom";
        }

        final Element newNode = createElement(modelNode, typeName);

        parentElem.addContent(newNode);

        if (modelNode instanceof ISubtreeNodeComponent) {
            for (final INodeComponent<T> child : ((ISubtreeNodeComponent<T>) modelNode).getDirectChildren()) {
                createDOMElement(newNode, child);
            }
        }
    }

    private static <T extends Enum<T> & ITreeNodeConfiguration<T>> Element
        createElement(final INodeComponent<T> modelNode,
                      final String typeName) throws ExportContentModelException {

        final Element newNode = new Element(typeName);

        newNode.setAttribute("name", modelNode.getName());

        final T type = modelNode.getType();
        final ImmutableSet<String> attributes = type.getAttributes();
        for (final String attributeField : attributes) {
            final Attribute attribute = modelNode.getAttribute(attributeField);
            if (attribute != null) {
                try {
                    final String attributeVal = (String) attribute.get();
                    newNode.setAttribute(attributeField, attributeVal);
                } catch (final NamingException e) {
                    final String message = "Attribute creation failed for element of type " + type.getNodeTypeName() +
                    " with name " + modelNode.getName();
                    LOG.warning(message);
                    throw new ExportContentModelException(message, e);
                } catch (final IllegalNameException ne) {
                    final String message = "Attribute creation failed for element of type " + type.getNodeTypeName() +
                    " with name " + modelNode.getName() + ": Illegal Name";
                    LOG.warning(message);
                    throw new ExportContentModelException(message, ne);
                } catch (final IllegalDataException ne) {
                    final String message = "Attribute creation failed for element of type " + type.getNodeTypeName() +
                    " with name " + modelNode.getName() + ": Illegal Data";
                    LOG.warning(message);
                    throw new ExportContentModelException(message, ne);
                }
            }
        }
        return newNode;
    }


}
