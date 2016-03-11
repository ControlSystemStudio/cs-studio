package org.csstudio.dct.ui.graphicalviewer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.dal.Timestamp;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.UiExecutionService;
import org.csstudio.dct.ui.graphicalviewer.model.Connection;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.dct.ui.graphicalviewer.view.ConnectionAnchor;
import org.csstudio.dct.ui.graphicalviewer.view.RecordFigure;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for {@link RecordNode}.
 *
 * @author Sven Wende
 *
 */
public class RecordNodeEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener, IProcessVariableValueListener {

    private static final Logger LOG = LoggerFactory.getLogger(RecordNodeEditPart.class);

    private ConnectionAnchor anchorRight, anchorLeft, anchorTop, anchorBottom;

    private ChopboxAnchor anchorCenter;

    private List<ConnectionAnchor> outgoingAnchors, incomingAnchors;

    private static final Map<ConnectionState, RGB> colors = new HashMap<ConnectionState, RGB>();

    static {
        colors.put(ConnectionState.INITIAL, new RGB(255, 255, 0));
        colors.put(ConnectionState.CONNECTED, new RGB(0, 255, 0));
        colors.put(ConnectionState.CONNECTION_LOST, new RGB(255, 0, 0));
        colors.put(ConnectionState.DISCONNECTED, new RGB(255, 0, 0));
        colors.put(ConnectionState.CONNECTION_FAILED, new RGB(255, 0, 0));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();

        getCastedModel().addPropertyChangeListener(this);

        final IRecord record = getCastedModel().getElement();

        if (!record.isAbstract()) {
            try {
                final String name = AliasResolutionUtil.getEpicsNameFromHierarchy(record);
                final String resolvedName = ResolutionUtil.resolve(name, record);
                final IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(resolvedName);
                ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService().register(this, pv, ValueType.DOUBLE);

                final RecordFigure figure = (RecordFigure) getFigure();

                // .. set initial color for the connection state
                figure.setConnectionIndictorColor(CustomMediaFactory.getInstance().getColor(colors.get(ConnectionState.INITIAL)));

                // .. prepare record information for tooltip
                final Map<String, String> fields = record.getFinalFields();

                final List<String> keys = new ArrayList<String>(fields.keySet());
                Collections.sort(keys);
                final LinkedHashMap<String, String> infos = new LinkedHashMap<String, String>();
                for (final String key : keys) {
                    final String value = fields.get(key);

                    if (value != null && value.length() > 0) {
                        infos.put(key, value);
                    }
                }
                figure.setRecordInformation(infos);

            } catch (final AliasResolutionException e) {
                LOG.error("Error: ", e);
            }
        }

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void deactivate() {
        getCastedModel().removePropertyChangeListener(this);

        final List l = getTargetConnections();
        for (int i = 0; i < l.size(); i++) {
            ((EditPart) l.get(i)).deactivate();
        }
        super.deactivate();

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final RecordFigure figure = new RecordFigure(getCastedModel().getCaption());

        outgoingAnchors = new ArrayList<ConnectionAnchor>();

        for (int i = 0; i < getCastedModel().getSourceConnections().size(); i++) {
            final ConnectionAnchor a = new ConnectionAnchor(figure);
            a.offsetH = 70;
            a.offsetV = i * 5 + 3;
            outgoingAnchors.add(a);
        }

        incomingAnchors = new ArrayList<ConnectionAnchor>();

        for (int i = 0; i < getCastedModel().getTargetConnections().size(); i++) {
            final ConnectionAnchor a = new ConnectionAnchor(figure);
            a.offsetH = 0;
            a.offsetV = i * 5 + 3;
            incomingAnchors.add(a);
        }

        anchorCenter = new ChopboxAnchor(figure);

        anchorLeft = new ConnectionAnchor(figure);

        anchorRight = new ConnectionAnchor(figure);
        anchorTop = new ConnectionAnchor(figure);
        anchorBottom = new ConnectionAnchor(figure);

        anchorLeft.topDown = false;
        anchorRight.topDown = false;
        anchorTop.topDown = false;
        anchorBottom.topDown = false;

        createOrUpdateAnchorsLocations();

        Color bgcolor = CustomMediaFactory.getInstance().getColor(100, 100, 255);
        Color fgcolor = CustomMediaFactory.getInstance().getColor(255, 255, 255);

        // .. render passive records with white background color
        final String scanFieldSetting = getCastedModel().getElement().getFinalFields().get("SCAN");
        if ("passive".equalsIgnoreCase(scanFieldSetting)) {
            bgcolor = CustomMediaFactory.getInstance().getColor(255, 255, 255);
            fgcolor = CustomMediaFactory.getInstance().getColor(0, 0, 0);
        }

        figure.setBackgroundColor(bgcolor);
        figure.setForegroundColor(fgcolor);

        // .. render connection state
        figure.setConnectionIndictorColor(bgcolor);

        return figure;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart connectionEditpart) {
        final Connection connection = (Connection) connectionEditpart.getModel();
        final int index = getCastedModel().getSourceConnections().indexOf(connection);
        return anchorCenter;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getSourceConnectionAnchor(final Request request) {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getTargetConnectionAnchor(final ConnectionEditPart connectionEditpart) {
        return anchorCenter;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getTargetConnectionAnchor(final Request request) {
        return null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List getModelSourceConnections() {
        return getCastedModel().getSourceConnections();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List getModelTargetConnections() {
        return getCastedModel().getTargetConnections();
    }

    protected RecordFigure getCastedFigure() {
        return (RecordFigure) getFigure();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void connectionStateChanged(final ConnectionState connectionState) {
        UiExecutionService.getInstance().queue(new Runnable() {
            public void run() {
                getCastedFigure().setConnectionIndictorColor(CustomMediaFactory.getInstance().getColor(colors.get(connectionState)));

                final LinkedHashMap<String, String> infos = new LinkedHashMap<String, String>();
                infos.put("State:", connectionState.name());

                getCastedFigure().setConnectionInformation(infos);
            }
        });

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void errorOccured(final String error) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void valueChanged(final Object value, final Timestamp timestamp) {
    }

    private RecordNode getCastedModel() {
        return (RecordNode) getModel();
    }

    private void createOrUpdateAnchorsLocations() {
        final Dimension size = new Dimension(70, 30);

        anchorLeft.offsetH = 0;
        anchorLeft.offsetV = size.height / 2;

        anchorRight.offsetH = size.width;
        anchorRight.offsetV = size.height / 2;

        anchorTop.offsetH = size.width / 2;
        anchorTop.offsetV = 0;

        anchorBottom.offsetH = size.width / 2;
        anchorBottom.offsetV = size.height;
    }

}
