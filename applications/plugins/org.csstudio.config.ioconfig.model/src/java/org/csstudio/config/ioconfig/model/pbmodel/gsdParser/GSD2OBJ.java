package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

/*******************************************************************************
 * Read all general data from GSD-File and set them into * general class model *
 * ********************************************************** Created by:
 * Torsten Boeckmann * Date: 15. February 2006 *
 * ********************************************************** This class set all
 * general data from GSD-File in a * general gsd object. *
 * ********************************************************** * last changed:
 * ................... * Grounds of changed: ................... *
 * ................... * Revision: * Status: Tested with gsd-files *
 * beckhoff.gsd * bimf5801.gsd * rmt3051.gsd * Sipart.gsd * wagob750.gsd *
 * softb203.gsd * Siem80A6.gsd * YP0004C2 * * Precondition: * Postcondition: * *
 ******************************************************************************/
//TODO (hrickens) [03.05.2011]: Raus damit!
public class GSD2OBJ {

    /**
     * 
     * @param gsdFile
     *            The String representation of the GSD File.
     * @param generalStruct
     *            The GSD Model to fill.
     */
    public final void setGeneralStruct(final String gsdFile, final GsdGeneralModel generalStruct) {
        String bool;
        GSDPROPERTY selProperty = new GSDPROPERTY();

        /** ************** Set GSD_Revision ********************* */
        generalStruct.setGsdRevision(
                (byte) getIntermediateValue(selProperty, gsdFile,"GSD_Revision")
        );

        /** ************** Set Vendor Name ********************* */
        generalStruct.setVendorName(
                selProperty.selectPropertyValue(gsdFile, "Vendor_Name")
        );

        /** ************** Set Model Name ********************* */
        generalStruct.setModelName(
                selProperty.selectPropertyValue(gsdFile, "Model_Name")
        );

        /** ************** Set Revision ********************* */
        generalStruct.setRevision(
                selProperty.selectPropertyValue(gsdFile, "Revision")
        );

        /** ************** Set RevisionNumber ******************* */
        generalStruct.setRevisionNumber(
                (byte) getIntermediateValue(selProperty, gsdFile,"Revision_Number")
        );

        /** ************** Set IdentNumber ********************* */
        generalStruct.setIdentNumber(
                (int)getIntermediateValue(selProperty, gsdFile,"Ident_Number")
        );

        /** ************** Set Protocol Ident ********************* */
        generalStruct.setProtocolIdent(
                (byte) getIntermediateValue(selProperty, gsdFile,"ProtocolIdent")
        );

        /** ************** Set stationType ******************* */
        generalStruct.setStationType(
                (byte) getIntermediateValue(selProperty, gsdFile,"Station_Type")
        );

        /** ************** Set FMS_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "FMS_supp");
        generalStruct.setFmsSupp(bool.equals("1"));
        
        /** ************** Set HardwareRelease ********************* */
        generalStruct.setHardwareRelease(
                selProperty.selectPropertyValue(gsdFile, "HardwareRelease")
        );

        /** ************** Set SoftwareRelease ********************* */
        generalStruct.setSoftwareRelease(
                selProperty.selectPropertyValue(gsdFile, "SoftwareRelease")
        );

        /** ************** Set _9k6_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "9.6_supp");
        generalStruct.setSupp9k6(bool.equals("1"));

        /** ************** Set _19k2_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "19.2_supp");
        generalStruct.setSupp19k2(bool.equals("1"));

        /** ************** Set _31k25_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "31.25_supp");
        generalStruct.setSupp31k25(bool.equals("1"));

        /** ************** Set _45k45_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "45.45_supp");
        generalStruct.setSupp45k45(bool.equals("1"));

        /** ************** Set _93k75_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "93.75_supp");
        generalStruct.setSupp93k75(bool.equals("1"));

        /** ************** Set _187k5_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "187.5_supp");
        generalStruct.setSupp187k5(bool.equals("1"));

        /** ************** Set _500_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "500_supp");
        generalStruct.setSupp500(bool.equals("1"));

        /** ************** Set _1M5_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "1.5M_supp");
        generalStruct.setSupp1M5(bool.equals("1"));

        /** ************** Set _3M_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "3M_supp");
        generalStruct.setSupp3M(bool.equals("1"));

        /** ************** Set _6M_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "6M_supp");
        generalStruct.setSupp6M(bool.equals("1"));

        /** ************** Set _12M_supp ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "12M_supp");
        generalStruct.setSupp12M(bool.equals("1"));
        /** ************** Set MaxTsdr9k6 ********************* */
        generalStruct.setMaxTsdr9k6(
                (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_9.6")
        );

        /** ************** Set maxTsdr19k2 ********************* */
        generalStruct.setMaxTsdr19k2(
                (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_19.2")
        );

        /** ************** Set MaxTsdr31k25 ********************* */
        generalStruct.setMaxTsdr31k25(
                (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_31.25")
        );

        /** ************** Set MaxTsdr45k45 ********************* */
        generalStruct.setMaxTsdr45k45(
                (short) getIntermediateValue(selProperty, gsdFile, "MaxTsdr_45.45")
        );

        /** ************** Set MaxTsdr93k75 ********************* */
        generalStruct.setMaxTsdr93k75(
                (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_93.75")
        );

        /** ************** Set MaxTsdr187k5 ********************* */
        generalStruct.setMaxTsdr187k5(
                (short)getIntermediateValue(selProperty, gsdFile,"MaxTsdr187.5")
        );

        /** ************** Set MaxTsdr500 ********************* */
        generalStruct.setMaxTsdr500(
                (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_500")
        );

        /** ************** Set MaxTsdr1M5 ********************* */
        generalStruct.setMaxTsdr1M5(
                (short)getIntermediateValue(selProperty, gsdFile,"MaxTsdr_1.5M")
        );

        /** ************** Set MaxTsdr3M ********************* */
        generalStruct.setMaxTsdr3M( 
            (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_3M")
        );

        /** ************** Set MaxTsdr6M ********************* */
        generalStruct.setMaxTsdr6M( 
            (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_6M")
        );
        /** ************** Set MaxTsdr12M ********************* */
        generalStruct.setMaxTsdr12M( 
            (short) getIntermediateValue(selProperty, gsdFile,"MaxTsdr_12M")
        );

        /** ************** Set Redundancy ********************* */
        bool = selProperty.selectPropertyValue(gsdFile, "Redundancy");
        generalStruct.setRedundancy(bool.equals("1"));

        /** ************** Set RepeaterCtrlSig ******************* */
        generalStruct.setRepeaterCtrlSig(
            (byte) getIntermediateValue(selProperty, gsdFile,"Repeater_Ctrl_Sig")
        );

        /** *************_24V_PinsNumber ******************* */
        generalStruct.setPins24V( 
            (byte) getIntermediateValue(selProperty, gsdFile,"24V_Pins")
        );

        /** ************** Set ImplementationType ********************* */
        generalStruct.setImplementationType(
                selProperty.selectPropertyValue(gsdFile, "Implementation_Type")
        );

        /** ************** Set BitmapDevice ********************* */
        generalStruct.setBitmapDevice(
                selProperty.selectPropertyValue(gsdFile, "Bitmap_Device")
        );

        /** ************** Set BitmapDiag ********************* */
        generalStruct.setBitmapDiag(
                selProperty.selectPropertyValue(gsdFile, "Bitmap_Diag")
        );

        /** ************** Set BitmapSF *********************** */
        generalStruct.setBitmapSF(
                selProperty.selectPropertyValue(gsdFile, "Bitmap_SF")
        );
    }

    /**
     * @param gsdFile
     *            The String representation of the GSD File.
     * @param masterStruct
     *            The GSD Model to fill.
     * @return !=0 when the gsd File is not a Master.
     */
    public final int setMasterStruct(final String gsdFile, final GsdMasterModel masterStruct) {

        String downloadSupp;
        String uploadSupp;
        String actParamBrctSupp;
        String actParamSupp;

        long intermediateValue = 0;
        byte bTset31k25;
        byte bTset45k45;
        byte bTset93k75;
        byte bTset187k5;
        byte bTset500;
        byte bTset1M5;
        byte bTset3M;
        byte bTset6M;
        byte bTset12M;
        byte bLasLen;
        byte bTsdi9k6;
        byte bTsdi19k2;
        byte bTsdi31k25;
        byte bTsdi45k45;
        byte bTsdi93k75;
        byte bTsdi187k5;
        byte bTsdi500;
        byte bTsdi1M5;
        byte bTsdi3M;
        byte bTsdi6M;
        byte bTsdi12M;
        byte bMaxSlavesSupp;
        byte bMaxMasterInputLen;
        byte bMaxMasterOutputLen;
        short sMaxMasterDataLen;

        GSDPROPERTY selProperty = new GSDPROPERTY();

        /** ************** Set stationType ******************* */

        if(getIntermediateValue(selProperty, gsdFile,"Station_Type")!=1){
         // Station isn't Master
            return -1;
        }

        /** ************** Set downloadSupp ********************* */
        downloadSupp = selProperty.selectPropertyValue(gsdFile, "Download_supp");
        masterStruct.setDownloadSupp(downloadSupp.equals("1"));

        /** ************** Set uploadSupp ********************* */
        uploadSupp = selProperty.selectPropertyValue(gsdFile, "Upload_supp");
        masterStruct.setUploadSupp(uploadSupp.equals("1"));

        /** ************** Set actParamBrctSupp ********************* */
        actParamBrctSupp = selProperty.selectPropertyValue(gsdFile, "Act_Param_Brct_supp");
        masterStruct.setActParamBrctSupp(actParamBrctSupp.equals("1"));

        /** ************** Set actParamSupp ********************* */
        actParamSupp = selProperty.selectPropertyValue(gsdFile, "Act_Param_supp");
        masterStruct.setActParamSupp(actParamSupp.equals("1"));

        /** ************** Set maxMpsLength ********************* */
        masterStruct.setMaxMpsLength(
                getIntermediateValue(selProperty, gsdFile, "Max_MPS_Length")
        );

        /** ************** Set maxLsduMS ********************* */
        masterStruct.setMaxLsduMS(
                (byte) getIntermediateValue(selProperty, gsdFile,"Max_Lsdu_MS")
        );

        /** ************** Set maxLsduMM ********************* */
        masterStruct.setMaxLsduMM(
                (byte) getIntermediateValue(selProperty, gsdFile,"Max_Lsdu_MM")
        );

        /** ************** Set minPollTimeout ********************* */
        masterStruct.setMinPollTimeout(
                (short) getIntermediateValue(selProperty, gsdFile,"Min_Poll_Timeout")
        );

        /** ************** Set trdy9k6 ********************* */
        masterStruct.setTrdy9k6(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_9.6")
        );

        /** ************** Set trdy19k2 ********************* */
        masterStruct.setTrdy19k2(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_19.2")
        );

        /** ************** Set trdy31k25 ********************* */
        masterStruct.setTrdy31k25(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_31.25")
        );

        /** ************** Set trdy45k45 ********************* */
        masterStruct.setTrdy45k45(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_45.45")
        );

        /** ************** Set trdy93k75 ********************* */
        masterStruct.setTrdy93k75(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_93.75")
        );

        /** ************** Set trdy187k5 ********************* */
        masterStruct.setTrdy187k5(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_187.5")
        );

        /** ************** Set trdy500 ********************* */
        masterStruct.setTrdy500(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_500")
        );

        /** ************** Set trdy1M5 ********************* */
        masterStruct.setTrdy1M5(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_1.5M")
        );

        /** ************** Set trdy3M ********************* */
        masterStruct.setTrdy3M(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_3M")
        );

        /** ************** Set trdy6M ********************* */
        masterStruct.setTrdy6M(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_6M")
        );

        /** ************** Set trdy12M ********************* */
        masterStruct.setTrdy12M(
                (byte) getIntermediateValue(selProperty, gsdFile,"Trdy_12M")
        );

        /** ************** Set tqui9k6 ********************* */
        masterStruct.setTqui9k6(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_9.6")
        );

        /** ************** Set tqui19k2 ********************* */
        masterStruct.setTqui19k2(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_19.2")
        );

        /** ************** Set tqui31k25 ********************* */
        masterStruct.setTqui31k25(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_31.25")
        );

        /** ************** Set tqui45k45 ********************* */
        masterStruct.setTqui45k45(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_45.45")
        );

        /** ************** Set tqui93k75 ********************* */
        masterStruct.setTqui93k75(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_93.75")
        );

        /** ************** Set tqui187k5 ********************* */
        masterStruct.setTqui187k5(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_187.5")
        );

        /** ************** Set tqui500 ********************* */
        masterStruct.setTqui500(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_500")
        );

        /** ************** Set tqui1M5 ********************* */
        masterStruct.setTqui1M5(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_1.5M")
        );

        /** ************** Set tqui3M ********************* */
        masterStruct.setTqui3M(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_3M")
        );

        /** ************** Set tqui6M ********************* */
        masterStruct.setTqui6M(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_6M")
        );

        /** ************** Set tqui12M ********************* */
        masterStruct.setTqui12M(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tqui_12M")
        );

        /** ************** Set tset9k6 ********************* */
        masterStruct.setTset9k6(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tset_9.6")
        );

        /** ************** Set tset19k2 ********************* */
        masterStruct.setTset19k2(
                (byte) getIntermediateValue(selProperty, gsdFile,"Tset_19.2")
        );

        /** ************** Set tset31k25 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_31.25");
        bTset31k25 = (byte) intermediateValue;
        masterStruct.setTset31k25(bTset31k25);

        /** ************** Set tset45k45 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_45.45");
        bTset45k45 = (byte) intermediateValue;
        masterStruct.setTset45k45(bTset45k45);

        /** ************** Set tset93k75 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_93.75");
        bTset93k75 = (byte) intermediateValue;
        masterStruct.setTset93k75(bTset93k75);

        /** ************** Set tset187k5 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_187.5");
        bTset187k5 = (byte) intermediateValue;
        masterStruct.setTset187k5(bTset187k5);

        /** ************** Set tset500 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_500");
        bTset500 = (byte) intermediateValue;
        masterStruct.setTset500(bTset500);

        /** ************** Set tset1M5 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_1.5M");
        bTset1M5 = (byte) intermediateValue;
        masterStruct.setTset1M5(bTset1M5);

        /** ************** Set tset3M ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_3M");
        bTset3M = (byte) intermediateValue;
        masterStruct.setTset3M(bTset3M);

        /** ************** Set tset6M ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_6M");
        bTset6M = (byte) intermediateValue;
        masterStruct.setTset6M(bTset6M);

        /** ************** Set tset12M ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tset_12M");
        bTset12M = (byte) intermediateValue;
        masterStruct.setTset12M(bTset12M);

        /** ************** Set LAS_Len ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "LAS_Len");
        bLasLen = (byte) intermediateValue;
        masterStruct.setLasLen(bLasLen);

        /** ************** Set tsdi9k6 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_9.6");
        bTsdi9k6 = (byte) intermediateValue;
        masterStruct.setTsdi9k6(bTsdi9k6);

        /** ************** Set tsdi19k2 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_19.2");
        bTsdi19k2 = (byte) intermediateValue;
        masterStruct.setTsdi19k2(bTsdi19k2);

        /** ************** Set tsdi31k25 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_31.25");
        bTsdi31k25 = (byte) intermediateValue;
        masterStruct.setTsdi31k25(bTsdi31k25);

        /** ************** Set tsdi45k45 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_45.45");
        bTsdi45k45 = (byte) intermediateValue;
        masterStruct.setTsdi45k45(bTsdi45k45);

        /** ************** Set tsdi93k75 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_93.75");
        bTsdi93k75 = (byte) intermediateValue;
        masterStruct.setTsdi93k75(bTsdi93k75);

        /** ************** Set tsdi187k5 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_187.5");
        bTsdi187k5 = (byte) intermediateValue;
        masterStruct.setTsdi187k5(bTsdi187k5);

        /** ************** Set tsdi500 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_500");
        bTsdi500 = (byte) intermediateValue;
        masterStruct.setTsdi500(bTsdi500);

        /** ************** Set tsdi1M5 ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_1.5M");
        bTsdi1M5 = (byte) intermediateValue;
        masterStruct.setTsdi1M5(bTsdi1M5);

        /** ************** Set tsdi3M ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_3M");
        bTsdi3M = (byte) intermediateValue;
        masterStruct.setTsdi3M(bTsdi3M);

        /** ************** Set tsdi6M ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_6M");
        bTsdi6M = (byte) intermediateValue;
        masterStruct.setTsdi6M(bTsdi6M);

        /** ************** Set tsdi12M ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Tsdi_12M");
        bTsdi12M = (byte) intermediateValue;
        masterStruct.setTsdi12M(bTsdi12M);

        /** ************** Set MaxSlaveSupp ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Max_Slaves_supp");
        bMaxSlavesSupp = (byte) intermediateValue;
        masterStruct.setMaxSlavesSupp(bMaxSlavesSupp);

        /** ************** Set MaxMasterInputLen ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Max_Master_Input_Len");
        bMaxMasterInputLen = (byte) intermediateValue;
        masterStruct.setMaxMasterInputLen(bMaxMasterInputLen);

        /** ************** Set MaxMasterOutputLen ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Max_Master_Output_Len");
        bMaxMasterOutputLen = (byte) intermediateValue;
        masterStruct.setMaxMasterOutputLen(bMaxMasterOutputLen);

        /** ************** Set MaxMasterDataLen ********************* */
        intermediateValue = getIntermediateValue(selProperty, gsdFile,
                "Max_Master_Data_Len");
        sMaxMasterDataLen = (byte) intermediateValue;
        masterStruct.setMaxMasterDataLen(sMaxMasterDataLen);

        return 0;
    }

    /**
     * Get a Property from the gsdFile and check a Property are found and
     * give this integer representation.
     * @param selProperty the property.
     * @param gsdFile the GSD File.
     * @param intermediateValue String number as Hex or Dec or "Property not found".
     * @return The integer Value.
     */
    private long getIntermediateValue(final GSDPROPERTY selProperty, final String gsdFile, final String intermediateValue) {
        return getIntermediateValue(selProperty.selectPropertyValue(gsdFile, intermediateValue));
        
    }

    /**
     * Check a Property are found and give this integer representation.
     * 
     * @param intermediateValue
     *            String number as Hex or Dec or "Property not found".
     * @return The integer Value.
     */
    private long getIntermediateValue(String intermediateValue) {
        long value;
        if (intermediateValue.equals("Property not found")) {
            value = -1;
            // Is value in hex
        } else if (intermediateValue.indexOf("0x") != -1) {
            intermediateValue = intermediateValue.substring(2, intermediateValue.length());
            value = Long.parseLong(intermediateValue, 16);
        } else {
            value = Long.parseLong(intermediateValue, 10);
        }

        return value;
    }
}
