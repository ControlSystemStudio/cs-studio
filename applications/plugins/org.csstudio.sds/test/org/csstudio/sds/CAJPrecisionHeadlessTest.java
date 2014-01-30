/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
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
package org.csstudio.sds;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.csstudio.dal.CssApplicationContext;
import org.csstudio.domain.common.junit.ConditionalClassRunner;
import org.csstudio.domain.common.junit.OsCondition;
import org.csstudio.domain.common.junit.RunIf;
import org.csstudio.domain.common.softioc.BasicSoftIocConfigurator;
import org.csstudio.domain.common.softioc.Caget;
import org.csstudio.domain.common.softioc.DBR;
import org.csstudio.domain.common.softioc.ISoftIocConfigurator;
import org.csstudio.domain.common.softioc.SoftIoc;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.internal.connection.BehaviorConnector;
import org.csstudio.sds.model.AbstractTextTypeWidgetModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.TextTypeEnum;
import org.eclipse.core.runtime.FileLocator;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.cosylab.util.CommonException;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 25.11.2011
 */
@RunWith(ConditionalClassRunner.class)
@RunIf(conditionClass = OsCondition.class, arguments = { OsCondition.WIN })
public class CAJPrecisionHeadlessTest {

    private static SoftIoc _softIoc;


    @BeforeClass
    public static void startUpSoftIoc() throws Exception {
        final URL dbBundleResourceUrl = CAJPrecisionHeadlessTest.class.getClassLoader()
                .getResource("resources/db/CAJPrecisionTest.db");
        final URL dbFileUrl = FileLocator.toFileURL(dbBundleResourceUrl);

        final ISoftIocConfigurator cfg = new BasicSoftIocConfigurator().with(new File(dbFileUrl
                                                                                      .getFile()));
        _softIoc = new SoftIoc(cfg);
        _softIoc.start();
        while (!_softIoc.isStartUpDone()) {
            // wait IOC startup finished
        }
        setSystemPropertys();
        Thread.sleep(400);
    }


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test(timeout = 5000)
    public void testCAGetPrecision() throws Exception {
//        while (!_softIoc.isStartUpDone()) {
//            // wait IOC startup finished
//        }
        final Caget caget = new Caget();
        caget.setDbr(DBR.DBR_TIME_STRING);
        caget.setWaitTime(3);
        final String recodName = "DALPrecisionTest1";
        final ArrayList<String> result = caget.caget(recodName);
        assertEquals(8, result.size());
        final String[] split = result.get(4).trim().split(":");
        assertEquals(2, split.length);
        final String value = split[1].trim();
        assertEquals("The display Value of " + recodName + " was wrong: ", "12345.6789", value);
    }

    @Test(timeout = 5000)
    public void testSDSLabelBehaviorDoublePrecision() throws Exception {
        final String recordName = "DALPrecisionTest1";
        final String behaviorId = "behavior.desy.label.connectiondefault";

        final LabelModel labelModel = buildLabelModel(recordName, behaviorId);
        labelModel.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.DOUBLE.getIndex());

        buildConnection(behaviorId, labelModel);

        Thread.sleep(200);
        final String stringValue = labelModel.getStringProperty(LabelModel.PROP_TEXTVALUE);
        assertEquals("The display Value of " + recordName + " was wrong: ",
                     "12345.678901234",
                     stringValue);
    }

    @Test(timeout = 5000)
    public void testSDSLabelBehaviorTextPrecision() throws Exception {
        final String recordName = "DALPrecisionTest1";
        final String behaviorId = "behavior.desy.label.connectiondefault";

        final LabelModel labelModel = buildLabelModel(recordName, behaviorId);
        labelModel.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.TEXT.getIndex());

        buildConnection(behaviorId, labelModel);

        Thread.sleep(200);
        final String stringValue = labelModel.getStringProperty(LabelModel.PROP_TEXTVALUE);
        assertEquals("The display Value of " + recordName + " was wrong: ",
                     "12345.6789",
                     stringValue);
    }

    @Test(timeout = 5000)
    public void testSDSLabelBehaviorExpPrecision() throws Exception {
        final String recordName = "DALPrecisionTest1";
        final String behaviorId = "behavior.desy.label.connectiondefault";

        final LabelModel labelModel = buildLabelModel(recordName, behaviorId);
        labelModel.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.EXP.getIndex());

        buildConnection(behaviorId, labelModel);

        Thread.sleep(200);
        final String stringValue = labelModel.getStringProperty(LabelModel.PROP_TEXTVALUE);
        // TODO (hrickens): Not sure that is the correct way/layer
        assertEquals("The display Value of " + recordName + " was wrong: ",
                     "12345.678901234",
                     stringValue);
    }

    @Test(timeout = 5000)
    public void testSDSLabelBehaviorHexPrecision() throws Exception {
        final String recordName = "DALPrecisionTest1";
        final String behaviorId = "behavior.desy.label.connectiondefault";

        final LabelModel labelModel = buildLabelModel(recordName, behaviorId);
        labelModel.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.HEX.getIndex());

        buildConnection(behaviorId, labelModel);

        Thread.sleep(200);
        final String stringValue = labelModel.getStringProperty(LabelModel.PROP_TEXTVALUE);
        // TODO (hrickens): Not sure that is the correct way/layer
        assertEquals("The display Value of " + recordName + " was wrong: ",
                     "12345.678901234",
                     stringValue);
    }

    @Test(timeout = 5000)
    public void testSDSLabelBehaviorAliasPrecision() throws Exception {
        final String recordName = "DALPrecisionTest1";
        final String behaviorId = "behavior.desy.label.connectiondefault";

        final LabelModel labelModel = buildLabelModel(recordName, behaviorId);
        labelModel.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.ALIAS.getIndex());

        buildConnection(behaviorId, labelModel);

        Thread.sleep(200);
        final String stringValue = labelModel.getStringProperty(LabelModel.PROP_TEXTVALUE);
        assertEquals("The display Value of " + recordName + " was wrong: ",
                     "DALPrecisionTest1",
                     stringValue);
    }

    @Test(timeout = 5000)
    public void testSDSLabelAliasPrecision() throws Exception {
        final String recordName = "DALPrecisionTest1";
        final LabelModel labelModel = buildLabelModel(recordName, null);
        labelModel.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.ALIAS.getIndex());
        labelModel.setJavaType(String.class);
        buildConnection(null, labelModel);

    }


    /**
     * @param behaviorId
     * @param labelModel
     * @throws InterruptedException
     */
    private void buildConnection(final LabelModel labelModel) throws InterruptedException {


//        ConnectionUtilNew.connectDynamizedProperties(labelModel.getPropertyInternal(name), labelModel.getMainPvAdress(), false, labelModel.get, labelModel.getRoot().getRuntimeContext());
//
//        final IProcessVariableAddress mainPv = labelModel.getMainPvAdress();
//        final Class<?> javaType = labelModel.getJavaType();
//        System.err.println("javaType: "+javaType.getSimpleName());
//        final RemoteInfo remoteInfo = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
//                                                     mainPv.getProperty(),
//                                                     null,
//                                                     null);
//        final ConnectionParameters connectionParameters = new ConnectionParameters(remoteInfo,
//                                                                                   javaType);
//
//        new SinglePropertyReadConnector(connectionParameters, javaType, "");

    }
    private void buildConnection(final String behaviorId, final LabelModel labelModel) throws InterruptedException {
        final AbstractBehavior behavior = SdsPlugin.getDefault().getBehaviourService()
                .getBehavior(behaviorId, labelModel.getTypeID());

        final IProcessVariableAddress mainPv = labelModel.getMainPvAdress();
        final Class<?> javaType = labelModel.getJavaType();
        final RemoteInfo remoteInfo = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
                                                     mainPv.getProperty(),
                                                     null,
                                                     null);
        System.err.println("javaType: "+javaType.getSimpleName());
        final ConnectionParameters connectionParameters = new ConnectionParameters(remoteInfo,
                                                                                   javaType);
        if (behavior != null) {
            final BehaviorConnector behaviorConnector = new BehaviorConnector(labelModel,
                                                                              connectionParameters,
                                                                              behavior);

            // .. connect to the control system to receive dynamic values and meta data
            register(connectionParameters, behaviorConnector);

            // .. connect to the widget to receive manual changes and forward them to the
            // control system
            final String[] settablePropertyIds = behavior.getSettablePropertyIds();

            for (final String id : settablePropertyIds) {
                labelModel.addPropertyChangeListener(id, behaviorConnector);
            }
        }
    }

    /**
     * @param recordName
     * @param behaviorId
     * @return
     */
    private LabelModel buildLabelModel(final String recordName, final String behaviorId) {
        final DisplayModel displayModel = new DisplayModel();
        final LabelModel labelModel = new LabelModel();
        labelModel.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "${Invalid}");
        labelModel.setPrimarPv(recordName);
        if(behaviorId!=null&&!behaviorId.isEmpty()) {
            labelModel.setPropertyValue(AbstractWidgetModel.PROP_BEHAVIOR, behaviorId);
        }
        displayModel.addWidget(labelModel);
        return labelModel;
    }

    /**
     *
     */
    private static void setSystemPropertys() {
        System.setProperty("dal.plugs", "EPICS");
        System.setProperty("dal.plugs.default", "EPICS");
        System.setProperty("dal.propertyfactory.EPICS",
                           "org.csstudio.dal.epics.PropertyFactoryImpl");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "YES");
        System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
        System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", "15.0");
        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port", "5065");
        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", "5064");
        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", "16384");
    }

    public void register(final ConnectionParameters parameters, final ChannelListener listener) {
        try {
            final SimpleDALBroker broker = SimpleDALBroker
                    .newInstance(new CssApplicationContext("CSS"));

            if (broker != null) {
                broker.registerListener(parameters, listener);
            }
        } catch (final InstantiationException e) {
//            CentralLogger.getInstance().error(this, e);
        } catch (final CommonException e) {
//            CentralLogger.getInstance().error(this, e);
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void stopSoftIoc() throws Exception {
        if (_softIoc != null) {
            _softIoc.stop();
        }
    }

}
