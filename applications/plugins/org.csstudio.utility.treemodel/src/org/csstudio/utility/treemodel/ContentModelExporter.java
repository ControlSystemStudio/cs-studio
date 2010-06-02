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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 19.05.2010
 */
public final class ContentModelExporter {

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
        void exportContentModelToXmlFile(@Nonnull final String filePath,
                                         @Nonnull final ContentModel<T> model,
                                         @Nullable final String dtdFilePath) throws ExportContentModelException {


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

    @Nonnull
    private static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        Document createDOM(@Nonnull final ContentModel<T> model, @Nullable final String dtdFilePath) {

        final T type = model.getRoot().getType();
        final Element rootElem = new Element(type.getNodeTypeName());
        rootElem.setAttribute("name", type.getRootTypeName());

        final Document doc = new Document(rootElem);

        if (dtdFilePath != null) {
            final DocType docType = new DocType(rootElem.getName(), dtdFilePath);
            doc.setDocType(docType);
        }

        for (final ISubtreeNodeComponent<T> modelNode : model.getRoot().getDirectChildren()) {
            createDOMElement(rootElem, modelNode);
        }

        return doc;
    }

    private static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        void createDOMElement(@Nonnull final Element parentElem,
                              @Nonnull final ISubtreeNodeComponent<T> modelNode) {

        // FIXME (bknerr) : once the deprecated esco, ioc components have vanished, the next lines can be removed
        String name = modelNode.getType().getNodeTypeName();
        if ("econ".equals(name) ||
            "esco".equals(name)) {
            name = "ecom";
        }

        final Element newNode = new Element(name);

        newNode.setAttribute("name", modelNode.getName());

        parentElem.addContent(newNode);

        for (final ISubtreeNodeComponent<T> child : modelNode.getDirectChildren()) {
            createDOMElement(newNode, child);
        }
    }


}
