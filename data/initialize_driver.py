#Dependencies
from selenium import webdriver
from  selenium.webdriver.common.keys import Keys 
import os
import time
import pickle
import pprint
import json
import uuid
from quantulum import parser
from fractions import Fraction
import random
import time

def getData():
	#Loacl Path of ChromeDriver program
	driverPath = 'insert your local path to driver'
	driver = webdriver.Chrome(driverPath)
	#Initialize Webdriver
	homepage = "http://allrecipes.com/recipes/?internalSource=hub%20nav&referringId=79&referringContentType=recipe%20hub&referringPosition=2&linkName=hub%20nav%20exposed&clickId=hub%20nav%202"

	try:
		#Check if links have already been stored locally
		masterDict = pickle.load( open("links.p", "rb"))
		if len(masterDict) < 10:
			 raise ValueError('Collecting more data')
	except:
		#If not scape top down from tags
		driver.get(homepage)
		categoryList, categoryLinks = [], []
		for x in range(1,100):
			try:
				category = driver.find_element_by_xpath('//*[@id="herolinks"]/div[1]/a[%d]' % x)
				categoryList.append(category.text)
				categoryLinks.append(category.get_attribute("href"))
			except:
				print "Top %d categories found" % x
				break

		#For each tag collect all the links to recipies with that tag 
		masterDict = {}
		for i in range(len(categoryLinks)):
			driver.get(categoryLinks[i])
			recipeLinks = set()
			links = driver.find_elements_by_xpath("//a[@href]")
			for link in links:
				if "/recipe/" in link.get_attribute("href"):
					recipeLinks.add(link.get_attribute("href"))
			masterDict[categoryList[i]] = list(recipeLinks)
			print len(recipeLinks), categoryList[i]

		#Persist recipe links locally
		pickle.dump( masterDict, open( "links.p", "wb" ) )

	#Return the current webdriver and recipe links w/ tags
	return driver, masterDict

if __name__ == '__main__':
	getData()