#!/bin/bash
app=${1:-hi}
cf s | grep $app | grep m-ftp && cf unbind-service $app m-ftp
cf ds -f m-ftp
cf cs ftp ftp-free m-ftp
cf bs $app m-ftp
