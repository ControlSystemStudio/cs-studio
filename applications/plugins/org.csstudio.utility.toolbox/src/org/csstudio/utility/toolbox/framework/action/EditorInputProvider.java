package org.csstudio.utility.toolbox.framework.action;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;

import com.google.inject.Provider;

public interface EditorInputProvider <T extends BindingEntity> extends Provider<GenericEditorInput<T>>{
	GenericEditorInput<T> get();
}
  