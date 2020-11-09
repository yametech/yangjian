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
    echo Start to test mysql-connector-java-$version
    sudo mvn -Dmaven.javadoc.skip=true -Dgpg.skip -Dmysql-connector-java.version=$version -Pintegration-tests-only -pl :mysql-jdbc-tests clean verify -f pom.xml
done
