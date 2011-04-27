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
package org.csstudio.utility.treemodel.builder;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;
import org.csstudio.utility.treemodel.TreeNodeComponent;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import com.google.common.collect.ImmutableSet;

/**
 * Builds a content model from an xml file.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.05.2010
 * @param <T> the object class type for which a tree shall be created
 */
public class XmlFileContentModelBuilder<T extends Enum<T> & ITreeNodeConfiguration<T>> extends AbstractContentModelBuilder<T> {

    private final T _configurationRoot;
    private final InputStream _inStream;


    /**
     * Constructor.
     */
    public XmlFileContentModelBuilder(@Nonnull final T configurationRoot,
                                      @Nonnull final InputStream stream) {
        _configurationRoot = configurationRoot;
        _inStream = stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    protected final ContentModel<T> createContentModel() throws CreateContentModelException {
        try {
            // Convert our input stream to a dataInputStream
            final DataInputStream in = new DataInputStream(_inStream);

            final SAXBuilder builder = new SAXBuilder(true);
            final Document doc = builder.build(in);

            return createContentModelFromFile(doc);

        } catch (final FileNotFoundException e) {
            throw new CreateContentModelException("File not found with exception " + e.getMessage(), e);
        } catch (final JDOMException e) {
            throw new CreateContentModelException("File contains parsing errors. " + e.getCause().getMessage(), e);
        } catch (final IOException e) {
            throw new CreateContentModelException("File could not be parsed due to I/O error.", e);
        }
    }
    /**
     * @param doc the xml document model
     * @throws CreateContentModelException if Rdn or LdapName could not be constructed
     */
    @Nonnull
    private ContentModel<T> createContentModelFromFile(@Nonnull final Document doc)
        throws CreateContentModelException {

        final Element xmlRootElement = doc.getRootElement();

        ContentModel<T> model = null;
        try {
            final String attributeValue = xmlRootElement.getAttributeValue("name");
            if (attributeValue == null || attributeValue.length() == 0) {
                throw new CreateContentModelException("Root element has not a valid name attribute.", null);
            }
            final String typeValue = _configurationRoot.getUnitTypeValue();
            if (!attributeValue.equals(typeValue)) {
                throw new CreateContentModelException("Root element does not match root type value=" + typeValue + " in Enum " + _configurationRoot.name(), null);
            }

            model = new ContentModel<T>(_configurationRoot);

        } catch (final InvalidNameException e) {
            throw new CreateContentModelException("Component model could not be constructed. Invalid LDAP name for root element.", e);
        }

        processElement(model, xmlRootElement, model.getVirtualRoot());

        return model;
    }


    /**
     * Get direct 'structural' children that are those contained in <ecoms> <
     * @param element
     * @return
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    private List<Element> getChildrenElements(@Nonnull final Element element) {
        final List<Element> children = element.getContent(new ElementFilter());
        return children;
    }

    /**
     * Recursive
     * @param model
     * @param element
     * @param iLdapTreeComponent
     * @throws CreateContentModelException if Rdn or LdapName could not be constructed
     */
    private void processElement(@Nonnull final ContentModel<T> model,
                                @Nonnull final Element element,
                                @Nonnull final ISubtreeNodeComponent<T> ldapParent) throws CreateContentModelException {


        final String type = element.getName();
        final T oc = _configurationRoot.getNodeTypeByNodeTypeName(type);
        final String name = element.getAttributeValue("name");

        final List<Rdn> rdns = new ArrayList<Rdn>(ldapParent.getLdapName().getRdns());
        try {
            rdns.add(new Rdn(type, name));
        } catch (final InvalidNameException e) {
            throw new CreateContentModelException("Rdn for type=" + type + " and name=" + name + " could not be constructed.", e);
        }
        final LdapName fullName = new LdapName(rdns);

        if (model.getByTypeAndLdapName(oc, fullName) == null) {

            final Attributes attributes = extractAttributes(element, oc.getAttributes());

            ISubtreeNodeComponent<T> newLdapChild;
            try {
                newLdapChild = new TreeNodeComponent<T>(name,
                                                        oc,
                                                        ldapParent,
                                                        attributes,
                                                        fullName);
            } catch (final InvalidNameException e) {
                throw new CreateContentModelException("Component model with LdapName " + fullName + " could not be constructed. Invalid LDAP name.", e);

            }
            model.addChild(ldapParent, newLdapChild);
        }
        final ISubtreeNodeComponent<T> ldapComponent =
            model.getByTypeAndLdapName(oc, fullName);

        final List<Element> children = getChildrenElements(element);

        // cycle through all immediate elements under the rootElement
        for (final Element child : children) {
            processElement(model, child, ldapComponent);
        }
    }

    @SuppressWarnings("unchecked")
    private Attributes extractAttributes(@Nonnull final Element element,
                                         @Nonnull final ImmutableSet<String> attributes) {

        final Attributes treeNodeAttributes = new BasicAttributes();


        final List<Attribute> xmlAttributes = element.getAttributes();

        for (final Attribute xmlAttribute : xmlAttributes) {
            final String attrName = xmlAttribute.getName();
            if (attributes.contains(attrName)) {
                treeNodeAttributes.put(attrName, xmlAttribute.getValue());
            }
        }

        return treeNodeAttributes;
    }

}
