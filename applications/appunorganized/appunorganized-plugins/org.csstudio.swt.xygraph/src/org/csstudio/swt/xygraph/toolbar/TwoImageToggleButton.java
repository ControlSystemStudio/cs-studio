/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.toolbar;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.ToggleButton;
import org.eclipse.swt.graphics.Image;

/**A button with gray image when disabled.
 * @author Xihui Chen
 *
 */
public class TwoImageToggleButton extends ToggleButton {

    IFigure image;
    IFigure disabledImage;

    public TwoImageToggleButton(Image image1, Image image2){
        super(new ImageFigure(image1));
        this.image = new ImageFigure(image1);
        if(image2 == null) {
            this.disabledImage = new ImageFigure(image1);
        }
        this.disabledImage = new ImageFigure(image2);
    }

    public void switchImage(boolean isToggled) {
        if(isToggled)
            setContents(image);
        else
            setContents(disabledImage);
    }


}
