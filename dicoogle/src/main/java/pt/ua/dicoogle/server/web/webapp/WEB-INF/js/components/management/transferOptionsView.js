import React from 'react';

import {TransferStore} from '../../stores/transferStore';
import {TransferActions} from '../../actions/transferActions';
import {Endpoints} from '../../constants/endpoints';
import $ from 'jquery';

const TransferOptionsView = React.createClass({

      getInitialState () {
        return {
          data: [],
          status: "loading",
          selectedIndex: 0
        };
      },
      componentDidMount() {
        console.log("componentdidmount: get");

        TransferActions.get();
      },
      componentWillMount() {
         // Subscribe to the store.
         console.log("subscribe listener");
         TransferStore.listen(this._onChange);
      },
      _onChange (data){
        if (this.isMounted()){
          console.log(data);
          this.setState({data: data, status: "done"});
        }
      },
      render () {
        if(this.state.status === "loading")
        {
          return (<div className="loader-inner ball-pulse">
            <div/><div/><div/>
           </div>);
        }

        var array = this.state.data;
        console.log("array", array);
        var options = (
            array.data[this.state.selectedIndex].options.map((item, index) => {
                return(
                  (<div key={index} className="data-table-row">
                      <label className="checkbox" title="1.2.840.10008.1.2.1.99">
                          <input type="checkbox" id={item.name} name="GlobalTransferStorageTransferStorage0" checked={item.value}
                            onChange={this.handleChange.bind(this, item.name, index)}/>{item.name}</label>
                  </div>
                  )
                );
            })
          );

        var sopclasses = (
          array.data.map((item, index) => {
            return (<option key={index}> <b>{item.sop_name}</b>  -- {item.uid}</option>);
          })
        );

        return (
          <div>
          <div className="tab-pane" id="transfer">
                <div className="panel panel-primary topMargin">
                    <div className="panel-heading">
                        <h3 className="panel-title">SOP Class Global Transfer Storage Options</h3>
                    </div>
                    <div className="panel-body">
                        <ul className="list-group">

                            <select id="sop_select"className="form-control" onChange={this.onSopSelected}>
                              {sopclasses}
                            </select>
                            <li className="list-group-item list-group-item-management">
                                <div className="row">
                                    <div className="col-xs-6 col-sm-4">
                                        Global Transfer Storage
                                    </div>
                                    <div className="col-xs-6 col-sm-8">
                                        <div id="GlobalTransferStorage" className="data-table">
                                          {options}
                                        </div>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
          </div>
        );

      },

      handleChange(id, index) {
        console.log("Index ", index);
        TransferActions.set(this.state.selectedIndex, index, document.getElementById(id).checked);
        this.request(id, document.getElementById(id).checked);
      },

      onSopSelected() {
        var selectedId = document.getElementById("sop_select").selectedIndex;
        console.log("selected", selectedId );
        this.setState({selectedIndex: selectedId});
      },

      request(id, value) {
        var uid = this.state.data.data[document.getElementById("sop_select").selectedIndex].uid;
        console.log("Selected uid:", uid);
        $.post(Endpoints.base + "/management/settings/transfer", {
          uid: uid,
          option: id,
          value: value
        }, (data, status) => {
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
        });
      }
});

export { TransferOptionsView };
