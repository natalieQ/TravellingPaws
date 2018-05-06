# TravellingPaws

## OVERVIEW

* Travelling Paws is a Andoird Application that let users share moments with their pets around the world. It supports tracking every place users visited with their pets by showing footprints on google map. Users can also join the community and discover places others have travelled with their animal partners.

## LEARNING PURPOSE

### Learn Android by typical app component (transferable to future projects)

* User authentication
* User profile management
* Post feed ( create, read, update, delete)
* Community (Like/unlike post, following system - unimplemented yet)
* Camera component | API
* Map component | API (Tracking user location at real time)
* Database and Cloud Storage

### Learn Android by specific Android component

* Activity, Fragment, Context, ViewPager, Toast
* Navigation - Intent, put extra, check incoming intent, bundle
* Views - EditText, ImageVIew ...... Custom Square ImageView
* Adapter - from simple array adapter to custom GridView & ListView adapter that supports onClick event / Touch gesture
* FragmentPagerAdapter (used with tabs) vs. FragmentStatePagerAdapter
* Toolbars, Tabs, Menu
* Layout - Relative layout, Coordinator layout, ScrollView, GridView
* Custom icons and drawables

### Learn Android with Database (FireBase)

* User authentication
* Check duplication username
* Making query to read, write, update, delete posts
* Deal with FireBase data type ( Hashmap<String, Object> ) to Java class
* Data models (User, Account, Photo, Like)
* Use FireBase Storage to store user data on cloud
* Details - showing progress bar as user uploading photos

### Learn Android with Java coding best practice

* Modular codding (Encapsulation)
* Create custom adapter (Inheritance)
* Handle Exception (Database, API, User permissions)
* Identify helper function used across different classes
* A lot of debugging in the process (where to add tags to help debugging)

### Details

* Add progress bar while loading page
* Load Images in Main Feed by steps (i.e. 10 at a time)
* If user is not logged in, navigate to log in page
* Show default image ( paw) when loading image or failed to retrieve image
* Custom Google Map Marker
