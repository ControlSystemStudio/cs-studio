/**
 *
 */
package org.csstudio.dct.model.persistence.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ProjectToXml}.
 *
 * @author Sven Wende
 *
 */
public final class ProjectToXmlTest {
    private Project project;

    /**
     * Setup.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // project = UseCase.createProject();

        project = ProjectFactory.createNewDCTProject();

        final IFolder f1 = new Folder("f1");
        project.addMember(f1);
        f1.setParentFolder(project);

        final IPrototype p1 = new Prototype("p1", UUID.randomUUID());
        new AddPrototypeCommand(f1, p1).execute();
        final IRecord r1 = RecordFactory.createRecord(project, "ai", "r1", UUID.randomUUID());
        new AddRecordCommand(p1, r1).execute();

        final IInstance i11 = new Instance(p1, UUID.randomUUID());
        new AddInstanceCommand(f1, i11).execute();

        final IPrototype p2 = new Prototype("p2", UUID.randomUUID());
        new AddPrototypeCommand(f1, p2).execute();
        final IRecord r2 = RecordFactory.createRecord(project, "ai", "r2", UUID.randomUUID());
        new AddRecordCommand(p2, r2).execute();

        final IInstance i2 = new Instance(p2, UUID.randomUUID());
        new AddInstanceCommand(p1, i2).execute();

        final IFolder f2 = new Folder("f2");
        project.addMember(f2);
        f2.setParentFolder(project);

        final IInstance i12 = new Instance(p1, UUID.randomUUID());
        new AddInstanceCommand(f2, i12).execute();

        System.err.println(p1.hashCode());
        System.err.println(p2.hashCode());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.persistence.internal.ProjectToXml#createDocument(org.csstudio.dct.model.internal.Project)}
     * .
     *
     * @throws IOException
     */
    @Test
    public void testCreateDocument() throws IOException {
        final ProjectToXml service = new ProjectToXml(project);

        final XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());

        // .. convert to XML document
        final Document document = service.createDocument();

        outp.output(document, System.out);

        // .. convert back to model
        final XmlToProject xmlToProject = new XmlToProject(document);
        final Project newProject = xmlToProject.getProject();

        counter = 0;
        traverseElement(project);
        System.out.println("-------------------");
        counter = 0;
        traverseElement(newProject);

        // .. verify
        assertEquals(project, newProject);

    }

    private static Element traverseElement(final IElement element) {
        final Element result = null;

        if (element instanceof IFolder) {
            traverseElement((IFolder) element);
        } else if (element instanceof IPrototype) {
            traverseElement((IPrototype) element);
        } else if (element instanceof IInstance) {
            traverseElement((IInstance) element);
        } else if (element instanceof IRecord) {
            traverseElement((IRecord) element);
        } else {
            throw new IllegalAccessError("Unsupported type:" + element.getClass());
        }

        return result;
    }

    private static void traverseElement(final IFolder folder) {
        for (final IFolderMember m : folder.getMembers()) {
            traverseElement(m);
        }
        printline("F-ID", folder.getId());
        printline("F-Name", folder.getName());
    }

    private static void traverseElement(final IPrototype prototype) {
        for (final IInstance i : prototype.getInstances()) {
            traverseElement(i);
        }
        for (final IRecord r : prototype.getRecords()) {
            traverseElement(r);
        }

        printline("P-ID", prototype.getId());
        printline("P-Name", prototype.getName());
        printline("P-Properties", prototype.getProperties());
        printline("P-Parameters", prototype.getParameters());
        printline("P-Folder", prototype.getParentFolder());
    }

    private static void traverseElement(final IInstance instance) {
        for (final IInstance i : instance.getInstances()) {
            traverseElement(i);
        }
        for (final IRecord r : instance.getRecords()) {
            traverseElement(r);
        }

        printline("I-ID", instance.getId());
        printline("I-Name", instance.getName());
        printline("I-Properties", instance.getProperties());
        printline("I-ParameterValues", instance.getParameterValues());
        printline("I-Container", instance.getContainer());
        printline("I-Folder", instance.getParentFolder());
    }

    private static void traverseElement(final IRecord record) {
        printline("R-ID", record.getId());
        printline("R-Parent", record.getParentRecord());
        printline("R-Name", record.getName());
        printline("R-Type", record.getType());
        printline("R-Fields", record.getFields());
        printline("R-Properties", record.getProperties());
    }

    private static int counter = 0;

    private static void printline(final String text, final Object o) {
        System.out.println(counter + " :" + (o != null ? o.hashCode() : "null") + " (" + text + ")");
        counter++;
    }
}
