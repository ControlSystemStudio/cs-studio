/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.diag.IOCremoteManagement.ui;
/**
 * @author Albert Kagarmanov
 *
 */
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.csstudio.diag.IOCremoteManagement.Activator;
import org.csstudio.diag.IOCremoteManagement.Preference.PreferencePage;
import org.csstudio.diag.IOCremoteManagement.ui.Node.typeOfHost;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
public class SnlDebugTree extends Composite  {
	final static boolean debug=false;
	final static boolean debugStrong=false;
	final static boolean debugData=false;
	final TreeViewer _viewer;
    LabelProvider _labelProvider;
    HostIP _invisibleRoot;
    PropertyPart propertyPart;
    String[] IPlist;
    final static String hostArrDefault = "131.169.113.110 131.169.113.111" ;
    
	public SnlDebugTree(final Composite parent, final int style) {
		 super(parent, style);
		    this.setLayout(new FillLayout());
		    initIPlist(); 
		    _viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		    //DrillDownAdapter _drillDownAdapter = new DrillDownAdapter(_viewer);
		    _labelProvider = new ViewLabelProvider();
		    _viewer.setLabelProvider(_labelProvider);
	        _viewer.setContentProvider(new SnlContentProvider());
	        _viewer.setInput(initInput());
	        //_viewer.setSorter(new NameSorter());
	        _viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	            public void selectionChanged(final SelectionChangedEvent event) {
					// if the selection is empty clear the label
					if(event.getSelection().isEmpty()) return;
					if(event.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection)event.getSelection();
						
						if (selection.getFirstElement() instanceof HostIP) {
								HostIP dom = (HostIP) selection.getFirstElement();
								dom.askNextLevel(propertyPart);
						} else if (selection.getFirstElement() instanceof Knot) {
							Knot knot = (Knot) selection.getFirstElement();
							knot.askNextLevel(propertyPart);
						} else if (selection.getFirstElement() instanceof EndNode) {
							if (debug) System.out.println("endNode: pressed");
							EndNode finalNode = (EndNode) selection.getFirstElement();
							finalNode.askNextLevel(propertyPart);
						} else {
							System.out.println("SnlDebug:selectionChanged: Bad class");
							//TODO
							return;
						}
					}   
	            }
	        });
	        
	}
	
	private void initIPlist() {
    String hostArr = PreferencePage.getHosts();
    if (hostArr.length() < 3) {
    	System.out.println("SnlDebugTree: hostArray error");
    	hostArr=hostArrDefault;
    }

    StringTokenizer st = new StringTokenizer(hostArr," ");  
    IPlist = new String[st.countTokens()];
    int dim=0;
    while (st.hasMoreTokens()) { 
    	IPlist[dim]=st.nextToken();
//    	RMTControl.getInstance().addSocket(IPlist[dim]);
    	dim++;
    }
}
	public void setPropertyPart(PropertyPart p) {
		propertyPart=p;
	}
	public String[] getIPlist() {
	   return 	IPlist;
	}
	
	private HostIP initInput() {
		String[] IPlist = getIPlist();
		_invisibleRoot = new HostIP("HostList");
		for( int i = 0; i < IPlist.length; i++ ) {			
			HostIP tmp = new HostIP(IPlist[i],IPlist[i],_invisibleRoot,_viewer,propertyPart,null,null,HostIP.typeOfHost.host);
			_invisibleRoot.child.add(tmp);
			if (debug) System.out.println("IP=" + IPlist[i] );
		} 
        return _invisibleRoot;
	}
	
	/************************************************************************
	 *  Private Classes:
	 *  
	 *  SnlContentProvider
	 *  
	 *  ViewLabelProvider
	 * 
	 ************************************************************************/
	private class SnlContentProvider implements ITreeContentProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof HostIP)    return ((HostIP)inputElement).child.toArray();
			else if (inputElement instanceof Knot) return ((Knot)inputElement).child.toArray();
			else if (inputElement instanceof EndNode) return null;
			else {
				System.out.println("SnlDebug:SnlContentProvider:getElements Bad class");	
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			if (parentElement == null) {
				 initInput();
				 Object[] o = getChildren(_invisibleRoot); 
	             return o;
			}
			return getElements(parentElement);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			if( element == null)	return null;
			if (element instanceof HostIP)	return null;
			if (element instanceof Knot) {
				Knot el = (Knot) element;
				Object parent = el.parent;
				if (parent != null) return parent;
				System.out.println("SnlDebug:SnlContentProvider:getParent Knot Bad class");
				return null;
			} else if (element instanceof EndNode) {
				EndNode el = (EndNode) element;
				Object parent = el.parent;
				if (parent != null) return parent;
				System.out.println("SnlDebug:SnlContentProvider:getParent endNode Bad class");
				return null;	
			}
			return null;	
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {	
			if (element instanceof HostIP) return  ((HostIP)element).child.size() > 0;
			if (element instanceof Knot) return  ((Knot)element).child.size() > 0;		
			return false;
		}
		public Object getRootParent() {return _invisibleRoot;}
	}	

	private class ViewLabelProvider extends LabelProvider {  
		private Map<String, Image> imageCache;
		/**
		 * Creates a new alarm tree label provider.
		 */
		public ViewLabelProvider() {
			imageCache = new HashMap<String, Image>();
		}
				
		/**
		 * Loads an image. The image is added to a cache kept by this provider and
		 * is disposed of when this provider is disposed of.
		 * @param name the image file name.
		 */
		private Image loadImage(String name) {
			if (imageCache.containsKey(name)) {
				return imageCache.get(name);
			} else {
				Image image = Activator.getImageDescriptor(name).createImage();
				imageCache.put(name, image);
				return image;
			}
		}
		
		/**
		 * Disposes of the images created by this label provider.
		 */
		@Override
		public void dispose() {
			for (Image image : imageCache.values()) {
				image.dispose();
			}
		}
        /** {@inheritDoc} */
        @Override
        public String getText(final Object obj) {
            return obj.toString();
        }
        
		/**
		 * Returns the icon for the given element.
		 * @param element the element.
		 * @return the icon for the element, or {@code null} if there is no icon
		 * for the element.
		 */

        public Image getImage(Object element) {
        	String name="icons/IP.gif"; // default pic
    		if (element instanceof HostIP) {
    			HostIP host= (HostIP) element;
    			typeOfHost tHost=host.getType();
    			  switch(tHost) {
    	            case slave:
    	            	name="icons/IPslave.gif";
    	            	 break;
    	            case unresolve:
    	            	name = "icons/IPunresolve.gif";
    	            	break;
    	            case master:
    	            	name = "icons/IPmaster.gif";
    	            	break;
    	            case host:
    	            	name = "icons/IP.gif";
    	            	break;
    	            default:
    	            	System.out.println("SnlDebug:ViewLabelProvider: strange HostIP subclass, will use default");
    	                break;
    	        }
    		} else if (element instanceof Knot) {
    			name = "icons/node_add.gif";
    		}  else if (element instanceof EndNode) {
    		EndNode node= (EndNode) element;
    			typeOfHost tHost=node.getType();
    			  switch(tHost) {
  	            case finalSatateSet:
  	            	name = "icons/stateSet.gif";
  	            	 break;
  	            case finalVar:
  	            	name = "icons/variable.gif";
  	            	 break;
 	            case otherLeaf:
  	            	name = "icons/leaf.gif";
  	            	break;
  	            default:
  	            	System.out.println("SnlDebug:ViewLabelProvider: strange FinalLevel subclass, will use default");
  	                break;
    			  }
    		} else {
    			System.out.println("SnlDebug:ViewLabelProvider: Bad class; will use default");
    		}
    		return loadImage(name);
    	}
    }
}