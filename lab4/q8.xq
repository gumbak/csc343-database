for $movie in distinct-values(doc("movies.xml")/Movies/Movie/Director/@PID)
for $person in doc("people.xml")/People/Person
where $movie = $person/@PID 
	and $movie = doc("movies.xml")/Movies/Movie/Actors/Actor/@PID
return {	
	
	$person/@PID, $person/Name/Last
}          