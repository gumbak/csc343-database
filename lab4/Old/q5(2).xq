for $movie in doc("movies.xml")/Movies/Movie
for $person in doc("people.xml")/People/Person
where $person/Name/First = "James"
	and $person/Name/Last = "Cameron"
	and $movie/Director/@PID = $person/@PID
return { $movie/Title }               