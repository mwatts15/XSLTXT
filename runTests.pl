#!/usr/local/bin/perl

# Run all of the tests in the test directory.  The tests directory
# contains a number of subdirectories called test_01, test_02, etc.
# Each one has a corresponding ant target test_01, test_01, etc.
#
# This script executes each of the targets and after it has executed
# it compares the contents of the xslresults and txtresults directories
# created by the target with each other and with the known_good_results
# directory.
#
# If you pass it a list of tests on the command line it will run only
# those tests e.g. runTests.pl test_01 test_03
#

use strict;

if ($#ARGV >= 0) {
    printf("Only going to run tests: %s\n", join(" ", @ARGV));
} else {
    printf("Going to run all tests\n");
}

my $sTestDir = "tests";

opendir(DIR, $sTestDir) || die("Couldn't open test directory " . $sTestDir);

my @aTests = sort(grep(/^test_/, readdir(DIR)));

closedir(DIR);

my @aTestResults;
my $sTest;

foreach $sTest (@aTests)
{
    if (($#ARGV >= 0) && !&member($sTest, @ARGV))
    {
        printf("Skipping test %s\n", $sTest);
        push(@aTestResults, sprintf("SKIPPED   Test %s\n", $sTest));
        next;
    }
    printf("Test %s\n", $sTest);
    printf("Running ant\n");
    if (system("sh", "runant.sh", $sTest) != 0) {
        printf("Error running test %s\n", $sTest);
    } else {
        printf("Ant successful. Going to compare results\n");
        my $bOk = 1;
        &compare($sTestDir, $sTest, "xslresults", "txtresults") ||
        ($bOk = 0);
        &compare($sTestDir, $sTest, "xslresults", "known_good_results") ||
        ($bOk = 0);
        &compare($sTestDir, $sTest, "txtresults", "known_good_results") ||
        ($bOk = 0);
        my $sResult;
        if (!$bOk) {
            $sResult = sprintf("FAILED    Test %s\n", $sTest);
        } else {
            $sResult = sprintf("SUCCEEDED Test %s\n", $sTest);
        }
        print $sResult;
        push(@aTestResults, $sResult);
    }
}

print("\n\n**** RESULTS ****\n");
my $sResult;
foreach $sResult (@aTestResults) {
    print $sResult;
}

exit;

sub member {
    my $sElem = shift @_;
    my @aArray = @_;

    my $sTestElem;
    foreach $sTestElem (@aArray) {
	if ($sTestElem eq $sElem) {
	    return 1;
	}
    }
    return 0;
}

sub compare {
    my ($sTestDir, $sTest, $sDirOne, $sDirTwo) = @_;

    my $nDiff = system("diff", "-b", "-x", "CVS", "-x", "*~",
		       $sTestDir . '/' . $sTest . '/' . $sDirOne,
		       $sTestDir . '/' . $sTest . '/' . $sDirTwo);
    if ($nDiff != 0) {
	printf("%s and %s differ\n", $sDirOne, $sDirTwo);
	return 0;
    } else {
    	printf("%s and %s the same\n", $sDirOne, $sDirTwo);
	return 1;
    }
}
