importPackage(Packages.org.csstudio.platform.data);

var loadingPV = pvArray[1];
var random = Math.random();
if(random >= 0 && random < 0.25)
	loadingPV.setValue(0);
else if(random >= 0.25 && random < 0.5)
	loadingPV.setValue(1);
else if(random >= 0.5 && random < 0.75)
	loadingPV.setValue(2);
else if(random >= 0.75 && random <=1)
	loadingPV.setValue(3);

