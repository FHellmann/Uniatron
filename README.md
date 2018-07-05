# Uniatron
[![Build Status](https://travis-ci.org/FHellmann/Uniatron.svg?branch=master)](https://travis-ci.org/FHellmann/Uniatron)
[![Coverage Status](https://coveralls.io/repos/github/FHellmann/Uniatron/badge.svg?branch=master)](https://coveralls.io/github/FHellmann/Uniatron?branch=master)

![Banner](https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/0_Play-Store_Functiongraph.png)

**Uniatron is the app to spend less time on the phone and more time with exercise!**

At the beginning of a day, each user launches with a set amount of usage time that gradually degrades as apps are used. By achieving activity goals (steps during the day), they can be used to re-enable usage time. Depending on the extent of the activity, a longer or shorter usage time can be added to your daily limit.

The automatic locking of apps will encourage the user to become more active and to counteract the general use of mobile phones.

[<img alt='Get it on Google Play' src='https://play.google.com/intl/en_gb/badges/images/generic/en_badge_web_generic.png' width="180" />](https://play.google.com/store/apps/details?id=com.edu.uni.augsburg.uniatron&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1)

## Motivation
- Quality > Quantity
- Clean-Code
- Clean-Design
- Easy to use

## Feature descriptions
### App time usage
Collect time for all used apps during the day (whether in blacklist or not).
The Top 5 apps and their percentages in usage time are displayed in the pie cart on the main page.
### Step Counter
You are rewarded for doing sportive activity (user can exchange steps for additional screen time).
All steps are being counted during the day via a build-in sensor.
The amount of recent steps is displayed on the lower left part of the main page.
After exchange to usage time the amount is decreased by the specified number. 
(For overview on all steps see History View)
### App locking
Lock social/fun apps from blacklist after a certain amount of time has been spent on the phone.
After App Lock is active when opening specific blocked app Uniatron 
### Learning Aid
Helps you keep concentrated by locking screen/apps for a certain amount of time.
You won't be destracted by your phone anymore.
E.g. screen is locked for 55 minutes before you get access to apps again.
### Emotion tracking
Track emotions after app usage and throughout the day (via emojis on a range from sad to happy).
You can track your mood afterward in the History View
### History View
By clicking on history (on main page) you get a list of recent days with info on average emotion, total of app usage time and total amount of steps during this day.
By selecting "Group by month" or "Group by year" the list consists of entrys with summarys for month or year
### Configurations
Configure app settings:
Select a Fitness Level: How easy/hard do you want it to be to exchange steps for usage time
In the blacklist all apps that you want to get blocked by Uniatron should be selected.



## Schedule
### Milestone 1: First prototype (Due date May 31, 2018)
We will create a first prototype with the basic functionality.

- Locking apps
- Collecting time of doing sportive activity
- Tracking emotions
- Configure the app

### Milestone 2: Final app - BETA (Due date July 5, 2018)
We will further refine the prototype. Thus, additional functionalities might be implemented.

- Learning aid
- Group history view by day/month/year

### Milestone 3: Final app - RELEASE

- Onboarding
- Detail history day/month/year view
- App Icon
- (Wearables connection)

## Design
Considering the fact that the user can reach every content of the app with three clicks, we opted for a flat navigation hierarchy. With a navigation bar at the bottom with three options (timeline, home, settings) every screen can be reached with just one click. Accordingly, a navigation depth of no more than two clicks can be claimed on each of these pages.

The design of our app complies with the design guidelines of [Google Material Design](https://material.io/guidelines/material-design/introduction.html).

### Final App
Home | History | History
:---:|:-------:|:------:
<img src='https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/1_Play-Store.png' /> | <img src='https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/2_Play-Store.png' /> | <img src='https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/3_Play-Store.png' />

### Prototype
![alt text](https://github.com/FHellmann/Uniatron/blob/master/doc/Concepts/Prototype.JPG)

## Coding

### Work Guidelines
1. Create Issue
2. Create Branch (Naming: {first letter of first name + last name}-{issue nr})
3. Work your task
4. Create Pullrequest
5. Review

### Coding Style Guidlines
[Google Android](https://source.android.com/setup/contribute/code-style)

- Use [Optional](http://www.baeldung.com/java-optional) instead of return null

## Collaborators
- [Fabio Hellmann](https://github.com/FHellmann)
- [Leon Wöhrl](https://github.com/leonpoint)
- [Anja Hager](https://github.com/anja-h)
- [Danilo Hoss](https://github.com/speedyhoopster3)














