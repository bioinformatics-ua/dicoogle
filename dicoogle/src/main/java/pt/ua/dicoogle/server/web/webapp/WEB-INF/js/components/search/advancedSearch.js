/*jshint esnext: true*/

var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

import {ResultSearch} from '../search/searchResultView';

var AdvancedSearch = React.createClass({
    getInitialState: function (){
        return { label:'login' };
    },
    render: function() {
        var managementInstance = (
            <div>
                 <div id="filter-group">
                    <div className="row space_up">
                        <div className="col-xs-12 col-sm-6">
                            <div className="globalmargin">
                                <div className="subject_text">Patient Name</div>
                                <input id="patient_name" type="text" className="form-control" placeholder="(All Patients)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Patient ID</div>
                                <input id="patient_id"type="text" className="form-control" placeholder="(All IDS)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Patient Gender</div>
                                <div className="inline_block">
                                    All
                                    <input id="gender_all" type="radio" name='genderRadio'></input>Male
                                    <input id="gender_male" type="radio" name='genderRadio'></input>Female
                                    <input id="gender_female" type="radio" name='genderRadio'></input>

                                </div>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Instituition Name</div>
                                <input id="instituition" type="text" className="form-control" placeholder="(All Instituitions)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Physician</div>
                                <input id="physician" type="text" className="form-control" placeholder="(All Physicians)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Operator Name</div>
                                <input id="OperatorName" type="text" className="form-control" placeholder="(All Operators)"></input>
                            </div>


                        </div>
                        <div className="col-xs-12 col-sm-6">
                            <div className="subject_text space_up">Modality</div>
                            <div>
                                <label for="modCR">CR</label>
                                <input id="modCR" type="checkbox" name="modCR" onclick="modalityCheckBoxToggle()"/>

                                <label for="modMG">MG</label>
                                <input id="modMG" type="checkbox" name="modMG" onclick="modalityCheckBoxToggle()"/>

                                <label for="modPT">PT</label>
                                <input id="modPT" type="checkbox" name="modPT" onclick="modalityCheckBoxToggle()"/>

                                <label for="modXA">XA</label>
                                <input id="modXA" type="checkbox" name="modXA" onclick="modalityCheckBoxToggle()"/>

                                <label for="modES">ES</label>
                                <input id="modES" type="checkbox" name="modES" onclick="modalityCheckBoxToggle()"/>

                            </div>
                            <div>
                                <label for="modCT">CT</label>
                                <input id="modCT" type="checkbox" name="modCT" onclick="modalityCheckBoxToggle()"/>

                                <label for="modMR">MR</label>
                                <input id="modMR" type="checkbox" name="modMR" onclick="modalityCheckBoxToggle()"/>

                                <label for="modRF">RF</label>
                                <input id="modRF" type="checkbox" name="modRF" onclick="modalityCheckBoxToggle()"/>

                                <label for="modUS">US</label>
                                <input id="modUS" type="checkbox" name="modUS" onclick="modalityCheckBoxToggle()"/>

                                <label for="modDX">DX</label>
                                <input id="modDX" type="checkbox" name="modDX" onclick="modalityCheckBoxToggle()"/>

                            </div>
                            <div>
                                <label for="modNM">NM</label>
                                <input id="modNM" type="checkbox" name="modNM" onclick="modalityCheckBoxToggle()"/>

                                <label for="modSC">SC</label>
                                <input id="modSC" type="checkbox" name="modSC" onclick="modalityCheckBoxToggle()"/>

                                <label for="modOT">OT</label>
                                <input id="modOT" type="checkbox" name="modOT" onclick="modalityCheckBoxToggle()"/>


                            </div>
                            <div className="subject_text space_up">Date</div>
                            <input type="text" id="datepicker"></input>



                        </div>

                    </div>
                    <button type="button" className="btn btn_dicoogle centerDivH" onClick={this.onSearchClicked}>Search</button>
                </div>
            </div>
            );
        return managementInstance;
    },
    componentWillMount: function() {
    // Subscribe to the store.
        SearchStore.listen(this._onChange);

    },

    _onChange : function(data){
        console.log(data);
     //    if (this.isMounted())
     // this.setState({label:data});
   },
   onSearchClicked : function(){
     console.log("SEARCH CLICKED");
       //NAME
       var patientName = document.getElementById("patient_name").value;
       var text = "PatientName: "+this.checkEmpty(patientName); //TODO FINISH

       //GENDER
       var gender;
       if (document.getElementById('gender_male').checked) {
          gender = " AND PatientSex:M";
       }
       else if(document.getElementById('gender_female').checked){
         gender = " AND PatientSex:F";
       }
       else
       {
         gender = "";
       }
       text = text + gender;
       //ID
       var patientId = document.getElementById("patient_id").value;
       if(this.checkEmpty(patientId) != "*")
       text = text + " AND PatientID: " + this.checkEmpty(patientId);
       //Instituition
       var instituition = document.getElementById("instituition").value;
       if(this.checkEmpty(instituition) != "*")
       text = text + " AND InstitutionName: " + this.checkEmpty(instituition);
       //Pshysician
       var physician = document.getElementById("physician").value;
       if(this.checkEmpty(physician) != "*")
       text = text + " AND PerformingPhysicianName: " + this.checkEmpty(physician);
       //OperatorName
       var OperatorName = document.getElementById("OperatorName").value;
       if(this.checkEmpty(OperatorName) != "*")
       text = text + " AND OperatorName: " + this.checkEmpty(OperatorName);
       ///////
       ///////
       var params = {text: text, keyword: true, other:true};

       React.render(<ResultSearch items={params}/>, document.getElementById("container"));


       //console.log("asadfgh");
   },
   checkEmpty: function(text){
     if(text.length ==0 )
      return "*";
     else
      return text;
   }
});

export {AdvancedSearch};

window.action = ActionCreators;
