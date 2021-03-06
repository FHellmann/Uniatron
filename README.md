# UNIAtron
[![Build Status](https://travis-ci.org/FHellmann/Uniatron.svg?branch=master)](https://travis-ci.org/FHellmann/Uniatron)
[![codecov](https://codecov.io/gh/FHellmann/Uniatron/branch/master/graph/badge.svg)](https://codecov.io/gh/FHellmann/Uniatron)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/59acfe0f478548479a3213daf8d78d1b)](https://www.codacy.com/app/fhellmann93/Uniatron?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=FHellmann/Uniatron&amp;utm_campaign=Badge_Grade)
[![BCH compliance](https://bettercodehub.com/edge/badge/FHellmann/Uniatron?branch=master)](https://bettercodehub.com/)


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
The Top 5 apps in usage time are displayed in a list on the main page (and their percentages).
You can optionally show every app by clicking on a button "Show all".
### Step Counter
You are rewarded for doing sportive activity (user can exchange steps for additional screen time).
All steps are being counted during the day via a built-in sensor.
A coinbag proportional to the amount of recent steps is displayed in the upper right corner.
After the exchange of coins for usage time the amount is decreased by the specified number. 
### App locking
Lock social/fun apps from Blacklist after a certain amount of time has been spent on the phone.
After App Lock is active Uniatron will block the access to blacklisted apps.
Locking can be disabled by using the Time Credit Shop. 
### Learning Aid
Helps you keep concentrated by immediately locking apps for a certain amount of time.
Your phone won't destract you from learning anymore.
E.g. blacklisted apps are locked for 55 minutes before you get access again.
### Time Credit Shop
In the shop you're able to chose between the Learning Aid and directly exchanging steps for free app usage time. You can chose the amount of coins that you're willing to sacrifice.
### Emotion tracking
Track emotions after app usage and throughout the day (via emojis on a range from sad to happy).
Selecting your emotion is mandatory when doing a transaction in the Time Credit Shop.
You can track your mood afterwards in the History View.
### History View
By clicking through different days via the arrows on the upper bar of the screen you get a summary with info on average emotion, total of app usage time and total amount of steps during the specified period.
The default view is set to the current period.
By clicking the label on the upper bar you can chose a date from a calendar.
With selecting a "Group by"-option in the settings you can switch between summarys for day, month or year.
### Configurations
Configure app settings:
Select a Fitness Level: How easy/hard do you want it to be to exchange steps for usage time.
In the Blacklist all apps that you want to get blocked by Uniatron should be selected.
### On-Boarding (Introduction)
The user will be prompted with an intro screen on the first app launch.
This will guide him through the setup process and request all necessary permissions.
Upon completion the user will be granted a bonus of 500 steps, so he can try the announced shop for himself.
Additionally, one sample entry for the current day will be added, to provide immediate feedback of the just now introduced UI.

## Design
Considering the fact that the user can reach every content of the app with three clicks, we opted for a flat navigation hierarchy. With a navigation bar at the bottom with three options (timeline, home, settings) every screen can be reached with just one click. Accordingly, a navigation depth of no more than two clicks can be claimed on each of these pages.

The design of our app complies with the design guidelines of [Google Material Design](https://material.io/guidelines/material-design/introduction.html).

## Coding Guidelines

### Work Process
1. Create Issue
2. Create Branch with the following naming: {first letter of first name + last name}-{issue nr}-{issue description}
3. Implement the solution for the related issue in the branch
4. Create a pull-request
5. Review by at least 1 reviewer
6. Update code to solve review comments
7. Merge pull-request if everything is fine

### Multiple feature testing
To test multiple features merge every feature to test in a new branch. This branch can then be merged into the master branch, if the tests were
successful.

### Coding Style
As for the coding style guide we use [Google's Android](https://source.android.com/setup/contribute/code-style) style.

In addition:
- Use [Optional](http://www.baeldung.com/java-optional) instead of return null

## Collaborators
- [Fabio Hellmann](https://github.com/FHellmann)
- [Leon Wöhrl](https://github.com/leonpoint)
- [Danilo Hoss](https://github.com/speedyhoopster3)
- [Anja Hager](https://github.com/anja-h)
