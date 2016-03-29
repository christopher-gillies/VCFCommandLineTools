#!/usr/bin/perl
use strict;
use warnings;

my $f =  `ls ./target/*.jar`;

$f =~ /\.\/target\/(VCFCommandLineTools-\d+\.\d+\.\d+)/;
chomp($f);
my $cmd = "mvn package -DskipTests=true;cp $f ./release/$1.jar\n";
print "$cmd";
print `$cmd`;