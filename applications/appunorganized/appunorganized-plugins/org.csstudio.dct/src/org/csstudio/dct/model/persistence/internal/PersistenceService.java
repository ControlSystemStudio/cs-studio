package org.csstudio.dct.model.persistence.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

import org.csstudio.dct.ExtensionPointUtil;
import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.metamodel.PromptGroup;
import org.csstudio.dct.metamodel.internal.Choice;
import org.csstudio.dct.metamodel.internal.DatabaseDefinition;
import org.csstudio.dct.metamodel.internal.FieldDefinition;
import org.csstudio.dct.metamodel.internal.MenuDefinition;
import org.csstudio.dct.metamodel.internal.RecordDefinition;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.persistence.IPersistenceService;
import org.csstudio.dct.model.visitors.RecordFunctionPropertiesVisitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.ConsoleInterface;
import com.cosylab.vdct.dbd.DBDData;
import com.cosylab.vdct.dbd.DBDFieldData;
import com.cosylab.vdct.dbd.DBDMenuData;
import com.cosylab.vdct.dbd.DBDRecordData;
import com.cosylab.vdct.dbd.DBDResolver;

/**
 * Default persistence service implementation.
 *
 * @author Sven Wende
 *
 */
public final class PersistenceService implements IPersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceService.class);

    /**
     *{@inheritDoc}
     */
    @Override
    public void saveProject(IFile file, Project project) throws Exception {
        file.setContents(getAsStream(project), true, false, new NullProgressMonitor());
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public InputStream getAsStream(Project project) throws Exception {
        Format format = Format.getPrettyFormat();
//        format.setEncoding("ISO-8859-1");

        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(format);
        ProjectToXml projectToXml = new ProjectToXml(project);

        Document doc = projectToXml.createDocument();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        outp.output(doc, bos);

        return new ByteArrayInputStream(bos.toByteArray());

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Project loadProject(IFile file) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file.getContents());

        XmlToProject xmlToProject = new XmlToProject(doc);

        Project p = xmlToProject.getProject();

        p.accept(new RecordFunctionPropertiesVisitor(ExtensionPointUtil.getRecordAttributes()));

        return p;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IDatabaseDefinition loadDatabaseDefinition(String path) {
        IDatabaseDefinition result = null;

        if (path != null && path.length() > 0) {

            // .. file system search
            File file = new File(path);

            if (file.exists()) {
                result = doLoadDatabaseDefinition(file.getAbsolutePath());
            }

            // .. workspace search
            if (result == null) {
                IFile workspaceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));

                if (workspaceFile != null && workspaceFile.getLocation() != null) {
                    result = doLoadDatabaseDefinition(workspaceFile.getLocation().toOSString());
                }
            }
        }

        return result;
    }

    /**
     * Loads a database definition from a dbd file using an API of the VDCT
     * software.
     *
     * @param path
     *            the absolute path to the dbd file
     * @return the database definition (meta model)
     */
    @SuppressWarnings( { "unchecked" })
    private IDatabaseDefinition doLoadDatabaseDefinition(String path) {
        DatabaseDefinition databaseDefinition = new DatabaseDefinition("1.0");

        // .. redirect the VDCT console
        Console.setInstance(new ConsoleInterface() {

            @Override
            public void flush() {

            }

            @Override
            public void print(String text) {
                LOG.info(text);
            }

            @Override
            public void println() {
                LOG.info("\r\n");
            }

            @Override
            public void println(String text) {
                LOG.info(text);
            }

            @Override
            public void println(Throwable thr) {
                LOG.error("",thr);
            }

            @Override
            public void silent(String text) {
                LOG.debug(text);
            }

        });

        // .. use the VDCT parser
        DBDData data = new DBDData();
        DBDResolver.resolveDBD(data, path);

        // .. transform the VDCT dbd model to our CSS-DCTs meta model

        // .. menus

        // .. records

        Enumeration<String> it = data.getRecordNames();
        while (it.hasMoreElements()) {
            String n = it.nextElement();
            DBDRecordData recordData = data.getDBDRecordData(n);

            RecordDefinition recordDefinition = new RecordDefinition(n);
            databaseDefinition.addRecordDefinition(recordDefinition);

            Iterator<DBDFieldData> it2 = recordData.getFieldsV().iterator();

            while (it2.hasNext()) {
                DBDFieldData fieldData = it2.next();
                String fieldName = fieldData.getName();
                LOG.info(fieldName);

                FieldDefinition fieldDefinition = new FieldDefinition(fieldName, DBDResolver.getFieldType(fieldData.getField_type()));

                // .. prompt group
                fieldDefinition.setPromptGroup(PromptGroup.findByType(fieldData.getGUI_type()));

                // .. prompt
                fieldDefinition.setPrompt(fieldData.getPrompt_value());

                // .. menu
                String menuName = fieldData.getMenu_name();

                if (menuName != null && menuName.length() > 0) {
                    DBDMenuData menuData = data.getDBDMenuData(menuName);

                    if (menuData != null) {
                        MenuDefinition menuDefinition = new MenuDefinition(menuName);

                        for (Choice c : menuData.getChoicesForCssDct()) {
                            menuDefinition.addChoice(c);
                        }

                        fieldDefinition.setMenuDefinition(menuDefinition);
                    }
                }

                // .. initial value
                fieldDefinition.setInitial(fieldData.getInit_value());

                recordDefinition.addFieldDefinition(fieldDefinition);
            }
        }

        return databaseDefinition;
    }
}
