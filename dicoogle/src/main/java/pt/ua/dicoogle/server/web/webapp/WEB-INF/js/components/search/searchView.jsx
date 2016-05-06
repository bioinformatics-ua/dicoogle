
import React, {PropTypes} from 'react';
import $ from 'jquery';
import {ActionCreators} from '../../actions/searchActions';
import {ProvidersStore} from '../../stores/providersStore';
import {ProvidersActions} from '../../actions/providersActions';
import {AdvancedSearch} from '../search/advancedSearch';
import {SearchResult} from '../search/searchResult';
import {DimFields} from '../../constants/dimFields';
import {getUrlVars} from '../../utils/url';
import {SearchStore} from '../../stores/searchStore';

var Search = React.createClass({
    propTypes: {
      params: PropTypes.object.isRequired,
      location: PropTypes.object.isRequired
    },

    getInitialState: function (){
        this.keyHash = getUrlVars()['_k'];
        return {
          label: 'login',
          searchState: "simple",
          providers: ["All providers"],
          queryText: '',
          requestedQuery: null,
          error: null,
          data: null
        };
    },

    componentWillMount: function(){
      ProvidersStore.listen(this._onProvidersChange);
      SearchStore.listen(this._onSearchResult);
    },

    componentDidMount: function(){

      this.enableAutocomplete();
      this.enableEnterKey();

      if(getUrlVars()['query']) {
        this.onSearchByUrl();
      }

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

    onReturn(error) {
      this.setState({
        requestedQuery: null,
        error
      });
    },

    getSearchOutcome() {
      const {data, error} = this.state;
      return data ? { data, error } : null;
    },

    render: function() {

      let providersList = this.state.providers.map(
          (item, index) => (<option key={index}> {item} </option>));

      let selectionButtons = (
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

      let simpleSearchInstance = (
            <div className="row space_up" id="main-search">
                    <div className="col-xs-8 col-sm-10">
                        <input id="free_text" type="text" className="form-control" placeholder="Free text or advanced query"
                               onChange={this.handleQueryTextChanged} value={this.state.queryText} />
                    </div>
                    <div className="col-xs-4 col-sm-2">
                        <button type="button" className="btn btn_dicoogle" id="search-btn"
                                onClick={this.onSearchClicked}>Search</button>
                    </div>
                </div>
            );

       if (this.state.requestedQuery !== null && !this.state.error) {
         return <SearchResult requestedQuery={this.state.requestedQuery}
                              searchOutcome={this.getSearchOutcome()}
                              onReturn={this.onReturn} />;
       } else if(this.state.searchState === "simple") {
            return (<div>
              {selectionButtons}
              {simpleSearchInstance}
              <div>{this.state.error}</div>
            </div>);
       } else if(this.state.searchState === "advanced") {
            return (<div>
              {selectionButtons}
              <AdvancedSearch/>
              <div>{this.state.error}</div>
            </div>);
       }
    },
    _onProvidersChange: function(data) {
        if (this.isMounted())
        this.setState({providers: data.data});
    },
    _onSearchResult: function(outcome) {
      if (this.isMounted())
      {
        console.log('outcome:', outcome);

        let error = null;
        if (!outcome.success) {
          error = "An error occurred. Please contact your system administrator.";
        } else if (outcome.data.numResults === 0) {
          error = "No studies were found for that search.";
        }
        this.setState({
          data: outcome.data,
          status: "stopped",
          success: outcome.success,
          requestedQuery: error ? null : this.state.requestedQuery,
          error
        });
      }
    },
    renderFilter: function(){
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
    handleQueryTextChanged(e) {
      this.setState({queryText: e.target.value});
    },
    onSearchClicked: function() {
        let text = this.state.queryText;

        // TODO support multi-select ; use state instead of fetching element from DOM
        let providerEl = document.getElementById("providersList");
        let selectedId = providerEl.selectedIndex;
        let provider = "";
        if(selectedId === 0){
          provider = "all"
        } else {
          provider = providerEl.options[selectedId].text;
        }

        let params = {text, keyword: this.isKeyword(text), other: true, provider};
        this.triggerSearch(params);
    },
    triggerSearch: function(params){
        this.setState({
          requestedQuery: params,
          data: null,
          error: null
        });
      ActionCreators.search(params);
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
