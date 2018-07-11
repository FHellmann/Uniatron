# UNIAtron
[![Build Status](https://travis-ci.org/FHellmann/Uniatron.svg?branch=master)](https://travis-ci.org/FHellmann/Uniatron)
[![Coverage Status](https://coveralls.io/repos/github/FHellmann/Uniatron/badge.svg?branch=master)](https://coveralls.io/github/FHellmann/Uniatron?branch=master)

![Banner](https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/0_Play-Store_Functiongraph.png)

**UNIAtron is the app to spend less time on the phone and more time with exercise!**

Smartphone usage today has become excessive and is becoming a serious concern for overall health. This app tries to counteract

At the beginning of each day the user launches with no time contingent. This will benefit both people who use their phone too much past midnight, as well as everyone who doesn't want to leave their bed and spends too much time on their phone directly after waking up. 

By achieving activity goals (steps during the day), the user can re-enable usage time. Depending on the extent of the activity, a longer or shorter usage time can be added to your daily limit.

The automatic locking of apps will encourage the user to become more active. Additionally, it will counteract excessive usage of mobile devices.

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
All steps are being counted during the day via a built-in sensor.
The amount of recent steps is displayed on the lower left part of the main page.
After exchange to usage time the amount is decreased by the specified number. 
(For overview on all steps see History View)
### App locking
Lock social/fun apps from Blacklist after a certain amount of time has been spent on the phone.
After App Lock is active when opening blacklisted apps Uniatron will block the access to these apps. 
### Learning Aid
Helps you keep concentrated by locking screen/apps for a certain amount of time.
You won't be destracted by your phone anymore.
E.g. screen is locked for 55 minutes before you get access to apps again.
### History View
By clicking on history (on main page) you get a list of recent days with info on average emotion, total of app usage time and total amount of steps during this day.
By selecting "Group by month" or "Group by year" the list consists of entrys with summarys for month or year
### Emotion tracking
Track emotions after app usage and throughout the day (via emojis on a range from sad to happy).
You can track your mood afterwards in the History View.
### Configurations
Configure app settings:
Select a Fitness Level: How easy/hard do you want it to be to exchange steps for usage time.
In the Blacklist all apps that you want to get blocked by Uniatron should be selected.
### On-Boarding (Introduction)
The user will be prompted with an intro screen on the first app launch.
This will guide him through the setup process and request all necessary permissions.
Upon completion the user will be granted a bonus of 500 steps, so he can try the announced shop for himself.
Additionally, one sample entry for the current day will be added, to provide immediate feedback of the just now introduced UI.

## Schedule
### Milestone 1: First prototype (Due date May 31, 2018)
We will create a first prototype with the basic functionality.

- Locking apps
- Collecting time of doing sportive activity
- Tracking emotions
- Configure the app

### Milestone 2: Final app - BETA (Due date July 11, 2018)
We will further refine the prototype. Thus, additional features might be implemented.

- Learning aid
- Group history view by day/month/year
- Detail history day/month/year view
- On-Boarding

### Milestone 3: Final app - RELEASE
- App Icon
- Lifestyle Assistant
- Wearables connection

## Design
Considering the fact that the user can reach every content of the app with three clicks, we opted for a flat navigation hierarchy. With a navigation bar at the bottom with three options (timeline, home, settings) every screen can be reached with just one click. Accordingly, a navigation depth of no more than two clicks can be claimed on each of these pages.

The design of our app complies with the design guidelines of [Google Material Design](https://material.io/guidelines/material-design/introduction.html).

### Final App - BETA
Screenshot | Description
:---------:|:----------:
<img src="https://github.com/FHellmann/Uniatron/blob/master/doc/Screenshots/Screenshot_Today.png" width="800"/> | **Start screen/Today**<br/>Several cards are displayed in the today screen. These cards contain different information, e. g. app usage, summary. At the top, two cards are represented colored to get the users focus on the import cards. In this case it would be the remaining time of app usage and the amount of coins to trade more time.
<img src="https://github.com/FHellmann/Uniatron/blob/master/doc/Screenshots/Screenshot_History.png" width="800"/> | **History**<br/>The history is layouted the same like the today view. The only difference is by not displaying the two upper cards about time account and coin bag.
<img src="https://github.com/FHellmann/Uniatron/blob/master/doc/Screenshots/Screenshot_Shop.png" width="800"/> | **Shop**<br/>If the user has enough coins collected, then he is able to trade these coins into more app usage time. Another option would be the "Learning Aid", if there are not enough coins available. The user also states his current mood here.
<img src="https://github.com/FHellmann/Uniatron/blob/master/doc/Screenshots/Screenshot_Settings.png" width="800"/> | **Settings**<br/>By now, there are two settings available. The first one is to set the fitness level of the user. Therefor three levels can be chosen - easy, normal, hard. Of course, the second setting is to select all the apps which should be blocked.
<img src="https://github.com/FHellmann/Uniatron/blob/master/doc/Onboarding/banner.png" width="800"/> | **On-Boarding (Introduction)**<br/>The On-Boarding is an introduction for the user to get first information about the app and how to use it. The user will also be asked to grant the required permissions to run the app.
<img src="https://github.com/FHellmann/Uniatron/blob/master/doc/Screenshots/Screenshot_About.png" width="800"/> | **About**<br/>The about view depicts the credit to the contributors.

### Prototype
![alt text](https://github.com/FHellmann/Uniatron/blob/master/doc/Concepts/Prototype.JPG)

## Coding Guidelines

### Work Process
1. Create Issue
2. Create Branch (Naming: {first letter of first name + last name}-{issue nr})
3. Work your task
4. Create Pullrequest
5. Review

### Coding Style
[Google Android](https://source.android.com/setup/contribute/code-style)

- Use [Optional](http://www.baeldung.com/java-optional) instead of return null

## Collaborators
- [Fabio Hellmann](https://github.com/FHellmann)
- [Leon Wöhrl](https://github.com/leonpoint)
- [Anja Hager](https://github.com/anja-h)
- [Danilo Hoss](https://github.com/speedyhoopster3)














