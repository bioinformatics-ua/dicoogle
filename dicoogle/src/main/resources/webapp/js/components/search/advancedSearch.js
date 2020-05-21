import React from "react";
import ReactDOM from "react-dom";
import { SearchStore } from "../../stores/searchStore";
import { ActionCreators } from "../../actions/searchActions";
import { SearchResult } from "./searchResult";

// TODO this component needs to be refactored:
// - it should not perform React renders
// - it should delegate the actual search to the parent component

const AdvancedSearch = React.createClass({
  getInitialState: function() {
    return { label: "login" };
  },

  render: function() {
    var managementInstance = (
      <div>
        <div id="filter-group">
          <div className="row space_up">
            <div className="row col-sm-6">
              <div className="globalmargin">
                <div className="subject_text">Patient Name</div>
                <input
                  id="patient_name"
                  type="text"
                  className="form-control"
                  placeholder="(All Patients)"
                />
              </div>
              <div className="globalmargin">
                <div className="subject_text">Patient ID</div>
                <input
                  id="patient_id"
                  type="text"
                  className="form-control"
                  placeholder="(All Patient IDs)"
                />
              </div>
              <div className="globalmargin">
                <div className="subject_text">Patient Gender</div>
                <div>
                  <div className="col-md-2 row">
                    All{" "}
                    <input
                      className="space_left"
                      id="gender_all"
                      type="radio"
                      name="genderRadio"
                    />
                  </div>
                  <div className="col-md-2 row">
                    Male{" "}
                    <input
                      className="space_left"
                      id="gender_male"
                      type="radio"
                      name="genderRadio"
                    />
                  </div>
                  <div className="col-md-2 row">
                    Female{" "}
                    <input
                      className="space_left"
                      id="gender_female"
                      type="radio"
                      name="genderRadio"
                    />
                  </div>
                </div>
              </div>
              <div className="globalmargin">
                <div className="subject_text">Instituition Name</div>
                <input
                  id="instituition"
                  type="text"
                  className="form-control"
                  placeholder="(All Institutions)"
                />
              </div>
              <div className="globalmargin">
                <div className="subject_text">Physician</div>
                <input
                  id="physician"
                  type="text"
                  className="form-control"
                  placeholder="(All Physicians)"
                />
              </div>
              <div className="globalmargin">
                <div className="subject_text">Operator Name</div>
                <input
                  id="OperatorName"
                  type="text"
                  className="form-control"
                  placeholder="(All Operators)"
                />
              </div>
            </div>
            <div className="row col-sm-6">
              <div className="globalmargin row">
                <div className="subject_text space_up">Modality</div>
                <div className="row">
                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modCR">CR</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modCR" type="checkbox" name="CR" />
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modMG">MG</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modMG" type="checkbox" name="MG" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modPT">PT</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modPT" type="checkbox" name="PT" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modXA">XA</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modXA" type="checkbox" name="XA" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modES">ES</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modES" type="checkbox" name="ES" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modCT">CT</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modCT" type="checkbox" name="CT" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modMR">MR</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modMR" type="checkbox" name="MR" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modRF">RF</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modRF" type="checkbox" name="RF" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modUS">US</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modUS" type="checkbox" name="US" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modDX">DX</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modDX" type="checkbox" name="DX" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modNM">NM</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modNM" type="checkbox" name="NM" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modSC">SC</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modSC" type="checkbox" name="SC" />
                    </div>
                  </div>

                  <div className="col-md-4">
                    <div className="row col-xs-8">
                      <label htmlFor="modOT">OT</label>
                    </div>
                    <div className="row col-xs-4">
                      <input id="modOT" type="checkbox" name="OT" />
                    </div>
                  </div>
                </div>
              </div>
              <div className="globalmargin row">
                <div className="subject_text space_up">Date</div>
                <input
                  type="text"
                  id="datepicker"
                  className="form-control form-control-date"
                  placeholder={"YYYYMMDD"}
                />
              </div>
            </div>
          </div>
          <button
            type="button"
            className="btn btn_dicoogle"
            onClick={this.onSearchClicked}
          >
            Search
          </button>
        </div>
      </div>
    );
    return managementInstance;
  },
  componentWillMount: function() {
    // Subscribe to the store.
    this.unsubscribe = SearchStore.listen(this._onChange);
  },
  componentWillUnmount() {
    this.unsubscribe();
  },

  _onChange: function(data) {
    console.log(data);
  },
  onSearchClicked: function() {
    console.log("SEARCH CLICKED");
    //NAME
    var patientName = document.getElementById("patient_name").value;
    var query = "PatientName: " + this.checkEmpty(patientName); //TODO FINISH

    //GENDER
    var gender;
    if (document.getElementById("gender_male").checked) {
      gender = " AND PatientSex:M";
    } else if (document.getElementById("gender_female").checked) {
      gender = " AND PatientSex:F";
    } else {
      gender = "";
    }
    query = query + gender;
    //ID
    var patientId = document.getElementById("patient_id").value;
    if (this.checkEmpty(patientId) !== "*")
      query = query + " AND PatientID: " + this.checkEmpty(patientId);
    //Instituition
    var instituition = document.getElementById("instituition").value;
    if (this.checkEmpty(instituition) !== "*")
      query = query + " AND InstitutionName: " + this.checkEmpty(instituition);
    //Pshysician
    var physician = document.getElementById("physician").value;
    if (this.checkEmpty(physician) !== "*")
      query =
        query + " AND PerformingPhysicianName: " + this.checkEmpty(physician);
    //OperatorName
    var OperatorName = document.getElementById("OperatorName").value;
    if (this.checkEmpty(OperatorName) !== "*")
      query = query + " AND OperatorName: " + this.checkEmpty(OperatorName);
    ///////

    var mods = document.querySelector(".modalities");
    var mods_checked = Array.prototype.slice.call(
      mods.querySelectorAll("input")
    );

    var modalities = "";
    mods_checked.forEach(function(element) {
      //console.log(element.checked);
      if (element.checked) {
        modalities = modalities + " " + element.name;
      }
    });

    if (modalities !== "") {
      modalities = " AND Modality: (" + modalities + ")";
      query = query + modalities;
    }

    //DATE
    let date = document.getElementById("datepicker").value; // format: YYYYMMDD
    if (date !== "") {
      query = query + " AND StudyDate:[" + date + " TO " + date + "]";
    }

    var providerEl = document.getElementById("providersList");
    var selectedId = providerEl.selectedIndex;
    var provider = "";
    if (selectedId === 0) {
      provider = "all";
    } else {
      provider = providerEl.options[selectedId].text;
    }

    ///////
    var params = { text: query, keyword: true, other: true, provider };

    ReactDOM.render(
      <SearchResult items={params} />,
      document.getElementById("container")
    );
  },
  checkEmpty: function(text) {
    if (text.length === 0) return "*";
    else return text;
  },
  fix2: function(n) {
    return n < 10 ? "0" + n : n;
  }
});

export { AdvancedSearch };

window.action = ActionCreators;
