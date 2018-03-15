
# XYZ Project
Udacity Android Nanodegree Project FIVE submission

## Project Specification

The aim of this project is to improved the provided code which implements a news readers 
app. Improvements are made based on the user feedback in the UI Review node and are implemented
to better the UI and make it conform to Material Design.

The app is using the Design Support library and its provided widgets:
 * [CoordinatorLayout](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html)
 * [AppBarLayout](https://developer.android.com/reference/android/support/design/widget/AppBarLayout.html)
 * [CollapsingToolbarLayout](https://developer.android.com/reference/android/support/design/widget/CollapsingToolbarLayout.html)
 * [FloatingActionButton](https://developer.android.com/reference/android/support/design/widget/FloatingActionButton.html)
 * [SnackBar](https://developer.android.com/reference/android/support/design/widget/Snackbar.html)

Full rubric can be found [here](https://review.udacity.com/#!/rubrics/63/view)

#### The main areas for improvement are:
 * **Duplicated behaviours**: the app had many customised views to provided UI behaviours already 
 defined by the Android Framework. The aim of this project is to remove all redundant code and 
 use the Material Design Library components
 * **Very large article string with formatting entries** are provided as part of the article 
 service reponse: The article string had to be cleaned up 
 prior to be set as a text of the article view. Also, due to its length, the UI frezzes. In 
 order to avoid this, the article is broken into paragraphs 
 which are stored in a list.
 * **Article sharing functionality**: This was not fully implemented and has to be completed.
 * **Article list refresh behaviour**: This was inconsistent and had to be 
 refractorised 
 so that it shows at pertinent times. e.g.: the loading bar showed every time we return from 
 the details activity.
 * **Nested classes**: when possible code have been put in its own class file in order to simplify 
 code and improve readability
 * **Duplicated code**: String and date formatting code has been put in utils classes in order to 
 avoid code duplication

## Implementation
As a requirement, this project is written using only Java.

## UI
The application follows the master-detail view pattern consisting of two views: Main and Detail view
### Main View
The
[CoordinatorLayout](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html) 
is used as the root view. The layout facilitates a custom toolbar animation. 

The
[SwipeRefreshLayout](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html)
is used  to hold the view 
content and to provide the capability to update the content. This 
feature 
can also 
be triggered from the option menu. 

The user is prompted with a message about the connection state when he try to refresh the content
and a connection is not available. A 
[SnackBar](https://developer.android.com/reference/android/support/design/widget/Snackbar.html)
is also shown when the connection is restablish.

[RecyclerView](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html) 
is used to display the article list. The list is reflowed and displayed with different 
number of column based on the screen dimensions. 

### Detail view
The activity shows the article's detail view. The activity uses a 
[ViewPager](https://developer.android.com/reference/android/support/v4/view/ViewPager.html) 
in order to show the set of articles that the user can see by swiping left or right between them

The
[CoordinatorLayout](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html) 
is used as the root view. A customised
[CoordinatorLayout.Behavior](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.Behavior.html), 
[ArticleTitleBehaviour](/XYZReader/src/main/java/com/example/xyzreader/ui/ArticleTitleBehaviour.java),
 is implemented to generate a transition from the title pane to the
[Toolbar](https://developer.android.com/reference/android/support/v7/widget/Toolbar.html) 
 title.

The activity implements a responsive layout where content is displayed based on the display
size. 
[CoordinatorLayout](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html) 
 is used to centre the article content pane using percentages.

The articles provided in the feed have very lengthy article strings. This generates problems when 
set as a text of a TextView freezing the UI that becomes unresponsive. In order to solve this 
issue, a 
[RecyclerView](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html)
is used to display the articles where each paragraph becomes an item of the list.

The activity provides a Floating Action Button to share the current article. 
The button stay on screen as its action remains the same across screens following the Material Design specification. 

The app specifies elevations for app bars, FABs, and other elements following the Material Design specification. 

### Design
###### Theme
A theme is defined for the app which extends the Theme.AppCompat.Light.NoActionBar. The theme sets
 the colour scheme.
###### Color
The colour scheme was created based on the colour of the launch button which was provided with 
the initial code. Its orange shade is used as the accent colour. The 
primary colours are a range of teal shades that contrast with the accent colour.

Colours are defined in the style.xml file 
###### Layout
Margin and padding in the app follow recommendations from the material.io guidelines.
###### Images
Images are high quality and specific to each article. These images provided as part of the 
article feed. Picasso is used for the loading of this images. All images in the app are displayed full-bleed.
###### Fonts
The app uses two fonts which are complementary and allow the user to concentrate in the content: 
* Rosario-Regular for main content text
* Roboto Condensed

Text appearance is determined by a set of styles defined in the styles.xml file. All text view 
styles are set using one of the customised font styles on this file. 


### Libraries
This is a list of the
external libraries use in the project as requirement:

* [square / okhttp](http://square.github.io/okhttp/)
* [square / picasso](https://github.com/square/picasso)
* 
For a list of support and other Android libraries, see the
[build.gradle](XYZReader/build.gradle)file

### Contact:
labs@desiderio.me.uk


