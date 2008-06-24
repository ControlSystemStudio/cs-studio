package org.csstudio.nams.configurator.editor.stackparts;

import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;
import org.csstudio.nams.configurator.modelmapping.IConfigurationModel;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class AlarmbearbeitergruppenStackPart extends
		AbstractStackPart<AlarmbearbeiterGruppenBean> {

	private Composite main;
	private IConfigurationModel model;
	private AlarmbearbeiterGruppenBean alarmbearbeiterGruppenBean;
	private AlarmbearbeiterGruppenBean alarmbearbeiterGruppenClone;

	public AlarmbearbeitergruppenStackPart(DirtyFlagProvider flagProvider,
			Composite parent) {
		super(flagProvider, AlarmbearbeiterGruppenBean.class, 2);

		this.createPartControl(parent);
	}

	private void createPartControl(Composite parent) {
		main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		{
			Composite textFieldComp = new Composite(main, SWT.None);
			textFieldComp.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					textFieldComp);

			{
				new Label(textFieldComp, SWT.READ_ONLY).setText("Name");

				Text name = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

				new Label(textFieldComp, SWT.READ_ONLY).setText("Gruppe");

				Combo gruppe = new Combo(textFieldComp, SWT.None);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(gruppe);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Minimale Anzahl aktiver Mitglieder");

				Text aktiveMitglieder = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						aktiveMitglieder);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Wartezeit bis Rückmeldung (Sek)");

				Text wartezeit = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						wartezeit);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Alarmgruppe aktiv");
				new Button(textFieldComp, SWT.CHECK);
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

	}

	@Override
	public Control getMainControl() {
		return main;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInput(IConfigurationBean input, IConfigurationModel model) {
		this.model = model;
		this.alarmbearbeiterGruppenBean = (AlarmbearbeiterGruppenBean) input;
		this.alarmbearbeiterGruppenClone = ((AlarmbearbeiterGruppenBean) input).getClone();

		// init JFaceDatabinding after input is set
		this.initDataBinding();
	}

	private void initDataBinding() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyChangedListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

}
