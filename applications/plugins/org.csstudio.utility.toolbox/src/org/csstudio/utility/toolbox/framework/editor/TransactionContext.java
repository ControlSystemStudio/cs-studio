package org.csstudio.utility.toolbox.framework.editor;

import org.csstudio.utility.toolbox.func.Func0Void;

import com.google.inject.persist.Transactional;

public class TransactionContext {

	@Transactional
	public void doRun(Func0Void func0)  {
		func0.apply();
	}
	
}
  