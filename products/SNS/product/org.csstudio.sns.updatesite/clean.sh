# Remove files from previous build
#
# When called with any argument, leave the build directory
# and source files in place
#
# Kay Kasemir

rm -f */build.log

if [ $# -gt 0 ]
then
    rm -rf build/assemble*
    rm -rf build/final*
    rm -rf build/package*
    rm -rf build/repository*
    rm -rf build/buildRepo*
    rm -rf build/I.*
else
    rm -rf build
fi


