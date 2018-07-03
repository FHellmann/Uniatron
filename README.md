# Uniatron
[![Build Status](https://travis-ci.org/FHellmann/Uniatron.svg?branch=master)](https://travis-ci.org/FHellmann/Uniatron)
[![Coverage Status](https://coveralls.io/repos/github/FHellmann/Uniatron/badge.svg?branch=master)](https://coveralls.io/github/FHellmann/Uniatron?branch=master)

![Banner](https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/0_Play-Store_Functiongraph.png)

**Uniatron is the app to spend less time on the phone and more time with exercise!**

At the beginning of a day, each user launches with a set amount of usage time that gradually degrades as apps are used. By achieving activity goals, they can be used to re-enable usage time. Depending on the extent of the activity, a longer or shorter usage time can be added to your daily limit.

The automatic locking of apps will encourage the user to become more active and to counteract the general use of mobile phones.

[<img alt='Get it on Google Play' src='https://play.google.com/intl/en_gb/badges/images/generic/en_badge_web_generic.png' width="180" />](https://play.google.com/store/apps/details?id=com.edu.uni.augsburg.uniatron&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1)

## Motivation
- Quality > Quantity
- Clean-Code
- Clean-Design
- Easy to use

## Features
- Lock social/fun apps after a certain amount of time has been spent
- Collect time for doing sportive activity (user can exchange steps for additional screen time)
- Track emotions after app usage and throughout the day (via questions/smileys)
- Configure app settings (user can set the time at which the daily stepcount reset happens)
- Aid learning (locking screen/apps for certain amount of time)

## Schedule
### Milestone 1: First prototype (Due date May 31, 2018)
We will create a first prototype with the basic functionality.

- Locking apps
- Collecting time of doing sportive activity
- Tracking emotions
- Configure the app

### Milestone 2: Final app (Due date June 28, 2018)
We will further refine the prototype. Thus, additional functionalities might be implemented.

- Learning aid

## Design
Considering the fact that the user can reach every content of the app with three clicks, we opted for a flat navigation hierarchy. With a navigation bar at the bottom with three options (timeline, home, settings) every screen can be reached with just one click. Accordingly, a navigation depth of no more than two clicks can be claimed on each of these pages.

The design of our app complies with the design guidelines of [Google Material Design](https://material.io/guidelines/material-design/introduction.html).

### Final App
Home | History | History
:---:|:-------:|:------:
<img src='https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/1_Play-Store.png' width="230" /> | <img src='https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/2_Play-Store.png' width="230" /> | <img src='https://github.com/FHellmann/Uniatron/blob/master/doc/Play%20Store/3_Play-Store.png' width="230" />

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














