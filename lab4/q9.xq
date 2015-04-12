xquery version "1.0"; 

<Stats>
{
	let $m := doc("movies.xml")/Movies
	for $c in distinct-values($m/Movie/Genre/Category)
	return <Bar 
		category="{ $c }" 
		count="{ count($m/Movie/Genre[Category=$c])
		}"></Bar>
}
</Stats>
