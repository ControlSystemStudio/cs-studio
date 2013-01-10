package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class OPIView extends ViewPart implements IOPIRuntime {

	protected OPIRuntimeDelegate opiRuntimeDelegate;

	private IEditorInput input;

	private IViewSite site;

	public static final String ID = "org.csstudio.opibuilder.opiView"; //$NON-NLS-1$
	
	private static final String TAG_INPUT = "input"; //$NON-NLS-1$
	private static final String TAG_FACTORY_ID = "factory_id"; //$NON-NLS-1$
	private boolean detached = false;
	

	/**
	 * Instance number, used to create a unique ID
	 * 
	 * @see #createNewInstance()
	 */
	private static int instance = 0;

	private OPIRuntimeToolBarDelegate opiRuntimeToolBarDelegate;
	
	private static boolean openFromPerspective = false;
	
	public OPIView() {
		opiRuntimeDelegate = new OPIRuntimeDelegate(this);
	}

	/** @return a new view instance */
	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.site = site;
		//site.getPage().addPartListener(partListener);
		if (memento != null) {
			IMemento inputMem = memento.getChild(TAG_INPUT);
			String factoryID = memento
						.getString(TAG_FACTORY_ID);
						
			IAdaptable element = null;
			IElementFactory factory = PlatformUI.getWorkbench()
					.getElementFactory(factoryID);
			if (factory == null) {
				throw new PartInitException(NLS.bind(
						"Cannot instantiate input element factory {0} for OPIView",
						factoryID));
			}
			// Get the input element.
			element = factory.createElement(inputMem);
			if (element == null || !(element instanceof IEditorInput)) {
				throw new PartInitException(
						NLS.bind(
								"Factory {0} returned null from createElement for OPIView",
								 factoryID));
			}	
			input = (IEditorInput)element;
		}
		if (input != null)
			setOPIInput(input);

	}

	public void setOPIInput(IEditorInput input) throws PartInitException {
		this.input = input;
		setTitleToolTip(input.getToolTipText());
		opiRuntimeDelegate.init(site, input);
		if(opiRuntimeToolBarDelegate != null)
			opiRuntimeToolBarDelegate.setActiveOPIRuntime(this);
	}

	@Override
	public void createPartControl(final Composite parent) {
		if(SWT.getPlatform().startsWith("rap")){ //$NON-NLS-1$
			SingleSourceHelper.rapOPIViewCreatePartControl(this, parent);
			return;
		}
			
		opiRuntimeDelegate.createGUI(parent);
		createToolbarButtons();
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if(parent.getShell().getText().length() == 0){ //the only way to know it is detached.
					if(!detached){
						detached = true;						
						UIBundlingThread.getInstance().addRunnable(new Runnable() {						
							public void run() {
								final Rectangle bounds;
								if(opiRuntimeDelegate.getDisplayModel() != null)
									bounds = opiRuntimeDelegate.getDisplayModel().getBounds();
								else
									bounds = new Rectangle(0, 0, 800, 600);
								if(bounds.x >=0 && bounds.y > 1)
									parent.getShell().setLocation(bounds.x, bounds.y);
								else{
								   org.eclipse.swt.graphics.Rectangle winSize = getSite().getWorkbenchWindow().getShell().getBounds();
									parent.getShell().setLocation( 
											winSize.x + winSize.width/5 + (int)(Math.random()*100), 
											winSize.y + winSize.height/8+ (int)(Math.random()*100));
								}
								parent.getShell().setSize(bounds.width+45, bounds.height+65);							
							}
						});
					}			
				}else
					detached = false;
			}
		});
	}
	
	public void createToolbarButtons(){
		opiRuntimeToolBarDelegate = new OPIRuntimeToolBarDelegate();
		IActionBars bars = getViewSite().getActionBars();
		opiRuntimeToolBarDelegate.init(bars, getSite().getPage());
		opiRuntimeToolBarDelegate.contributeToToolBar(bars.getToolBarManager());
		opiRuntimeToolBarDelegate.setActiveOPIRuntime(this);
	}
	

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		if(input == null)
			return;
		IPersistableElement persistable = input.getPersistable();
		if (persistable != null) {
			/*
			 * Store IPersistable of the IEditorInput in a separate section
			 * since it could potentially use a tag already used in the parent
			 * memento and thus overwrite data.
			 */
			IMemento persistableMemento = memento
					.createChild(TAG_INPUT);
			persistable.saveState(persistableMemento);
			memento.putString(TAG_FACTORY_ID,
					persistable.getFactoryId());
			// save the name and tooltip separately so they can be restored
			// without having to instantiate the input, which can activate
			// plugins
//			memento.putString(IWorkbenchConstants.TAG_NAME, input.getName());
//			memento.putString(IWorkbenchConstants.TAG_TOOLTIP,
//					input.getToolTipText());
		}
	}

	@Override
	public void setFocus() {

	}

	public void setWorkbenchPartName(String name) {
		setPartName(name);
	}

	public OPIRuntimeDelegate getOPIRuntimeDelegate() {
		return opiRuntimeDelegate;
	}

	public IEditorInput getOPIInput() {
		return getOPIRuntimeDelegate().getEditorInput();
	}

	public DisplayModel getDisplayModel() {
		return getOPIRuntimeDelegate().getDisplayModel();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		Object obj = opiRuntimeDelegate.getAdapter(adapter);
		if (obj != null)
			return obj;
		else
			return super.getAdapter(adapter);

	}

	public static boolean isOpenFromPerspective() {
		return openFromPerspective;
	}

	public static void setOpenFromPerspective(boolean openFromPerspective) {
		OPIView.openFromPerspective = openFromPerspective;
	}

	
}
