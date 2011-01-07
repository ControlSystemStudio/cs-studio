importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var xArray = DataUtil.createDoubleArray(400);
var yArray = DataUtil.createDoubleArray(400);

var xPV = pvArray[0];
var yPV = pvArray[1];

for(var i=0; i<400; i++){
	xArray[i] = i*0.25;
}

for(var i=0; i<400; i++){
	yArray[i] = Math.random()*2-1;
}

xPV.setValue(xArray);
yPV.setValue(yArray);
