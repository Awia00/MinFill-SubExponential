#!/bin/bash
for graph in `ls *.graph | sort -V`; do
	echo -n " $graph	"
	if [ "$1" = "-k" ]; then
		cat "$graph" | java Kernelizer | ./GraphStatistics
	else
		cat "$graph" | ./GraphStatistics
	fi
done
