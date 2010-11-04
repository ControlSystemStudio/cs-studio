/**
 * 
 */
package org.csstudio.channelfinder.commands;

import gov.bnl.channelfinder.model.XmlChannel;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 *
 */
public class RemoveTags extends AbstractHandler {

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
		.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (Iterator<XmlChannel> iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				XmlChannel element = iterator.next();
				System.out.println(element.getName());
			}
		}
		return null;
	}

}
