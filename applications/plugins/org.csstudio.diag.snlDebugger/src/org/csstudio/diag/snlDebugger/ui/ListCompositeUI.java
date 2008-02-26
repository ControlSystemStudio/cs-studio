/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.diag.snlDebugger.ui;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ListCompositeUI extends Composite {
  ListViewer _viewer;
  public ListCompositeUI(Composite parent) {
    super(parent, SWT.NULL);
    populateControl();
  }
	 public static void main(String[] args) {
		  Display display = new Display();
		  Shell shell = new Shell(display);
		  shell.setLayout(new FillLayout());
		  ListCompositeUI a = new ListCompositeUI(shell);
		  
		  shell.open();
		  while (!shell.isDisposed()) {
		    if (!display.readAndDispatch()) {
		      display.sleep();
		    }
		  }
		  display.dispose();
	  }
	 public ListViewer getListViewer() {return _viewer;}
	 
  protected void populateControl() {
    FillLayout compositeLayout = new FillLayout();
    setLayout(compositeLayout);
    createListViewer(SWT.SINGLE|SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

  }
  private void createListViewer(int style) {
    ListViewer viewer = new ListViewer(this, style);
    _viewer=viewer;
    /*
    viewer.addDoubleClickListener(new IDoubleClickListener() {
        public void doubleClick(DoubleClickEvent event) {
          IStructuredSelection selection = (IStructuredSelection) event
              .getSelection();
          ListItem item= (ListItem) selection.getFirstElement();
          System.out.println("doubleClick"+ item.name );
        }
      });
      */   
    viewer.setLabelProvider(new LabelProvider() {
      public String getText(Object element) {
        return ((ListItem) element).name;
      }
    });

    viewer.addFilter(new ViewerFilter() {
      public boolean select(Viewer viewer, Object parent, Object element) {
        return true;
      }
    });

    viewer.setSorter(new ViewerSorter() {
      public int compare(Viewer viewer, Object obj1, Object obj2) {
        return ((ListItem) obj1).name.compareTo( ((ListItem) obj2).name );
      }
    });

    viewer.setContentProvider(new IStructuredContentProvider() {

      public Object[] getElements(Object inputElement) {
        return ((List) inputElement).toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer viewer, Object oldInput,
          Object newInput) {
      }
    });
    
  }

}

class ListItem {
  public String name;

  public int value;

  public ListItem(String n, int v) {
    name = n;
    value = v;
  }
}
