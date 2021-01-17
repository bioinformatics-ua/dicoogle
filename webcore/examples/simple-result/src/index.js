/* global Dicoogle */

import * as React from 'react';

// bundled dependencies
import * as Reactable from 'reactable';

class ResultTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            results: [],
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


export default class MyPlugin {

    constructor() {
        this.resultTable = null;
        this.handler = null;
    }

    /** 
     * @param {DOMElement} parent
     * @param {DOMElement} slot
     */
    render(parent, slot) {
        // mount a new web component here
        this.resultTable = <ResultTable />;
        this.handler = React.render(this.resultTable, parent);
    }

    onResult(data, requestTime, options) {
        if (!this.handler) {
            console.error("onResult was invoked before the result plugin was rendered, ignoring");
            return;
        }
        console.log('[onResult] Got ', data.numResults, ' entries.');
        if (data.numResults === 0) {
            this.handler.setState({
                results: {},
                n: 0,
                elapsedTime: data.elapsedTime,
                requestTime: requestTime,
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
        this.handler.setState({
            results: data.results,
            n: data.numResults,
            elapsedTime: data.elapsedTime,
            requestTime: requestTime,
        });
    }
}
