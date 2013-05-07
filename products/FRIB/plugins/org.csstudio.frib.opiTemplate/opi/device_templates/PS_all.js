importPackage(Packages.org.csstudio.opibuilder.scriptUtil);
//if on/off local pv changes
if(triggerPV == pvs[0])
	if(pvs[0] == 1)//if set to 1 change all to 1
		pvs[2].setValue(1);
	if(pvs[0] == 0)//if set to 0 set all to 0
		pvs[2].setValue(0);

//if reset local pv changes
if(triggerPV == pvs[1])
	pvs[3].setValue(1); //set reset pv to 1