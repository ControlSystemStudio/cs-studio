package org.csstudio.dct.model.persistence.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.Record;
import org.csstudio.dct.model.internal.RecordFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Converter that transforms a xml representation back to a DCT model.
 *
 * @author Sven Wende
 *
 */
public final class XmlToProject {
    private Document document;

    /**
     * Preconditions that have to be met, before a certain xml element can be
     * converted to its model. (Key = the xml element, value = a set of ids of
     * other xml elements that have to be converted before the key element).
     */
    private Map<Element, Set<UUID>> preconditions;

    /**
     * Stores model elements that are already created. Key is the id of the
     * model element.
     */
    private Map<UUID, IElement> modelElements;

    private Project project;

    /**
     * Constructor.
     *
     * @param document
     *            the xml document
     */
    public XmlToProject(final Document document) {
        assert document != null;
        this.document = document;
    }

    /**
     * Returns the DCT project that was read from the provided xml document.
     *
     * @return a DCT project
     */
    public Project getProject() {
        modelElements = new HashMap<UUID, IElement>();
        preconditions = new HashMap<Element, Set<UUID>>();

        Element root = document.getRootElement();

        // .. determine all preconditions
        determinePreconditions(root);

        // .. create the project
        project = ProjectFactory.createExistingDCTProject(root);

        modelElements.put(project.getId(), project);
        preconditions.remove(root);

        // .. create all other model elements
        boolean goOn = true;

        while (!preconditions.isEmpty() && goOn) {
            // .. collect elements that were processed in this iteration
            List<Element> done = new ArrayList<Element>();

            // .. process elements for which all preconditions are fulfilled
            for (Element e : preconditions.keySet()) {
                Set<UUID> pre = preconditions.get(e);

                if (pre.isEmpty() || modelElements.keySet().containsAll(pre)) {
                    convertToModel(e);
                    done.add(e);
                }
            }

            if (!done.isEmpty()) {
                // .. remove all preconditions for elements that have been
                // processed in this iteration
                for (Element e : done) {
                    preconditions.remove(e);
                }
            } else {
                // .. exit, when there is no progress was no progress at all -
                // maybe the model could not be restored fully because of
                // circular dependencies between the model elements
                goOn = false;
            }
        }

        // .. finally we need to check whether the model is completely read
        if (!preconditions.isEmpty()) {
            StringBuffer sb = new StringBuffer("Could not restore model completely because of circular dependencies: ");

            for (Element e : preconditions.keySet()) {
                Set<UUID> pre = preconditions.get(e);

                sb.append(" ");
                sb.append(e.getAttributeValue("id"));
                sb.append(" depends on [");
                for (UUID p : pre) {
                    if (modelElements.get(p) == null) {
                        sb.append(p);
                        sb.append(",");
                    }
                }
                sb.append("],");
            }

            throw new RuntimeException(sb.toString());
        }

        return project;
    }

    /**
     * Converts the specified xml element to a model element.
     *
     * @param xmlElement
     *            the xml element
     */
    void convertToModel(Element xmlElement) {
        if ("folder".equals(xmlElement.getName())) {
            createFolder(xmlElement);
        } else if ("prototype".equals(xmlElement.getName())) {
            createPrototype(xmlElement);
        } else if ("instance".equals(xmlElement.getName())) {
            createInstance(xmlElement);
        } else if ("record".equals(xmlElement.getName())) {
            createRecord(xmlElement);
        } else {
            return;
        }
    }

    /**
     * Creates a folder and links it to the hierarchy.
     *
     * The xml representation for a folder looks like this:
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
     * @param xmlFolderElement
     *            the xml element
     */
    void createFolder(Element xmlFolderElement) {
        assert xmlFolderElement != null;
        assert xmlFolderElement.getName().equals("folder");
        assert modelElements.get(xmlFolderElement.getAttributeValue("parent")) == null;
        assert xmlFolderElement.getParentElement() != null;
        assert xmlFolderElement.getParentElement().getAttributeValue("id") != null;
        assert modelElements.get(UUID.fromString(xmlFolderElement.getParentElement().getAttributeValue("id"))) != null;

        // DETERMINE IDENTIFIERS
        // .. the id of the folder
        UUID id = ProjectFactory.getIdFromXml(xmlFolderElement);

        // .. the id of the model element that contains the prototype
        UUID containerId = ProjectFactory.getIdFromXml(xmlFolderElement.getParentElement());

        // CREATE FOLDER
        IFolder folder = new Folder(xmlFolderElement.getAttributeValue("name"), id);

        // ADD FOLDER TO CONTAINER
        IFolder container = getAlreadyCreatedModelElement(containerId);
        int index = xmlFolderElement.getParentElement().getChildren().indexOf(xmlFolderElement);
        container.setMember(index, folder);
        folder.setParentFolder(container);

        // MEMORIZE
        modelElements.put(id, folder);
    }

    /**
     * Creates a prototype and links it to the hierarchy.
     *
     * The xml representation for a prototype looks like this:
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
     * @param xmlPrototypeElement
     *            the xml element
     */
    @SuppressWarnings("unchecked")
    void createPrototype(Element xmlPrototypeElement) {
        assert xmlPrototypeElement != null;
        assert xmlPrototypeElement.getName().equals("prototype");
        assert modelElements.get(xmlPrototypeElement.getAttributeValue("parent")) == null;
        assert xmlPrototypeElement.getParentElement() != null;
        assert xmlPrototypeElement.getParentElement().getAttributeValue("id") != null;
        assert modelElements.get(UUID.fromString(xmlPrototypeElement.getParentElement().getAttributeValue("id"))) != null;

        // DETERMINE IDENTIFIERS
        // .. the id of the prototype
        UUID id = ProjectFactory.getIdFromXml(xmlPrototypeElement);

        // .. the id of the model element that contains the prototype
        UUID containerId = ProjectFactory.getIdFromXml(xmlPrototypeElement.getParentElement());

        // CREATE PROTOTYPE
        Prototype prototype = new Prototype(xmlPrototypeElement.getAttributeValue("name"), id);

        // ADD PROTOTYPE TO FOLDER
        IFolder folder = getAlreadyCreatedModelElement(containerId);
        assert folder != null;
        int index = xmlPrototypeElement.getParentElement().getChildren().indexOf(xmlPrototypeElement);
        folder.setMember(index, prototype);
        prototype.setParentFolder(folder);

        // READ PARAMETERS
        for (Element xmlParameterElement : (List<Element>) xmlPrototypeElement.getChildren("parameter")) {
            String name = xmlParameterElement.getAttributeValue("name");
            String value = xmlParameterElement.getAttributeValue("value");
            prototype.addParameter(new Parameter(name, value));
        }

        // READ PROPERTIES
        readProperties(prototype, xmlPrototypeElement);

        // MEMORIZE
        modelElements.put(id, prototype);
    }

    /**
     * Creates a record and links it properly to the hierarchy.
     *
     * The xml representation for a record looks like this:
     *
     * <instance name="{name}" id={id} parent="{parentId}"> <record
     * ...>...</record> ... <instance ...>...</instance> ... </instance>
     *
     * @param xmlInstanceElement
     *            the xmlElement representing the instance
     */
    @SuppressWarnings("unchecked")
    void createInstance(Element xmlInstanceElement) {
        assert xmlInstanceElement != null;
        assert xmlInstanceElement.getName().equals("instance");
        assert modelElements.get(UUID.fromString(xmlInstanceElement.getAttributeValue("parent"))) != null;
        assert xmlInstanceElement.getParentElement() != null;
        assert xmlInstanceElement.getParentElement().getAttributeValue("id") != null;
        assert modelElements.get(UUID.fromString(xmlInstanceElement.getParentElement().getAttributeValue("id"))) != null;

        // DETERMINE IDENTIFIERS
        // .. the id of the instance
        UUID id = ProjectFactory.getIdFromXml(xmlInstanceElement);
        // .. the id of the parent (another instance or a prototype)
        UUID parentId = UUID.fromString(xmlInstanceElement.getAttributeValue("parent"));
        // .. the id of the model element that contains the instance
        UUID containerId = ProjectFactory.getIdFromXml(xmlInstanceElement.getParentElement());

        // GET PARENT
        // .. can be an instance or a prototype
        IContainer parent = getAlreadyCreatedModelElement(parentId);

        // CREATE INSTANCE
        Instance instance = new Instance(parent, id);

        // .. back-link to parent
        parent.addDependentContainer(instance);

        // READ INSTANCE NAME
        String name = xmlInstanceElement.getAttributeValue("name");
        instance.setName(name.equals("{inherited}") ? null : name);

        // READ PARAMETER VALUES
        for (Element xmlParameterElement : (List<Element>) xmlInstanceElement.getChildren("parameter")) {
            String key = xmlParameterElement.getAttributeValue("name");
            String value = xmlParameterElement.getAttributeValue("value");
            instance.setParameterValue(key, value);
        }

        // ADD INSTANCE TO CONTAINER

        // .. get the container (can be a folder, prototype or an instance)
        IElement container = getAlreadyCreatedModelElement(containerId);

        assert container != null;

        if (container instanceof IFolder) {
            IFolder f = (IFolder) container;
            // .. add to folder
            int index = xmlInstanceElement.getParentElement().getChildren().indexOf(xmlInstanceElement);
            f.setMember(index, instance);
            // .. back-link
            instance.setParentFolder(f);
        } else if (container instanceof IContainer) {
            IContainer c = (IContainer) container;
            // .. add to container
            int index = xmlInstanceElement.getParentElement().getChildren("instance").indexOf(xmlInstanceElement);
            c.setInstance(index, instance);
            // .. back-link
            instance.setContainer(c);
        }

        // MEMORIZE
        modelElements.put(id, instance);
    }

    /**
     * Creates a record and links it properly to the hierarchy.
     *
     * The xml representation for a record looks like this:
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
     * @param xmlRecordElement
     *            the xmlElement representing the record
     */
    @SuppressWarnings("unchecked")
    void createRecord(Element xmlRecordElement) {
        assert xmlRecordElement != null;
        assert xmlRecordElement.getName().equals("record");
        assert xmlRecordElement.getParentElement() != null;
        assert xmlRecordElement.getParentElement().getAttributeValue("id") != null;
        assert modelElements.get(UUID.fromString(xmlRecordElement.getParentElement().getAttributeValue("id"))) != null;

        Record record = null;

        // DETERMINE IDENTIFIERS
        // .. the id of the record
        UUID id = ProjectFactory.getIdFromXml(xmlRecordElement);

        // .. the id of the model element that contains the record
        UUID containerId =ProjectFactory.getIdFromXml(xmlRecordElement.getParentElement());

        // .. the id of the parent record
        UUID parentRecordId = xmlRecordElement.getAttributeValue("parent")!=null?UUID.fromString(xmlRecordElement.getAttributeValue("parent")):null;

        // CREATE RECORD
        if (parentRecordId != null) {
            // .. inherit from parent record
            assert modelElements.get(parentRecordId) != null;
            assert modelElements.get(parentRecordId) instanceof IRecord;

            IRecord parentRecord = (IRecord) modelElements.get(parentRecordId);

            // .. parent link
            record = new Record(parentRecord, id);

            // .. parent back-link
            parentRecord.addDependentRecord(record);
        } else {
            // .. new record
            record = RecordFactory.createRecord(project, xmlRecordElement.getAttributeValue("type"), xmlRecordElement.getAttributeValue("name"), id);
            record.getParentRecord().addDependentRecord(record);
        }

        assert record != null;

        // READ NAME
        String recordName = xmlRecordElement.getAttributeValue("name");
        record.setName(recordName.equals("{inherited}") ? null : recordName);

        // READ EPICS NAME
        String epicsName = xmlRecordElement.getAttributeValue("epicsname");
        record.setEpicsName(epicsName.equals("{inherited}") ? null : epicsName);

        // READ DISABLED
        String disabled = xmlRecordElement.getAttributeValue("disabled", "{inherited}");
        record.setDisabled(disabled.equals("{inherited}") ? null : Boolean.valueOf(disabled));

        // READ FIELD INFORMATION
        for (Element xmlFieldElement : (List<Element>) xmlRecordElement.getChildren("field")) {
            String name = xmlFieldElement.getAttributeValue("name");
            String value = xmlFieldElement.getAttributeValue("value");
            record.addField(name, value);
        }

        // READ PROPERTIES
        readProperties(record, xmlRecordElement);
        for (Element xmlFieldElement : (List<Element>) xmlRecordElement.getChildren("property")) {
            String name = xmlFieldElement.getAttributeValue("name");
            String value = xmlFieldElement.getAttributeValue("value");
            record.addProperty(name, value);
        }

        // ADD RECORD TO CONTAINER
        IContainer container = getAlreadyCreatedModelElement(containerId);
        assert container != null;
        // .. add
        //FiXME: ??
        int index = xmlRecordElement.getParentElement().getChildren("record").indexOf(xmlRecordElement);
        container.setRecord(index, record);
        // .. back-link
        record.setContainer(container);

        // MEMORIZE
        modelElements.put(id, record);
    }

    /**
     * Reads properties, whoose xml representation looks like this:
     *
     * <code>
     *      ...
     *      <property name="{propertyName}" value="{propertyValue}" />
     *   ...
     * </code>
     *
     * @param container the container
     * @param xmlParentElement the xml element
     */
    @SuppressWarnings("unchecked")
    private void readProperties(IPropertyContainer container, Element xmlParentElement) {
        assert container != null;
        assert xmlParentElement != null;
        for (Element xmlPropertyElement : (List<Element>) xmlParentElement.getChildren("property")) {
            String name = xmlPropertyElement.getAttributeValue("name");
            String value = xmlPropertyElement.getAttributeValue("value");
            container.addProperty(name, value);
        }

    }

    /**
     * Returns a model element that has already been created.
     *
     * For convenience, the returned element is casted to the type of the
     * variable the result is bound to. This is not static type safe but relies
     * on guaranteed invariants of this class.
     *
     * @param <E>
     *            the expected return type
     * @param id
     *            the id of the element
     * @return the model element
     */
    @SuppressWarnings("unchecked")
    <E extends IElement> E getAlreadyCreatedModelElement(UUID id) {
        assert id != null;
        assert modelElements.containsKey(id);
        assert modelElements.get(id) != null;
        return (E) modelElements.get(id);
    }

    /**
     * The model has to be restored from xml in a certain sequence that takes
     * the complex hierarchical structure into account. This recursive method
     * collects preconditions that have to be met, before a certain model
     * element can be created. In many cases, the parents and containers of a
     * model element have to be created before the model element itself.
     *
     *
     * @param xmlElement
     *            the xml element that represents a certain model element
     */
    @SuppressWarnings("unchecked")
    void determinePreconditions(Element xmlElement) {
        // only elements with an id may have preconditions
        UUID id = ProjectFactory.getIdFromXml(xmlElement);

        if (id != null) {
            Set<UUID> conditions = new HashSet<UUID>();

            // .. the id of the container (in nested structures, parents have to
            // be
            // created before children in any case)
            if (xmlElement.getParentElement() != null) {
                UUID containerId = ProjectFactory.getIdFromXml(xmlElement.getParentElement());
                assert containerId != null;
                conditions.add(containerId);
            }

            // .. the id of a hierarchical parent (e.g. a parent record in case
            // of records)
            String parentId = xmlElement.getAttributeValue("parent");
            if (parentId != null) {
                conditions.add(UUID.fromString(parentId));
            }

            // .. prototypes may have special preconditions
            if ("prototype".equals(xmlElement.getName())) {
                determinePreConditionsForPrototypes(xmlElement, conditions);
            }
            this.preconditions.put(xmlElement, conditions);
        }

        // traverse sub elements
        for (Element subE : (List<Element>) xmlElement.getChildren()) {
            determinePreconditions(subE);
        }
    }

    /**
     * Recursive method which determines the preconditions for a prototype.
     *
     * @param xmlElement
     *            an xml element representing a prototype or instance
     *
     * @param conditions
     *            the conditions
     */
    @SuppressWarnings("unchecked")
    void determinePreConditionsForPrototypes(Element xmlElement, Set<UUID> conditions) {
        assert xmlElement != null;
        assert xmlElement.getName().equals("prototype") || xmlElement.getName().equals("instance");
        assert conditions != null;
        for (Element instance : (List<Element>) xmlElement.getChildren("instance")) {
            UUID parentId = UUID.fromString(instance.getAttributeValue("parent"));
            conditions.add(parentId);
            determinePreConditionsForPrototypes(instance, conditions);
        }
    }

//    private UUID getId(Object element) {
//        assert element != null;
//        assert modelElements.containsValue(element) : "modelElements.containsValue(element)";
//
//        for (UUID id : modelElements.keySet()) {
//            if (modelElements.get(id) == element) {
//                return  id;
//            }
//        }
//
//        throw new IllegalArgumentException("Element is not created yet.");
//    }

}
