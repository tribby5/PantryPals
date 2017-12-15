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
import link_users
import ingredients
import intialize_driver

recipeDict = {}

#MAIN SCRAPPER

def getRecipes(driver, masterDict):
	#For each tag from getData scrape all the info from each recipe link
	counter = 0
	for category in masterDict:
		for recipeLink in masterDict[category]:
			try:
				driver.get(recipeLink)
				name = driver.find_element_by_xpath("//section/div[1]/div/section[2]/h1").text
				rating = driver.find_element_by_xpath("//section/div[1]/div/section[2]/meta[1]").get_attribute("content")
				submitter = driver.find_element_by_xpath('//section/div[1]/div/section[2]/div[4]/p/span[2]').text
				caption = driver.find_element_by_class_name("submitter__description").text[1:-1]
				tag = [category]
				#need a random time stamp for the data in order to use in our app
				timePosted = genRandTime()
				#need to collect all ingredients and parse them
				ingredients = getIngredients(driver)
				#need to collect all instructions and parse them
				instructions =  ingredients.getInstructions(driver)
				#Image paths vary based on the type of image uploaded by the user
				try:
					image = driver.find_element_by_xpath('//*[@id="BI_openPhotoModal1"]').get_attribute("src")
				except:
					image = driver.find_element_by_xpath('//section/div[1]/div/section[1]/div[1]/div[2]/ul/li[1]/a/img').get_attribute('src')
				#Format the data as a nested dictionary that will later be turned into json
				formatData(tag, name, caption, rating, submitter, ingredients, instructions,image,timePosted)
				counter +=1
				print counter
			except KeyboardInterrupt:
				raise
			#If error occurs for a recipe, note it and keep scrapping
			except:
				print 'ERROR'

def genRandTime():
	#generate a random time for each post between oct and dec
	format = '%Y-%m-%d %I:%M:%S'
	start = "2016-10-10 01:01:01"
	end = "2017-12-09 01:01:01"
	prop = random.random()

	stime = time.mktime(time.strptime(start, format))
	etime = time.mktime(time.strptime(end, format))
	ptime = stime + prop * (etime - stime)

	return str(time.strftime(format, time.localtime(ptime)))

def getInstructions(driver):
	#Collect instructions and avoid advertisements
	iList = []
	for x in range(1,20):
		try:
			ing = driver.find_element_by_xpath('//section/section[2]/div/div[1]/ol/li[%x]' % x).text
			if 'ADVERTISEMENT' not in ing:
				iList.append(ing)
		except:
			break
	return iList

def formatData(category, name, caption, rating, submitter, ingredients, instructions,image, timePosted):
	#format data to be recipe: attributes which will later be converted to json
	#generate uniqueID for each recipe
	uid = str(uuid.uuid4())
	recipeDict[uid] = {}
	recipeDict[uid]["name"] = name
	recipeDict[uid]["tags"] = category
	recipeDict[uid]["caption"] =  caption
	recipeDict[uid]["postedBy"] =  submitter
	recipeDict[uid]["averageRating"] =  rating
	recipeDict[uid]["ingredients"] =  ingredients.parseIngredients(ingredients)
	recipeDict[uid]["instructions"] =  instructions
	recipeDict[uid]["imageURL"] =  image
	recipeDict[uid]["timePosted"] =  timePosted
	pickle.dump(recipeDict, open( "recipeDict.p", "wb" ) )

def write2json(recipeDict, inJson):
	#conver recipe dictionary to json
	js = json.dumps(recipeDict, sort_keys=True, indent=4)
	file = open(inJson, 'a')
	file.write(js)
	file.close()

def main():
	inJson = 'pantry-pals.json'
	outJson =  'pantry-pals-updated.json'
	#initalize driver and get tag data
	driver, masterDict =  initalize_driver.getData()
	#scrap, parse, and format info for top all recipes
	getRecipes(driver, masterDict)
	#write all info to a new json
	write2json(recipeDict, inJson)
	#link existing users in the database to sraped recipes
	link_users.linkUsers(inJson, outJson)
	
if __name__ == '__main__':
	main()