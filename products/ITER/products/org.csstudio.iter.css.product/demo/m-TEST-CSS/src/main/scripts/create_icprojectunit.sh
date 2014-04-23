#!/bin/bash

# This script creates a new unit for the I&C Project.


RED=$(tput setaf 1)
BLUE=$(tput setaf 4)
BROWN=$(tput setaf 3)
RESET=$(tput sgr0)

#------------------------------------------------------------------------------------------#

if [[ $BASH_SOURCE != $0 ]] ;
then
echo -e "${RED}Script cannot be executed in source execution mode using '.' or 'source'."
echo -e "Please execute directly with out using '.' or 'source'.${RESET}"
return 1
fi

#------------------------------------------------------------------------------------------#

execCmd ()
{
	echo -e "\n${BLUE}$*${RESET}"
	$*
	rc=$?
	if [ "$rc" != "0" ] ; then
		echo -e "\n${RED}Script execution aborted due to failure of command '$*'${RESET}"
		exit $rc
	fi
} 

#------------------------------------------------------------------------------------------#

SCRIPTDIR=$(dirname $(readlink -f $0))
UNITBASEDIR=`readlink -f "${SCRIPTDIR}"/../../../`
UNITPARENTDIR=`readlink -f "${UNITBASEDIR}"/../`

#------------------------------------------------------------------------------------------#

if [[ $PWD =~ "${UNITPARENTDIR}" ]] ; 
then 
echo "${RED}The script cannot be executed from the current working directory.${RESET}"
echo "${RED}Please execute from a directory whose path doesn't contain '${UNITPARENTDIR}'.${RESET}"
exit 1;
fi

#------------------------------------------------------------------------------------------#

execCmd mvn iter:newunit -Dunit=m-ExIICSS -Diandc=true
cd m-ExIICSS

execCmd mvn iter:newapp -Dapp=CWS-TCPH -Dtype=sddconf
execCmd mvn iter:newapp -Dapp=SharedTemplate -Dtype=sddconf

execCmd mvn iter:newioc -Dioc=CWS-TCPH-PSH0CORE -Dtype=generic  -Dapp=CWS-TCPH
execCmd mvn iter:newioc -Dioc=CWS-TCPH-PSH0SYSM -Dtype=generic  -Dapp=CWS-TCPH

execCmd mvn iter:include -Dtype=beast -Dconfiguration=CWS-TCPH-beast.xml

execCmd mvn iter:include -Dtype=beauty -Dconfiguration=CWS-TCPH-beauty.xml

execCmd cp -r "${UNITBASEDIR}/src" .

if [ -f "${UNITBASEDIR}/sdd.xml" ] ; then
cp "${UNITBASEDIR}/sdd.xml" .
else
echo -e "\n${BROWN}[WARNING] I&C Project's XML snapshot could not be found and is not copied to the unit.${RESET}"
fi
