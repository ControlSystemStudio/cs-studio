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
import java.io.InputStream;

import org.csstudio.sds.importer.AbstractDisplayImporter;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.persistence.PersistenceUtil;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.ParserADL;
import org.csstudio.utility.adlconverter.utility.widgets.ActionButton;
import org.csstudio.utility.adlconverter.utility.widgets.Arc;
import org.csstudio.utility.adlconverter.utility.widgets.Bargraph;
import org.csstudio.utility.adlconverter.utility.widgets.Display;
import org.csstudio.utility.adlconverter.utility.widgets.Ellipse;
import org.csstudio.utility.adlconverter.utility.widgets.GroupingContainer;
import org.csstudio.utility.adlconverter.utility.widgets.Label;
import org.csstudio.utility.adlconverter.utility.widgets.Meter;
import org.csstudio.utility.adlconverter.utility.widgets.Polygon;
import org.csstudio.utility.adlconverter.utility.widgets.Polyline;
import org.csstudio.utility.adlconverter.utility.widgets.Rectangle;
import org.csstudio.utility.adlconverter.utility.widgets.RelatedDisplay;
import org.csstudio.utility.adlconverter.utility.widgets.Textinput;
import org.csstudio.utility.adlconverter.utility.widgets.Valuator;
import org.csstudio.utility.adlconverter.utility.widgets.Waveform;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

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

    /**
     * 
     */
    public ADLDisplayImporter() {}

    /**
     * {@inheritDoc}
     * @throws CoreException 
     */
    @Override
    public final boolean importDisplay(final String sourceFile, final IPath targetProject, final String targetFileName) throws CoreException {
        File file = new File(sourceFile);
        ParserADL pars = new ParserADL(file);
        ADLWidget root = pars.getNextElement();
        
        // this is the target display model
        DisplayModel displayModel = new DisplayModel();
        _colormapSet=false;
        _displaySave=false;
        _storDisplay = null;
        
        displayModel.getLayerSupport().addLayer(new Layer("Bargraph","Bargraph"));
        for (ADLWidget strings : root.getObjects()) {
            try {
                display(strings, displayModel);
                if(strings.getType().equals("arc")){
                    displayModel.addWidget(new Arc(strings).getElement());
                }else if(strings.getType().equals("bar")){
                    displayModel.addWidget(new Bargraph(strings).getElement());
                }else if(strings.getType().equals("\"color map\"")){
                        ADLHelper.setColorMap(strings);
                        _colormapSet=true;
                }else if(strings.getType().equals("composite")){
                    displayModel.addWidget(new GroupingContainer(strings).getElement());
                }else if(strings.getType().equals("indicator")){
                    displayModel.addWidget(new Bargraph(strings).getElement());
                }else if(strings.getType().equals("menu")){
                    displayModel.addWidget(new RelatedDisplay(strings).getElement());
                }else if(strings.getType().equals("\"message button\"")){
                    displayModel.addWidget(new ActionButton(strings).getElement());
                }else if(strings.getType().equals("meter")){
                    displayModel.addWidget(new Meter(strings).getElement());
                }else if(strings.getType().equals("oval")){
                    displayModel.addWidget(new Ellipse(strings).getElement());
                }else if(strings.getType().equals("polygon")){
                    displayModel.addWidget(new Polygon(strings).getElement());
                }else if(strings.getType().equals("polyline")){
                    displayModel.addWidget(new Polyline(strings).getElement());
                }else if(strings.getType().equals("rectangle")){
                    displayModel.addWidget(new Rectangle(strings).getElement());
                }else if(strings.getType().equals("\"related display\"")){
                    displayModel.addWidget(new RelatedDisplay(strings).getElement());
                }else if(strings.getType().equals("\"strip chart\"")){
                    displayModel.addWidget(new Waveform(strings).getElement());
                }else if(strings.getType().equals("text")){
                    displayModel.addWidget(new Label(strings).getElement());
                }else if(strings.getType().equals("\"text update\"")){
                    displayModel.addWidget(new Label(strings).getElement());
                }else if(strings.getType().equals("\"text entry\"")){
                    displayModel.addWidget(new Textinput(strings).getElement());
                }else if(strings.getType().equals("valuator")){
                    displayModel.addWidget(new Valuator(strings).getElement());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InputStream modelInputStream = PersistenceUtil.createStream(displayModel);

        // create the target file in the workspace
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath filePath = targetProject.append(targetFileName);
        IFile fileOut = workspaceRoot.getFile(filePath);

//        IPath test123 = filePath.removeFirstSegments(2);
//        IFile fileOut = workspaceRoot.getFile(test123);
        if(fileOut.exists()){
            fileOut.setContents(modelInputStream, true,false, null);
        }else{
            fileOut.create(modelInputStream, true, null);
        }
        return true;
    }
    

    /**
     * @param adlWidget the Main display widget.
     * @param root the root Element of css-sds file.
     */
    private void display(final ADLWidget adlWidget, final DisplayModel root) {
        
        if(adlWidget.getType().startsWith("display")||_displaySave){
            
            try{
                if(adlWidget.getType().startsWith("display")&&!_colormapSet&&!_displaySave){
                    _storDisplay=adlWidget;
                    _displaySave=true;
                }else if(!adlWidget.getType().startsWith("display")&&_colormapSet&&_displaySave){
                    new Display(_storDisplay, root);
                    _displaySave=false;
                }else if(adlWidget.getType().startsWith("display")&&_colormapSet&&!_displaySave){
                    new Display(adlWidget, root);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }


}
