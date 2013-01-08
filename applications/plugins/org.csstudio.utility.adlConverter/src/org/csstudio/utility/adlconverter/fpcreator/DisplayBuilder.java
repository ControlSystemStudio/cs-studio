/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.adlconverter.fpcreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.LinkingContainerModel;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.eclipse.core.runtime.Path;

/**
 * TODO (jhatje) : 
 * 
 * @author jhatje
 * @since 01.06.2012
 */
public class DisplayBuilder {

    private List<DisplayModel> _modelList = new ArrayList<DisplayModel>();
    
    public DisplayModel[] buildDisplayModel(RecordConfiguration config) {
        EpicsRecordTypesEnum[] recordTypes = EpicsRecordTypesEnum.values();
        for (EpicsRecordTypesEnum recordType : recordTypes) {
            createFpForType(config, recordType);
        }
        return _modelList.toArray(new DisplayModel[_modelList.size()]);
    }

    private void createFpForType(RecordConfiguration config, EpicsRecordTypesEnum recordType) {
        EpicsRecord epicsRecord;
        EpicsRecord firstRecord = null;
        EpicsRecord lastRecord = null;
        ArrayList<EpicsRecord> recordsOfType = config.getRecordsOfType(recordType);
        int displayNo = 0;
        while (recordsOfType.size() > 0) {
            displayNo++;
            DisplayModel model = createDisplayModel(recordType, displayNo);
            int heightOffset = 0;
            int fpPosInLine=0;
            int horizontalPos=0;
            for (int i = 0; i < 16; i++) {
                if (i == 8) {
                    heightOffset=heightOffset+455;
                    horizontalPos=5;
                    fpPosInLine=0;
                }
                horizontalPos=150*fpPosInLine+5;
                fpPosInLine++;
                if (recordsOfType.size() > 0) {
                    epicsRecord = recordsOfType.remove(0);
                    lastRecord = epicsRecord;
                    if (firstRecord == null) {
                        firstRecord = epicsRecord;
                    }
                    System.out.println(i + " total size: " + recordsOfType.size() + " - " + epicsRecord.getName());
                    model.addWidget(buildContainer(epicsRecord, heightOffset+50, horizontalPos));
                } else {
                    break;
                }
            }
            setTitleOnDisplay(model, recordType, displayNo, firstRecord, lastRecord);
            firstRecord = null;
            lastRecord = null;
            _modelList.add(model);
        }
    }

    private void setTitleOnDisplay(DisplayModel model, EpicsRecordTypesEnum recordType, int displayNo, EpicsRecord firstRecord, EpicsRecord lastRecord) {
        LabelModel titelLabel = new LabelModel();
        titelLabel.setPropertyValue(LabelModel.PROP_POS_X, 300);
        titelLabel.setPropertyValue(LabelModel.PROP_POS_Y, 5);
        titelLabel.setPropertyValue(LabelModel.PROP_WIDTH, 600);
        titelLabel.setPropertyValue(LabelModel.PROP_HEIGHT, 40);
        titelLabel.setPropertyValue(LabelModel.PROP_TEXTVALUE, recordType.getTypeNameEnum() + "-Records " + firstRecord.getName() + " - " + lastRecord.getName());
        titelLabel.setPropertyValue(LabelModel.PROP_COLOR_FOREGROUND, "#000000");
        titelLabel.setPropertyValue(LabelModel.PROP_FONT, ColorAndFontUtil.toFontString("Arial", 18));
        model.setPropertyValue(DisplayModel.PROP_NAME, recordType.getTypeNameEnum() + "-Records " + firstRecord.getName() + " - " + lastRecord.getName());
        model.addWidget(titelLabel);
    }

    private DisplayModel createDisplayModel(EpicsRecordTypesEnum recordType, int displayNo) {
        DisplayModel model = new DisplayModel();
        model.setPropertyValue(DisplayModel.PROP_WIDTH, 1240);
        model.setPropertyValue(DisplayModel.PROP_HEIGHT, 970);
        return model;
    }

    private LinkingContainerModel buildContainer(EpicsRecord record, int height, int width) {
        System.out.println("record: " + record.getName() + " w: " + width + " h: " + height);
        LinkingContainerModel linkingContainerModel = new LinkingContainerModel();
        linkingContainerModel.setPropertyValue(LinkingContainerModel.PROP_RESOURCE,
                                               record.getPath());
        linkingContainerModel.setPropertyValue(LinkingContainerModel.PROP_POS_Y,
                                               height);
        linkingContainerModel.setPropertyValue(LinkingContainerModel.PROP_POS_X,
                                               width);
        linkingContainerModel.setPropertyValue(LinkingContainerModel.PROP_HEIGHT,
                                               450);
        linkingContainerModel.setPropertyValue(LinkingContainerModel.PROP_WIDTH,
                                               145);
        linkingContainerModel.addAlias("name", record.getNameWithoutTypeSuffix());
        return linkingContainerModel;
    }
}
