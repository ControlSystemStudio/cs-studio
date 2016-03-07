package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.IPropertyChangeListener;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.commands.ChangeDynamicsCommand;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService;
import org.csstudio.sds.ui.internal.properties.view.DynamicAspectsWizard;
import org.csstudio.sds.ui.internal.properties.view.ModalWizardDialog;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Abstract base class for all section types. All property sections should
 * inherit from here, to ensure a common look and feel when editing properties
 * using the tabbed properties view.
 *
 * @author Sven Wende
 *
 * @param <E>
 *            the type of {@link WidgetProperty} that will be edited using this
 *            section
 *
 *
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBaseSection<E extends WidgetProperty> extends AbstractPropertySection
        implements IPropertyChangeListener {
    protected static final Color COLOR_CONTROL_ACTIVE = CustomMediaFactory.getInstance()
            .getColor(255, 255, 64);
    protected static final Color COLOR_CONTROL_INACTIVE = CustomMediaFactory.getInstance()
            .getColor(255, 0, 0);
    protected static final Color COLOR_DIFFERENT_LABEL = CustomMediaFactory.getInstance()
            .getColor(109, 34, 124);
    protected static final Color COLOR_SHARED_VALUE = CustomMediaFactory.getInstance()
            .getColor(CustomMediaFactory.COLOR_BLACK);

    static final int STANDARD_WIDGET_WIDTH = 150;
    // static final int STANDARD_CCOMBO_WIDTH = STANDARD_WIDGET_WIDTH + 10;
    static final int STANDARD_CCOMBO_WIDTH = STANDARD_WIDGET_WIDTH;
    static final int STANDARD_WIDGET_HEIGHT = 12;
    static final int STANDARD_CCOMBO_HEIGHT = STANDARD_WIDGET_HEIGHT + 2;
    static final int STANDARD_IMAGEBUTTON_WIDTH = 24;

    private String label;
    private String propertyId;

    protected AbstractWidgetModel selectedWidget;
    protected List<AbstractWidgetModel> selectedWidgets;
    protected E mainWidgetProperty;
    private CommandStack commandStack;
    // private Button openDynamicsButton;
    // private Button removeDynamicsButton;
    private Composite compositeForControls;
    private CLabel nameLabel;
    private ImageHyperlink nameHyperlink;
    private ImageHyperlink editSettingsButton;
    private ImageHyperlink removeSettingsButton;

    /**
     * Constructs the section.
     *
     * @param propertyId
     *            the id of the property beeing edited
     */
    public AbstractBaseSection(String propertyId) {
        assert propertyId != null;
        this.propertyId = propertyId;
    }

    protected Font getDefaultFont() {
        return CustomMediaFactory.getInstance().getFont("Arial", 9, SWT.NONE);
    }

    /**
     * Returns the property beeing edited. If multiple widgets are selected -
     * this will be the property of the primary selected widget.
     *
     * @return the property beeing edited (of the primary selected widget) or
     *         null
     */
    public E getMainWidgetProperty() {
        return mainWidgetProperty;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final void createControls(final Composite parent,
                                     TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);

        // .. main composite
        Composite composite = getWidgetFactory().createComposite(parent);
        GridLayoutFactory.swtDefaults().numColumns(6).margins(0, 2).spacing(0, 2)
                .applyTo(composite);

        // .. info icon with additional help (will be invisible if no additional
        // help is available)
        nameHyperlink = getWidgetFactory().createImageHyperlink(composite, SWT.NONE);
        GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).align(SWT.BEGINNING, SWT.BEGINNING)
                .applyTo(nameHyperlink);
        Image image = CustomMediaFactory.getInstance().getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
                                                                          "icons/info.png");
        nameHyperlink.setActiveImage(image);
        nameHyperlink.setImage(image);
        nameHyperlink.setVisible(false);

        // .. name of the property
        nameLabel = getWidgetFactory().createCLabel(composite, label);
        GridDataFactory.fillDefaults().hint(STANDARD_WIDGET_WIDTH, SWT.DEFAULT)
                .align(SWT.BEGINNING, SWT.BEGINNING).applyTo(nameLabel);

        // .. composite for controls that are added by subclasses
        compositeForControls = getWidgetFactory().createComposite(composite);
        GridDataFactory.fillDefaults().hint(STANDARD_WIDGET_WIDTH * 3 + 10, getMinimumHeight())
                .applyTo(compositeForControls);

        editSettingsButton = getWidgetFactory().createImageHyperlink(composite, SWT.NONE);
        editSettingsButton.setImage(CustomMediaFactory.getInstance()
                .getImageFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/dynamics_add.gif"));
        editSettingsButton.setToolTipText("Edit channel configuration");
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
                .applyTo(editSettingsButton);
        editSettingsButton.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                WidgetProperty p = getMainWidgetProperty();
                IPropertyDescriptorFactory desriptorFactory = PropertyDescriptorFactoryService
                        .getInstance().getPropertyDescriptorFactory(p.getPropertyType());
                IPropertyDescriptor descriptor = desriptorFactory.createPropertyDescriptor(p
                        .getId(), p);

                Map<String, String> allInheritedAliases = mainWidgetProperty.getWidgetModel()
                        .getAllInheritedAliases();

                // .. open the dynamics wizard
                DynamicAspectsWizard wizard = new DynamicAspectsWizard(mainWidgetProperty
                                                                               .getDynamicsDescriptor(),
                                                                       allInheritedAliases,
                                                                       descriptor,
                                                                       mainWidgetProperty
                                                                               .getPropertyValue());

                if (wizard != null) {
                    if (Window.OK == ModalWizardDialog.open(Display.getCurrent().getActiveShell(),
                                                            wizard)) {
                        DynamicsDescriptor dynamicsDescriptor = wizard.getDynamicsDescriptor();
                        applyDynamics(dynamicsDescriptor);
                    }
                }
            }
        });

        removeSettingsButton = getWidgetFactory().createImageHyperlink(composite, SWT.NONE);
        removeSettingsButton.setImage(CustomMediaFactory.getInstance()
                .getImageFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/dynamics_remove.gif"));
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
                .applyTo(removeSettingsButton);
        removeSettingsButton.setToolTipText("Removes existing channel configuration.");
        removeSettingsButton.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                applyDynamics(null);
            }
        });

        // .. create a underline
        Composite line = new Composite(composite, SWT.NONE);
        GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 1).span(5, 1).applyTo(line);
        line.setBackground(CustomMediaFactory.getInstance().getColor(247, 239, 231));

        // .. delegate to subclasses
        doCreateControls(compositeForControls, aTabbedPropertySheetPage);
    }

    /**
     * Template method. Subclasses need to implement the controls that should be
     * used to edit the property.
     *
     * @param parent
     *            the parent composite
     *
     * @param aTabbedPropertySheetPage
     *            the current sheet page
     */
    protected abstract void doCreateControls(final Composite parent,
                                             TabbedPropertySheetPage aTabbedPropertySheetPage);

    /**
     * Template method. Subclasses need to update their controls according to
     * the specified widget property.
     *
     * @param widgetProperty
     *            the property of the primary selected widget which can be
     *            changed by this section editor
     */
    protected abstract void doRefreshControls(E widgetProperty);

    /**
     *{@inheritDoc}
     */
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);

        // .. get the command stack of the active editor
        if (part instanceof DisplayEditor) {
            commandStack = ((DisplayEditor) part).getCommandStack();
        }
        // .. remove property change listener from previous property
        if (mainWidgetProperty != null) {
            mainWidgetProperty.removePropertyChangeListener(this);
        }

        // .. get the selected widget (main selection)
        selectedWidget = getFromSelection(selection);

        // .. get all selected widgets
        selectedWidgets = new ArrayList<AbstractWidgetModel>();

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            Iterator<Object> it = structuredSelection.iterator();

            Assert.isNotNull(it);

            while (it.hasNext()) {
                AbstractWidgetModel widget = getFromEditPart(it.next());

                if (widget != null) {
                    selectedWidgets.add(widget);
                }
            }
        }

        // .. get the main widget property to be edited
        mainWidgetProperty = selectedWidget != null ? (E) selectedWidget
                .getPropertyInternal(propertyId) : null;

        // .. update descriptions
        nameHyperlink.setToolTipText("");
        nameHyperlink.setVisible(false);

        if (mainWidgetProperty != null) {
            String description = mainWidgetProperty.getLongDescription();
            if (description != null && description.length() > 0) {
                nameHyperlink.setToolTipText(description);
                nameHyperlink.setVisible(true);
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void aboutToBeHidden() {
        // .. stop listening to the property
        if (mainWidgetProperty != null) {
            mainWidgetProperty.removePropertyChangeListener(this);
        }

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void aboutToBeShown() {
        // .. start listening to the property
        if (mainWidgetProperty != null) {
            mainWidgetProperty.addPropertyChangeListener(this);
        }
    }

    /**
     *{@inheritDoc}
     */
    public final void refresh() {
        if (mainWidgetProperty != null) {
            // .. update label and buttons icons depending on the dynamics
            // configuration
            boolean dynamic = mainWidgetProperty.getDynamicsDescriptor() != null;
            nameLabel.setText(mainWidgetProperty.getDescription());

            int fontStyle = dynamic ? SWT.BOLD : SWT.NORMAL;
            Color color = COLOR_SHARED_VALUE;
            if (!haveAllSelectedWidgetsTheSameValue()) {
                fontStyle = fontStyle | SWT.ITALIC;
                color = COLOR_DIFFERENT_LABEL;
            }
            nameLabel.setFont(CustomMediaFactory.getInstance().getDefaultFont(fontStyle));
            nameLabel.setForeground(color);

            editSettingsButton.setImage(CustomMediaFactory.getInstance()
                    .getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
                                        dynamic ? "icons/dynamics_edit.gif"
                                                : "icons/dynamics_add.gif"));
            removeSettingsButton.setVisible(dynamic);
        }

        // .. delegate to subclasses
        doRefreshControls(mainWidgetProperty);
    }

    private boolean haveAllSelectedWidgetsTheSameValue() {
        if (mainWidgetProperty == null || selectedWidgets.size() < 2) {
            return true;
        }

        for (AbstractWidgetModel widget : selectedWidgets) {
            if (!widget.getPropertyInternal(propertyId).getPropertyValue()
                    .equals(mainWidgetProperty.getPropertyValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void dynamicsDescriptorChanged(DynamicsDescriptor dynamicsDescriptor) {
        refresh();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void propertyManualValueChanged(String propertyId, Object manualValue) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void propertyValueChanged(Object oldValue, Object newValue) {
        refresh();
    }

    /**
     * Cancels editing. May be called by subclasses to implement a cancel
     * behaviour depending on their controls.
     */
    protected final void cancelEditing() {
        refresh();
        nameLabel.setFocus();
    }

    /**
     * Saves the specified value to the properties edited. Will only fire in
     * case the new value does not not equal the old value.
     *
     * @param newValue
     */
    protected void applyPropertyChange(Object newValue) {
        if (newValue != null
                && mainWidgetProperty != null
                && !mainWidgetProperty.getPropertyValue()
                        .equals(mainWidgetProperty.checkValue(newValue))) {

            CompoundCommand chain = new CompoundCommand();

            if (selectedWidgets != null) {
                for (AbstractWidgetModel widget : selectedWidgets) {
                    assert widget.hasProperty(propertyId);
                    chain.add(new SetPropertyCommand(widget, propertyId, newValue));
                }
            }

            commandStack.execute(chain);
        }
        doRefreshControls(mainWidgetProperty);
    }

    /**
     * Saves the specified dynamics descriptor to the properties being edited.
     *
     * @param newDynamicsDescriptor
     *            the new dynamics descriptor
     */

    protected void applyDynamics(DynamicsDescriptor newDynamicsDescriptor) {
        CompoundCommand chain = new CompoundCommand();

        if (selectedWidgets != null) {
            for (AbstractWidgetModel widget : selectedWidgets) {
                assert widget.hasProperty(propertyId);
                chain.add(new ChangeDynamicsCommand(widget.getPropertyInternal(propertyId),
                                                    newDynamicsDescriptor != null ? newDynamicsDescriptor
                                                            .clone() : null));
            }
        }

        commandStack.execute(chain);
    }

    /**
     * Utility method to retrieve the primary selected widget from the specified
     * selection.
     *
     * @param selection
     *            the current selection
     * @return the primary selected widget or null
     */
    private AbstractWidgetModel getFromSelection(ISelection selection) {
        AbstractWidgetModel result = null;

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            return getFromEditPart(structuredSelection.getFirstElement());
        }

        return result;
    }

    /**
     * Utility method to retrieve a widget model from a GEF Editpart.
     *
     * @param o
     *            the Editpart
     * @return the widget or null
     */
    private AbstractWidgetModel getFromEditPart(Object o) {
        AbstractWidgetModel result = null;

        if (o instanceof EditPart) {
            EditPart ep = (EditPart) o;

            if (ep.getModel() instanceof AbstractWidgetModel) {
                result = (AbstractWidgetModel) ep.getModel();
            }
        }

        return result;
    }

}