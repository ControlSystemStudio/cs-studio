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
package org.csstudio.utility.adlconverter.ui;

import java.io.File;
import java.util.Formatter;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.importer.AbstractDisplayImporter;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.persistence.DisplayModelInputStream;
import org.csstudio.sds.model.persistence.PersistenceUtil;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.ParserADL;
import org.csstudio.utility.adlconverter.utility.widgets.ADLDisplay;
import org.csstudio.utility.adlconverter.utility.widgets.ActionButton;
import org.csstudio.utility.adlconverter.utility.widgets.Arc;
import org.csstudio.utility.adlconverter.utility.widgets.Bargraph;
import org.csstudio.utility.adlconverter.utility.widgets.Ellipse;
import org.csstudio.utility.adlconverter.utility.widgets.GroupingContainer;
import org.csstudio.utility.adlconverter.utility.widgets.Image;
import org.csstudio.utility.adlconverter.utility.widgets.Label;
import org.csstudio.utility.adlconverter.utility.widgets.Meter;
import org.csstudio.utility.adlconverter.utility.widgets.Polygon;
import org.csstudio.utility.adlconverter.utility.widgets.Polyline;
import org.csstudio.utility.adlconverter.utility.widgets.Rectangle;
import org.csstudio.utility.adlconverter.utility.widgets.RelatedDisplay;
import org.csstudio.utility.adlconverter.utility.widgets.Symbol;
import org.csstudio.utility.adlconverter.utility.widgets.Textinput;
import org.csstudio.utility.adlconverter.utility.widgets.Valuator;
import org.csstudio.utility.adlconverter.utility.widgets.Waveform;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 22.10.2007
 */
public class ADLDisplayImporter extends AbstractDisplayImporter {
    /**
     * Is true when the Color map ready finalize.
     */
    private boolean _colormapSet;
    /**
     * Is true when the Display ready finalize.
     */
    private boolean _displaySave;
    /**
     * Store the Display to finalize if the colormap not finalize.
     */
    private ADLWidget _storDisplay;
    private int _status;

    /**
     * Default Constructor.
     */
    public ADLDisplayImporter() {
    }

    /**
     * {@inheritDoc}
     * 
     * @throws CoreException
     */
    @Override
    public final boolean importDisplay(final String sourceFile, final IPath targetProject,
            final String targetFileName) throws CoreException {

        _status=0;
        ADLWidget root = ParserADL.getNextElement(new File(sourceFile));

        // this is the target display model
        DisplayModel displayModel = new DisplayModel();
        _colormapSet = false;
        _displaySave = false;
        _storDisplay = null;
        displayModel.getLayerSupport().addLayer(
                new Layer(Messages.ADLDisplayImporter_ADLBackgroundLayerName,
                        Messages.ADLDisplayImporter_ADLBackgroundLayerDes), 0);
        displayModel.getLayerSupport().addLayer(
                new Layer(Messages.ADLDisplayImporter_ADLBargraphLayerName,
                        Messages.ADLDisplayImporter_ADLBargraphLayerDes), 2);
        displayModel.getLayerSupport().addLayer(
                new Layer(Messages.ADLDisplayImporter_ADLActionLayerName,
                        Messages.ADLDisplayImporter_ADLActionLayerDes), 2);
        for (ADLWidget strings : root.getObjects()) {
            try {
                // Display
                display(strings, displayModel);
                if (strings.getType().equals("arc")) { //$NON-NLS-1$
                    displayModel.addWidget(new Arc(strings, displayModel).getElement());
                } else if (strings.getType().equals("bar")) { //$NON-NLS-1$
                    displayModel.addWidget(new Bargraph(strings).getElement());
                } else if (strings.getType().equals("\"cartesian plot\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new Waveform(strings).getElement());
                } else if (strings.getType().equals("\"color map\"")) { //$NON-NLS-1$
                    ADLHelper.setColorMap(strings);
                    _colormapSet = true;
                } else if (strings.getType().equals("composite")) { //$NON-NLS-1$
                    displayModel.addWidget(new GroupingContainer(strings).getElement());
                } else if (strings.getType().equals("\"dynamic symbol\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new Symbol(strings).getElement());
                } else if (strings.getType().equals("file")) { //$NON-NLS-1$
                    // TODO: FILE --> Name and Version
                } else if (strings.getType().equals("image")) { //$NON-NLS-1$
                    displayModel.addWidget(new Image(strings, displayModel).getElement());
                } else if (strings.getType().equals("indicator")) { //$NON-NLS-1$
                    displayModel.addWidget(new Bargraph(strings).getElement());
                } else if (strings.getType().equals("menu")) { //$NON-NLS-1$
                    displayModel.addWidget(new RelatedDisplay(strings).getElement());
                } else if (strings.getType().equals("\"message button\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new ActionButton(strings).getElement());
                } else if (strings.getType().equals("meter")) { //$NON-NLS-1$
                    displayModel.addWidget(new Meter(strings).getElement());
                } else if (strings.getType().equals("oval")) { //$NON-NLS-1$
                    displayModel.addWidget(new Ellipse(strings, displayModel).getElement());
                } else if (strings.getType().equals("polygon")) { //$NON-NLS-1$
                    displayModel.addWidget(new Polygon(strings, displayModel).getElement());
                } else if (strings.getType().equals("polyline")) { //$NON-NLS-1$
                    Polyline polyline = new Polyline(strings, displayModel);
                    displayModel.addWidget(polyline.getElement());
                    polyline = null;
                } else if (strings.getType().equals("rectangle")) { //$NON-NLS-1$
                    displayModel.addWidget(new Rectangle(strings, displayModel).getElement());
                } else if (strings.getType().equals("\"related display\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new RelatedDisplay(strings).getElement());
                } else if (strings.getType().equals("\"strip chart\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new Waveform(strings).getElement());
                } else if (strings.getType().equals("text")) { //$NON-NLS-1$
                    displayModel.addWidget(new Label(strings).getElement());
                } else if (strings.getType().equals("\"text update\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new Label(strings).getElement());
                } else if (strings.getType().equals("\"text entry\"")) { //$NON-NLS-1$
                    displayModel.addWidget(new Textinput(strings).getElement());
                } else if (strings.getType().equals("valuator")) { //$NON-NLS-1$
                    displayModel.addWidget(new Valuator(strings).getElement());
                } else {
                    CentralLogger.getInstance().warn(this,
                            Messages.ADLDisplayImporter_WARN_UNHANDLED_TYPE + strings.getType());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DisplayModelInputStream.setXMLHeader("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"); //$NON-NLS-1$
        DisplayModelInputStream modelInputStream = (DisplayModelInputStream) PersistenceUtil
                .createStream(displayModel);

        // create the target file in the workspace
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath filePath = targetProject.append(targetFileName);
        if (!workspaceRoot.exists(targetProject)) {
            String[] dialogButtonsText = new String[] {
                    Messages.ADLDisplayImporter_Dialog_Yes_Button,
                    Messages.ADLDisplayImporter_Dialog_No_Button,
                    "cancel"};
            Formatter f = new Formatter();
            f.format("Dir \"%s\" not exist!\r\nCreat this Folder?", targetProject);

            MessageDialog md = new MessageDialog(Display.getCurrent().getActiveShell(),
                    Messages.ADLDisplayImporter_Dialog_Header_Directory_not_exist, null, f
                            .toString(), MessageDialog.WARNING, dialogButtonsText, 0);
            
            switch(md.open()){
                case 0:
                    IFolder folder = workspaceRoot.getFolder(targetProject);
                    createFolder(folder);
                    break;
                case 1:
                    _status=1;
                    return false;
                case 2:
                    _status=2;
                    return false;
                default:
                    _status=3;
                    return false;
            }
        }
        IFile fileOut = workspaceRoot.getFile(filePath);

        if (fileOut.exists()) {
            fileOut.setContents(modelInputStream, true, false, null);
        } else {
            fileOut.create(modelInputStream, true, null);
        }
        return true;
    }

    /**
     * Generate a Folder and parent folder.
     * 
     * @param folder the lowest folder in the tree to generated
     * @throws CoreException is a OperationCanceledException
     *             if the operation is canceled. Cancelation can occur even if
     *             no progress monitor is provided.
     * @see IFolder#create(int,boolean,IProgressMonitor)
     */
    private void createFolder(final IFolder folder) throws CoreException {
        if (!folder.getParent().exists()) {
            if (folder.getParent() instanceof IFolder) {
                createFolder((IFolder) folder.getParent());
            }
        }
        folder.create(true, true, null);

    }

    /**
     * @param adlWidget
     *            the Main display widget.
     * @param root
     *            the root Element of css-sds file.
     */
    private void display(final ADLWidget adlWidget, final DisplayModel root) {

        if (adlWidget.getType().startsWith("display") || _displaySave) { //$NON-NLS-1$

            try {
                if (adlWidget.getType().startsWith("display") && !_colormapSet && !_displaySave) { //$NON-NLS-1$
                    _storDisplay = adlWidget;
                    _displaySave = true;
                } else if (!adlWidget.getType().startsWith("display") && _colormapSet && _displaySave) { //$NON-NLS-1$
                    new ADLDisplay(_storDisplay, root);
                    _displaySave = false;
                } else if (adlWidget.getType().startsWith("display") && _colormapSet && !_displaySave) { //$NON-NLS-1$
                    new ADLDisplay(adlWidget, root);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @return
     */
    public int getStatus() {
        return _status;
    }

}
