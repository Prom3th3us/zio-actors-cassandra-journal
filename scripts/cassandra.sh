#!/bin/bash

# fail if something goes wrong
set -e

[ $# -eq 1 ] || { echo "missing option arg" ; exit 1 ; }

option=$1

function setup() {
    docker exec -i cassandra bash -c 'cqlsh' < scripts/cassandra.setup.cql
}

function clean() {
    docker exec -i cassandra bash -c 'cqlsh' < scripts/cassandra.cleanup.cql
}

case $option in
    "usage") echo "usage: setup | clean";;
    "setup") setup;;
    "clean") clean;;
    *) echo "wrong option: $option [check usage]";;
esac
