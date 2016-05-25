/*jshint esnext: true*/

import React from 'react';
import $ from 'jquery';
import {ActionCreators} from '../../actions/searchActions';
import {ProvidersStore} from '../../stores/providersStore';
import {ProvidersActions} from '../../actions/providersActions';
import {AdvancedSearch} from '../search/advancedSearch';
import {ResultSearch} from '../search/searchResultView';
import {DimFields} from '../../constants/dimFields';
import {getUrlVars} from '../../utils/url';

const Search = React.createClass({
    getInitialState: function (){
        this.keyHash = getUrlVars()['_k'];
        return {
          label: 'login',
          searchState: "simple",
          providers: ["All providers"],
          requestedQuery: null
        };
    },
    componentDidMount: function(){

      this.enableAutocomplete();
      this.enableEnterKey();

      if(getUrlVars()['query']) {
        this.onSearchByUrl();
      }

      //document.getElementById('container').style.display = 'block';

      ProvidersActions.get();
    },
    componentWillUpdate: function() {

        if (getUrlVars()['_k'] !== this.keyHash)
        {
            this.keyHash = getUrlVars()['_k'];
            this.setState({
                requestedQuery: null
            });
        }
        this.keyHash = getUrlVars()['_k'];


    },
    componentDidUpdate: function(){
      this.enableAutocomplete();
      this.enableEnterKey();
    },
    componentWillMount: function(){
      ProvidersStore.listen(this._onChange);
    },
    render: function() {

      var self = this;
      var providersList = (
        self.state.providers.map(function(item, index){
          return (<option key={index}> {item} </option>);
        })
      );

      var selectionButtons = (
          <div>
          <button type="button" className="btn btn_dicoogle" onClick={this.renderFilter} data-trigger="advance-search" id="btn-advance">
            {this.state.searchState === "simple" ? "Advanced" : "Basic"}
          </button>
              <div className="btn-group">
                  <select id="providersList" className="btn btn_dicoogle form-control">
                    {providersList}
                  </select>
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
                </div>
            );

       if (this.state.requestedQuery !== null) {
         return <ResultSearch items={this.state.requestedQuery}/>;
       } else if(this.state.searchState === "simple"){
            return (<div> {selectionButtons} {simpleSearchInstance} </div>);
       }
       else if(this.state.searchState === "advanced")
       {
            return (<div> {selectionButtons} <AdvancedSearch/> </div>);
       }
    },
    _onChange: function(data) {
        if (this.isMounted())
        this.setState({providers: data.data});
    },
    renderFilter: function(){
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
    onSearchByUrl: function(){
      let params = {text: getUrlVars()['query'], keyword: getUrlVars()['keyword'], provider: getUrlVars()['provider']};
      this.setState({
        requestedQuery: params
      });
    },
    onSearchClicked: function(){
        // TODO don't do this, use state instead
        let text = document.getElementById("free_text").value;

        let providerEl = document.getElementById("providersList");
        let selectedId = providerEl.selectedIndex;
        let provider = "";
        if(selectedId === 0){
          provider = "all"
        } else {
          provider = providerEl.options[selectedId].text;
        }

        let params = {text, keyword: this.isKeyword(text), other: true, provider};
        this.setState({
          requestedQuery: params
        })
    },
    isKeyword: function(freetext) {
      return !!freetext.match(/[^\s\\]:\S/);
    },
  isAutocompletOpened: function(){
    if($('.ui-autocomplete').css('display') === 'none'){return false;}
    return true;
  },

    enableAutocomplete: function(){
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
        if(event.keyCode === 13){
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
          var termtrick = ((terms.length >= 1) ? " AND " : "") + ui.item.value;
          terms.push(termtrick);
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = (terms.join( "" ) + ": ");

          return false;
        }
      });
    },

    enableEnterKey: function(){
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
        $("#free_text").keypress((e) => {
        if (e.keyCode === 13) {
            if (++count >= 1) {
                this.onSearchClicked();
                count = 0;
            }
        }
    });
    }
});

export {Search};

window.action = ActionCreators;
