/*jshint esnext: true*/

var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

import {AdvancedSearch} from '../search/advancedSearch';
import {ResultSearch} from '../search/searchResultView';

import {DimFields} from '../../constants/dimFields';

var Router = require('react-router');
var Route = Router.Route;
var Link = Router.Link;
var RouteHandler = Router.RouteHandler

import {UserMixin} from '../mixins/userMixin';

var Search = React.createClass({
    mixins : [UserMixin],
    getInitialState: function (){

        return { label:'login', searchState: "simple" };
    },
    componentDidMount: function(){
      this.enableAutocomplete();
      this.enableEnterKey();
    },
    componentDidUpdate: function(){
      this.enableAutocomplete();
      this.enableEnterKey();
    },
    componentWillMount: function(){

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

       if(this.state.searchState === "simple"){
            return (<div> {selectionButtons} {simpleSearchInstance} </div>);
       }
       else if(this.state.searchState === "advanced")
       {
            return (<div> {selectionButtons} <AdvancedSearch/> </div>);
       }
    },
    _onChange : function(data){
        console.log("onChange");
        if (this.isMounted())
        this.setState({label:data});
    },
    renderFilter : function(){
      //React.render(<AdvancedSearch/>, this.getDOMNode());
      var switchState;
      if(this.state.searchState === "simple"){
        switchState = "advanced";
    }
      else{
        switchState = "simple";
      }
    this.setState({searchState: switchState})

    },

    onSearchClicked : function(){
        // console.log(React.getInitialState(<ResultSearch/>) );
        var text = document.getElementById("free_text").value;

        var params = {text: text, keyword: this.isKeyword(text), other:true};

        React.render(<ResultSearch items={params}/>, document.getElementById("container"));
        //console.log("asadfgh");
    },
    isKeyword: function(freetext){
    for(var i=0; i<DimFields.length;i++)
      {
        if((freetext.indexOf(DimFields[i])) != -1)
        {
          return true;
        }
      }

    },
  isAutocompletOpened:function(){
    if($('.ui-autocomplete').css('display')==='none'){return false;}
    return true;
},

    enableAutocomplete : function(){
      var self =this;
      function split( val ) {
      return val.split( /\sAND\s/ );
    }
    function extractLast( term ) {
      return split( term ).pop();
    }

    $( "#free_text" )
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
      /*  if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).autocomplete( "instance" ).menu.active ) {
          event.preventDefault();
        }*/
        if(event.keyCode==13){
          //event.preventDefault();
          event.stopPropagation();
        }
      })
      .autocomplete({
        minLength: 0,
        source: function( request, response ) {
          // delegate back to autocomplete, but extract the last term
          response( $.ui.autocomplete.filter(
            DimFields, extractLast( request.term ) ) );

        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          console.log(terms);
          // remove the current input
          terms.pop();
          //if(terms.lenght >1)
          //terms.join(" AND ");
          // add the selected item
          console.log(terms.length);
          var termtrick =  ((terms.length >= 1) ? " AND " : "")+ui.item.value;
          terms.push(termtrick);
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = (terms.join( "" ) + ": ");

          return false;
        }
      });
    },

    enableEnterKey:function(){
      var self = this;
      /*$('#free_text').keyup(function(e){
        if(e.keyCode == 13)
          {
            //self.onSearchClicked();
            console.log("OPENED: ",self.isAutocompletOpened());
          }
        });
        */
        //
        //Trick to not search when press enter on autocomplete
        //
        var count = 0;
        jQuery("#free_text").keypress(function(e) {
        if (e.keyCode == 13) {
            if (++count >= 1) {
                self.onSearchClicked();
                count = 0;
            }
        }
    });
    }
});

export {Search};

window.action = ActionCreators;
