#!/bin/bash

app=${1:-hi}
spring jar hi.jar hi.groovy 
cf push -p hi.jar $app