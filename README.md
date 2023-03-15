# FetchRewardsFuchsberger

Welcome to the GitHub repository for my Fetch Take Home Assessment which displays data from a JSON API! This was my first time building an app with Kotlin as I had built
Android apps using Java up until this point! 

This app is built using Kotlin and utilizes the Volley library to make HTTP requests and the Gson library to parse JSON data. It retrieves data from 
https://fetch-hiring.s3.amazonaws.com/hiring.json, filters out any items where "name" is blank or null, and displays the results to the user in a ListView grouped 
by "listId" and sorted by "listId" and "name".

To run this app, you can simply clone this repository to your local machine and open the project in Android Studio. You will need to add the following dependencies to your 
app-level build.gradle file:

  implementation 'com.android.volley:volley:1.2.1'
  implementation 'com.google.code.gson:gson:2.8.9'

You will also need to add the following permission to your app's manifest file in order to access the internet:

  <uses-permission android:name="android.permission.INTERNET" />

Once you have added the dependencies and permissions, you can build and run the app on an Android device or emulator. The app should automatically retrieve the data from 
the API and display it to the user in a ListView. Test cases have been created and stored in the repository as well.
