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
    componentDidMount: function() {
      $("#datepicker").datepicker();
    },

    render: function() {
        var managementInstance = (
            <div>
                 <div id="filter-group">
                    <div className="row space_up">
                        <div className="col-xs-12 col-sm-8">
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
                        <div className="col-xs-12 col-sm-4">
                            <div className="subject_text space_up">Modality</div>
                            <div className="modalities">
                                <label for="modCR">CR</label>
                                <input id="modCR" type="checkbox" name="CR" />

                                <label for="modMG">MG</label>
                                <input id="modMG" type="checkbox" name="MG" />

                                <label for="modPT">PT</label>
                                <input id="modPT" type="checkbox" name="PT" />

                                <label for="modXA">XA</label>
                                <input id="modXA" type="checkbox" name="XA" />

                                <label for="modES">ES</label>
                                <input id="modES" type="checkbox" name="ES" />


                                <label for="modCT">CT</label>
                                <input id="modCT" type="checkbox" name="CT" />

                                <label for="modMR">MR</label>
                                <input id="modMR" type="checkbox" name="MR" />

                                <label for="modRF">RF</label>
                                <input id="modRF" type="checkbox" name="RF" />

                                <label for="modUS">US</label>
                                <input id="modUS" type="checkbox" name="US" />

                                <label for="modDX">DX</label>
                                <input id="modDX" type="checkbox" name="DX" />

                                <label for="modNM">NM</label>
                                <input id="modNM" type="checkbox" name="NM" />

                                <label for="modSC">SC</label>
                                <input id="modSC" type="checkbox" name="SC" />

                                <label for="modOT">OT</label>
                                <input id="modOT" type="checkbox" name="OT" />


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
       var query = "PatientName: "+this.checkEmpty(patientName); //TODO FINISH

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
       query = query + gender;
       //ID
       var patientId = document.getElementById("patient_id").value;
       if(this.checkEmpty(patientId) != "*")
       query = query + " AND PatientID: " + this.checkEmpty(patientId);
       //Instituition
       var instituition = document.getElementById("instituition").value;
       if(this.checkEmpty(instituition) != "*")
       query = query + " AND InstitutionName: " + this.checkEmpty(instituition);
       //Pshysician
       var physician = document.getElementById("physician").value;
       if(this.checkEmpty(physician) != "*")
       query = query + " AND PerformingPhysicianName: " + this.checkEmpty(physician);
       //OperatorName
       var OperatorName = document.getElementById("OperatorName").value;
       if(this.checkEmpty(OperatorName) != "*")
       query = query + " AND OperatorName: " + this.checkEmpty(OperatorName);
       ///////

       var mods = document.querySelector(".modalities");
       var mods_checked = Array.prototype.slice.call(mods.querySelectorAll('input'));

       var modalities = "";
       mods_checked.forEach(function(element){
         //console.log(element.checked);
         if(element.checked)
         {
           modalities = modalities + " " + element.name;
         }
       })

       if(modalities != "")
       {
         modalities = " AND Modality: ("+modalities+")";
         query = query + modalities;
       }

       //DATE
       if(document.getElementById("datepicker").value != "")
       {
         var day
         var date = $('#datepicker').datepicker('getDate').getFullYear() + this.fix2($('#datepicker').datepicker('getDate').getMonth()) + this.fix2($('#datepicker').datepicker('getDate').getDate());

         query = query + " AND StudyDate:["+date +" TO "+date+"]";
       }

       var providerEl = document.getElementById("providersList");
       var selectedId= providerEl.selectedIndex;
       var provider = "";
       if(selectedId == 0){
         provider = "all"
       }
       else {
          provider = providerEl.options[selectedId].text;
       }

       ///////
       var params = {text: query, keyword: true, other:true, provider:provider};

       React.render(<ResultSearch items={params}/>, document.getElementById("container"));
  },
   checkEmpty: function(text){
     if(text.length ==0 )
      return "*";
     else
      return text;
   },
   fix2:function(n) {
        return (n < 10) ? '0' + n : n;
    }
});

export {AdvancedSearch};

window.action = ActionCreators;
