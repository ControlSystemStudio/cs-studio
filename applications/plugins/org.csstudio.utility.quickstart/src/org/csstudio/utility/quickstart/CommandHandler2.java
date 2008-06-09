package org.csstudio.utility.quickstart;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class CommandHandler2 extends AbstractHandler {
 @Override
 public Object execute(ExecutionEvent event) throws ExecutionException {
   MessageDialog.openInformation(
     HandlerUtil.getActiveShellChecked(event), "My Handler 2",
     "Not yet implemented");
   return null;
 }
}