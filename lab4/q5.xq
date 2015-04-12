xquery version "1.0"; 

<table>
	<tr>
		<th>Movie ID</th>
		<th>Title</th>
		<th>Year</th>
	</tr>
	{ 
		for $m in doc("movies.xml")/Movies/Movie
		let $jc := doc("people.xml")/People/Person[Name/First="James" and Name/Last=
			"Cameron"]
		where ($m/Director/@PID = $jc/@PID) and (xs:int($m/@year) > 2000)
		return <tr>
			<td> { string($m/@MID) } </td>
			<td> { $m/Title/text() } </td>
			<td> { data($m/@year) } </td>
			</tr>
	}
</table>
