# Conversion of some colors used in an opi file
# Usage: sed -f css_3_0_convert.cmd opi_file > converted_opi_file
#		opi_file: opi input file to be converted
#		converted_opi_file: opi output file as a result of the conversion

s/IO DataDisplayBg/IO InputPV Bg/
s/IO DataDisplay/IO InputPV Fg/
s/IO DataInputBg/IO OutputPV Bg/
s/IO DataInput/IO OutputPV Fg/

s/IO Bar/IO StatusBar Bg/
s/IO TextBar/IO StatusBar Fg/