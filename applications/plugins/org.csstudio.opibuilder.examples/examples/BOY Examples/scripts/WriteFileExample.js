importPackage(Packages.org.csstudio.opibuilder.scriptUtil);
importPackage(Packages.java.io);

var value = PVUtil.getDouble(pvArray[0]);
var time = PVUtil.getTimeString(pvArray[0]);

var fileWriter = new FileWriter("C:\\tmp\\test.txt", true);
var out = new BufferedWriter(fileWriter);
out.write(time + "\t" + value);
out.newLine();
out.close();
