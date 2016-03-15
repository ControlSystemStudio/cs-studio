/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;


import java.io.InputStream;

import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**The editor input for OPI Runner.
 * @author Xihui Chen
 *
 */
public class RunnerInput implements IRunnerInput{

    private DisplayOpenManager displayOpenManager;
    private MacrosInput macrosInput;
    private IPath path;

    public RunnerInput(IPath path, DisplayOpenManager displayOpenManager,
            MacrosInput macrosInput){
        this.path = path;
        this.setDisplayOpenManager(displayOpenManager);
        this.macrosInput = macrosInput;
    }

    public RunnerInput(IPath path, DisplayOpenManager displayOpenManager){
        this(path, displayOpenManager, null);
    }


    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.runmode.IRunnerInput#setDisplayOpenManager(org.csstudio.opibuilder.runmode.DisplayOpenManager)
     */
    @Override
    public void setDisplayOpenManager(DisplayOpenManager displayOpenManager) {
        this.displayOpenManager = displayOpenManager;
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.runmode.IRunnerInput#getDisplayOpenManager()
     */
    @Override
    public DisplayOpenManager getDisplayOpenManager() {
        return displayOpenManager;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((macrosInput == null) ? 0 : macrosInput.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RunnerInput other = (RunnerInput) obj;
        if (macrosInput == null) {
            if (other.macrosInput != null)
                return false;
        } else if (!macrosInput.equals(other.macrosInput))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof RunnerInput)) {
//            return false;
//        }
//        RunnerInput other = (RunnerInput) obj;
//        boolean macroSame = false;
//        if(macrosInput != null && other.getMacrosInput() !=null){
//            macroSame = macrosInput.equals(other.getMacrosInput());
//        }else if(macrosInput == null && other.getMacrosInput() == null)
//            macroSame = true;
//        return getPath().equals(other.getPath()) && macroSame;
//    //        displayOpenManager == other.getDisplayOpenManager()  &&
//
//    }


    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.runmode.IRunnerInput#getMacrosInput()
     */
    @Override
    public MacrosInput getMacrosInput() {
        return macrosInput;
    }

    @Override
    public void saveState(IMemento memento) {
        RunnerInputFactory.saveState(memento, this);
    }

    @Override
    public String getFactoryId() {
        return RunnerInputFactory.getFactoryId();
    }

    @Override
    public IPath getPath() {
        return path;
    }

    @Override
    public boolean exists() {
        InputStream in = null;
        try {
             in = getInputStream();
        } catch (Exception e) {
            return false;
        }
        return in != null;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return getPath().lastSegment();
    }

    @Override
    public IPersistableElement getPersistable() {
        return this;
    }

    @Override
    public String getToolTipText() {
        return path.toString();
    }


    @Override
    public InputStream getInputStream() throws Exception {
        return ResourceUtil.pathToInputStream(getPath(), false);
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        return null;
    }

    @Override
    public String toString() {
        return getPath().toString();
    }

}
