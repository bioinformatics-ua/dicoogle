/*jshint esnext: true*/

var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

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
                                <input type="text" className="form-control" placeholder="(All Patients)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Patient ID</div>
                                <input type="text" className="form-control" placeholder="(All IDS)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Patient Gender</div>
                                <div className="inline_block">
                                    All
                                    <input type="checkbox" aria-label="..." checked></input>Male
                                    <input type="checkbox" aria-label="..."></input>Female
                                    <input type="checkbox" aria-label="..."></input>

                                </div>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Instituition Name</div>
                                <input type="text" className="form-control" placeholder="(All Instituitions)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Physician</div>
                                <input type="text" className="form-control" placeholder="(All Physicians)"></input>
                            </div>
                            <div className="globalmargin">
                                <div className="subject_text">Operator Name</div>
                                <input type="text" className="form-control" placeholder="(All Operators)"></input>
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
                    <button type="button" className="btn btn_dicoogle centerDivH" onclick="search()">Search</button>
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
    }
});

export {AdvancedSearch};

window.action = ActionCreators;
