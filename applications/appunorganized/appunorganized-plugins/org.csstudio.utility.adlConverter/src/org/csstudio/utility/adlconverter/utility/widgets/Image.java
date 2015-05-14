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
package org.csstudio.utility.adlconverter.utility.widgets;

import org.csstudio.sds.components.model.ImageModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 16.11.2007
 */
public class Image extends Widget {

    /**
     * @param image ADLWidget that describe the Image.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @param targetProject
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public Image(final ADLWidget image, AbstractWidgetModel abstractWidgetModel, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute, IPath targetPath) throws WrongADLFormatException {
        super(image, storedBasicAttribute, storedDynamicAttribute);
        for (FileLine fileLine : image.getBody()) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length<2){
                throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin+obj+Messages.Label_WrongADLFormatException_Parameter_End);
            }
            if(row[0].equals("type")){ //$NON-NLS-1$
                ;// not used
            }else if(row[0].equals("\"image name\"")){ //$NON-NLS-1$
                DebugHelper.add(this, row[1]);
                row[1] = ADLHelper.cleanString(row[1])[0];
                IResource res = ResourcesPlugin.getWorkspace().getRoot();
                String target = Activator.getDefault().getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
                IPath path;
                if(!row[1].contains("/")) { //$NON-NLS-1$
                    path = targetPath;
                } else {
                    path = res.getFullPath().append(target); //$NON-NLS-1$
                }
                path = path.append(row[1]);
                _widget.setPropertyValue(ImageModel.PROP_FILENAME, path);
            }else{
                throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin+ obj+Messages.Label_WrongADLFormatException_Parameter_End);
            } //image have no Parameter
        }
        ADLHelper.checkAndSetLayer(_widget, abstractWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setWidgetType() {
        _widget = createWidgetModel(ImageModel.ID);
    }

}
