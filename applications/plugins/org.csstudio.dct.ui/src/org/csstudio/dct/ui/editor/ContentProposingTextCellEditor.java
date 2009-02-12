package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class ContentProposingTextCellEditor extends TextCellEditor implements IContentProposalListener2 {
	public ContentProposingTextCellEditor(Composite parent, final IRecord record) {
		super(parent);
		char[] autoActivationCharacters = new char[] { '>', '$' };
		KeyStroke keyStroke;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
			// assume that myTextControl has already been created in some way
			ContentProposalAdapter adapter = new ContentProposalAdapter(getControl(), new TextContentAdapter(), new MyContentProposalProvider(
					AliasResolutionUtil.getFinalAliases(record.getContainer())), keyStroke, autoActivationCharacters);
			adapter.setPropagateKeys(true);
			adapter.setPopupSize(new Point(400,300));
			
			adapter.addContentProposalListener(this);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}
	boolean contentProposalOpen=false;
	
	public void proposalPopupClosed(ContentProposalAdapter adapter) {
		contentProposalOpen=false;
	}

	public void proposalPopupOpened(ContentProposalAdapter adapter) {
		contentProposalOpen=true;
	}
	
	protected void focusLost() {
		if (isActivated()&&!contentProposalOpen) {
			fireApplyEditorValue();
			deactivate();
		}
	}

	private static final class MyContentProposalProvider implements IContentProposalProvider {
		private Map<String, String> aliases;

		private MyContentProposalProvider(Map<String, String> aliases) {
			this.aliases = aliases;
		}

		public IContentProposal[] getProposals(String contents, int position) {
			List<IContentProposal> proposals = new ArrayList<IContentProposal>();

			if (contents.substring(position-1, position).equals("$")) {
				for (String key : aliases.keySet()) {
					proposals.add(new FieldFunctionContentProposal("(" + key + ")", "$(" + key + ")", "Variable. Current value is "
							+ aliases.get(key), 5));
				}
			}
			
			if (contents.equals(">")) {
				proposals.add(new FieldFunctionContentProposal("ioname()", ">ioname()", "Function. Resolves the IO name of a device.", 0));
				proposals.add(new FieldFunctionContentProposal("datalink(<target>)", ">datalink()", "Function. Creates a data link.", 9));
				proposals.add(new FieldFunctionContentProposal("forwardlink(<target>)", ">forwardlink()", "Function. Creates a forward link.", 12));
			}
			return proposals.toArray(new IContentProposal[proposals.size()]);
		}

	}

	private static final class FieldFunctionContentProposal implements IContentProposal {

		private String content;
		private String label;
		private String description;
		private int cursorPosition;

		public FieldFunctionContentProposal(String content, String label, String description, int cursorPosition) {
			this.content = content;
			this.label = label;
			this.description = description;
			this.cursorPosition = cursorPosition;
		}

		public String getContent() {
			return content;
		}

		public int getCursorPosition() {
			return cursorPosition;
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

	}


}
