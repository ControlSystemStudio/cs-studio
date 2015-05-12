package org.csstudio.dct.model.internal;

import java.util.UUID;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.commands.ChangeDbdFileCommand;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.jdom.Element;


public class ProjectFactory {

    private static final IPreferencesService prefs = Platform.getPreferencesService();
    private static final String DEFAULT_DCTPROJECT_NAME = "Neu";
    private static final String DEFAULT_DBDPATH = "";
    private static final String DEFAULT_IOC = "";
    private static final String DEFAULT_DCTEXTENSION = "css-dct";
    private static final String DEFAULT_DCTPROJECT_NAME_KEY = "DEFAULT_DCTPROJECT_NAME";
    private static final String DEFAULT_DBDPATH_KEY = "DEFAULT_DBDPATH";
    private static final String DEFAULT_IOC_KEY = "DEFAULT_IOC";
    private static final String DEFAULT_DCTEXTENSION_KEY = "DEFAULT_DCTEXTENSION";

    public static String getDctFileExtension() {
        return prefs.getString(DctActivator.PLUGIN_ID, DEFAULT_DCTEXTENSION_KEY, DEFAULT_DCTEXTENSION, null);
    }

    private static String getPrefValue(String key, String defaultValue) {
        if (prefs == null) return defaultValue;
        return prefs.getString(DctActivator.PLUGIN_ID, key, defaultValue, null);
    }

    public static Project createNewDCTProject() {
        return createNewDCTProject(null);
    }

    public static Project createNewDCTProjectFile(String fileName) {
        String name = getPrefValue(DEFAULT_DCTPROJECT_NAME_KEY, DEFAULT_DCTPROJECT_NAME);
        if (fileName != null && "<filename>".equals(name)) {
            String ext = "." + getDctFileExtension();
            name = fileName.endsWith(ext) ? fileName.substring(0, fileName.length() - ext.length()) : fileName;
        }
        return createNewDCTProject(name);
    }

    public static Project createNewDCTProject(String name) {
        if (name == null) name = getPrefValue(DEFAULT_DCTPROJECT_NAME_KEY, DEFAULT_DCTPROJECT_NAME);
        Project project = new Project(name, UUID.randomUUID());
        project.setIoc(getPrefValue(DEFAULT_IOC_KEY, DEFAULT_IOC));
        new ChangeDbdFileCommand(project, getPrefValue(DEFAULT_DBDPATH_KEY, DEFAULT_DBDPATH)).execute();
        return project;
    }

    public static Project createExistingDCTProject(Element root) {
        Project project = new Project(root.getAttributeValue("name"), getIdFromXml(root));
        project.setIoc(root.getAttributeValue("ioc", ""));
        new ChangeDbdFileCommand(project, root.getAttributeValue("dbd")).execute();
        return project;
    }

    public static UUID getIdFromXml(Element xmlElement) {
        String id = xmlElement.getAttributeValue("id");
        UUID uuid = id!=null?UUID.fromString(id):null;
        return uuid;
    }

}
