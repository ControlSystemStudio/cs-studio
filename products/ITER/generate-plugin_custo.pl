#!/usr/bin/perl -w

use strict;
use warnings;

use Pod::Usage;
use Getopt::Long;
use FindBin qw($Bin);
use Term::ANSIColor qw(:constants);
use Cwd 'realpath';
use File::Basename;

=head1 NAME

generate-plugin_custo.pl - Tool to generate plugin_customization.ini file for each CSS product using preferences.ini of all plugins.

=head1 SYNOPSIS

B<generate-plugin_custo.pl> B<-h> REPODIR B<-o> ORGANIZATION B<-p> PLUGIN_CUSTO (B<-l> PLUGINS_LIST | B<-w> WAR_PRODUCT  ) [B<-f> PLUGIN_CUSTO_OUTPUT]

=head1 DESCRIPTION

This script will generate a plugin_customization.ini file with preferences.ini content
of each plugin list in plugins.list.
If an original plugin_customization.ini file is given, the generated file will contains also
all preferences from the original one only if it is different from default value.

=head1 OPTIONS

=over 5

=item B<-h> B<-->help

Show this help message.

=item B<-r> REPODIR, B<-->repo REPODIR

Path to the CSS repository.

=item B<-o> ORGANIZATION, B<--organization> ORGANIZATION

Organization name (matching directory in products/).

=item B<-p> PLUGIN_CUSTO, B<--pluginCustomization> PLUGIN_CUSTO

Path to the plugin_customization.ini file (or css_rap.ini file in case of web product).

=item B<-l> PLUGINS_LIST, B<--pluginList> PLUGINS_LIST

Path to the plugins.list file. This argument cannot be used with -w|--warProducts

=item B<-w> WAR_PRODUCT, B<--warProducts> WAR_PRODUCT

Path to *.warproduct file. This argument can be multiple. This argument cannot be used with -l|--pluginList

=item B<-f> PLUGIN_CUSTO_OUTPUT, B<--file> PLUGIN_CUSTO_OUTPUT

Path to the output file. (default: file specified by -p option)

=back

=cut

# Default values of options
my $scriptdir = $Bin;
my $help = 0;
my $repo = "";
my $organization = "";
my $pluginCustomizationPath = "";
my $outputPath = "";
my $pluginListPath = "";
my @warProductsPath = ();
# Parsing options
GetOptions(
    'repo=s' => \$repo,
    'organization|o=s' => \$organization,
    'pluginCustomization|p=s' => \$pluginCustomizationPath,
    'pluginList|l=s' => \$pluginListPath,
    'warProductsPath|w=s' => \@warProductsPath,
    'file|f=s' => \$outputPath,
    'help|?' => \$help
) or pod2usage(2);

pod2usage(1) if $help;
pod2usage(1) if ( $repo eq '' );
pod2usage(1) if ( $organization eq '' );
pod2usage(1) if ( $pluginCustomizationPath eq '' );
pod2usage(1) if ( $pluginListPath eq '' && $#warProductsPath == 0 );
pod2usage(1) if ( $pluginListPath ne '' && $#warProductsPath > 0 );

$repo =~ s|^\~/|$ENV{'HOME'}/|;
$repo = File::Spec->rel2abs($repo);
$repo = realpath($repo);
die RED . BOLD . "Git root '$repo' not found!" . RESET if ! -d "$repo";

$pluginCustomizationPath =~ s|^\~/|$ENV{'HOME'}/|;
$pluginCustomizationPath = File::Spec->rel2abs($pluginCustomizationPath);
$pluginCustomizationPath = realpath($pluginCustomizationPath);
die RED . BOLD . "plugin_customization.ini file '$pluginCustomizationPath' not found!" . RESET if ! -f "$pluginCustomizationPath";

if ($pluginListPath ne '') {
  $pluginListPath =~ s|^\~/|$ENV{'HOME'}/|;
  $pluginListPath = File::Spec->rel2abs($pluginListPath);
  $pluginListPath = realpath($pluginListPath);
  die RED . BOLD . "plugins.list file '$pluginListPath' not found!" . RESET if ! -f "$pluginListPath";
}

if ($outputPath eq '') {
  $outputPath = $pluginCustomizationPath;
}

for my $warProductPath (@warProductsPath) {
  $warProductPath =~ s|^\~/|$ENV{'HOME'}/|;
  $warProductPath = File::Spec->rel2abs($warProductPath);
  $warProductPath = realpath($warProductPath);
  # If WARPRODUCT end with .path, the file only contains the path to the real war product file
  if ($warProductPath =~ /\.path$/ ) {
    open my $linkFile, "<$warProductPath" or die RED . BOLD . "Failed to open '".$warProductPath."' for reading!" . RESET;
    my $link = <$linkFile>;
    chomp($link);
    close $linkFile;
    $warProductPath = File::Spec->rel2abs(dirname($warProductPath)."/".$link);
    $warProductPath = realpath($warProductPath);
  }
  die RED . BOLD . "warproduct file '$warProductPath' not found!" . RESET if ! -f "$warProductPath";
}

###############################################################################
## Functions
###############################################################################

sub uniq {
    return keys %{{ map { $_ => 1 } @_ }};
}

sub trim($) {
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}

sub processPreferencesFile {
  my $customFile = $_[0];
  my $pluginName = $_[1];
  my $preferencesFilePath = $_[2];
  my %existingPreferences = %{$_[3]};
  my @existingPreferenceKeys = keys %existingPreferences;

  print $customFile "#/\n";
  print $customFile "## $pluginName \n";
  print $customFile "#/\n";
  my @preferences=();
  open my $preferencesFile, "<$preferencesFilePath" or die RED . BOLD . "Failed to open '".$preferencesFilePath."' for reading!" . RESET;
  while (my $row = <$preferencesFile>) {
    chomp($row);
    $row = trim($row);
    if($row eq '' || $row =~ /^#/) {
      $row =~ s/^#( )*//;
      print $customFile "#/ $row\n";
    } else {
      my $prefValue = $row;
      print $customFile "# $pluginName/$row\n";
      while($row =~ /\\$/) {
        $row = <$preferencesFile>;
        chomp($row);
        $prefValue .= "\n ".$row;
        print $customFile "# $row\n";
      }
      $prefValue = trim($prefValue);
      push(@preferences, $prefValue);
    }
  }
  close $preferencesFile;
  @preferences = sort(@preferences);
  
  print $customFile "\n";
  for my $preference(@preferences) {
    my $commentedPref = $preference;
    $commentedPref =~ s/\n/\n#<</g;
    print $customFile "#<< $pluginName/$commentedPref\n";
    # Insert properties of existing plugin_customization.ini file
    my $preferenceKey = "$pluginName/$preference";
    $preferenceKey =~ s/\r|\n//g;
    $preferenceKey =~ s/( )*=.*$//;
    for my $existingPreferenceKey(@existingPreferenceKeys) {
      if($existingPreferenceKey eq "$preferenceKey") {
        my $existingPreferenceValue = $existingPreferences{$existingPreferenceKey};
        my $preferenceValue = $preference;
        $preferenceValue =~ s/^[^=]*=( )*//;
        if($existingPreferenceValue ne $preferenceValue) {
          print $customFile "$existingPreferenceKey=$existingPreferenceValue\n";
        }
      }
    }
  }
  for my $existingPreferenceKey(@existingPreferenceKeys) {
    if($existingPreferenceKey =~ /^\Q$pluginName\//) {
      my $present = 0;
      for my $preference(@preferences) {
        my $preferenceKey = "$pluginName/$preference";
        $preferenceKey =~ s/\r|\n//g;
        $preferenceKey =~ s/( )*=.*$//;
        if($existingPreferenceKey eq $preferenceKey) {
          $present = 1;
          last;
	    }
      }
      if($present==0) {
        print $customFile "#>>\n";
        print $customFile "$existingPreferenceKey=$existingPreferences{$existingPreferenceKey}\n";
      }
    }
  }
  print $customFile "\n";
}

###############################################################################
## Main program
###############################################################################

# List existing preferences
print "Analyze original plugin_customization.ini|css_rap.ini file.\n";
my %existingPreferences=(());
open my $origPluginCustoFile, "<$pluginCustomizationPath" or die RED . BOLD . "Failed to open '$pluginCustomizationPath' for reading!" . RESET;
while (my $row = <$origPluginCustoFile>) {
  chomp($row);
  $row = trim($row);
  if($row ne '' && $row =~ /^[^#][^=]*=.*$/) {
    my $key = $row;
    $key =~ s/( )*=.*$//;
    my $value = $row;
    $value =~ s/^[^=]*=( )*//;
    $existingPreferences{$key} = $value;
    while($row =~ /\\$/) {
      $row = <$origPluginCustoFile>;
      chomp($row);
      $existingPreferences{$key} .= "\n" . $row;
    }
  }
}
close $origPluginCustoFile;

my @plugins=();
if($pluginListPath ne '') {
  print "Analyze plugins.list file.\n";
  open my $pluginListFile, "<$pluginListPath" or die RED . BOLD . "Failed to open '$pluginListPath' for reading!" . RESET;
  while (my $plugin = <$pluginListFile>) {
    chomp($plugin);
    if($plugin ne '') {
      push(@plugins, $plugin);
    }
  }
  close $pluginListFile;
}
for my $warProductPath (@warProductsPath) {
  die RED . BOLD . "warproduct file '$warProductPath' not found!" . RESET if ! -f "$warProductPath";
  print "Analyze war product file: $warProductPath.\n";
  open my $warProductFile, "<$warProductPath" or die RED . BOLD . "Failed to open '$warProductPath' for reading!" . RESET;
  while (my $row = <$warProductFile>) {
    chomp($row);
    if ($row =~ /<plugin id=/) {
      $row =~ s/.*<plugin id="([^"]*)".*/$1/g;
      if ($row ne '') {
        push(@plugins, $row);
      }
    }
  }
  close $warProductFile;
}
@plugins = sort(uniq(@plugins));
if($#plugins==0) {
  die RED . BOLD . "No plugin found!" . RESET;
}

my $now= `date`;
chomp($now);
if ( -f "$outputPath.bak") {
    system("rm -f $outputPath.bak");
}
if ( -f "$outputPath") {
    system("cp $outputPath $outputPath.bak");
}

open my $customFile, ">$outputPath" or die RED . BOLD . "Failed to open '$outputPath' for writing!" . RESET;
print $customFile "#/\n";
print $customFile "## Generated date: $now\n";
print $customFile "#/\n";
print $customFile "\n";

print "Start processing organization plugins.\n";

print $customFile "#/\n";
print $customFile "## Organization ($organization) specific plugin preferences\n";
print $customFile "#/\n";
print $customFile "\n";
for my $plugin(@plugins) {
  # search first is products/ORGANIZATION/plugins for site specific plugins
  my $prefFiles = "$repo/products/$organization/plugins/$plugin/preferences.ini";
  if ( ! -f "$prefFiles" && $organization eq "ITER") {
    if(($plugin =~ m/org.csstudio.scan/)
        || ($plugin =~ m/org.csstudio.imagej/)) {
      # Search in products/SNS/plugins for o.c.scan.* and o.c.imagej plugins to get SNS version (a version exists also in products/FRIB)
      $prefFiles = "$repo/products/SNS/plugins/$plugin/preferences.ini";
    } else {
      $prefFiles = "$repo/products/DESY/plugins/$plugin/preferences.ini";
    }
  }
  if ( -f "$prefFiles") {
    processPreferencesFile($customFile, $plugin, $prefFiles, \%existingPreferences);
  }
}

print "Start processing core plugins.\n";

print $customFile "#/\n";
print $customFile "## Core plugins preferences\n";
print $customFile "#/\n";
print $customFile "\n";
for my $plugin(@plugins) {
  my $prefFiles = "$repo/core/plugins/$plugin/preferences.ini";
  if ( -f "$prefFiles") {
    processPreferencesFile($customFile, $plugin, $prefFiles, \%existingPreferences);
  }
}

print "Start processing application plugins.\n";

print $customFile "#/\n";
print $customFile "## Application plugins preferences\n";
print $customFile "#/\n";
print $customFile "\n";
for my $plugin(@plugins) {
  my $prefFiles = "$repo/applications/plugins/$plugin/preferences.ini";
  if (-f "$prefFiles") {
    processPreferencesFile($customFile, $plugin, $prefFiles, \%existingPreferences);
  }
}

#list preferences inside original pluginCusto corresponding to plugins not listed in plugins.list
print $customFile "#/\n";
print $customFile "## Unused preferences\n";
print $customFile "#/\n";
print $customFile "\n";
for my $existingPreferenceKey(keys %existingPreferences) {
  my $present = 0;
  for my $plugin(@plugins) {
    if($existingPreferenceKey =~ /^\Q$plugin\//) {
      $present = 1;
      last;
    }
  }
  if($present==0) {
    print $customFile "#>>\n";
    print $customFile "$existingPreferenceKey=$existingPreferences{$existingPreferenceKey}\n";
  }
}  
close $customFile;

__END__
