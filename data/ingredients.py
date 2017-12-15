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

def getIngredients(driver):
	#Collect ingredients and avoid advertisements
	iList = []
	for x in range(1,10):
		try:
			ing = driver.find_element_by_xpath('//*[@id="lst_ingredients_1"]/li[%x]' % x).text
			if 'ADVERTISEMENT' not in ing:
				iList.append(ing)
		except:
			break
	for x in range(1,10):
		try:
			ing = driver.find_element_by_xpath('//*[@id="lst_ingredients_2"]/li[%x]' % x).text
			if 'ADVERTISEMENT' not in ing:
				iList.append(ing)
		except:
			break
	return iList[:-1]

def parseIngredients(ingredients):
	#given a list of ingredients use NLP to parse amount, unit, name
	ingredientDict = {}
	count = 0
	for ing in ingredients:
		#using pretrained model identify and disambiguate units of measurement 
		quant = parser.parse(ing)
		try:
			unit = quant[0].unit.name
			amount = quant[0].value
			#Remove the amount and quantity from the name of the ing
			temp = [unit, unit+'s', str(Fraction(amount))]
			if unit == 'dimensionless': unit = None
			#String processing on the ingredient
			ingredient = ' '.join([i for i in ing.split() if i not in temp])
		except:
			amount = None
			unit = None
			ingredient = ing
		ingredientDict[count] = {"amount": amount,
								"unit": unit,
								"name": ingredient}
		count+=1
	return ingredientDict

if __name__ == '__main__':
	ingredients = getIngredients(driver)
	parseIngredients(ingredient)