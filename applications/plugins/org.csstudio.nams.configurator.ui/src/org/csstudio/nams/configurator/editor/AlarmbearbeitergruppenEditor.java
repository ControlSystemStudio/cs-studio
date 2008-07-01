package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

public class AlarmbearbeitergruppenEditor extends AbstractEditor<AlarmbearbeiterGruppenBean> {

	private Text name;
	private Combo gruppe;
	private Text aktiveMitglieder;
	private Text wartezeit;
	private Button activeButton;
	
	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmbearbeitergruppenEditor";

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		{
			Composite textFieldComp = new Composite(main, SWT.None);
			textFieldComp.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					textFieldComp);

			{
				new Label(textFieldComp, SWT.READ_ONLY).setText("Name");

				name = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

				new Label(textFieldComp, SWT.READ_ONLY).setText("Gruppe");

				gruppe = new Combo(textFieldComp, SWT.None);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(gruppe);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Minimale Anzahl aktiver Mitglieder");

				aktiveMitglieder = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						aktiveMitglieder);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Wartezeit bis Rückmeldung (Sek)");

				wartezeit = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						wartezeit);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Alarmgruppe aktiv");
				activeButton = new Button(textFieldComp, SWT.CHECK);
			}

			{
				Composite tabelleUndButtonsComp = new Composite(main, SWT.None);
				tabelleUndButtonsComp.setLayout(new GridLayout(2, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						tabelleUndButtonsComp);

				{
					TableViewer tableViewer = new TableViewer(
							tabelleUndButtonsComp);
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							tableViewer.getControl());

					tableViewer.setContentProvider(new ArrayContentProvider());

					Table table = tableViewer.getTable();
					table.setHeaderVisible(true);
					table.setLinesVisible(true);
					table.setSize(400, 300);

					TableColumn alarmbearbieter = new TableColumn(table,
							SWT.LEFT);
					alarmbearbieter.setText("Alarmbearbeiter");
					alarmbearbieter.setWidth(200);

					TableColumn aktiv = new TableColumn(table, SWT.CHECK);
					aktiv.setText("Aktiv");
					aktiv.setWidth(100);

					TableColumn hinweis = new TableColumn(table, SWT.RIGHT);
					hinweis.setText("Hinweis vom Alarmbearbeiter");
					hinweis.setWidth(250);
				}

				{
					Composite buttonsComp = new Composite(
							tabelleUndButtonsComp, SWT.None);
					buttonsComp.setLayout(new GridLayout(1, false));
					GridDataFactory.fillDefaults().grab(false, true).applyTo(
							buttonsComp);
					{

						Button upup = new Button(buttonsComp, SWT.PUSH);
						upup.setText("UpUp");
						Button up = new Button(buttonsComp, SWT.PUSH);
						up.setText("Up");
						Button down = new Button(buttonsComp, SWT.PUSH);
						down.setText("Down");
						Button downdown = new Button(buttonsComp, SWT.PUSH);
						downdown.setText("DownDown");
					}
				}
			}
		}
		initDataBinding();
	}

	@Override
	protected void doInit(IEditorSite site, IEditorInput input) {
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables
				.observeValue(
						this.beanClone,
						AlarmbearbeiterGruppenBean.PropertyNames.name
								.name());

		IObservableValue aktiveMitgliederTextObservable = BeansObservables
				.observeValue(
						this.beanClone,
						AlarmbearbeiterGruppenBean.PropertyNames.minGroupMember
								.name());

		IObservableValue warteZeitTextObservable = BeansObservables
				.observeValue(
						this.beanClone,
						AlarmbearbeiterGruppenBean.PropertyNames.timeOutSec
								.name());

//		IObservableValue activeMembersTextObservable = BeansObservables
//				.observeValue(this.beanClone,
//						AlarmbearbeiterGruppenBean.PropertyNames.minGroupMember.name());

		IObservableValue activeCheckboxObservable = BeansObservables
		.observeValue(this.beanClone,
				AlarmbearbeiterGruppenBean.PropertyNames.active.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(name, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(aktiveMitglieder,
				SWT.Modify), aktiveMitgliederTextObservable, null, null);

		context.bindValue(
				SWTObservables.observeText(wartezeit, SWT.Modify),
				warteZeitTextObservable, null, null);

		context.bindValue(
				SWTObservables.observeSelection(activeButton),
				activeCheckboxObservable, null, null);

		//TODO bind Group
		
	}

	@Override
	public void setFocus() {
		name.setFocus();
	}

}
