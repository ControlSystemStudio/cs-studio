package org.csstudio.nams.configurator.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Comparator;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * A set of utility-methods for building UI elements used by the editor-parts.
 * 
 * This class/utilities are stateless, all methods are static and there is no
 * need for creating an instance.
 * 
 * This class need to be initialized before first used by injecting the {@link Logger}.
 */
public final class EditorUIUtils {
	
	private static final class PropertyChangeListenerImplementation implements
			PropertyChangeListener {
		private final PropertyEditorUtil propertyEditor;
		private final String propertyName;
		private final ComboViewer viewer;

		private PropertyChangeListenerImplementation(String propertyName,
				PropertyEditorUtil propertyEditor, ComboViewer viewer) {
			this.propertyName = propertyName;
			this.propertyEditor = propertyEditor;
			this.viewer = viewer;
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (propertyName.equals(event.getPropertyName())) {
				Object newSelection = propertyEditor.getValue();
				if (newSelection != null) {
					IStructuredSelection oldSelection = (IStructuredSelection) viewer
							.getSelection();
					if (oldSelection.getFirstElement() != newSelection) {
						viewer.setSelection(new StructuredSelection(
								newSelection));
					}
				}
			}
		}
	}

	static private class PropertyEditorUtil {
		private final Object bean;
		private final PropertyDescriptor propertyDescriptor;

		public PropertyEditorUtil(PropertyDescriptor propertyDescriptor,
				Object bean) {
			this.propertyDescriptor = propertyDescriptor;
			this.bean = bean;
		}

		public Object getValue() {
			Object result = null;
			Method readMethod = propertyDescriptor.getReadMethod();
			try {
				result = readMethod.invoke(bean);
			} catch (Throwable t) {
				throw new RuntimeException("failed to write property", t);
			}
			return result;
		}

		public void setValue(Object value) {
			Method writeMethod = propertyDescriptor.getWriteMethod();
			try {
				writeMethod.invoke(bean, value);
			} catch (Throwable t) {
				throw new RuntimeException("failed to write property", t);
			}
		}
	}

	private static Logger logger;

	/**
	 * Erzeugt einen ComboViewer für Enums. Die Auswahländerungen des Viewers
	 * werden auf die angegebene Property der angegebenen Bean übertragen.
	 * Änderungen an der Bean werden entsprechend auf dem ComboViewer
	 * ausgewählt.
	 * 
	 * @param <T>
	 *            Der Enum-Typ.
	 * @param parent
	 *            Das SWT-Parent Composite
	 * @param enumValues
	 *            Die darzustellenden Enum-Werte (Achtung: Sind in der Property
	 *            der Bean andere Werte als diese möglich, so wird dieses zu
	 *            einer Exception im ComoViewer führen!)
	 * @param boundBean
	 *            Die anzubindende Bean.
	 * @param propertyName
	 *            Der Name der Property welche die aktuelle Selektion hält
	 *            (siehe Anmerkung Parameter enumValues).
	 * @return Der fertig initialisierte ComboViewer
	 * @throws IntrospectionException
	 *             Wenn die angegebene Property in der Bean nicht exisitiert.
	 */
	public static <T extends Enum<?>> ComboViewer createComboViewerForEnumValues(
			final Composite parent, final T[] enumValues,
			final IConfigurationBean boundBean, final String propertyName)
			throws IntrospectionException {

		Contract.requireNotNull("parent", parent);
		Contract.requireNotNull("enumValues", enumValues);
		Contract.require(enumValues.length > 0, "enumValues.length > 0");
		Contract.requireNotNull("boundBean", boundBean);
		Contract.requireNotNull("propertyName", propertyName);
		Contract
				.require(propertyName.length() > 0, "propertyName.length() > 0");

		PropertyDescriptor propertyDescriptor = new PropertyDescriptor(
				propertyName, boundBean.getClass());

		Contract
				.require(propertyDescriptor.getPropertyType().equals(
						enumValues[0].getClass()),
						"propertyDescriptor.getPropertyType().equals(enumValues.getClass())");

		final PropertyEditorUtil propertyEditor = new PropertyEditorUtil(
				propertyDescriptor, boundBean);

		final ComboViewer result = new ComboViewer(parent, SWT.READ_ONLY);

		result.setContentProvider(new ArrayContentProvider());
		result.setComparator(new ViewerComparator(new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		}));
		result.setInput(enumValues);

		Object startSelection = propertyEditor.getValue();
		if (startSelection != null) {
			result.setSelection(new StructuredSelection(startSelection));
		}

		result.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Object selectedElement = selection.getFirstElement();
				if (propertyEditor.getValue() != selectedElement) {
					propertyEditor.setValue(selectedElement);
					EditorUIUtils.logger.logDebugMessage(this, "#selectionListener.selectionChanged(SelectionChangedEvent): Set Property "
									+ propertyName + " to " + selectedElement);
				}
			}
		});

		final PropertyChangeListener propertyChangeListenerOnBoundBean = new PropertyChangeListenerImplementation(
				propertyName, propertyEditor, result);
		boundBean.addPropertyChangeListener(propertyChangeListenerOnBoundBean);

		result.getCombo().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				boundBean
						.removePropertyChangeListener(propertyChangeListenerOnBoundBean);
			}
		});

		Contract.ensureResultNotNull(result);
		return result;
	}

	static public boolean isValidDigit(String value) {
		char[] charArray = value.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (!Character.isDigit(charArray[i])) {
				return false;
			}
		}
		return true;
	}

	public static void staticInject(Logger logger) {
		EditorUIUtils.logger = logger;
	}

	private EditorUIUtils() {
		// Avoid instances of this class.
	}
}
