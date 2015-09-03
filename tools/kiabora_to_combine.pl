#!/usr/bin/perl
use strict; use warnings;

my $rules = {};
my @scc   = ();

my $output_prefix = shift;
$output_prefix = "rules_output" unless $output_prefix;
print STDERR "Output prefix: ${output_prefix}\n";

my ($F1_FES,$F1_FUS,$F1_BTS);
open ($F1_FES, "> ${output_prefix}.1.fes") or die "Cannot open FES1 file";
open ($F1_FUS, "> ${output_prefix}.1.fus") or die "Cannot open FUS1 file";
open ($F1_BTS, "> ${output_prefix}.1.bts") or die "Cannot open BTS1 file";

my ($F2_FES,$F2_FUS,$F2_BTS);
open ($F2_FES, "> ${output_prefix}.2.fes") or die "Cannot open FES2 file";
open ($F2_FUS, "> ${output_prefix}.2.fus") or die "Cannot open FUS2 file";
open ($F2_BTS, "> ${output_prefix}.2.bts") or die "Cannot open BTS2 file";

my $state = 0;
while (<>) {
	if (/===/) {
		if (/= RULE SET =/) {
			$state = 1;
		}
		elsif (/= SCC =/) {
			$state = 2;
		}
		elsif (/= COMBINE \(FES\) =/) {
			$state = 3;
		}
		elsif (/= COMBINE \(FUS\) =/) {
			$state = 4;
		}
		else {
			$state = 0;
		}
		next;
	}
	if ($state == 1) {
		if (/\[([^\]]+)\]/) {
			$rules->{$1} = $_;
		}
	}
	elsif ($state == 2) {
		if (/C[0-9]+ = {(.*)}/) {
			push @scc, $1;
		}
	}
	elsif ($state == 3) {
		if (/C([0-9]+): ([^\s]+)/) {
			my $c = $1;
			my $t = $2;
			print_component($scc[$c], $F1_FES) if ($t =~ /FES/);
			print_component($scc[$c], $F1_FUS) if ($t =~ /FUS/);
			print_component($scc[$c], $F1_BTS) if ($t =~ /BTS/);
		}
	}
	elsif ($state == 4) {
		if (/C([0-9]+): ([^\s]+)/) {
			my $c = $1;
			my $t = $2;
			print_component($scc[$c], $F2_FES) if ($t =~ /FES/);
			print_component($scc[$c], $F2_FUS) if ($t =~ /FUS/);
			print_component($scc[$c], $F2_BTS) if ($t =~ /BTS/);
		}
	}
}

sub print_component {
	my ($c, $F) = @_;
	my @rs = split ',', $c;
	for my $r (@rs) {
		print $F $rules->{$r};
	}
}

=pod

=head1 NAME
	kiabora_to_combine - parses Kiabora output to generate convenient files

=head1 SYNOPSIS
	B<kiabora_to_combine> [<prefix>]

=head1 DESCRIPTION
	Just a simple tool that parses Kiabora output and split 
	the rules into different files.
	There are three files that ends by .1.FES / .1.FUS / .1.BTS,
	they contain the rules that must be processed respectively
	in a forward, bacward or unknown manner, in order to prioritise
	forward chaining.
	The three files that ends by .2.FES / .2.FUS / .2.BTS are similar
	but allow to prioritise bacward chaining.
=cut

