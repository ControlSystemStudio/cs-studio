
package org.csstudio.nams.configurator.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Comparator;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.service.logging.declaration.ILogger;
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
 * This class need to be initialized before first used by injecting the
 * {@link ILogger}.
 */
public final class EditorUIUtils {

	private static final class PropertyChangeListenerImplementation implements
			PropertyChangeListener {
		private final PropertyEditorUtil propertyEditor;
		private final String propertyName;
		private final ComboViewer viewer;

		private PropertyChangeListenerImplementation(final String propertyName,
				final PropertyEditorUtil propertyEditor,
				final ComboViewer viewer) {
			this.propertyName = propertyName;
			this.propertyEditor = propertyEditor;
			this.viewer = viewer;
		}

		@Override
        public void propertyChange(final PropertyChangeEvent event) {
			if (this.propertyName.equals(event.getPropertyName())) {
				final Object newSelection = this.propertyEditor.getValue();
				if (newSelection != null) {
					final IStructuredSelection oldSelection = (IStructuredSelection) this.viewer
							.getSelection();
					if (oldSelection.getFirstElement() != newSelection) {
						this.viewer.setSelection(new StructuredSelection(
								newSelection));
					}
				}
			}
		}
	}

	static private class PropertyEditorUtil {
		private final Object bean;
		private final PropertyDescriptor propertyDescriptor;

		public PropertyEditorUtil(final PropertyDescriptor propertyDescriptor,
				final Object bean) {
			this.propertyDescriptor = propertyDescriptor;
			this.bean = bean;
		}

		public Object getValue() {
			Object result = null;
			final Method readMethod = this.propertyDescriptor.getReadMethod();
			try {
				result = readMethod.invoke(this.bean);
			} catch (final Throwable t) {
				throw new RuntimeException("failed to write property", t); //$NON-NLS-1$
			}
			return result;
		}

		public void setValue(final Object value) {
			final Method writeMethod = this.propertyDescriptor.getWriteMethod();
			try {
				writeMethod.invoke(this.bean, value);
			} catch (final Throwable t) {
				throw new RuntimeException("failed to write property", t); //$NON-NLS-1$
			}
		}
	}

	private static ILogger logger;

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

		Contract.requireNotNull("parent", parent); //$NON-NLS-1$
		Contract.requireNotNull("enumValues", enumValues); //$NON-NLS-1$
		Contract.require(enumValues.length > 0, "enumValues.length > 0"); //$NON-NLS-1$
		Contract.requireNotNull("boundBean", boundBean); //$NON-NLS-1$
		Contract.requireNotNull("propertyName", propertyName); //$NON-NLS-1$
		Contract
				.require(propertyName.length() > 0, "propertyName.length() > 0"); //$NON-NLS-1$

		final PropertyDescriptor propertyDescriptor = new PropertyDescriptor(
				propertyName, boundBean.getClass());

		Contract
				.require(propertyDescriptor.getPropertyType().equals(
						enumValues[0].getClass()),
						"propertyDescriptor.getPropertyType().equals(enumValues.getClass())"); //$NON-NLS-1$

		final PropertyEditorUtil propertyEditor = new PropertyEditorUtil(
				propertyDescriptor, boundBean);

		final ComboViewer result = new ComboViewer(parent, SWT.READ_ONLY);

		result.setContentProvider(new ArrayContentProvider());
		result.setComparator(new ViewerComparator(new Comparator<Object>() {
			
		    @Override
            public int compare(final Object o1, final Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		}));
		result.setInput(enumValues);

		final Object startSelection = propertyEditor.getValue();
		if (startSelection != null) {
			result.setSelection(new StructuredSelection(startSelection));
		}

		result.addSelectionChangedListener(new ISelectionChangedListener() {
			
		    @Override
            public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				final Object selectedElement = selection.getFirstElement();
				if (propertyEditor.getValue() != selectedElement) {
					propertyEditor.setValue(selectedElement);
					EditorUIUtils.logger.logDebugMessage(this,
							"#selectionListener.selectionChanged(SelectionChangedEvent): Set Property " //$NON-NLS-1$
									+ propertyName + " to " + selectedElement); //$NON-NLS-1$
				}
			}
		});

		final PropertyChangeListener propertyChangeListenerOnBoundBean = new PropertyChangeListenerImplementation(
				propertyName, propertyEditor, result);
		boundBean.addPropertyChangeListener(propertyChangeListenerOnBoundBean);

		result.getCombo().addDisposeListener(new DisposeListener() {
			
		    @Override
            public void widgetDisposed(final DisposeEvent e) {
				boundBean
						.removePropertyChangeListener(propertyChangeListenerOnBoundBean);
			}
		});

		Contract.ensureResultNotNull(result);
		return result;
	}

	static public boolean isValidDigit(final String value) {
		final char[] charArray = value.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (!Character.isDigit(charArray[i])) {
				return false;
			}
		}
		return true;
	}

	public static void staticInject(final ILogger l) {
		EditorUIUtils.logger = l;
	}

	/**
	 * Creates a {@link String} of the message and stack trace reported by given
	 * {@link Throwable}.
	 * 
	 * @param t
	 *            The {@link Throwable}, not null.
	 * @return A string containing all specified data, not null, may empty.
	 */
	static public String throwableAsMessageString(final Throwable t) {
		Contract.requireNotNull("t", t); //$NON-NLS-1$

		String result = null;

		final StringWriter resultWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(resultWriter);
		t.printStackTrace(printWriter);
		printWriter.flush();
		resultWriter.flush();
		result = resultWriter.toString();

		printWriter.close();
		try {
			resultWriter.close();
		} catch (final IOException e) {
			// Ignored.
		}

		Contract.ensureResultNotNull(result);
		return result;
	}

	private EditorUIUtils() {
		// Avoid instances of this class.
	}
}
