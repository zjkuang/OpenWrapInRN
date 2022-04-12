#!/bin/bash

python --version > /dev/null
if [ $? == 0 ]
then
  ./secrets.py -a apply-secrets
else
  python3 --version > /dev/null
  if [ $? == 0 ]
  then
    ./secrets3.py -a apply-secrets
  else
    echo 'Neither python nor python3 is found.'
  fi
fi
