package org.csstudio.ui.menu.test;

import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;

public class ErrorCommandHandler extends AbstractAdaptedHandler<ProcessVariable> {

	public ErrorCommandHandler() {
		super(ProcessVariable.class);
	}
	
	@Override
	protected void execute(List<ProcessVariable> data, ExecutionEvent event) {
		throw new RuntimeException("This is an error!", new RuntimeException("This is the cause"));
	}

}
