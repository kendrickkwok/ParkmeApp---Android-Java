ParkMe App — Android/Java Application

(Downloadable in Google PlayStore)

- ParkMe App is a buy and sell parking application that helps user
purchase and sell parking spots. Buyers and sellers drop pins to notify
each other where the parking location is being sold or being bought.
The buyer then meets the seller, where they will be taken to the
purchase activity where the parking spots can either be bought or sold.

Platform:
1. Android SDK 7.1 API level 25
2. Google Play-Services 10.2.1
3. Google Play-Services-Maps 10.2.1
4. Gson 2.8.0

Design Overview:
1. Model-View-Controller
2. Mediator Pattern (Maps used as mediator)
3. Observer Pattern (User-created pins changes the application)
4. OOP (Object Oriented Programming)

Concepts used:
1. MySQL Workbench/Amazon Server - Server hosts database to store
coordinates of pins and users using the application
2. Multi-threading design -AsyncTasks and Runnable Threads to
facilitate better performance and design specifically used for dropping
and posting pins
3. Volley /JSON - Volley request commands using JSON objects to
communicate with PHP scripts in the server. Response Listeners
created to guide communications between the network
4. Google API - Google API map to facilitate dropping and retreive of pins
5. Retrofit API - Calculates the distance and time between one pin
location to another pin location.
