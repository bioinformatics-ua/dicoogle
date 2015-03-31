
var React = require('react');
var ReactBootstrap = require('react-bootstrap');

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';


var ResultSearch = React.createClass({

  getInitialState: function() {
    return {data: [],
    status: "loading"};
  },
  componentDidMount: function() {
  	this.initSearch();
  },
  componentWillMount: function() {
    // Subscribe to the store.
    SearchStore.listen(this._onChange);
  },

	initSearch: function(){
    console.log("PARAM: ", this.props.items);
    ActionCreators.search(this.props.items);
	},

  render: function() {
    var self = this;

		if (this.state.status === "loading"){
		  //loading animation
      return (<div className="loader-inner ball-pulse">
      <div></div>
      <div></div>
      <div></div>
      </div>);
		}

    //Check if search fails
    if(this.state.success === false)
    {
      return (<div> Search error</div>);
    }
    //Check if search return no results
    if(this.state.data.numResults === 0)
    {
      return (<div> No Results</div>);
    }

    var arraylist = this.state.data.results;

    var resultNodes = (
      arraylist.map(function(item){
		      return (
				          <li className="list_item"> {item.uri}</li>
			           );
       })
	    );


		return (
      <div>
			   <ul className="result_list">
                { resultNodes }
        </ul>

      </div>
    );
	},

  _onChange : function(data){
    console.log("onchange");
    console.log(data.success);
    console.log(data.status);
    if (this.isMounted())
    {
      this.setState({data:data.data,
      status:"stopped",
      success: data.success});
    }

  }
});

export {ResultSearch};
