package org.csstudio.ui.util;

import org.csstudio.ui.util.internal.localization.Messages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Empty IEditorInput.
 *
 *  <p>When an editor is started without a file,
 *  for example from the main menu,
 *  this can be used as its initial input.
 *
 *  @author Kay Kasemir
 */
public class EmptyEditorInput implements IEditorInput
{
    final private ImageDescriptor icon;
    
    /** Initialize
     *  @param icon Desired icon
     */
    public EmptyEditorInput(final ImageDescriptor icon)
    {
        this.icon = icon;
    }
 
    /** Initialize */
    public EmptyEditorInput()
    {
        this(null);
    }
 
    /** Cause application title to reflect the 'not saved' state. */
    @Override
    public String getName()
    {
        return Messages.EmptyEditorInput_NotSaved;
    }

     /** Cause tool top to reflect the 'not saved' state. */
     @Override
     public String getToolTipText()
     {
         return Messages.EmptyEditorInput_NotSaved_TT;
     }

     /** @return Returns <code>false</code> since no file exists. */
     @Override
     public boolean exists()
     {
         return false;
     }

     /** Returns no image. */
     @Override
     public ImageDescriptor getImageDescriptor()
     {
         return icon;
     }

     /** Can't persist. */
     @Override
     public IPersistableElement getPersistable()
     {
         return null;
     }

     /** Don't adapt. */
     @SuppressWarnings("rawtypes")
     @Override
     public Object getAdapter(Class adapter)
     {
         return null;
     }
 }
