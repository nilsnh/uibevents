This app was built as a final project in a course in Android App Development facilited by a [Google Developers Group in Bergen](http://www.meetup.com/GDGBergen/). In addition to weekly study jams we followed an course in [Android Fundamentals on Udacity](https://www.udacity.com/wiki/ud853). 

## UiB Events: What the app does

The app talks to an event api which servers the events happening at the University of Bergen. [Info about their event api](http://hackathon.b.uib.no/data-og-idear/uibdata/) (Norwegian). UiB has in fact [an open data initiative](https://it.uib.no/%C3%85pne_data) (Norwegian).

## How it does it

1. Data is retrieved and exposed through a contentprovider. Furthermore a syncadapter is used for the syncing to ensure efficient power usage. 
2. The app makes sure to bundle relevant state on sudden app restarts and reload state when the app comes back into focus.
3. Separate layouts are provided for tablet and mobile to create a responsive design that does efficient usage of screen real estate.

## Todo

- Add a proper json database instead of the flatfile it currently uses.
