for $movie in doc("movies.xml")/Movies/Movie
for $person in doc("people.xml")/People/Person
where $person/Name/First = "Sam"
	and $person/Name/Last = "Worthington"
	and $movie/Actors/Actor/@PID = $person/@PID
order by $movie/@year descending
return {$movie/Title, $movie/@year}          