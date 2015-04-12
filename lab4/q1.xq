xquery version "1.0"; 

<div>
{	
	for $ps in doc("people.xml")/People
	let $pcount := count($ps/Person[Oscar])
	let $ocount := count($ps//Oscar)
	return $ocount div $pcount

}
</div>
