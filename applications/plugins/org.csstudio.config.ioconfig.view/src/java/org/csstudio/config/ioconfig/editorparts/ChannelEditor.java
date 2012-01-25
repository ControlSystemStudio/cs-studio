/*
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.editorparts;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.SensorsDBO;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.IOConfigActivatorUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for {@link ChannelDBO} node's
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @since 21.05.2010
 */
public class ChannelEditor extends AbstractNodeEditor<ChannelDBO> {

    /**
     * @author hrickens
     * @author $Author: $
     * @since 30.09.2010
     */
    private final class AssembleEpicsAddSelectionListener implements
    SelectionListener {

        public AssembleEpicsAddSelectionListener() {
            // Default Constructor.
        }

        public void setChannelName(@Nonnull final ChannelDBO channel,
                                   @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
            if (moduleChannelPrototype.getType() != channel.getParent()
                    .getStructureType()) {
                channel.getParent()
                .setStructureType(moduleChannelPrototype.getType());
                if (channel.getParent().isSimple()) {
                    channel.setChannelType(moduleChannelPrototype.getType());
                }
            }
            channel.setName(moduleChannelPrototype.getName());
        }

        public void setWidgetName(@Nonnull final ChannelDBO channel,
                                  @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
            String name = moduleChannelPrototype.getName();
            name += !moduleChannelPrototype.isStructure()?"": channel.getSortIndex();
            final Text nameWidget = getNameWidget();
            if (nameWidget != null && !name.equals(channel.getName())) {
                nameWidget.setText(name);
            }
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doAssemble();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doAssemble();
        }

        private void doAssemble() {
            final ChannelDBO channel = getNode();
            final GSDModuleDBO module = channel.getModule().getGSDModule();
            if (module != null) {
                final TreeSet<ModuleChannelPrototypeDBO> moduleChannelPrototypes =
                    module.getModuleChannelPrototypeNH();
                final ModuleChannelPrototypeDBO[] array =
                    moduleChannelPrototypes
                    .toArray(new ModuleChannelPrototypeDBO[0]);
                final ModuleChannelPrototypeDBO moduleChannelPrototype =
                    array[channel
                          .getParent()
                          .getSortIndex()];
                channel.setStatusAddressOffset(moduleChannelPrototype.getShift());
                channel.setChannelNumber(moduleChannelPrototype.getOffset());
                setWidgetName(channel, moduleChannelPrototype);
                setChannelName(channel, moduleChannelPrototype);
            }
            final String oldAdr = channel.getEpicsAddressString();
            try {
                channel.assembleEpicsAddressString();
                final String newAdr = channel.getEpicsAddressString();
                final Text addressText = getAddressText();
                if (addressText != null && !newAdr.equals(oldAdr)) {
                    addressText.setText(newAdr);
                }
            } catch (final PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null,
                                               "Can't calulate Epics Address. Database error!",
                                               e);
                LOG.error("Can't calulate Epics Address. Database error!", e);
            }
        }
    }

    public static final String ID = "org.csstudio.config.ioconfig.view.editor.channel";
    protected static final Logger LOG = LoggerFactory.getLogger(ChannelEditor.class);

    /**
     * The EPICS address string of the Channel.
     */
    private Text _addressText;
    /**
     * The GSD File of the parent Module.
     */
    private Text _ioNameText;
    private ComboViewer _sensorsViewer;

    /**
     * Cancel all change value.
     */
    @Override
    public final void cancel() {
        super.cancel();
        final Spinner indexSpinner = getIndexSpinner();
        if (indexSpinner != null) {
            indexSpinner.setSelection((Short) indexSpinner.getData());
        }
        final Text nameWidget = getNameWidget();
        if (nameWidget != null) {
            setName((String) nameWidget.getData());
        }
    }

    @Override
    public final void createPartControl(@Nonnull final Composite parent) {
        super.createPartControl(parent);
        NodeMap.countChannelConfigComposite();
        setSavebuttonEnabled(null, getNode().isPersistent());
        final String[] heads = {"Channel settings", "Documents", "GSD File List" };
        general(heads[0]);
        if (getNode().isDirty()) {
            perfromSave();
        }
        _ioNameText.setFocus();
        selecttTabFolder(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        // Channel Settings
        final ChannelDBO channel = getNode();
        channel.setIoName(_ioNameText.getText());
        final Text nameWidget = getNameWidget();
        channel.setName(nameWidget==null?"":nameWidget.getText());
        _ioNameText.setData(_ioNameText.getText());
        if (_sensorsViewer != null) {
            final SensorsDBO firstElement =
                (SensorsDBO) ((StructuredSelection) _sensorsViewer
                        .getSelection()).getFirstElement();
            channel.setCurrentValue(Integer.toString(firstElement.getId()));
            final Combo combo = _sensorsViewer.getCombo();
            combo.setData(combo.getSelectionIndex());
        }
        // Document
        final Set<DocumentDBO> docs = getDocumentationManageView().getDocuments();
        channel.setDocuments(docs);
        save();
    }

    /**
     *
     * @param ioNameText
     *            Set the new IOName for this channel.
     */
    public final void setIoNameText(@Nonnull final String ioNameText) {
        _ioNameText.setText(ioNameText);
    }

    /**
     * @param comp
     */
    private void createEpicsAddress(@Nonnull final Composite comp) {
        final Group epicsAddressGroup = new Group(comp, SWT.NONE);
        final GridLayoutFactory glf = GridLayoutFactory.fillDefaults().numColumns(2);
        epicsAddressGroup.setLayout(glf.create());
        epicsAddressGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        epicsAddressGroup.setText("EPICS address string: ");

        setAddressText(new Text(epicsAddressGroup, SWT.FLAT | SWT.SINGLE));
        final Text addressText = getAddressText();
        if (addressText != null) {
            final ChannelDBO channel = getNode();
            if (channel.getName() != null) {
                addressText.setText(channel.getEpicsAddressString());
            }
            addressText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                                                   true, 1, 1));
            addressText.setEditable(false);
            addressText.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(@Nonnull final ModifyEvent e) {
                    setSavebuttonEnabled("epicsAddressString", true);
                }
            });
        }
        final Button assembleButton = new Button(epicsAddressGroup, SWT.FLAT);
        final GridDataFactory gdf = GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER);
        assembleButton.setLayoutData(gdf.create());
        assembleButton.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID,
        "icons/refresh.gif").createImage());
        assembleButton.setToolTipText("Refresh the EPICS Address String\n and save it into the DB");
        assembleButton.addSelectionListener(new AssembleEpicsAddSelectionListener());
    }

    /**
     * @param comp
     */
    private void createIndex(@Nonnull final Composite comp) {
        final Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
        gName.setLayout(new GridLayout(3, false));
        setNameWidget(new Text(gName, SWT.BORDER | SWT.SINGLE));
        final Text nameWidget = getNameWidget();
        final ChannelDBO channel = getNode();
        if (nameWidget != null) {
            nameWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
            setText(nameWidget, channel.getName(), 255);
            nameWidget.setEditable(false);
        }
        final Spinner indexSpinner =
            ConfigHelper.getIndexSpinner(gName,
                                         channel,
                                         getMLSB(),
                                         "Index",
                                         getProfiBusTreeView(), 99);
        setIndexSpinner(indexSpinner);
        indexSpinner.setEnabled(false);
    }

    /**
     * @param comp
     * @return
     */
    @Nonnull
    private Group createIOName(@Nonnull final Composite comp) {
        final Group ioNameGroup = new Group(comp, SWT.NONE);
        ioNameGroup.setLayout(new GridLayout(1, false));
        ioNameGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ioNameGroup.setText("IO Name: ");
        _ioNameText = new Text(ioNameGroup, SWT.BORDER | SWT.SINGLE);
        _ioNameText.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        setText(_ioNameText, getNode().getIoName(), 255);
        return ioNameGroup;
    }

    /**
     * @param comp
     */
    private void createSensorField(@Nonnull final Composite comp) {
        final String ioName = getNode().getIoName();
        if (ioName != null && !ioName.isEmpty()) {
            List<SensorsDBO> loadSensors = null;
            try {
                loadSensors = Repository.loadSensors(ioName);
            } catch (final PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, "Can't read sensor ID's from Database", e);
                LOG.error("Can't read sensor ID's from Database", e);
            }
            if (loadSensors != null && loadSensors.size() > 0) {
                makeSensorField(comp, loadSensors);
            }
        }
    }

    /**
     * @param comp
     */
    private void createSize(@Nonnull final Composite comp) {
        final Group sizeGroup = new Group(comp, SWT.NONE);
        sizeGroup.setLayout(new GridLayout(1, false));
        sizeGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
        sizeGroup.setText("Size: ");
        final Text sizeText = new Text(sizeGroup, SWT.SINGLE | SWT.RIGHT);
        sizeText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        final ChannelDBO channel = getNode();
        setText(sizeText, channel.getChSize(), 255);
        sizeText.setEditable(false);
    }


    /**
     * @param head
     *            is TabHead Text
     */
    private void general(@Nonnull final String head) {
        final Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 5, 300, 290);
        comp.setLayout(new GridLayout(4, false));

        createIndex(comp);

        final Group ioNameGroup = createIOName(comp);

        createSensorField(comp);
        createEpicsAddress(comp);
        createSize(comp);

        // Description Group
        makeDescGroup(comp, 3);
        final Text descText = getDescText();
        if(descText != null) {
            final Control[] tabList = new Control[] {ioNameGroup, descText.getParent()};
            comp.setTabList(tabList);
        }
    }

    /**
     * @param comp
     * @param loadSensors
     */
    private void makeSensorField(@Nonnull final Composite comp,
                                 @Nonnull final List<SensorsDBO> loadSensors) {
        final Group sensorsGroup = new Group(comp, SWT.NONE);
        sensorsGroup.setLayout(new GridLayout(1, false));
        sensorsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        sensorsGroup.setText("Sensors: ");
        _sensorsViewer = new ComboViewer(sensorsGroup, SWT.READ_ONLY);
        _sensorsViewer.setLabelProvider(new LabelProvider());
        _sensorsViewer.setContentProvider(new ArrayContentProvider());
        _sensorsViewer.setInput(loadSensors.toArray());
        int id = 0;
        final ChannelDBO channel = getNode();
        final String currentValue = channel.getCurrentValue();
        if (currentValue != null && currentValue.length() > 0) {
            id = Integer.parseInt(currentValue);
        } else {
            id = loadSensors.get(0).getId();
            channel.setCurrentValue(Integer.toString(id));
            channel.setDirty(true);
        }
        _sensorsViewer.getCombo().select(0);
        for (final SensorsDBO sensors : loadSensors) {
            if (id == sensors.getId()) {
                _sensorsViewer.setSelection(new StructuredSelection(sensors));
            }
        }
        _sensorsViewer.getCombo().setData(_sensorsViewer.getCombo().getSelectionIndex());
        _sensorsViewer.getCombo().addModifyListener(getMLSB());
    }

    @CheckForNull
    protected final Text getAddressText() {
        return _addressText;
    }

    protected final void setAddressText(@CheckForNull final Text addressText) {
        _addressText = addressText;
    }

}
