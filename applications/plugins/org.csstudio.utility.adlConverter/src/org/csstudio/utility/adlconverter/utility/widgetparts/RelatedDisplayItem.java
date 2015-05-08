/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility.widgetparts;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.model.AbstractChartModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionModel;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionModelFactory;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.09.2007
 */
public class RelatedDisplayItem extends WidgetPart {

    private static final Logger LOG = LoggerFactory.getLogger(RelatedDisplayItem.class);

    /**
     * The Button Label Text.
     */
    private String _label;
    /**
     * The display to open.
     */
    private String _name;
    /**
     * the record for the new Display.
     */
    private String[] _args;
    /**
     * The root path for Widget.
     */
    private String _path;
    private String _policy;

    /**
     * The default constructor.
     *
     * @param display
     *            An ADLWidget that correspond a ADL Related Display Item.
     * @param parentWidgetModel
     *            The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException
     *             Wrong ADL format or untreated parameter found.
     */
    public RelatedDisplayItem(final ADLWidget display, final AbstractWidgetModel parentWidgetModel)
            throws WrongADLFormatException {
        super(display, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void init() {
        //_path = Activator.getDefault().getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
    }


    final String checkPath(String path, String name){
        path = path.trim();
        if(path.endsWith("/"))
            path = path.substring(0, path.length()-1);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        // Both file formats are checked, to ensure that if multiple displays are being
        // converted out of order, this method doesn't break by failing to find
        // the yet to be converted <filename>.adl display.
        File file1 = new File(path + "/" + name + ".adl");
        File file2 = new File(path + "/" + name + ".css-sds");
        Path path1 = new Path(path + "/" + name + ".adl");
        Path path2 = new Path(path + "/" + name + ".css-sds");

        if(file1.exists() || file2.exists())
            return path.replaceAll(root.getRawLocation().toString(), "");

        if(root.exists(path1) || root.exists(path2))
            return root.getFullPath().toString();

        return null;
    }


    /**
     * First checks the parent path of the calling display, then the workspace path, then
     * each of the display paths provided in the preferences for the source file
     * of this display.  Returns the FIRST directory where the display exists.
     */
    final String findPath() {
        // At this point, _name = <filename>.css-sds.
        String name = _name.replaceAll(".css-sds", "");
        String parent = ADLHelper.getFolderPath();
        String path = null;
        String allpaths = Activator.getDefault().getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Display_Paths);
        String[] displaypaths = allpaths.split(",");
        if(name.startsWith("/"))
            name = name.substring(1,name.length());

        path = checkPath(parent, name);
        if(path != null)
            return path;

        path = checkPath("", name);
        if(path != null)
            return path;

        for(String dpath : displaypaths){
            path = checkPath(dpath, name);
            if(path != null)
                return path;
        }

        // Return the default if nothing else is found
        return Activator.getDefault().getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget display) throws WrongADLFormatException {
        // assert !display.isType("display[n]") :
        // "This "+display.getType()+" is not a ADL displayItem";

        for (FileLine fileLine : display.getBody()) {
            String parameter = fileLine.getLine();
            if (parameter.trim().startsWith("//")) { //$NON-NLS-1$
                continue;
            }
            String head = parameter.split("=")[0]; //$NON-NLS-1$
            String tmp = "";
            try {
                tmp = parameter.substring(head.length() + 1);
            } catch (StringIndexOutOfBoundsException exp) {
                throw new WrongADLFormatException(
                        Messages.RelatedDisplayItem_WrongADLFormatException_Begin + head
                                + Messages.RelatedDisplayItem_WrongADLFormatException_Middle
                                + fileLine + "(" + display.getObjectNr() + ":" + display.getType()
                                + ")[" + parameter + "]");
            }
            head = head.trim().toLowerCase();
            /* XXX: Wenn es noch mal Probleme geben sollte mit Punkten im Dateinamen, sollte ein
             *  eigener String-Cleaner für Pfade gemacht werden! Dafür muss das clean String in den
             *  'if else block' und 'name' würde dann den neuen String-Path-Cleaner aufrufen.
             */
            DebugHelper.add(this, head+" : "+tmp);
            String[] row = ADLHelper.cleanString(tmp);
            if (head.equals("label")) { //$NON-NLS-1$
                _label = row[0];
            } else if (head.equals("name")) { //$NON-NLS-1$
                _name = row[0];
            } else if (head.equals("args")) { //$NON-NLS-1$
                _args = row;
//                _args = Arrays.copyOf(row, row.length+1);
//                _args[_args.length-1]=fileLine.toString();
            } else if (head.equals("policy")) { //$NON-NLS-1$
                _policy = row[0];
            } else if (head.equals("x")) { //$NON-NLS-1$
                // Do Nothing
                // SDS not support this Property
            } else if (head.equals("y")) { //$NON-NLS-1$
                // Do Nothing
                // SDS not support this Property
            } else if (head.equals("width")) { //$NON-NLS-1$
                // Do Nothing
                // SDS not support this Property
            } else if (head.equals("height")) { //$NON-NLS-1$
                // Do Nothing
                // SDS not support this Property
            } else {
                throw new WrongADLFormatException(
                        Messages.RelatedDisplayItem_WrongADLFormatException_Begin + head
                                + Messages.RelatedDisplayItem_WrongADLFormatException_Middle
                                + fileLine + "(" + display.getObjectNr() + ":" + display.getType()
                                + ")");
            }
        }
    }

    /**
     * Generate all Elements from Related Display Item.
     */
    @Override
    final void generateElements() {
        _widgetModel.setPropertyValue(AbstractChartModel.PROP_LABELED_TICKS, true);

        ActionData actionData = _widgetModel.getActionData();
        if (actionData == null) {
            actionData = new ActionData();
        }

        // new Open Shell Action
        OpenDisplayActionModelFactory factoy = new OpenDisplayActionModelFactory();
        OpenDisplayActionModel action = (OpenDisplayActionModel) factoy.createWidgetActionModel();

        if (_label != null) {
            action.getProperty(OpenDisplayActionModel.PROP_DESCRIPTION).setPropertyValue(
                    _label.replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Set the Resource
        if (_name != null) {
            _name = ADLHelper.cleanFilePath(_name);
            if (!_name.toLowerCase().endsWith(".css-sds")) {
                _name = _name.concat(".css-sds");
            }
            ////////////////////////////
            _path = findPath();
            ////////////////////////////
            IPath path = new Path(_path);
            path = path.append(_name);
            WidgetProperty prop = action.getProperty(OpenDisplayActionModel.PROP_RESOURCE);
            prop.setPropertyValue(path);
        }

        if (_args != null) {
            Map<String, String> map = new HashMap<String, String>();
            String[] params = _args[0].split(",");//$NON-NLS-1$
            for (int i = 0; i < params.length; i++) {

                if (params[i].contains("=")) {
                    String[] param = params[i].split("=");//$NON-NLS-1$
                    if (param.length  >1) {
                        map.put(param[0].trim(), param[1].trim());
                    }else{
                        map.put(param[0].trim(), "");
                    }
                } else {
                    if (params[i].trim().length() > 0) {
                        LOG.info( Messages.RelatedDisplayItem_Parameter_Error+" ~ line: ", params[i],_args[_args.length-1]);
                    }
                }
            }

            action.getProperty(OpenDisplayActionModel.PROP_ALIASES).setPropertyValue(map);
        }

        if (_policy != null) {
            action.getProperty(OpenDisplayActionModel.PROP_CLOSE).setPropertyValue(
                    _policy.contains("replace display"));
        }
        actionData.addAction(action);
        _widgetModel.setPropertyValue(AbstractWidgetModel.PROP_ACTIONDATA, actionData);
    }

    /**
     *
     * @return the Label of the Related Display Item.
     */
    public final String getLabel() {
        return _label;
    }
}
