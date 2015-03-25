(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.simpleResult = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/* module.jsx - Simple Result Module file (React JSX)
 */

// attempt to get React
// var React = require('react');
if (typeof React !== 'object') {
  if (typeof require !== 'function') {
    console.error("React is not supported in this page!");
  } else {
    React = require('react');
  }
}

var ResultTable = React.createClass({displayName: "ResultTable",
  
  getInitialState: function() {
    return {
      results : [
        { SOPInstanceUID: 'xxxxxxx', StudyID: 'assdasdsdsadasd' },
        { SOPInstanceUID: 'yyyyyyy', StudyID: 'assdasdsdsadasd' },
        { SOPInstanceUID: 'zzzzzzz', StudyID: 'assdasdsdsadasd' }
      ],
      requestTime: 0
    };
  },
  onChange: function(e) {
    //this.setState({text: e.target.value});
  },
  shouldComponentUpdate: function(nextProps, nextState) {
    return nextProps.id !== this.props.id
      || (nextState.requestTime - this.state.requestTime) > 0;
  },
  componentWillUpdate: function(nextProps, nextState) {
  },
  render: function() {
    var Table = Reactable.Table;
    return (
      React.createElement("div", null, 
        React.createElement(Table, {className: "table", data: this.state.results, itemsPerPage: 20})
      )
    );
  }
});

module.exports = function() {
  var handler;
  this.render = function() {
    var e = document.createElement('div');
    handler = React.render(React.createElement(ResultTable, null), e);
    return e;
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
      data.results[i].fields = undefined;
      for (var fname in fields) {
        data.results[i][fname] = fields[fname];
      }
    }
    handler.setState({
      results: data.results,
      n: data.numResults,
      elapsedTime: data.elapsedTime,
      requestTime: requestTime,
      });
  };
}


},{"react":undefined}]},{},[1])(1)
});