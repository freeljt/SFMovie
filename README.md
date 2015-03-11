SF Movies
=========

Description of Problem
----------------------
Create a service that shows on a map where movies have been filmed in San Francisco. The user should be able to filter the view using autocompletion search.

The data is available on [DataSF](http://www.datasf.org/): [Film Locations](https://data.sfgov.org/Arts-Culture-and-Recreation-/Film-Locations-in-San-Francisco/yitu-d5am).

Key points
* Retrieve the movies' data from [DataSF](https://data.sfgov.org/Arts-Culture-and-Recreation-/Film-Locations-in-San-Francisco/yitu-d5am) via http request and store the json text as Java Script Objects
* Display a google map centered at SF, translate the locations to (Latitude, Longitude) and mark them on the map
* Implement a filter that user can filter the markers in the map that supports autocompletion search 

Solution
--------
A [SF Movie](http://default-environment-vcifw3k78h.elasticbeanstalk.com) web application has been developed using Java and [Google Web Toolkit](http://www.gwtproject.org/) to show where movies had been filmed on a map of San Francisco. It is hosted on AWS: http://default-environment-vcifw3k78h.elasticbeanstalk.com

This solution is mainly focused on front-end. To accomplish the key points of this problem, technical choices are listed as below.
* Sent http request to the Socrata Open Data API (SODA) endpoint https://data.sfgov.org/resource/yitu-d5am.json and store the json data in a MovieLocation(extends JavaScriptObject) array
* Used Google Maps API to create the google map widget and geocoder service in the gwt-map library to translate text movie locations to the (Latitude, Longitude) position
* Created a hash table to associate each location marker in the map with each MovieLocation object, so that the click handler of each pin can display information including movie title, release year, location... etc of that movie in the interface   
* Created a filter using the multiple word suggest text box and stored the data like director, writers, actors... etc in the suggest list, which helps user autocomplete the filter typing
* Implemented the filtering fuction by searching the the data user provided through the hash table and mark the matched pins visible and others invisable in the map  

Trade-offs
----------
* The application used a repeating scheduled timer on the client side send geocoder requests second by second due to the limit of this service, which affects the performance of initializing the markers on the map and makes user wait for the loading when first accessing the web app. Given more time, some back-end code will be written to cache all the data on the server-side or store them in a MySQL database
* Will develop the automated tests for the app using gwt-test-utils framework if given more time
* Tried using Backbone.js to write the front-end code but found it hard to learn it and implement the web service in one week. So a more familiar GWT is used in stead. Yet, some studies online indicates Backbone.js is indeed a good & easy-to-use JS framework
* Only movie details are currently displayed when clicking the pin in the map. With additional time, other info like street view of that pin and movie poster can be added in the display

Created By Juntao Li
--------------------
[Resume Link](https://drive.google.com/file/d/0B6JeZFJab_deX3k2Tk4ya1hVZWM/view?usp=sharing)
[Linkedin Link](https://www.linkedin.com/profile/view?id=98496805&trk=nav_responsive_tab_profile)
