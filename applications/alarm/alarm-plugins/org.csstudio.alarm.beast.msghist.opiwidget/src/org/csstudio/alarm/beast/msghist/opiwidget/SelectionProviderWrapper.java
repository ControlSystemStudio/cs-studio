/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * SelectionProvider that enables switching between multiple selection
 * providers.
 * <p>Single IWorkBenchSite {@link IWorkBenchSite} can only take one selection provider. This class
 * enables switching between multiple selection providers, but returns only one for the {@link IWorkBenchSite}.
 *
 *
 * @author Borut Terpinc
 *
 */
public class SelectionProviderWrapper implements ISelectionProvider {

    private ISelectionProvider provider;
    private List<ISelectionChangedListener> selectionListeners;
    private ISelection sel = StructuredSelection.EMPTY;

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        if (selectionListeners == null){
            selectionListeners = new ArrayList<>(1);
        }
        selectionListeners.add(listener);
        if (provider != null){
            provider.addSelectionChangedListener(listener);
            }
   }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        if (selectionListeners != null) {
            selectionListeners.remove(listener);
            if (provider != null){
                provider.removeSelectionChangedListener(listener);
            }
        }
    }

    @Override
    public ISelection getSelection() {
        return provider != null ? provider.getSelection() : sel;
    }

    @Override
    public void setSelection(ISelection selection) {
        if (provider != null) {
            provider.setSelection(selection);
        } else {
            sel = selection;
            if (selectionListeners != null) {
                SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
                for (Iterator<ISelectionChangedListener> it = selectionListeners.iterator(); it.hasNext();) {
                    it.next().selectionChanged(event);
                }
            }

        }
    }

    /**
     * Set current selection provider.
     */
    public void setSelectionProvider(ISelectionProvider provider) {
        if (this.provider != provider) {
            ISelection selection = null;
            if (selectionListeners != null) {
                int numOfListeners = selectionListeners.size();
                ISelectionChangedListener listener;
                if (this.provider != null) {
                    for (int i = 0; i < numOfListeners; i++) {
                        listener = selectionListeners.get(i);
                        this.provider.removeSelectionChangedListener(listener);
                    }
                }

                if (provider != null) {
                    for (int i = 0; i < numOfListeners; i++) {
                        listener = selectionListeners.get(i);
                        provider.addSelectionChangedListener(listener);
                    }

                    selection = provider.getSelection();
                } else {
                    selection = sel;
                }
            }
            this.provider = provider;
            if (selection != null) {
                // force a selection change event propagation
                setSelection(selection);
            }
        }
    }

    /**
     * @return currently selected selection provider.
     */
    public ISelectionProvider getSelectionProvider() {
        return provider;
    }
}