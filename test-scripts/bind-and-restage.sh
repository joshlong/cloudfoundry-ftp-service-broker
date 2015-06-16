#!/bin/bash

app=${1:-hi}
./test.sh $app
cf restage $app
