/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static org.csstudio.utility.channel.CSSChannelUtils.getCSSChannelTagNames;
import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 * 
 */
public class RemoveTagCommandHandler extends AbstractAdaptedHandler<Channel> {
	
	public RemoveTagCommandHandler() {
		super(Channel.class);
	}

	@Override
	protected void execute(List<Channel> channels, ExecutionEvent event) {
		Shell shell = HandlerUtil.getActiveShell(event);
		ElementListSelectionDialog selectTags = new ElementListSelectionDialog(
				shell, new LabelProvider());
		
		selectTags.setTitle("Tag Selection");

		selectTags.setMessage("Select the Tags to be removed (* = any string, ? = any char):");
		selectTags.setMultipleSelection(true);
		Collection<String> existingTagNames = getCSSChannelTagNames(channels);
		selectTags.setElements(existingTagNames
				.toArray(new String[existingTagNames.size()]));
		selectTags.setBlockOnOpen(true);
		if (selectTags.open() == Window.OK) {
			Object[] selected = selectTags.getResult();
			Collection<String> selectedTags = new TreeSet<String>();
			for (int i = 0; i < selected.length; i++) {
				selectedTags.add((String) selected[i]);
			}
			if (selectedTags.size() > 0) {
				Job job = new RemoveTagsJob("removeTags", channels,
						selectedTags);
				job.schedule();
			}
		}
	}

}
