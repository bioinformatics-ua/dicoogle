var React = require('react');

import {IndexStatusActions} from "../../actions/indexStatusAction";
import {IndexStatusStore} from "../../stores/indexStatusStore";
var IndexStatusView = React.createClass({
  getInitialState: function() {
    return {data: {},
    status: "loading"};
  },
  componentDidMount: function(){

    IndexStatusActions.get();
    //$("#consolediv").scrollTop($("#consolediv")[0].scrollHeight);
   },
   componentDidUpdate:function(){
     console.log("logger update");
   },
  componentWillMount: function() {
     // Subscribe to the store.
     console.log("subscribe listener");
     IndexStatusStore.listen(this._onChange);
   },
  _onChange: function(data){
    if (this.isMounted()){

      this.setState({data:data.data,status: "done"});
    }
  },
      render: function() {
        if(this.state.status == "loading"){
          return (<div>loading...</div>);
        }
        return (
          <div className="panel panel-primary topMargin">
                            <div className="panel-heading">
                                <h3 className="panel-title">Indexing Status</h3>
                            </div>
                            <div className="panel-body">
                                <div className="progress">
                                    <div className="indexprogress progress-bar progress-bar-success progress-bar-striped" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100">
                                        <span className="sr-only">40% Complete (success)</span>
                                    </div>
                                </div>

                            </div>
        </div>
        );
        }
      });

export {
  IndexStatusView
}
