# PantryPals

## Overview of project:
The application is a social network for amateur cooks. Users of the application will have “pantries” which
they keep updated with ingredients that they possess. Users can interact with the application in several ways. They can post 
recipes of their favorite meals for others to view, or make standard posts of images and text. Users can also follow other 
users at different levels: if a user follows all posts of a user, they will view all of their updates. If a user instead follows only relevant posts of a user, they will only see recipes with ingredients that they possess. The application delivers recipes to the user based on available ingredients, cuisine/taste preferences, dietary restrictions, available cooking time, and expiration dates of ingredients. Users can also create joint pantries with other users in order to share ingredients in communal settings. Users have the ability to join social groups centered around common food themes. There also exist other convenient features such as Grocery List, which automatically populates the grocery list with ingredients from recipes that are Saved For Later that the user does not currently have. Users can also join communities of people which will allow them to interact with people of similar interests. Finally, people can search for people, recipes, or posts based on keywords. There are additional features discussed that will increase the complexity of this project including requesting to borrow ingredients from other users and integrating fitness/health applications with this one.

At the end of the project, we wish to demonstrate the implementation of a dynamic social network through which users can interact and consume information in real-time. Most importantly, we wish to demonstrate how the application leverages the efficient management of data in order to simplify day-to-day pantrypals.activities and improve the quality of living for amateur cooks who wish to improve their skills and repertoire.

# Overview of how code is structured:
Inside app/src/main/java lies all of the java classes that were created for the functionality. Within this folder everything is under the pantrypals package which breaks down into many subpackages. The subpackages are as follows:
* activities: Has all of the main activities in our app.
* database: Code for interacting with Firebase database
* discover: discover tab
* groups: Group code for users joining groups
* home: Home screen
* models: The java model files for interacting with the Firebase database. These model files are used to both push and retrieve data from Firebase and reflect generally the data mdoel.
* notifications: Code for notifying users
* pantry: code for everything related to pantries and that tab in the application.
* profile: everything related ot the profile tab
* recipe: related to creating and observing recipes
* util: developer utilities for the application

Inside app/src/main/res/layout are all of the .xml files which describe the layouts of the activities/fragments in the application.
String resource file under app/src/main/values/string.xml

# Compile, set up, deploy:
* Initial Steps
     * Download the zip
     * Build the gradle through Android Studio. Might have to clean gradle the first time around.
* Emulator
     * Can use an emulator through Android Studio after compiled. 
* On a device (not currently on Google Play Store, current process is emailing APK):
     * Build an APK through Android Studio
     * Send that APK through email.
     * Make sure your device is configured to accept APKs through unknown sources (not on Google Play Store)
     * Down that APK on your device through your email

# Current limitations of our implementation:
* Have not optimized Android Fragment Management. In the future there needs to be better handling of fragment transactions for speed and a smoother experience
* Only for Android users currently, hope to expand in the future. 

