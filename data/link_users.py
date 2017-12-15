#Dependencies
import json
import pprint
pp = pprint.PrettyPrinter(indent=2)


def linkUsers(inJson, outJson):
	#injest the recipes from the scraper and 50 users already in the database
	with open(inJson) as data_file:
	    data = json.load(data_file)
	users = data['userAccounts'].keys()
	count = 0

	#systematically assign users to recipes in the database
	for recipe in data['recipes']:
		#use a modular rotation to ensure uniform assignment
		val = count%50
		userAssignment = {}
		for i in range(len(users)):
			if i == val:
				userAssignment[users[i]] = True
			else:
				pass
		#update the recipe to have the assigned user linked
		data['recipes'][recipe]['postedBy'] = userAssignment
		count +=1

	#output the new user linked recipes to a new json
	js = json.dumps(data, indent=4)
	file = open(outJson, 'w')
	file.write(js)
	file.close()

if __name__ == '__main__':
	inJson = 'pantry-pals.json'
	outJson =  'pantry-pals-updated.json'
	linkUsers(inJson, outJson)