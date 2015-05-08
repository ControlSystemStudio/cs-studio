/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileReturnPart;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileSource;
import org.csstudio.swt.widgets.figures.TextInputFigure.SelectorType;
import org.eclipse.draw2d.Figure;


/**
 * @author Xihui Chen
 *
 */
public class TextInputFigureTest extends TextFigureTest{

    @Override
    public Figure createTestWidget() {
        TextInputFigure figure = new TextInputFigure();
        figure.setSelectorType(SelectorType.DATETIME);
        figure.setFileSource(FileSource.LOCAL);
        figure.setFileReturnPart(FileReturnPart.NAME_ONLY);
        return figure;
    }


    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();
        String[] myProps = new String[]{
                "dateTimeFormat",
                "selectorType",
                "fileSource",
                "fileReturnPart",
                "startPath"
        };

        return concatenateStringArrays(superProps, myProps);
    }

    @Override
    public boolean isAutoTest() {
        return true;
    }
}
