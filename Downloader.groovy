@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')

import groovyx.net.http.RESTClient

// Example URL: https://www.meetup.com/de-DE/Zurich-Hike-Outdoor/photos/29900145/
// -> group = Zurich-Hike-Outdoor
// -> photoAlbumIds = [29900145]

def meetupKey = '--Key Goes Here, Nice try mr hacker--'  // See https://secure.meetup.com/meetup_api/key
def directory = '.'                // Directory prefix goes here
def group = '--Group Name--'       // Printable name of group as in URL
def photoAlbumIds = [--Album ID--] // ID of Albums, to download only certain 
                                   // albums, not all. Groovy list.
                                   // set to -1 to download ALL albums

def defaultQueries = [sign: 'true', 'photo-host': 'public', page: 20, key: meetupKey]

def groupMeetup = new RESTClient("https://api.meetup.com/$group/")
def albums = groupMeetup.get(path: 'photo_albums', query: defaultQueries)

albumMap = albums.data.collectEntries {
    [(it.id) : [title:(it.title), created:(new Date(it.created).format("yyyy-MM-dd"))] ]
}

albumMap.each{
    id, album ->
        if (photoAlbumIds.contains(-1) || photoAlbumIds.contains(id)){
            // Make directory
            def currentDirectory = "$directory/${album.created}_${(album.title).replaceAll("[\\W\\s]+", "")}"
            new File(currentDirectory).mkdir()

            println "Getting all photos for the album ${album.title}"

            // Request all photos under the id, starting from 0
            def offset = 0
            def query = defaultQueries + [offset: offset]
            def results = groupMeetup.get(path:"photo_albums/$id/photos", query: query)

            // Download, as long as there's something to get
            while (results.data) {
                results.data?.each {
                    // Store caption in filename, if there's one
                    String caption = "${(it.caption ? "_" + it.caption : "").replaceAll("[\\W\\s]+", "")}"
                    String fileName = "$currentDirectory/${it.id}${caption}.jpg"
                    // println "Würde herunterladen: $fileName"

                    // If the file does already exist lets not continue
                    if (!new File(fileName).exists()) {
                        println "- Downloading Photo: ${it.id}"
                        new File(fileName).withOutputStream { out ->
                            out << new URL(it.highres_link).openStream()
                        }
                    } else {
                        println "- Skipping Photo: ${it.id}"
                    }
                }
                // Get next page of images
                query = defaultQueries + [offset: offset++]
                results = groupMeetup.get(path:"photo_albums/$id/photos", query: query)
            }

            println ""
        }
}
