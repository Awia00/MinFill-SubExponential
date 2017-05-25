#!/bin/bash
for graph in `ls *.graph | sort -V`; do
	echo -n " $graph	"
	timeout -k 1s 5s cat "$graph" | java -jar ../Tools/out/artifacts/Tools_jar/Tools.jar
done
