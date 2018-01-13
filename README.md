# Meetup Photo Downloader

This project is intended to download all of the photos from your meetup group by using your meetup.com API key. The intent of this script is to be used to backup the photos.

This project assumes that Groovy is isntalled on your system.

## How to Use 

 1. Modify the following variables in the script: meetupKey, directory, group. The group variable is the textual Group URL name. (This can be found after meetup.com/... as the name).
 2. Run the groovy script as groovy Download.groovy
 3. Wait for the results to finish. When the script is done the console will be ready for another command. If there was an issue with grabbing a file sometimes a 500 will be thrown. Try to run the script again and it may succced.
 
 
 
## About the Author 

Steven Hicks is a developer that can be found at http://theexceptioncatcher.com. He has been a self taught, and formally educated developer for nearly 20 years.

## Edits after Forking

ccenergy: Rename folder based on photoalbum created property
