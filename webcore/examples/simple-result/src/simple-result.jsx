// simple-result.jsx - Simple Result Module file (React JSX)

// JSHint directives
/* global require: false */
/* global document: false */
/* global console: false */
/* global module: false */

// external dependencies
var React = require('react');

// bundled dependencies
var Reactable = require('reactable');

class ResultTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      results : [],
      resultNum: null,
      requestTime: 0
    };
  }
  onChange(e) {
    //this.setState({text: e.target.value});
  }
  shouldComponentUpdate(nextProps, nextState) {
    return nextProps.id !== this.props.id ||
      (nextState.requestTime - this.state.requestTime) > 0;
  }
  componentWillUpdate(nextProps, nextState) {
  }
  render() {
    var Table = Reactable.Table;
    return (
        <div>
        <Table className="table" data={this.state.results} itemsPerPage={10} />
      </div>
    );
  }
}

module.exports = function() {
    var resultTable;
    var handler;
    this.render = function(parent) {
      resultTable = <ResultTable />;
      handler = React.render(resultTable, parent);
    };
    this.onResult = function(data, requestTime, options) {
      if (!handler) {
        console.error("onResult was invoked before the result plugin was rendered, ignoring");
        return;
      }
      console.log('[onResult] Got ', data.numResults, ' entries.');
      if (data.numResults === 0) {
        handler.setState({
          results: {},
          n: 0,
          elapsedTime: data.elapsedTime,
          requestTime: requestTime,
          });
          return;
      }
      for (var i = 0 ; i < data.results.length ; i++) {
        var fields = data.results[i].fields;
        if (fields) {
          delete data.results[i].fields;
          for (var fname in fields) {
            data.results[i][fname] = fields[fname];
          }
        }
      }
      handler.setState({
        results: data.results,
        n: data.numResults,
        elapsedTime: data.elapsedTime,
        requestTime: requestTime,
      });
    };
};
