var React = require('react');

import {TransferenceStore} from '../../stores/transferenceStore';
import {TransferenceActions} from '../../actions/transferenceActions';



var TransferenceOptionsView = React.createClass({

      getInitialState: function() {
        return {data: [],
        status: "loading",
        selectedIndex: 0};
      },
      componentDidMount: function(){
        console.log("componentdidmount: get");

        TransferenceActions.get();
       },
      componentWillMount: function() {
       	// Subscribe to the store.
         console.log("subscribe listener");
         TransferenceStore.listen(this._onChange);
     	},
      _onChange: function(data){
        if (this.isMounted()){
          console.log(data);
          this.setState({data:data,status: "done"});
        }
      },
      render: function() {
        var self =this;

        if(this.state.status == "loading")
        {
          return (<div>loading</div>);
        }

        var array = self.state.data;
        console.log("array",array);
        var options = (
            array.data[this.state.selectedIndex].options.map(function(item, index){
                return(
                  (<div className="data-table-row">
                      <label className="checkbox" title="1.2.840.10008.1.2.1.99">
                          <input type="checkbox" id={item.name} name="GlobalTransferStorageTransferStorage0" checked={item.value}
                            onChange={self.handleChange.bind(this, item.name, index)}/>{item.name}</label>
                  </div>
                  )
                );
            })
          );

        var sopclasses = (
          array.data.map(function(item){
            return (<option> <b>{item.sop_name}</b>  --  {item.uid}</option>);
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

                                     <select id="sop_select"className="form-control" onChange={self.onSopSelected}>
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

      handleChange:function(id, index){
        console.log("Index ", index);
        TransferenceActions.set(this.state.selectedIndex, index,document.getElementById(id).checked);
        this.request(id,document.getElementById(id).checked);

      },

      /*
      NOT USED
      */
      selectAll:function(){
        var self = this;
        this.state.data.data.options.map(function(item){
          document.getElementById(item.name).checked = true;
          self.request(item.name, true)
        });
      }
      ,
      selectNone:function(){
        var self = this;
        this.state.data.data.options.map(function(item){
          document.getElementById(item.name).checked = false;
          self.request(item.name, false);
        });
      },

      onSopSelected: function(){

        var selectedId=document.getElementById("sop_select").selectedIndex;
        console.log("selected", selectedId );
        this.setState({selectedIndex: selectedId});

      },

      request: function(id, value){
        var uid = this.state.data.data[document.getElementById("sop_select").selectedIndex].uid;
        console.log("Selected uid:",uid);
        $.post("http://localhost:8080/management/settings/transfer",
        {
          uid: uid,
          option: id,
          value: value

        },
          function(data, status){
            //Response
            console.log("Data: " + data + "\nStatus: " + status);
          });
      }
      });

export {
  TransferenceOptionsView
}
