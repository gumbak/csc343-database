for $movie in doc("movies.xml")/Movies/Movie
where count($movie/Oscar) != 0
return {	
	$movie/Title,

	for $oscar in doc("oscars.xml")/Oscars/Oscar
	where $movie/Oscar/@OID = $oscar/@OID
	return $oscar/Type
}          