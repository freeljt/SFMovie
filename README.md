Coding challenge or existing code?
==================================

The [coding challenge](coding_challenge.md) is optional if you already have
some code that you're proud of and can share with us.

Existing code
-------------

If you have existing code, please follow the following guidelines:

* Include a link to the hosted repository (e.g. Github, Bitbucket...). We cannot
  review archives or single files.
* The repo should include a README that follows the [principles described
  below](#readme) In particular, please make sure to include high-level
  explanation about what the code is doing.
* Ideally, the code you're providing:
  * Has been written by you alone. If not, please tell us which part you wrote
    and are most proud of in the README.
  * Is leveraging web technologies.
  * Is deployed and hosted somewhere.

Readme
------

Regardless of whether it's your own code or our coding challenge, write your
README as if it was for a production service. Include the following items:

* Description of the problem and solution.
* Whether the solution focuses on back-end, front-end or if it's full stack.
* Reasoning behind your technical choices, including architectural. Trade-offs
  you might have made, anything you left out, or what you might do differently
  if you were to spend additional time on the project.
* Link to other code you're particularly proud of.
* Link to your resume or public profile.
* Link to to the hosted application where applicable.

How we review
-------------

Your application will be reviewed by at least three of our engineers. The
aspects of your code we will judge include:

* Clarity: does the README clearly explains the problem and solution?
* Correctness: does the application do what was asked? If there is anything
  missing, does the README explain why it is missing?
* Code quality: is the code simple, easy to understand, and maintainable?  Are
  there any code smells or other red flags?
* Testing: how thorough are the automated tests? Will they be difficult to
  change if the requirements of the application were to change?
* UX: is the web interface understandable and pleasing to use?
* Technical choices: do choices of libraries, databases, architecture etc. seem
  appropriate for the chosen application?

Coding Challenge
----------------

[Guidelines can be found here.](coding_challenge.md)


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
A [SF Movie](http://default-environment-vcifw3k78h.elasticbeanstalk.com) web application has been developed using Java and Google Web Toolkit to show where movies had been filmed on a map of San Francisco. It is hosted on AWS: http://default-environment-vcifw3k78h.elasticbeanstalk.com

This solution is mainly focused on front-end. To accomplish the key points of this problem, technical choices are listed as below.
* Sent http request to the Socrata Open Data API (SODA) endpoint https://data.sfgov.org/resource/yitu-d5am.json and store the json data in a MovieLocation(extends JavaScriptObject) array
* Used Google Maps API to create the google map widget and geocoder service in the gwt-map library to translate text movie locations to the (Latitude, Longitude) position
* Created a hash table to associate each location marker in the map with each MovieLocation object, so that the click handler of each pin can display information including movie title, release year, location... etc of that movie in the interface   
* Created a filter using the multiple word suggest text box and stored the data like director, writers, actors... etc in the suggest list, which helps user autocomplete the filter typing
* Implemented the filtering fuction by searching the the data user provided through the hash table and mark the matched pins visible and others invisable in the map  

Trade-offs
----------
* Will develop the automated tests for the app using gwt-test-utils framework if given more time
* Tried using Backbone.js to write the front-end code but found it hard to learn it and implement the web service in one week. So a more familiar GWT is used in stead. Yet, some studies online indicates Backbone.js is indeed a good & easy-to-use JS framework
* Since the geocoder service has request limit per second, a repeating scheduled timer has been implemented on the client side and process 8 requests per second, which affects the performance of initializing the markers on the map when first accessing the web app. Given more time, some back-end code will be written to cache all the data on the server-side or store them in a MySQL database
* Only movie details are currently displayed when clicking the pin in the map. With additional time, other info like street view of that pin and movie poster can be added in the display