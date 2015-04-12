let $people := doc("people.xml")/People/Person
let $totaloscars := count($people/Oscar)
let $numppl := count(doc("people.xml")/People/Person)  
return { $totaloscars div $numppl }                        