for $movie in doc("movies.xml")/Movies/Movie
return { $movie/@MID , count($movie/Actors/Actor)}               