/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.model;

/** One device in the model.
 *  @see ModelPV
 *  @author 9pj
 */
public class Racks // implements IFrontEndControllerName
{
    final String rack_dvc;

    public Racks(final String rack_dvc)
    {
        this.rack_dvc = rack_dvc;
    }
    
    @Override
    public String toString()
    {
        return "Rack: " + rack_dvc; //$NON-NLS-1$
    }

    public String getRack()
    {
        return rack_dvc;
    }


}
