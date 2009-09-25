package org.csstudio.diag.diles.actions;

import java.util.List;

import org.csstudio.diag.diles.DilesEditor;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.AnalogInput;
import org.csstudio.diag.diles.model.TDDTimer;
import org.csstudio.diag.diles.model.TDETimer;
import org.csstudio.diag.diles.model.Timer;
import org.csstudio.diag.diles.palette.DilesPalette;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class OneStepAction extends Action implements IWorkbenchAction {

	private static final String ID = "org.csstudio.diag.diles.actions.OneStepAction";

	private static int current_column = -1;
	private static boolean start = true;

	public OneStepAction() {
		setId(ID);
		setText("One Step Action");
		setToolTipText("Run the program for one step");
		setImageDescriptor(ImageDescriptor.createFromFile(DilesPalette.class,
				"icons/true.png"));
	}

	/**
	 * Used to locate the column where first widget is located.
	 * <p>
	 * If it wasn't used, application would run from column 1 in all cases.
	 * </p>
	 * 
	 * @param c
	 *            list of widgets
	 * @return the column in where first widget is located
	 */
	private static int getFirstWidget(List<Activity> children) {
		int first_col = -1;

		loop: for (int i = 0; i < 100; i++) {

			for (int j = 0; j < children.size(); j++) {
				if (children.get(j).getColumn() == i) {
					first_col = i;
					break loop;
				}
			}
		}
		return first_col;
	}

	/**
	 * Makes a next step when running the application.
	 */
	protected static void nextStep() {
		String text = null;
		List<Activity> children = DilesEditor.getChart().getChildren();

		if (start) {
			current_column = getFirstWidget(children);
		}

		for (int j = 0; j < children.size(); j++) {
			if (children.get(j).getColumn() == current_column) {
				children.get(j).setResult();

				if (children.get(j) instanceof AnalogInput) {
					((AnalogInput) children.get(j)).setDoubleResult();

					/*
					 * Makes TDETimer wait, if conditions are met.
					 */
				} else if (children.get(j) instanceof TDETimer) {
					if (!children.get(j).getTargetConnections().isEmpty()) {
						if (!children.get(j).getTargetConnections().get(0)
								.getStatus()) {
							AutoRunAction.setRun(false);
							RunThread.setMiliseconds(((Timer) children.get(j))
									.getDelay() * 1000);
							AutoRunAction.setRun(true);
						}
					}
					/*
					 * Makes TDDTimer wait, if conditions are met.
					 */
				} else if (children.get(j) instanceof TDDTimer) {
					if (!children.get(j).getTargetConnections().isEmpty()) {
						if (children.get(j).getTargetConnections().get(0)
								.getStatus()) {
							AutoRunAction.setRun(false);
							RunThread.setMiliseconds(((Timer) children.get(j))
									.getDelay() * 1000);
							AutoRunAction.setRun(true);
						}
					}
				}

				text += "\n" + children.get(j).getLocation() + " "
						+ children.get(j).getResult();
				DilesEditor.getChart().setActive(true);
				children.get(j).setResultManually(children.get(j).getResult());
			}
		}

		for (int j = 0; j < children.size(); j++) {
			if (children.get(j).getColumn() == current_column - 1) {
				DilesEditor.getChart().setActive(false);
			}
		}

		boolean is_any_left = false;
		for (int i = current_column; i < current_column + 100; i++) {

			for (int j = 0; j < children.size(); j++) {
				if (children.get(j).getColumn() == i) {
					is_any_left = true;
				}
			}
		}

		if (text == null) {
			text = "Column number " + current_column + " is empty.";
		}

		if (is_any_left) {
			DilesEditor.getChart().changeActive();
			current_column++;
		} else {
			current_column = getFirstWidget(children);
			AutoRunAction.setRun(false);
			text = "The End";
		}
		start = false;
	}

	/**
	 * Sets the column where application running should occur.
	 * 
	 * @param i
	 *            the column
	 */
	public static void setCurrentColumn(int i) {
		current_column = i;
	}

	public static int getCurrentColumn() {
		return current_column;
	}

	public static void setStart(boolean b) {
		start = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		nextStep();
	}
}
