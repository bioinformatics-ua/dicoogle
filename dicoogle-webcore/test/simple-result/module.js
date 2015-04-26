(function(root, factory) {
    if (typeof exports === "object") {
        module.exports = factory(require, exports, module);
    } else if (typeof define === "function" && define.amd) {
        define("simple-result", [ "require", "exports", "module", "dicoogle-webcore", "react", "reactable" ], factory);
    } else {
        var req = function(id) {
            return root[id];
        }, exp = root, mod = {
            exports: exp
        };
        factory(req, exp, mod);
    }
})(this, function(require, exports, module) {
    // simple-result.jsx - Simple Result Module file (React JSX)
    // JSHint directives
    /* global require: false */
    /* global document: false */
    /* global console: false */
    /* global module: false */
    // external dependencies
    var React = require("react");
    var DicoogleWeb = require("dicoogle-webcore");
    // bundled dependencies
    var Reactable = require("reactable");
    var ResultTable = React.createClass({
        displayName: "ResultTable",
        getInitialState: function() {
            return {
                results: [],
                resultNum: null,
                requestTime: 0
            };
        },
        onChange: function(e) {},
        shouldComponentUpdate: function(nextProps, nextState) {
            return nextProps.id !== this.props.id || nextState.requestTime - this.state.requestTime > 0;
        },
        componentWillUpdate: function(nextProps, nextState) {},
        render: function() {
            var Table = Reactable.Table;
            return React.createElement("div", null, React.createElement(Table, {
                className: "table",
                data: this.state.results,
                itemsPerPage: 20
            }));
        }
    });
    module.exports = function() {
        var resultTable;
        var handler;
        this.render = function(parent) {
            resultTable = React.createElement(ResultTable, null);
            handler = React.render(resultTable, parent);
        };
        this.onResult = function(data, requestTime, options) {
            if (!handler) {
                console.error("onResult was invoked before the result plugin was rendered, ignoring");
                return;
            }
            console.log("[onResult] Got ", data.numResults, " entries.");
            if (data.numResults === 0) {
                handler.setState({
                    results: {},
                    n: 0,
                    elapsedTime: data.elapsedTime,
                    requestTime: requestTime
                });
                return;
            }
            for (var i = 0; i < data.results.length; i++) {
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
                requestTime: requestTime
            });
        };
    };
    return module.exports;
});