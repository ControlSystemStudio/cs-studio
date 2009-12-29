importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.java.io);

var value = ValueUtil.getDouble(pvArray[0].getValue());
var time = pvArray[0].getValue().getTime();

var fileWriter = new FileWriter("C:\\tmp\\test.txt", true);
var out = new BufferedWriter(fileWriter);
out.write(time + "\t" + value);
out.newLine();
out.close();
