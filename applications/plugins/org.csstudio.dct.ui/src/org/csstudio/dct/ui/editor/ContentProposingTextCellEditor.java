package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.FieldFunctionContentProposal;
import org.csstudio.dct.nameresolution.FieldFunctionExtension;
import org.csstudio.dct.ui.DctPerspective;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;
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
	IRecord record;

	public ContentProposingTextCellEditor(Composite parent, final IRecord record) {
		super(parent);
		this.record = record;

		char[] autoActivationCharacters = new char[] { '>', '$', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
				'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		KeyStroke keyStroke;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
			// assume that myTextControl has already been created in some way
			ContentProposalAdapter adapter = new ContentProposalAdapter(getControl(), new TextContentAdapter(), new MyContentProposalProvider(
					record, AliasResolutionUtil.getFinalAliases(record.getContainer())), keyStroke, autoActivationCharacters);
			adapter.setPropagateKeys(true);
			adapter.setPopupSize(new Point(400, 300));

			adapter.addContentProposalListener(this);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}

	boolean contentProposalOpen = false;

	public void proposalPopupClosed(ContentProposalAdapter adapter) {
		contentProposalOpen = false;
	}

	public void proposalPopupOpened(ContentProposalAdapter adapter) {
		contentProposalOpen = true;
	}

	protected void focusLost() {
		if (isActivated() && !contentProposalOpen) {
			fireApplyEditorValue();
			deactivate();
		}
	}

	private static final class MyContentProposalProvider implements IContentProposalProvider {
		private Map<String, String> aliases;
		private IRecord record;

		private MyContentProposalProvider(IRecord record, Map<String, String> aliases) {
			this.record = record;
			this.aliases = aliases;
		}

		public IContentProposal[] getProposals(String contents, int position) {
			List<IContentProposal> proposals = new ArrayList<IContentProposal>();

			// .. propose variables
			if (contents.substring(position - 1, position).equals("$")) {
				for (String key : aliases.keySet()) {
					proposals.add(new FieldFunctionContentProposal("(" + key + ")", "$(" + key + ")", "Variable. Current value is "
							+ aliases.get(key), 5));
				}
			}

			// .. propose functions
			List<FieldFunctionExtension> extensions = DctActivator.getDefault().getFieldFunctionService().getFieldFunctionExtensions();

			if (contents.startsWith(">")) {
				String fName = contents.substring(1);
				for (FieldFunctionExtension e : extensions) {
					if (e.getName().startsWith(fName)) {
						String proposal = e.getName().replaceFirst(fName, "");
						proposals.add(new FieldFunctionContentProposal(proposal + "()", ">" + e.getSignature(), e.getDescription(), proposal
								.length() + 1));
					}
				}
			}

			// .. propose function parameters
			Pattern pattern = Pattern.compile(">([^(]+)\\((([^,()]*[,]?)*)[)]?");
			Matcher matcher = pattern.matcher(contents.substring(0, position));
			CentralLogger.getInstance().info(null, contents.substring(0, position));

			if (matcher.matches()) {
				String name = matcher.group(1);
				String parameters = matcher.group(2);

				int param = 0;
				for (int i = 0; i < parameters.length(); i++) {
					if (",".equals(parameters.substring(i, i + 1))) {
						param++;
					}
				}

				for (FieldFunctionExtension e : extensions) {
					if (name.equals(e.getName())) {
						proposals.addAll(e.getFunction().getParameterProposal(param, record));
					}
				}
			}

			return proposals.toArray(new IContentProposal[proposals.size()]);
		}

	}

}
