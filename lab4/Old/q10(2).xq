for $movies in doc("movies.xml")/Movies
for $person in doc("people.xml")/People/Person
let $count := count($movies/Movie/Actors/Actor[@PID eq $person/@PID])
where $person/@gender = "female"
	and $person/@PID = $movies/Movie/Actors/Actor/@PID
(: order by count($movies/Movie/Actors/Actor[@PID eq $person/@PID]) descending :)
order by $count descending
return { $person/Name/First, $person/Name/Last, count($movies/Movie/Actors/Actor[@PID eq $person/@PID]), $count }

(:
declare function local:getcount($a){
let $person := doc("people.xml")/People/Person
for $movies in doc("movies.xml")/Movies
for $actress in doc("movies.xml")/Movies/Movie/Actors/Actor
let $count := count($movies/Movie/Actors/Actor[@PID eq $actress/@PID])
where $actress/@PID = $person/@PID
      and $person/@gender = "female"
order by $count
return { $count } 
};
:)





