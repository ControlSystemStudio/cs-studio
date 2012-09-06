package org.csstudio.utility.toolbox.view.dialogs;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class IsContainedInDialog extends TitleAreaDialog {

	private Article article;
	private String articleDescription;

	private WidgetFactory<BindingEntity> wf = new WidgetFactory<BindingEntity>();

	public IsContainedInDialog(Shell parentShell, String articleDescription, Article article) {
		super(parentShell);
		this.articleDescription = articleDescription;
		this.article = article;
		wf.init();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Article Info");
		setMessage("Article " + articleDescription + " is contained in:", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		MigLayout miglayout = new MigLayout("fill", "[][grow]", "[]14[][][][fill]");
		parent.setLayout(miglayout);

		wf.label(parent).text("Internal-ID:").build();
		wf.label(parent).text(article.getInternId()).hint("split 2").build();
		
		wf.button(parent, "copyToClipboard").text("copy to clipboard").hint("wrap")
					.listener(new SimpleSelectionListener() {
						public void widgetSelected(SelectionEvent event) {
							String textData = article.getInternId();
							TextTransfer textTransfer = TextTransfer.getInstance();
							final Clipboard cb = new Clipboard(Display.getCurrent());
							cb.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
						}
					}).build();

		wf.label(parent).text("Article:").build();
		wf.label(parent).text(article.getBeschreibung()).hint("wrap").build();
		
		wf.label(parent).text("Serial-Nr:").build();
		wf.label(parent).text(article.getSerienNr()).hint("wrap").build();
		
		wf.label(parent).text("Status:").build();
		wf.label(parent).text(article.getStatus()).hint("wrap").build();

		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);

		MigLayout miglayout = new MigLayout("insets 0", "[grow]", "[fill,grow]");
		composite.setLayout(miglayout);
		composite.setLayoutData("spanx 2, growx");

		wf.button(parent, "OK").text("Ok").hint("w 90!, h 27!, ax right").listener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				okPressed();
			}
		}).defaultButton().build();

		return composite;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}