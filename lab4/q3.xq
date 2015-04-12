xquery version "1.0"; 

<table>
	<tr>
		<th>Person ID</th>
		<th>Last name</th>
	</tr>
	{for $p in doc("people.xml")/People/Person, $d in doc("movies.xml")/Movies/Movie/Director
	where $p/@PID = $d/@PID
	return 
		if ($p[@pob])
		then ()
		else <tr><td>{string($p/@PID)}</td><td>{$p/Name/Last/text()}</td></tr>
	}
</table>
