/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editor;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**The overview outline page.
 * @author Xihui Chen
 *
 */
public class OverviewOutlinePage extends Page implements IContentOutlinePage
    {

        private Canvas overview;
        private Thumbnail thumbnail;
        private DisposeListener disposeListener;
        private ScalableFreeformRootEditPart rootEP;


        public OverviewOutlinePage(ScalableFreeformRootEditPart root){
            rootEP = root;
        }

        @Override
        public void createControl(Composite parent){
            overview = new Canvas(parent, SWT.NONE);
            LightweightSystem lws = new LightweightSystem(overview);

            thumbnail = new ScrollableThumbnail((Viewport)rootEP.getFigure());
            thumbnail.setBorder(new MarginBorder(3));
            thumbnail.setSource(rootEP.getLayer(LayerConstants.PRINTABLE_LAYERS));
            lws.setContents(thumbnail);

            disposeListener = new DisposeListener() {
                    @Override
                    public void widgetDisposed(DisposeEvent e) {
                        if (thumbnail != null) {
                            thumbnail.deactivate();
                            thumbnail = null;
                        }
                    }
            };
            rootEP.getViewer().getControl().addDisposeListener(disposeListener);
        }

        @Override
        public void dispose(){
            if (thumbnail != null) {
                thumbnail.deactivate();
                thumbnail = null;
            }
            super.dispose();
        }

        @Override
        public Control getControl() {
            return overview;
        }

        @Override
        public void setFocus() {
            if(getControl() != null)
                getControl().setFocus();
        }


        @Override
        public void addSelectionChangedListener(
                ISelectionChangedListener listener) {

        }

        @Override
        public ISelection getSelection() {
            return StructuredSelection.EMPTY;
        }

        @Override
        public void removeSelectionChangedListener(
                ISelectionChangedListener listener) {

        }

        @Override
        public void setSelection(ISelection selection) {

        }
    }

