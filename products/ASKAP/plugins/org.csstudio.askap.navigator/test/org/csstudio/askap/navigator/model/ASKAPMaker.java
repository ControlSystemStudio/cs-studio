package org.csstudio.askap.navigator.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ASKAPMaker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ASKAP askap = new ASKAP(makeOpis(), makeViews());
//		Gson gson = new Gson();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(askap);
		System.out.println(str);
		
//		System.out.println("**********************");
//		
//		ASKAP obj = gson.fromJson(str, ASKAP.class);		 
//		System.out.println(obj);
	}

	private static View[] makeViews() {
		Branch drivesBranches[] = new Branch[3];
		drivesBranches[0] = new Branch("DrivesConfig", null, null, "DrivesConfig");
		drivesBranches[1] = new Branch("DrivesMotor", null, null, "DrivesMotor");
		drivesBranches[2] = new Branch("DrivesPlots", null, null, "DrivesPlots");
		
		
		Branch subSystemBranches[] = new Branch[3];
		subSystemBranches[0] = new Branch("DRX", null, null, "DRX");
		subSystemBranches[1] = new Branch("Drives", drivesBranches, null, "Drives");
		subSystemBranches[2] = new Branch("ANS", null, null, "ANS");
		
		
		Branch branches[] = new Branch[1];
		branches[0] = new Branch("antenna", subSystemBranches, null, "");
		
		View view = new View("Antenna View", branches, makeNode());
		
		return new View[]{view};
		
	}
	

	private static Node makeNode() {
		String macros[][] = new String[][]{{"prefix", "ma"}};
		
		Branch branches[] = new Branch[2];
		
		String antMacros[][] = new String[][]{{"antid", "08:"}};
		branches[0] = new Branch("ma08", "antenna", antMacros);
				
		antMacros = new String[][]{{"antid", "09:"}};
		branches[1] = new Branch("ma09", "antenna", antMacros);
		
		Node node = new Node("MATES", "Main", branches, macros);
		
		return node;
	}
	
	private static OPI[] makeOpis() {
		OPI opiList[] = new OPI[14];
		
        opiList[0] = new OPI("Main", "/askap/main/MATES.opi");
        opiList[1] = new OPI("DRX", "/askap/drx/ConfigurationView.opi");

        opiList[2] = new OPI("Drives", "/askap/drives/main.opi");
        opiList[3] = new OPI("DrivesConfig", "/askap/drives/drivesAskapConfig.opi");
        opiList[4] = new OPI("DrivesMotor", "/askap/drives/drivesAskapMotor.opi");
        opiList[5] = new OPI("DrivesPlots", "/askap/drives/drivesAskapPlots.opi");


        opiList[6] = new OPI("ANS", "/askap/ans/main.opi");
        opiList[7] = new OPI("FMS", "/askap/ans/fmcModule.opi");
        opiList[8] = new OPI("spihub", "/askap/ans/spihub.opi");
        opiList[9] = new OPI("spisplitter", "/askap/ans/spisplitter.opi");
        opiList[10] = new OPI("convSysTest", "/askap/ans/convSysTest.opi");
        opiList[11] = new OPI("convSysTestDig", "/askap/ans/convSysTestDig.opi");
        opiList[12] = new OPI("fdCard2", "/askap/ans/fdCard2.opi");
        opiList[13] = new OPI("fdCard1", "/askap/ans/fdCard1.opi");

        return opiList;
	}
}
