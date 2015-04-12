for $director in doc("movies.xml")/Movies/Movie/Director/@PID
for $person in doc("people.xml")/People/Person[not(@dob)]
where $director = $person/@PID
return { $director , $person/Name/Last }               