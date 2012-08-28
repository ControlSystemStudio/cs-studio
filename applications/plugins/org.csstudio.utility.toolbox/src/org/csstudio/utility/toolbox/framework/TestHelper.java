package org.csstudio.utility.toolbox.framework;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;


public interface TestHelper<T extends BindingEntity> {

	WidgetFactory<T> getWidgetFactory();
}
