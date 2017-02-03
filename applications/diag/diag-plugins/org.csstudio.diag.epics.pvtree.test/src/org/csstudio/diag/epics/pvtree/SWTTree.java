/*******************************************************************************
 * Copyright (c) 2006 - 2016 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *     Lars.Vogel <Lars.Vogel@vogella.com> - Bug 414565, 475361, 487940
 *******************************************************************************/

package org.csstudio.diag.epics.pvtree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple TreeViewer to demonstrate usage
 */
public class SWTTree
{
    private class MyContentProvider implements ITreeContentProvider
    {
        @Override
        public Object[] getElements(Object inputElement)
        {
            return ((MyModel) inputElement).child.toArray();
        }

        @Override
        public Object[] getChildren(Object parentElement)
        {
            return getElements(parentElement);
        }

        @Override
        public Object getParent(Object element)
        {
            if (element == null)
            {
                return null;
            }

            return ((MyModel) element).parent;
        }

        @Override
        public boolean hasChildren(Object element)
        {
            return ((MyModel) element).child.size() > 0;
        }

        @Override
        public void dispose()
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput)
        {
            // TODO Auto-generated method stub

        }
    }

    int model_items = 0;

    public class MyModel
    {
        public MyModel parent;
        public List<MyModel> child = new ArrayList<>();
        public int counter;

        public MyModel(int counter, MyModel parent)
        {
            ++model_items;
            this.parent = parent;
            this.counter = counter;
        }

        private void describe(StringBuilder buf, MyModel item)
        {
            if (item.parent == null)
                buf.append("Item ").append(item.counter);
            else
            {
                describe(buf, item.parent);
                buf.append('.').append(item.counter);
            }
        }

        @Override
        public String toString()
        {
            final StringBuilder buf = new StringBuilder();
            describe(buf, this);
            return buf.toString();
        }
    }

    public SWTTree(Shell shell)
    {
        final TreeViewer v = new TreeViewer(shell);
        v.setLabelProvider(new LabelProvider());
        v.setContentProvider(new MyContentProvider());
        v.setInput(createModel());
    }

    private static final int LEVELS = 10, ITEMS = 4;

    private MyModel createModel()
    {

        MyModel root = new MyModel(0, null);
        addChildren(root, LEVELS);
        System.out.println(model_items + " model items");
        return root;
    }

    private void addChildren(MyModel item, int level)
    {
        if (level <= 0) return;
        for (int i = 0; i < ITEMS; i++)
        {
            MyModel child = new MyModel(i, item);
            item.child.add(child);
            addChildren(child, level - 1);
        }
    }

    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        new SWTTree(shell);
        shell.open();

        while (!shell.isDisposed())
            if (!display.readAndDispatch()) display.sleep();

        display.dispose();
    }
}
