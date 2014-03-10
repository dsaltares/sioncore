#!/usr/bin/python
# -*- coding: utf-8 -*-

import argparse
import os
import csv
import re
import ConfigParser
import collections

KEY_COLUMN = 0
STRING_COLUMN = 1

class MultiOrderedDict(collections.OrderedDict):
    def __setitem__(self, key, value):
        if isinstance(value, list) and key in self:
            self[key].extend(value)
        else:
            super(collections.OrderedDict, self).__setitem__(key, value)

def parseArguments():
	parser = argparse.ArgumentParser()
	parser.add_argument('-c', '--config', help='config file to use for localisation template generation', required=True)
	return parser.parse_args()

def collectStrings(dir, patterns):
	print '* Looking for strings in %s' % dir
	
	strings = {}
	
	for (dirPath, dirNames, fileNames) in os.walk(dir):
		for file in fileNames:
			if file.endswith('.java') or file.endswith('.xml') or file.endswith('.tmx'):
				print '    * Processing %s' % file
				textFile = open(os.path.join(dirPath, file))
				lines = textFile.readlines()
				
				for line in lines:
					for pattern in patterns:
						match = pattern.search(line)
					
						if match != None:
							key = match.group('key');
						
							if key != None:
								print '        * Found key %s' % key
								strings[key] = key
								break
				
	return strings

def parseLocalisationFile(file):
	print '    * Parsing localisation file %s' % file
	
	strings = {}
	
	if os.path.exists(file):
		print '    * Localisation file found'
		
		csvFile = open(file, 'rb')
		csvReader = csv.reader(csvFile)
		
		skip = True
		
		for row in csvReader:
			if skip:
				skip = False
				continue
			
			strings[row[KEY_COLUMN]] = row[STRING_COLUMN]
			
		csvFile.close()
	else:
		print '    * Localisation file not found, creating'
		
	return strings
	
def updateLocalisationFile(fileName, codeStrings, localisedStrings):
	print '    * Updating localised strings'

	for key in localisedStrings.keys():
		if key not in codeStrings:
			print '        * Deleting key %s' % key
			del localisedStrings[key]
	
	for key, value in codeStrings.iteritems():
		if key not in localisedStrings:
			print '        * Adding new key %s' % key
			localisedStrings[key] = value
		
	csvFile = open(fileName, 'wb')
	csvWriter = csv.writer(csvFile)
	
	csvWriter.writerow(['Key', 'Value', 'Context'])
	
	sortedStrings = localisedStrings.keys()
	sortedStrings.sort()
	
	for key in sortedStrings:
		csvWriter.writerow([key, localisedStrings[key], ''])
	
	csvFile.close()

def getPatterns(lines):
	patterns = []

	for line in lines:
		patterns.append(re.compile(line))
		
	return patterns
	
def main():
	print '\nLOCALISATION TOOL'
	print '=================\n'
	
	args = parseArguments()
	
	config = ConfigParser.RawConfigParser(dict_type=MultiOrderedDict)
	config.read([args.config])

	patterns = getPatterns(config.get('localisation', 'patterns'))
	langs = config.get('localisation', 'langs')
	sourceDir = config.get('localisation', 'sourceDir')[0]
	targetDir = config.get('localisation', 'targetDir')[0]
	
	codeStrings = collectStrings(sourceDir, patterns)
	
	for lang in langs:
		print '* Processing locale %s' % lang 
		langFile = os.path.join(targetDir, lang + '.csv')
		localisedStrings = parseLocalisationFile(langFile)
		updateLocalisationFile(langFile, codeStrings, localisedStrings)
	
if __name__ == "__main__":
	main()