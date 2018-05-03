import React, {PropTypes} from 'react';

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

import Webcore from 'dicoogle-webcore';
import {DefaultOptions} from '../../constants/defaultOptions';
import {SearchResult} from './searchResult';



/**
 * This class is not used anymore.
 * Deprecated. 
 */
const SearchResultView = React.createClass({

  propTypes: {
    items: PropTypes.shape({
      text: PropTypes.string,
      keyword: PropTypes.bool,
      other: PropTypes.bool,
      provider: PropTypes.string
    }).isRequired // the requested query
  },

  getInitialState: function() {
    return {
      data: {}, // {numResults, results}
      status: "loading",
      showExport: false,
      showDangerousOptions: DefaultOptions.showSearchOptions,
      current: 0,
      success: undefined,
      batchPlugins: [],
      currentPlugin: null
    };
  },

  componentDidMount: function() {
    this.initSearch(this.props.items);
  },

  componentWillMount: function() {
    this.unsubscribe = SearchStore.listen(this._onChange);
    Webcore.fetchPlugins('result-batch', (packages) => {
      Webcore.fetchModules(packages);
      this.setState({batchPlugins: packages.map(pkg => ({
        name: pkg.name,
        caption: pkg.dicoogle.caption || pkg.name
      }))});
    });
  },

  componentWillUnmount() {
    this.unsubscribe();
  },

	initSearch: function(props){
    ActionCreators.search(props);
	},

  render: function() {
    const sOut = {
      data: this.state.data,
      error: !this.state.success && 'An error occurred. Please contact your system administrator.'
    };
    const rq = this.props.items;
    return (<SearchResult requestedQuery={rq} searchOutcome={sOut} />);
	},

  _onChange: function(outcome) {
      console.log('outcome:', outcome);

      this.setState({
        data: outcome.data,
        status: "stopped",
        success: outcome.success
      });
  }
});

export {SearchResultView};
