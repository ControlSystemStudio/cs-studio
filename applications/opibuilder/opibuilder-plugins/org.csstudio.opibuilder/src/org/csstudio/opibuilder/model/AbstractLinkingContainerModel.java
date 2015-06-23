package org.csstudio.opibuilder.model;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.GraphicalViewer;

/**The abstract base model for LinkingContainer widgets.
 */
public abstract class AbstractLinkingContainerModel extends AbstractContainerModel {

    /**
     * The ID of the resource property.
     */
    public static final String PROP_OPI_FILE = "opi_file"; //$NON-NLS-1$

    /**
     * The name of the group container widget in the OPI file, which
     * will be loaded if it is specified. If it is not specified, the whole
     * OPI file will be loaded.
     */
    public static final String PROP_GROUP_NAME = "group_name"; //$NON-NLS-1$



    /**
     * The display Scale options of the embedded OPI.
     */
    private DisplayModel displayModel = null;

    @Override
    protected void configureBaseProperties() {
        super.configureBaseProperties();

        addProperty(new FilePathProperty(PROP_OPI_FILE, "OPI File",
                WidgetPropertyCategory.Behavior, new Path(""), //$NON-NLS-1$
                new String[] { OPIBuilderPlugin.OPI_FILE_EXTENSION}));

        addProperty(new StringProperty(PROP_GROUP_NAME, "Group Name",
                WidgetPropertyCategory.Behavior, "")); //$NON-NLS-1$
    }

    public String getGroupName(){
        return (String)getPropertyValue(PROP_GROUP_NAME);
    }

    /**
     * Return the target resource.
     *
     * @return The target resource.
     */
    public IPath getOPIFilePath() {
        IPath absolutePath = (IPath) getProperty(PROP_OPI_FILE).getPropertyValue();
        if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
            absolutePath = ResourceUtil.buildAbsolutePath(this, absolutePath);
        return absolutePath;
    }

    public void setOPIFilePath(String path){
        setPropertyValue(PROP_OPI_FILE, new Path(path));
    }

    /**Set the display model of the loaded opi.
     * @param displayModel
     */
    public synchronized void setDisplayModel(DisplayModel displayModel) {
        this.displayModel = displayModel;
    }

    /**
     * @return display model of the loaded opi. null if no opi has been loaded.
     */
    public synchronized DisplayModel getDisplayModel() {
        return displayModel;
    }

    public synchronized void setDisplayModelViewer(GraphicalViewer viewer) {
        this.displayModel.setViewer(viewer);
    }

    public synchronized void setDisplayModelDisplayID(int displayID){
        this.displayModel.setDisplayID(displayID);
    }

    public synchronized void setDisplayModelExecutionMode(ExecutionMode executionMode) {
        this.displayModel.setExecutionMode(executionMode);
    }

    public synchronized void setDisplayModelOpiRuntime(IOPIRuntime opiRuntime) {
        this.displayModel.setOpiRuntime(opiRuntime);
    }
}
