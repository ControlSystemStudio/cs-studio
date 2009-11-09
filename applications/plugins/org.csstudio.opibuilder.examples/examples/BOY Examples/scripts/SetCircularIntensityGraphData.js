importPackage(Packages.java.lang);
importPackage(Packages.java.lang.reflect);
importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.org.eclipse.swt.graphics);

var simuData = java.lang.reflect.Array.newInstance(java.lang.Double.TYPE, 65536);
var value = ValueUtil.getDouble(pvArray[0].getValue());

for(var i=0; i<256; i++){
	for(var j=0; j<256; j++){
		var x = j-128;
		var y = i-128;		
		var p = Math.sqrt(x*x + y*y);
		simuData[i*256 + j] = Math.sin(p*2*Math.PI/256 + value);
	}
}

widgetController.setValue(simuData);
	

