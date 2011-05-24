from org.csstudio.opibuilder.scriptUtil import PVUtil

import array
import math

simuData = array.array('d', range(65536))
value = PVUtil.getDouble(pvs[0])
dataSrc = PVUtil.getString(pvs[1])

if dataSrc == "Linear Sine Wave":
	for i in range(256):
		for j in range(256):
			simuData[i*256 + j] = math.sin(j*6*math.pi/256 + i*6*math.pi/256 + value)
		
else:
	for i in range(256):
		for j in range(256):
			x = j-128
			y = i-128
			p = math.sqrt(x*x + y*y)
			simuData[i*256 + j] = math.sin(p*2*math.pi/256 + value)		
		
widget.setValue(simuData);	
