xquery version "1.0"; 

<table>
	<tr>
		<th>Oscar award name</th>
		<th>Year first awarded</th>
		<th>Title of movie first awarded to</th>
	</tr>
	{
		let $oscars := doc("oscars2.xml")/Oscars/Oscar
		for $o in $oscars, $m in doc("movies.xml")/Movies/Movie
		let $sametype := $oscars[Type = $o/Type]
		where $o/@OID = $m/Oscar/@OID and $o/@year = min( $sametype/@year )

		return <tr>
			<td> { $o/Type/text() } </td>
			<td> { string($o/@year) } </td>
			<td> { $m/Title/text() } </td>
			</tr>
	}
</table>
