#!/bin/bash

PRGDIR=`dirname "$0"`
test_version_file=${PRGDIR}/test-version.list
echo ${test_version_file}
if [[ ! -f $test_version_file ]]; then
    echo "cannot found 'test-version.list'"
    exit 1
fi

versions=`grep -v -E "^$|^#" ${test_version_file}`
for version in ${versions}
  do
    echo Start to test apache-dubbo-$version
    sudo mvn -Ddubbo.version=$version -Pintegration-tests-only -pl :apache-dubbo-tests clean verify
done
