@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')

import groovyx.net.http.RESTClient

def meetupKey = '--Key Goes Here, Nice try mr hacker--'
def directory = '.' //Directory prefix goes here
def group = '--Group Name--'

def defaultQueries = [sign: 'true', 'photo-host': 'public', page: 20, key: meetupKey]

def groupMeetup = new RESTClient("https://api.meetup.com/$group/")
def albums = groupMeetup.get(path: 'photo_albums', query: defaultQueries)


albumMap = albums.data.collectEntries {
    [(it.id) : it.title ]
}

//https://api.meetup.com/Chicago-Reddit-Meetup/photo_albums/26197855?&sign=true&photo-host=public
println albumMap

albumMap.each{
    id, name ->
        //Make directory
        def currentDirectory = "$directory/${name.replaceAll("[\\W\\s]+", "")}"
        new File(currentDirectory).mkdir()

        println "Getting all photos for the album $name"

        //Request all photos under the id
        def results = groupMeetup.get(path:"photo_albums/$id/photos", query: defaultQueries)

        //Download
        results.data?.each {
            println "- Downloading Photo: ${it.id}"
            String fileName = "$currentDirectory/${it.id}.jpg"

            //If the file does already exist lets not continue
            if (!new File(fileName).exists()) {
                new File(fileName).withOutputStream { out ->
                    out << new URL(it.highres_link).openStream()
                }
            }
        }
}