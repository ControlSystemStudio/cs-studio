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
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;

import org.csstudio.sds.importer.AbstractDisplayImporter;
import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.internal.persistence.DisplayModelInputStream;
import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.ParserADL;
import org.csstudio.utility.adlconverter.utility.faceplate.FaceplateParser;
import org.csstudio.utility.adlconverter.utility.striptool.StripTool;
import org.csstudio.utility.adlconverter.utility.striptool.StripToolParser;
import org.csstudio.utility.adlconverter.utility.widgets.ADLDisplay;
import org.csstudio.utility.adlconverter.utility.widgets.ActionButton;
import org.csstudio.utility.adlconverter.utility.widgets.Arc;
import org.csstudio.utility.adlconverter.utility.widgets.Bargraph;
import org.csstudio.utility.adlconverter.utility.widgets.ChoiceButton;
import org.csstudio.utility.adlconverter.utility.widgets.Ellipse;
import org.csstudio.utility.adlconverter.utility.widgets.GroupingContainer;
import org.csstudio.utility.adlconverter.utility.widgets.Image;
import org.csstudio.utility.adlconverter.utility.widgets.Label;
import org.csstudio.utility.adlconverter.utility.widgets.Meter;
import org.csstudio.utility.adlconverter.utility.widgets.Polygon;
import org.csstudio.utility.adlconverter.utility.widgets.Polyline;
import org.csstudio.utility.adlconverter.utility.widgets.Rectangle;
import org.csstudio.utility.adlconverter.utility.widgets.RelatedDisplay;
import org.csstudio.utility.adlconverter.utility.widgets.SixteenBinaryBar;
import org.csstudio.utility.adlconverter.utility.widgets.StripChart;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 22.10.2007
 */
public class ADLDisplayImporter extends AbstractDisplayImporter {

    private static final Logger LOG = LoggerFactory.getLogger(ADLDisplayImporter.class);
    /**
     * Store the Display to finalize if the colormap not finalize.
     */
    private ADLWidget _storDisplay;
    private int _status;
    private static boolean _createFolderNever = false;
    private static boolean _createFolderAllways = false;

    /**
     * Default Constructor.
     */
    public ADLDisplayImporter() {
        // Default Constructor.
    }

    /**
     * {@inheritDoc}
     *
     * @throws CoreException
     */
    @Override
    public final boolean importDisplay(final String sourceFile,
                                       final IPath targetProject,
                                       final String targetFileName) throws CoreException {

        _status = 0;
        ADLWidget storedBasicAttribute = null;
        ADLWidget storedDynamicAttribute = null;
        ADLWidget root = ParserADL.getNextElement(new File(sourceFile));

        // this is the target display model
        DisplayModel displayModel = new DisplayModel();
        _storDisplay = null;
        displayModel.getLayerSupport()
                .addLayer(new Layer(Messages.ADLDisplayImporter_ADLBackgroundLayerName,
                                    Messages.ADLDisplayImporter_ADLBackgroundLayerDes),
                          0);
        displayModel.getLayerSupport()
                .addLayer(new Layer(Messages.ADLDisplayImporter_ADLDynamicLayerName,
                                    Messages.ADLDisplayImporter_ADLDynamicLayerDes),
                          2);
        displayModel.getLayerSupport()
                .addLayer(new Layer(Messages.ADLDisplayImporter_ADLBargraphLayerName,
                                    Messages.ADLDisplayImporter_ADLBargraphLayerDes),
                          3);
        displayModel.getLayerSupport()
                .addLayer(new Layer(Messages.ADLDisplayImporter_ADLActionLayerName,
                                    Messages.ADLDisplayImporter_ADLActionLayerDes),
                          4);

        // search and set the color map
        for (ADLWidget adlWidget : root.getObjects()) {
            if(adlWidget.getType().equals("color map")) { //$NON-NLS-1$
                try {
                    ADLHelper.setColorMap(adlWidget);
                    display(adlWidget, displayModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        for (ADLWidget adlWidget : root.getObjects()) {
            try {
                if(adlWidget.getType().equals("arc")) { //$NON-NLS-1$
                    AbstractWidgetModel element = new Arc(adlWidget,
                                                          displayModel,
                                                          storedBasicAttribute,
                                                          storedDynamicAttribute).getElement();
                    displayModel.addWidget(element);
                } else if(adlWidget.getType().equals("bar")) { //$NON-NLS-1$
                    AbstractWidgetModel element = new Bargraph(adlWidget,
                                                               storedBasicAttribute,
                                                               storedDynamicAttribute).getElement();
                    displayModel.addWidget(element);
                } else if(adlWidget.getType().equals("byte")) { //$NON-NLS-1$
                    displayModel.addWidget(new SixteenBinaryBar(adlWidget,
                                                                storedBasicAttribute,
                                                                storedDynamicAttribute)
                            .getElement());
                } else if(adlWidget.getType().equals("cartesian plot")) { //$NON-NLS-1$
                    displayModel.addWidget(new Waveform(adlWidget,
                                                        storedBasicAttribute,
                                                        storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("choice button")) { //$NON-NLS-1$
                    displayModel.addWidget(new ChoiceButton(adlWidget,
                                                            storedBasicAttribute,
                                                            storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("color map")) { //$NON-NLS-1$
                    // do nothing
                    // ADLHelper.setColorMap(adlWidget);
                    // display(adlWidget, displayModel);
                } else if(adlWidget.getType().equals("composite")) { //$NON-NLS-1$
                    displayModel.addWidget(new GroupingContainer(adlWidget,
                                                                 storedBasicAttribute,
                                                                 storedDynamicAttribute,
                                                                 targetProject).getElement());
                } else if(adlWidget.getType().startsWith("display")) { //$NON-NLS-1$
                    display(adlWidget, displayModel);
                } else if(adlWidget.getType().equals("dynamic symbol")) { //$NON-NLS-1$
                    displayModel.addWidget(new Symbol(adlWidget,
                                                      storedBasicAttribute,
                                                      storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("file")) { //$NON-NLS-1$
                    // TODO: FILE --> Name and Version
                    ADLHelper.setPath(adlWidget);
                } else if(adlWidget.getType().equals("image")) { //$NON-NLS-1$
                    displayModel.addWidget(new Image(adlWidget,
                                                     displayModel,
                                                     storedBasicAttribute,
                                                     storedDynamicAttribute,
                                                     targetProject).getElement());
                } else if(adlWidget.getType().equals("indicator")) { //$NON-NLS-1$
                    displayModel.addWidget(new Bargraph(adlWidget,
                                                        storedBasicAttribute,
                                                        storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("menu")) { //$NON-NLS-1$
                    displayModel.addWidget(new RelatedDisplay(adlWidget,
                                                              storedBasicAttribute,
                                                              storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("message button")
                        || adlWidget.getType().equals("toggle button")) { //$NON-NLS-1$
                    displayModel.addWidget(new ActionButton(adlWidget,
                                                            storedBasicAttribute,
                                                            storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("meter")) { //$NON-NLS-1$
                    displayModel.addWidget(new Meter(adlWidget,
                                                     storedBasicAttribute,
                                                     storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("oval")) { //$NON-NLS-1$
                    displayModel.addWidget(new Ellipse(adlWidget,
                                                       displayModel,
                                                       storedBasicAttribute,
                                                       storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("polygon")) { //$NON-NLS-1$
                    displayModel.addWidget(new Polygon(adlWidget,
                                                       displayModel,
                                                       storedBasicAttribute,
                                                       storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().endsWith("line")) { //$NON-NLS-1$
                    Polyline polyline = new Polyline(adlWidget,
                                                     displayModel,
                                                     storedBasicAttribute,
                                                     storedDynamicAttribute);
                    displayModel.addWidget(polyline.getElement());
                    polyline = null;
                } else if(adlWidget.getType().equals("rectangle")) { //$NON-NLS-1$
                    displayModel.addWidget(new Rectangle(adlWidget,
                                                         displayModel,
                                                         storedBasicAttribute,
                                                         storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("related display")) { //$NON-NLS-1$
                    displayModel.addWidget(new RelatedDisplay(adlWidget,
                                                              storedBasicAttribute,
                                                              storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("strip chart")) { //$NON-NLS-1$
                    displayModel.addWidget(new StripChart(adlWidget,
                                                          storedBasicAttribute,
                                                          storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("text")) { //$NON-NLS-1$
                    displayModel.addWidget(new Label(adlWidget,
                                                     displayModel,
                                                     storedBasicAttribute,
                                                     storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("text update")) { //$NON-NLS-1$
                    displayModel.addWidget(new Label(adlWidget,
                                                     displayModel,
                                                     storedBasicAttribute,
                                                     storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("text entry")) { //$NON-NLS-1$
                    displayModel.addWidget(new Textinput(adlWidget,
                                                         storedBasicAttribute,
                                                         storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().equals("valuator")) { //$NON-NLS-1$
                    displayModel.addWidget(new Valuator(adlWidget,
                                                        storedBasicAttribute,
                                                        storedDynamicAttribute).getElement());
                } else if(adlWidget.getType().contains("basic attribute")) { //$NON-NLS-1$
                    storedBasicAttribute = adlWidget;
                } else if(adlWidget.getType().contains("dynamic attribute")) { //$NON-NLS-1$
                    storedDynamicAttribute = adlWidget;
                } else {
                    int lineNr = adlWidget.getBody().get(0).getLineNumber();
                    Object[] args = new Object[] {adlWidget.getType(), lineNr,
                            adlWidget.getObjectNr(), sourceFile};
                    LOG.info(Messages.ADLDisplayImporter_WARN_UNHANDLED_TYPE
                            + "{} Line: {} (ObjectNo: {})in File: {}", args);
                }
            } catch (Exception e) {
                LOG.error("Error:_", e);
            }
        }

        // create the target file in the workspace
        return createFile(targetProject, targetFileName, displayModel);
    }

    public boolean importFaceplate(final String sourceFile,
                                   final IPath targetProject,
                                   final String targetFileName) throws CoreException {
        ADLWidget root = ParserADL.getNextElement(new File(sourceFile));
        DisplayModel displayModel = new DisplayModel();
        FaceplateParser.parse(root, displayModel);
        return createFile(targetProject, targetFileName, displayModel);
    }

    public boolean importStripTool(final String sourceFile,
                                   final IPath targetProject,
                                   final String targetFileName) throws CoreException, IOException {
        // ADLWidget root = ParserS.getNextElement(new File(sourceFile));
        StripTool stripTool = new StripTool();
        StripToolParser.parse(sourceFile, stripTool);
        return createFile(targetProject, targetFileName, stripTool);
    }

    private boolean createFile(IPath targetProject, String targetFileName, DisplayModel displayModel) throws CoreException {
        DisplayModelInputStream.setXMLHeader("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"); //$NON-NLS-1$
        DisplayModelInputStream modelInputStream = (DisplayModelInputStream) PersistenceUtil
                .createStream(displayModel);
        IFile fileOut = handelPathAndFile(targetProject, targetFileName);
        if(fileOut == null) {
            return false;
        } else if(fileOut.exists()) {
            fileOut.setContents(modelInputStream, true, false, null);
        } else {
            fileOut.create(modelInputStream, true, null);
        }
        return true;
    }

    private boolean createFile(IPath targetProject, String targetFileName, StripTool stripTool) throws CoreException,
                                                                                               IOException {
        IFile fileOut = handelPathAndFile(targetProject, targetFileName);
        if(fileOut == null) {
            return false;
        } else if(fileOut.exists()) {
            InputStream xmlFileInputStream = stripTool.getXmlFileInputStream();
            fileOut.setContents(xmlFileInputStream, true, false, null);
        } else {
            InputStream xmlFileInputStream = stripTool.getXmlFileInputStream();
            fileOut.create(xmlFileInputStream, true, null);
        }
        return true;
    }

    private IFile handelPathAndFile(IPath targetProject, String targetFileName) throws CoreException {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath filePath = targetProject.append(targetFileName.trim());
        if(!workspaceRoot.exists(targetProject)) {
            if(_createFolderAllways) {
                IFolder folder = workspaceRoot.getFolder(targetProject);
                createFolder(folder);
            } else if(_createFolderNever) {
                return null;
            } else {
                String[] dialogButtonsText = new String[] {
                        Messages.ADLDisplayImporter_Dialog_Yes_Button,
                        Messages.ADLDisplayImporter_Dialog_Yes2All_Button,
                        Messages.ADLDisplayImporter_Dialog_No_Button,
                        Messages.ADLDisplayImporter_Dialog_No2All_Button, "cancel"};
                Formatter f = new Formatter();
                f.format("Dir \"%s\" not exist!\r\nCreat this Folder?", targetProject);

                MessageDialog md = new MessageDialog(Display.getCurrent().getActiveShell(),
                                                     Messages.ADLDisplayImporter_Dialog_Header_Directory_not_exist,
                                                     null,
                                                     f.toString(),
                                                     MessageDialog.WARNING,
                                                     dialogButtonsText,
                                                     0);

                switch (md.open()) {
                    case 1:
                        _createFolderAllways = true;
                        //$FALL-THROUGH$
                    case 0:
                        IFolder folder = workspaceRoot.getFolder(targetProject);
                        createFolder(folder);
                        break;
                    case 3:
                        _createFolderNever = true;
                        //$FALL-THROUGH$
                    case 2:
                        _status = 2;
                        return null;
                    default:
                        _status = 5;
                        return null;
                }
            }
        }
        return workspaceRoot.getFile(filePath);
    }

    /**
     * Generate a Folder and parent folder.
     *
     * @param folder
     *            the lowest folder in the tree to generated
     * @throws CoreException
     *             is a OperationCanceledException if the operation is canceled. Cancelation can
     *             occur even if no progress monitor is provided.
     * @see IFolder#create(int,boolean,IProgressMonitor)
     */
    private void createFolder(final IFolder folder) throws CoreException {
        if(!folder.getParent().exists()) {
            if(folder.getParent() instanceof IFolder) {
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
        try {
            if(adlWidget.getType().startsWith("display")) {
                if(ADLHelper.getRGB("0") == null) { //$NON-NLS-1$
                    _storDisplay = adlWidget;
                } else {
                    new ADLDisplay(adlWidget, root);
                }
            } else if(adlWidget.getType().equals("\"color map\"") && _storDisplay != null) { //$NON-NLS-1$
                new ADLDisplay(_storDisplay, root);
                _storDisplay = null;
            }
        } catch (Exception e) {
            LOG.error("Error: ",e);
        }
    }

    /**
     * @return
     */
    public int getStatus() {
        return _status;
    }

    public static void reset() {
        _createFolderAllways = false;
        _createFolderNever = false;
    }

}
