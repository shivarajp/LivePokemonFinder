Based on https://github.com/omkarmoghe/Pokemap

# Pokemap [![Build Status](https://travis-ci.org/omkarmoghe/Pokemap.svg?branch=dev)](https://travis-ci.org/omkarmoghe/Pokemap) [![Download](https://img.shields.io/badge/download-latest-brightgreen.svg?style=flat-square)](https://github.com/omkarmoghe/Pokemap/releases)
[Can't log in? Check the PTC login status.](http://ispokemongodownornot.com/)

A native Android client built with https://github.com/AHAAAAAAA/PokemonGo-Map

<img src="http://imgur.com/Wd3MPhs.png" width="270" height="480"/>
<img src="http://imgur.com/2bvp0k5.png" width="270" height="480"/>
<img src="http://imgur.com/He6oHLw.png" width="270" height="480"/>

screenshots from @linkout @mike @z14942744 @kyr0 @rancor

### Please Read
* Due to the recent server side changes by Niantic they have implemented request throttling. Please do not open any tickets about no pokemon showing up. This is known and options are being discussed.
[Refer Here](https://github.com/omkarmoghe/Pokemap/issues/383)


Make sure you use the latest [android studio version 2.2 (canary builds)] (http://tools.android.com/download/studio/canary/latest) and have installed the latest versions of the build tools and support libraries in order to successfully compile and run the project. 

# Contributing
Please follow the [Android style guides & best practices](https://source.android.com/source/code-style.html)

All PRs should go to the `dev` branch. `master` will be updated periodically with stable* releases.</str>

## [TODO](https://slack-files.com/T1TQY34KE-F1TSY25UL-10400392c2)
Please read through the main repo to see how the Python code is grabbing the spawned Pokemon, etc. We need to recreate that functionality in Java :D.

Also, please read this to understand how this all works: https://www.reddit.com/r/pokemongodev/comments/4svl1o/guide_to_pokemon_go_server_responses/

## Translations
If you are contributing a PR related to language translations, please have other members of the community :thumbsup: your change.

## Reporting Bugs
Please include a screenshot and instructions on how to recreate the bug you are reporting. If you have any LogCat stack traces, etc. please include those as well. **Use the search bar to make sure you are not creating duplicate issues. If your bug has already been reported, feel free to comment with additional info.**


> Live visualization of all pokemon (with option to show gyms and pokestops) in your area. This is a proof of concept that we can load all nearby pokemon given a location. Currently runs on a Flask server displaying a Google Map with markers on it.
> 


---

\* lol

:pineapple:
