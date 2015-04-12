for $person in doc("people.xml")/People/Person
for $movies in doc("movies.xml")/Movies
let $max := max(count($movies/Movie/Actors/Actor[@PID eq $person/@PID]))
where $person/@gender = "female"
	and $person/@PID = $movies/Movie/Actors/Actor/@PID
order by count($movies/Movie/Actors/Actor[@PID eq $person/@PID]) descending
return 
       (:
	<ActressInfo>

	for $count in doc("movies.xml")/Movies/Movie/Actors/Actor
	where $count/Actor/@PID = $person/@PID
	<Actress>
	:)

	<ActressInfo>{
	
	$person/Name/First,$person/Name/Last,count($movies/Movie/Actors/Actor[@PID eq $person/@PID]), max(count($movies/Movie/Actors/Actor[@PID eq $person[1]/@PID])), $max

	}</ActressInfo>
(:	</Actress>

	</ActressInfo>

        if (count($movie/Actors/Actor/@PID) = max(count($movie/Title)))
		then ($person/@PID, $person/Name/Last, $movie/Title)
		else ()  :)
