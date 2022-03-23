#!/usr/bin/python

import os, sys, getopt, json, re

# parseArg(sys.argv)
def parseArg(argv):
  argv0 = argv[0]
  argvnet = argv[1:]
  action = ''
  try:
    opts, args = getopt.getopt(argvnet, "a:", ["action="])
  except getopt.GetoptError:
    print(argv0 + ' -a apply-secrets')
    print(argv0 + ' -a hide-secrets')
    sys.exit(2)
  for opt, arg in opts:
    if opt in ("-a", "--action"):
      action = arg
  return action

def readJSONFile(filepath):
  f = open(filepath)
  data = json.load(f) # returns JSON object as a dictionary
  f.close()
  return data

secretData = readJSONFile('../secrets.json')
gamAppIDKey = 'com.google.android.gms.ads.APPLICATION_ID'
gamAppIDValue = secretData[gamAppIDKey]
gamAppIDDummyValue = '~~~' + gamAppIDKey + '~~~'

def applyGAMAppID():
  androidManifestXMLFilePath = '../android/app/src/main/AndroidManifest.xml'
  newAndroidManifestXMLFilePath = androidManifestXMLFilePath + '.new'
  androidManifestXML = open(androidManifestXMLFilePath, 'rt')
  androidManifestXMLLines = androidManifestXML.readlines()
  androidManifestXML.close()
  newAndroidManifestXML = open(newAndroidManifestXMLFilePath, 'wt')
  for line in androidManifestXMLLines:
    replaced = line.replace(gamAppIDDummyValue, gamAppIDValue)
    newAndroidManifestXML.write(replaced)
  newAndroidManifestXML.close()
  os.remove(androidManifestXMLFilePath)
  os.rename(newAndroidManifestXMLFilePath, androidManifestXMLFilePath)

def hideGAMAppID():
  androidManifestXMLFilePath = '../android/app/src/main/AndroidManifest.xml'
  newAndroidManifestXMLFilePath = androidManifestXMLFilePath + '.new'
  androidManifestXML = open(androidManifestXMLFilePath, 'rt')
  androidManifestXMLLines = androidManifestXML.readlines()
  androidManifestXML.close()
  newAndroidManifestXML = open(newAndroidManifestXMLFilePath, 'wt')
  pattern = 'android\:value="[a-zA-Z0-9\-~]*"'
  replacementDone = False
  inGAMAppIDItem = False
  for line in androidManifestXMLLines:
    replaced = line
    if (not replacementDone):
      if (not inGAMAppIDItem):
        index = line.find(gamAppIDKey)
        if (index != -1):
          inGAMAppIDItem = True
      if (inGAMAppIDItem and re.search(pattern, line)):
        replaced = re.sub(pattern, 'android:value="' + gamAppIDDummyValue + '"', line)
        replacementDone = True
    newAndroidManifestXML.write(replaced)
  newAndroidManifestXML.close()
  os.remove(androidManifestXMLFilePath)
  os.rename(newAndroidManifestXMLFilePath, androidManifestXMLFilePath)

def main():
  action = parseArg(sys.argv)
  if (action == 'apply-secrets'):
    applyGAMAppID()
    print('Secrets applied.')
  elif (action == 'hide-secrets'):
    hideGAMAppID()
    print('Secrets hidden.')

main()
