package org.csstudio.utility.toolbox.framework.template;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;

public interface CrudEventListener<T extends BindingEntity> {

   CanSaveAction canSave(T entity);

   void saveComplete(T entity);

}
