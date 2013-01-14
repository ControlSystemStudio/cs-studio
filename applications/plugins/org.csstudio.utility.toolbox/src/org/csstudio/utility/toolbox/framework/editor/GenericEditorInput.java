package org.csstudio.utility.toolbox.framework.editor;

import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.AppLogger;
import org.csstudio.utility.toolbox.common.Dialogs;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.annotations.InputLength;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.func.Func0Void;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

public class GenericEditorInput<T extends BindingEntity> implements IEditorInput {

	@Inject
	private EntityManager em;

	@Inject
	private Validator validator;

	@Inject
	private TransactionContext transactionContext;

	@Inject
	private AppLogger logger;

	@Inject
	private UniqueIdGenerator idGenerator;

	@Inject
	private TypeLiteral<T> typeLiteral;

	private Option<T> data;

	private Option<Func1Void<IStructuredSelection>> goBack;

	private EditorMode editorMode = EditorMode.CREATE;

	private Option<IStructuredSelection> selection;

	private String title = "GENERIC";

	private int editorId;

	private boolean saveSuccessful = false;

	private Func1Void<T> beforeCommit;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (data.hasValue()) {
			data.get().addPropertyChangeListener(listener);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (data.hasValue()) {
			data.get().removePropertyChangeListener(listener);
		}
	}

	public void setBeforeCommit(Func1Void<T> beforeCommit) {
		this.beforeCommit = beforeCommit;
	}

	public void init(String title, Option<T> data, Option<Func1Void<IStructuredSelection>> goBack,
				Option<IStructuredSelection> selection) {

		Validate.notNull(title, "Title must not be null");
		Validate.notNull(editorMode, "editMode must not be null");
		Validate.notNull(data, " Data must not be null");
		Validate.notNull(goBack, "GoBack must not be null");

		if (data.hasValue()) {
			Validate.notNull(data.get(), "Data.get must not return null");
		}

		this.selection = selection;
		editorId = idGenerator.getAndIncrement();

		this.title = title;
		this.data = data;
		this.goBack = goBack;
	}

	public boolean hasData() {
		return data.hasValue();
	}

	public void setData(T newData) {
		Validate.notNull(newData, "Generic Editor Input: New Data must not be null");
		PropertyChangeListener[] listeners = new PropertyChangeListener[] {};
		if (data.hasValue()) {
			listeners = data.get().getPcs().getPropertyChangeListeners();
			for (PropertyChangeListener listener : listeners) {
				data.get().getPcs().removePropertyChangeListener(listener);
			}
		}
		this.data = new Some<T>(newData);
		for (PropertyChangeListener listener : listeners) {
			if (listener instanceof DataChangeSupport) {
				data.get().addPropertyChangeListener(listener);
			}
		}
	}

	public Option<IStructuredSelection> getStructuredSelection() {
		return selection;
	}

	public void saveData() {
		try {
			transactionContext.doRun(new Func0Void() {
				@Override
				public void apply() {
					Object mergedObject = em.merge(data.get());
					// try to copy ID
					// This is neccessary since we only use merge and the id
					// is only
					// updated on an attached object.
					try {
					   if (hasIdField(data.get())) {
					      BeanUtils.setProperty(data.get(), "id", BeanUtils.getProperty(mergedObject, "id"));
					   }
					} catch (Exception e) {
					   logger.logError(e);
					}
					if (!Environment.isTestMode()) {
						em.flush();
					}
					if (beforeCommit != null) {
						beforeCommit.apply(data.get());
					}
					if (Environment.isTestMode()) {
						em.clear();
					}
				}
			});
			em.clear();
			data.get().setNewRecord(false);
			saveSuccessful = true;
		} catch (RollbackException e) {
			saveSuccessful = false;
			refreshData();
			logger.logError(e);
			Dialogs.exception("Error while saving...", e);
		}
	}

	private boolean hasIdField(Object object) {
	   boolean found;
	   try {
	      object.getClass().getDeclaredField("id");
	      found = true;
	   } catch (NoSuchFieldException e) {
	      found = false;
	   }
	   return found;
	}
	
	public boolean isSaveSuccessful() {
		return saveSuccessful;
	}

	public void refreshData() {
		if (data.hasValue() && em.contains(data.get())) {
			try {
				em.refresh(data.get());
			} catch (Exception e) {
			}
		}
	}

	public Set<ConstraintViolation<T>> validateData() {
		return validator.validate(data.get());
	}

	public Option<Integer> getSizeLimit(Property property) {
		Validate.notNull(property, "Property must not be null");
		if (data.hasValue()) {
			try {
				Option<AccessibleObject> accessibleObject = getAccessibleObject(property);
				if (accessibleObject.hasValue()) {
					if (accessibleObject.get().isAnnotationPresent(Size.class)) {
						Size size = accessibleObject.get().getAnnotation(Size.class);
						return new Some<Integer>(size.max());
					} else if (accessibleObject.get().isAnnotationPresent(InputLength.class)) {
						InputLength inputLength = accessibleObject.get().getAnnotation(InputLength.class);
						return new Some<Integer>(inputLength.value());
					}
				}
			} catch (Exception e) {
				logger.logError(e);
				throw new IllegalStateException(e);
			}
		}
		return new None<Integer>();
	}

	public String getDataPropertyValueByName(String propertyName) {
		Validate.notNull(propertyName, "PropertyName must not be null");
		Validate.isTrue(data.hasValue(), "data has no value");
		try {
			return BeanUtils.getProperty(data.get(), propertyName);
		} catch (Exception e) {
			logger.logError(e);
			throw new IllegalStateException(e);
		}
	}

	public boolean isDatePropertyField(Property property) {
		Validate.notNull(property, "Property must not be null");
		Option<Field> field = getField(property);
		return (field.hasValue() && (field.get().getType() == Date.class));
	}

	@SuppressWarnings("unchecked")
	public boolean isReadOnlyPropertyField(Property property) {
		Validate.notNull(property, "Property must not be null");
		if (!data.hasValue()) {
			return false;
		}
		return containsAnyAnnotation(property, Immutable.class, ReadOnly.class);
	}

	@SuppressWarnings("unchecked")
	public boolean isRequiredProperty(Property property) {
		Validate.notNull(property, "Property must not be null");
		if (!data.hasValue()) {
			return false;
		}
		return containsAnyAnnotation(property, Id.class, NotNull.class, NotEmpty.class);
	}

	private boolean containsAnyAnnotation(Property property, Class<? extends Annotation>... clazzes) {
		Set<Class<? extends Annotation>> annotationSet = new HashSet<Class<? extends Annotation>>(
					Arrays.asList(clazzes));
		Option<AccessibleObject> accessibleObject = getAccessibleObject(property);
		if (!accessibleObject.hasValue()) {
			throw new IllegalStateException("Can't find property " + property);
		}
		for (Class<? extends Annotation> annotation : annotationSet) {
			if (accessibleObject.get().isAnnotationPresent(annotation)) {
				return true;
			}
		}
		return false;
	}

	private Option<AccessibleObject> getAccessibleObject(Property property) {
		try {
			Field field = typeLiteral.getRawType().getDeclaredField(property.getName());
			return new Some<AccessibleObject>(field);
		} catch (NoSuchFieldException e) {
			// No field for the given property was found. Check if there is
			// a matching getter method for this property.
			try {
				Class<?> noparams[] = {};
				String getterName = "get" + StringUtils.capitalize(property.getName());
				Method method = typeLiteral.getRawType().getDeclaredMethod(getterName, noparams);
				return new Some<AccessibleObject>(method);
			} catch (Exception e1) {
				logger.logError(e);
				throw new IllegalStateException(e);
			}
		}
	}

	private Option<Field> getField(Property property) {
		try {
			Field field = typeLiteral.getRawType().getDeclaredField(property.getName());
			return new Some<Field>(field);
		} catch (NoSuchFieldException e) {
			return new None<Field>();
		}
	}

	public IObservableValue createObservableValueForProperty(Property property) {
		if (!data.hasValue()) {
			throw new IllegalStateException("Trying to bind data but no data given");
		}
		return BeanProperties.value(data.get().getClass(), property.getName()).observe(data.get());
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public void processData(Func1Void<T> processData) {
		if (data.hasValue()) {
			processData.apply(data.get());
		}
	}

	public void processGenericData(Func1Void<Some<Object>> processData) {
		if (data.hasValue()) {
			processData.apply(new Some<Object>(data.get()));
		}
	}

	public boolean isNewData() {
		if (data.hasValue()) {
			return data.get().isNew();
		}
		return false;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return title;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public int getEditorId() {
		return editorId;
	}

	public boolean hasGoBack() {
		return goBack.hasValue();
	}

	public void executeGoBack(IStructuredSelection selection) {
		if (!goBack.hasValue()) {
			return;
		}
		goBack.get().apply(selection);
	}

	public TypeLiteral<T> getTypeLiteral() {
		return typeLiteral;
	}

	@Override
	public int hashCode() {
		if (!data.hasValue()) {
			return editorId;
		}
		return data.get().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GenericEditorInput<?>)) {
			return false;
		}
		GenericEditorInput<?> editorIput = (GenericEditorInput<?>) obj;
		if (!hasData()) {
			return editorIput.editorId == editorId;
		}
		if (!data.hasValue()) {
			return editorIput.editorId == editorId;
		}
		return data.equals(editorIput.data);
	}

}
