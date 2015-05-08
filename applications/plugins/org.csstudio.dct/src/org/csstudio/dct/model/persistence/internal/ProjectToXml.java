package org.csstudio.dct.model.persistence.internal;

import java.util.Map;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.domain.common.strings.StringUtil;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Converts a {@link Project} to XML format.
 *
 * @author Sven Wende
 *
 */
public final class ProjectToXml {
    private Project project;

    /**
     * Constructor.
     * @param project a project
     */
    public ProjectToXml(Project project) {
        assert project != null;
        this.project = project;
    }

    /**
     * Returns a xml representation of the provided project;
     *
     * @return a xml representation of the provided project.
     */
    public Document createDocument() {
        Document document = new Document();
        Element rootElement = createElement(project);
        document.setRootElement(rootElement);
        return document;
    }

    private Element createElement(IElement element) {
        Element result = null;

        if (element instanceof IFolder) {
            result = createElement((IFolder) element);
        } else if (element instanceof IPrototype) {
            result = createElement((IPrototype) element);
        } else if (element instanceof IInstance) {
            result = createElement((IInstance) element);
        } else if (element instanceof IRecord) {
            result = createElement((IRecord) element);
        } else {
            throw new IllegalAccessError("Unsupported type:" + element.getClass());
        }

        return result;
    }

    private Element createElement(Project project) {
        Element element = createElement((IFolder) project);
        element.setAttribute("dbd", StringUtil.trimNull(project.getDbdPath()));
        element.setAttribute("ioc", StringUtil.trimNull(project.getIoc()));

        return element;
    }

    /**
     * Creates the xml representation for a folder like this:
     *
     * <code>
     *     <folder name="{name}" id={id}">
     *         <prototype ...>
     *             ...
     *         </prototype>
     *         ...
     *         <instance ...>
     *             ...
     *         </instance>
     *         ...
     *      <folder ...>
     *          ...
     *      </folder>
     *      ...
     *     </folder>
     * </code>
     *
     * @param folder
     *            the folder
     * @return the xml representation
     */
    private Element createElement(IFolder folder) {
        assert folder != null;

        Element folderXmlElement = createBaseElement(folder, "folder");

        // FOLDER NAME
        folderXmlElement.setAttribute("name", getNonEmptyString(folder.getName(), "unknown"));

        // LIST INDEX
        if(folder.getParentFolder()!=null) {
            folderXmlElement.setAttribute("index", ""+folder.getParentFolder().getMembers().indexOf(folder));
        }

        // CHILDREN
        for (IFolderMember m : folder.getMembers()) {
            Element childElement = createElement((IElement) m);
            folderXmlElement.addContent(childElement);
        }

        return folderXmlElement;
    }

    /**
     * Creates the xml representation for a prototype like this:
     *
     * <code>
     *     <prototype name="{name}" id={id}">
     *         <parameter name="${parameterName}" value="{defaultValue}" />
     *         ...
     *         <property name="${propertyName}" value="${propertyValue}" />
     *         ...
     *         <record ...>...</record>
     *         ...
     *      <instance ...>...</instance>
     *      ...
     *     </prototype>
     * </code>
     *
     * @param prototype
     *            the prototype
     * @return the xml representation
     */
    private Element createElement(IPrototype prototype) {
        Element prototypeXmlElement = createBaseElement(prototype, "prototype");

        // PROTOTYPE NAME
        prototypeXmlElement.setAttribute("name", prototype.getName());

        // LIST INDEX
        if(prototype.getParentFolder()!=null) {
            prototypeXmlElement.setAttribute("index", ""+prototype.getParentFolder().getMembers().indexOf(prototype));
        }

        // PARAMETERS
        for (Parameter p : prototype.getParameters()) {
            Element pElement = new Element("parameter");
            pElement.setAttribute("name", p.getName());
            pElement.setAttribute("value", p.getDefaultValue()!=null?p.getDefaultValue():"");
            prototypeXmlElement.addContent(pElement);
        }

        // PROPERTIES
        addProperties(prototype, prototypeXmlElement);

        // RECORDS
        for (IRecord record : prototype.getRecords()) {
            prototypeXmlElement.addContent(createElement(record));
        }

        // PROTOTYPE INSTANCES
        for (IInstance instance : prototype.getInstances()) {
            prototypeXmlElement.addContent(createElement(instance));
        }

        return prototypeXmlElement;
    }

    /**
     * Creates the xml representation for an instance like this: * <code>
     *     <instance name="{name}" id={id} parent="{parentId}">
     *         <record ...>...</record>
     *         ...
     *      <instance ...>...</instance>
     *      ...
     * </instance>
     * </code>
     *
     * @param instance the instance
     * @return
     */
    private Element createElement(IInstance instance) {
        Element instanceXmlElement = createBaseElement(instance, "instance");
        instanceXmlElement.setAttribute("parent", instance.getParent().getId().toString());

        // INSTANCE NAME
        String n = instance.getName();
        instanceXmlElement.setAttribute("name", (n != null && n.length() > 0) ? n : "{inherited}");

        // LIST INDEX
        int index = 0;
        if(instance.getParentFolder()!=null) {
            index = instance.getParentFolder().getMembers().indexOf(instance);
        } else {
            index = instance.getContainer().getInstances().indexOf(instance);
        }
        instanceXmlElement.setAttribute("index", ""+index);

        // PARAMETER VALUES
        Map<String, String> parameterValues = instance.getParameterValues();
        for (String pName : parameterValues.keySet()) {
            Element e = new Element("parameter");
            e.setAttribute("name", pName);
            e.setAttribute("value", parameterValues.get(pName));
            instanceXmlElement.addContent(e);
        }

        // PROPERTIES
        addProperties(instance, instanceXmlElement);

        // RECORDS
        for (IRecord record : instance.getRecords()) {
            instanceXmlElement.addContent(createElement(record));
        }

        // SUB INSTANCES
        for (IInstance in : instance.getInstances()) {
            instanceXmlElement.addContent(createElement(in));
        }

        return instanceXmlElement;
    }

    /**
     * Create the xml representation for a record like this:
     *
     * <code>
     *     <record name="{name}" id={id} parent="{parentRecordId}">
     *         <field name="{fieldName} value={fieldValue} />
     *      <field name="{fieldName} value={fieldValue} />
     *      ...
     *      <property name="{propertyName}" value="{propertyValue}" />
     *      <property name="{propertyName}" value="{propertyValue}" />
     *      ...
     * </record>
     * </code>
     *
     * @param record
     *            the record
     *
     * @return the xml element representing the record
     */
    private Element createElement(IRecord record) {
        Element element = createBaseElement(record, "record");

        // RECORD NAME
        String n = record.getName();
        element.setAttribute("name", (n != null && n.length() > 0) ? n : "{inherited}");

        // EPICS NAME
        String epicsName = record.getEpicsName();
        element.setAttribute("epicsname", (epicsName != null && epicsName.length() > 0) ? epicsName : "{inherited}");

        // DISABLED
        Boolean disabled = record.getDisabled();
        element.setAttribute("disabled", (disabled != null) ? disabled.toString() : "{inherited}");

        // TYPE
        element.setAttribute("type", record.getType());

        // PARENT
        IRecord parentRecord = record.getParentRecord();
        if (parentRecord != null && parentRecord.getParentRecord() != null) {
            element.setAttribute("parent", parentRecord.getId().toString());
        }

        // FIELDS
        for (String field : record.getFields().keySet()) {
            Element fieldElement = new Element("field");
            fieldElement.setAttribute("name", field);
            fieldElement.setAttribute("value", record.getField(field).toString());
            element.addContent(fieldElement);
        }

        // PROPERTIES
        addProperties(record, element);

        return element;
    }

    private void addProperties(IPropertyContainer container, Element xmlElement) {

        for (String property : container.getProperties().keySet()) {
            Element propertyElement = new Element("property");
            propertyElement.setAttribute("name", property);
            propertyElement.setAttribute("value", container.getProperty(property).toString());
            xmlElement.addContent(propertyElement);
        }

    }

    private Element createBaseElement(IElement model, String name) {
        assert model != null;
        assert name != null;

        Element element = new Element(name);
        element.setAttribute("id", model.getId().toString());

        return element;
    }

    /**
     * Convenience method that echos the specified source string when it is not
     * empty. Otherwise the fall back string is echoed.
     *
     * @param source
     *            the source string
     * @param fallback
     *            the fall back string
     *
     * @return the non-empty source string or the fall back string
     */
    private static String getNonEmptyString(String source, String fallback) {
        if (source != null && source.length() > 0) {
            return source;
        } else {
            return fallback;
        }
    }
}
