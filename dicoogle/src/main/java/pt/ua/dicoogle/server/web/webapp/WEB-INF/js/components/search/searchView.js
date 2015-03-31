/*jshint esnext: true*/

var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

import {AdvancedSearch} from '../search/advancedSearch';
import {ResultSearch} from '../search/searchResultView';

var Router = require('react-router');
var Route = Router.Route;
var Link = Router.Link;
var RouteHandler = Router.RouteHandler

var Search = React.createClass({

    getInitialState: function (){

        return { label:'login', searchState: "simple" };
    },
    render: function() {
        var selectionButtons = (
            <div>
            <button type="button" className="btn btn_dicoogle" onClick={this.renderFilter} data-trigger="advance-search" id="btn-advance">Advanced</button>
                <div className="btn-group">
                    <button type="button" className="btn btn_dicoogle dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                        Select Providers <span className="caret"></span>
                    </button>
                    <ul className="dropdown-menu" role="menu">
                        <li><a href="#">Lucene</a>
                        </li>
                        <li><a href="#">All</a></li>
                        <li> { this.state.data   }</li>

                    </ul>
                </div>
                </div>
            );

        var simpleSearchInstance = (
            <div className="row space_up" id="main-search">
                    <div className="col-xs-8 col-sm-10">
                        <input id="free_text" type="text" className="form-control" placeholder="Free text or advanced query"></input>
                    </div>
                    <div className="col-xs-4 col-sm-2">
                        <button type="button" className="btn btn_dicoogle" id="search-btn" onClick={this.onSearchClicked}>Search</button>
                    </div>
                    <RouteHandler/>
                </div>
            );

       if(this.state.searchState == "simple"){
            return (<div> {selectionButtons} {simpleSearchInstance} </div>);
       }
       else if(this.state.searchState == "advanced")
       {
            return (<div> {selectionButtons} <AdvancedSearch/> </div>);
       }
    },
    componentWillMount: function() {
    // Subscribe to the store.
        //SearchStore.listen(this._onChange);

    },
    muu : function(btn){
        console.log("dados", ActionCreators);
        //ActionCreators.search("dados");

        var view = this;

        ActionCreators.triggerPromise('http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene').then(function(body) {
            // Render the response body
            console.log(body);

            view.setState({"data" : body});

          }).catch(function(err) {
              // Handle the API error object
              console.log(err);
          });
    },
    _onChange : function(data){
        console.log("onChange");
        if (this.isMounted())
        this.setState({label:data});
    },
    renderFilter : function(){
      console.log("beidjinhos", React);

      //React.render(<AdvancedSearch/>, this.getDOMNode());
      var switchState;
      if(this.state.searchState =="simple"){
        switchState = "advanced";
    }
      else{
        switchState = "simple";
      }
    this.setState({searchState: switchState})

    },

    onSearchClicked : function(){
        // console.log(React.getInitialState(<ResultSearch/>) );
        var params = {text: document.getElementById("free_text").value, other:true};

        React.render(<ResultSearch items={params}/>, document.getElementById("container"));
        //console.log("asadfgh");
    }
});

export {Search};

window.action = ActionCreators;
