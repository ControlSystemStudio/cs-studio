#!/usr/bin/env perl
#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-maven-iter-plugin/tags/CODAC-CORE-3.1.0/src/main/resources/test_template.pl $
# $Id: test_template.pl 25208 2012-02-07 10:17:51Z zagara $
#
# Project       : CODAC Core System
#
# Description   : Test script
#
# Author        : Cosylab
#
# Copyright (c) : 2010-2014 ITER Organization,
#                 CS 90 046
#                 13067 St. Paul-lez-Durance Cedex
#                 France
#
# This file is part of ITER CODAC software.
# For the terms and conditions of redistribution or use of this software
# refer to the file ITER-LICENSE.TXT located in the top level directory
# of the distribution package.
#
#-======================================================================

use CA;

printf "***** Starting (test name) *****\n";

#######################################################
# The main test logic must be written here.
#
# When an error occurs, this script can give up by
# "die" statement.
# 
# The example is shown below.
#######################################################
#my $chan = CA->new('ABC:XXX');
#CA->pend_io(1);
#$chan->is_connected || die "ERROR; Failed to connect to ABC:XXX.";

printf "***** End of (test name) *****\n";
