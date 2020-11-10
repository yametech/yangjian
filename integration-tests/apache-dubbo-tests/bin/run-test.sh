#!/bin/bash

PRGDIR=`dirname "$0"`
supported_version_file=${PRGDIR}/../support-version.list
echo ${supported_version_file}
if [[ ! -f $supported_version_file ]]; then
    echo "cannot found 'support-version.list'"
    exit 1
fi

versions=`grep -v -E "^$|^#" ${supported_version_file}`
for version in ${versions}
  do
    echo Start to test apache-dubbo-$version
    sudo mvn -Ddubbo.version=$version -Pintegration-tests-only -pl :apache-dubbo-tests clean verify
done
