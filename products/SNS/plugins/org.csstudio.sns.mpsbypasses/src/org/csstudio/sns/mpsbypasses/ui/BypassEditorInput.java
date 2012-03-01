package org.csstudio.sns.mpsbypasses.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/** Editor 'input' that represents the MPS bypass table
 *
 *  <p>Can 'persist' itself and acts as a factory to
 *  re-create the input from persisted information (which is empty).
 *  This way the editor will re-open when application is restarted,
 *  but it doesn't persist any settings (filter, ...)
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BypassEditorInput implements IElementFactory, IPersistableElement, IEditorInput
{
	/** IElementFactory ID registered in plugin.xml */
	final public static String FACTORY_ID = "org.csstudio.sns.mpsbypasses.editorfactory";

	/** Singleton instance */
	final public static BypassEditorInput instance = new BypassEditorInput();

	/** @see IElementFactory */
	@Override
    public IAdaptable createElement(final IMemento memento)
    {
	    return instance;
    }

	/** @see IPersistableElement */
	@Override
    public void saveState(final IMemento memento)
    {
	    // NOP
    }

	/** @see IPersistableElement */
	@Override
    public String getFactoryId()
    {
	    return FACTORY_ID;
    }

	// All the rest is for IEditorInput
	@Override
    public String getName()
    {
        return "MPS Bypasses";
    }

	@Override
    public String getToolTipText()
    {
        return getName();
    }

	@SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter)
    {
        return null;
    }

	@Override
    public boolean exists()
    {
        return true;
    }

	@Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


	@Override
    public IPersistableElement getPersistable()
    {
        return this;
    }
}
