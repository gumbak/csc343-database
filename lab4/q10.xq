xquery version "1.0"; 

<ActressInfo>
{ 
	let $females := doc("people.xml")/People/Person[@gender="female"]
	let $actors := 	doc("movies.xml")//Actor
	for $f in $females
	let $appearances := count($actors[@PID = $f/@PID]) 
	where $appearances = max(
			for $f2 in $females, $a2 in $actors
			where $f2/@PID = $a2/@PID
			return count($actors[@PID= $f2/@PID]) )
	return <Actress firstname="{$f/Name/First}" lastname="{$f/Name/Last}" count="{$appearances}" />
}
</ActressInfo>
