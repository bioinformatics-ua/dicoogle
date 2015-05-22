function getPatients(data) {
    var patients = []

    data.map(function(item){
    	if(!containsPatient(patients, item.fields.PatientID))
    		patient.push({id: item.fields.PatientID});
    };

    return patients;
}

function getStudiesByPatient(data, id)
{
	
}

function containsPatient(patients){
	for(var i = 0; i<data.length;i++){
		if(patient[i].id == id)
			return true;
	}
	return false;
}
 
 
export {getPatients};