#!/bin/bash

python --version > /dev/null
if [ $? == 0 ]
then
  ./secrets.py -a hide-secrets
else
  python3 --version > /dev/null
  if [ $? == 0 ]
  then
    ./secrets3.py -a hide-secrets
  else
    echo 'Neither python nor python3 is found.'
  fi
fi
