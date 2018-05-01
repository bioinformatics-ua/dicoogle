import React from 'react';
import {DumpStore} from '../../stores/dumpStore';
import {DumpActions} from '../../actions/dumpActions';
import {getUrlVars} from '../../utils/url';
import $ from 'jquery';

const DirectDumpView = React.createClass({
  propTypes: {
    params: React.PropTypes.object.isRequired
  },
  getInitialState: function() {
    return {data: [],
    status: "loading",
    current: 0};
  },
  componentDidMount: function() {
    var uid = this.props.params.uid || getUrlVars()['SOPInstanceUID'];
    DumpActions.get(uid);
  },
  componentWillMount: function() {
    // Subscribe to the store.
    this.unsubscribe = DumpStore.listen(this._onChange);
  },
  componentWillUnmount() {
    this.unsubscribe();
  },
  componentDidUpdate: function(){
    $('#dumptable').dataTable({paging: false, searching: false, info: false, responsive: false});
  },

  _onChange(data) {
    this.setState({
      data,
      status: "stopped"
    });
  },

	render() {
		if(this.state.status === "loading") {
      return (<div className="loader-inner ball-pulse"/>);
    }
    var obj = this.state.data.data.results.fields;
    var rows = [];

    var fields = [];
    Object.keys(obj).forEach(function(key, i) {
          rows.push(<p key={i}><b>{key}:</b> {obj[key]}</p>);
          fields.push({att: key, field: obj[key]});
        });

    var fieldstable = fields.map((item) => {
      return (
        <tr>
          <td> <p>{item.att}</p></td>
          <td> <p>{item.field}</p></td>
          </tr>
      );
    });

		return (
      <table id="dumptable" className="table-test table table-striped table-bordered responsive" cellSpacing="0" width="100%">
        <thead>
          <tr>
            <th>Attribute</th>
            <th>Field</th>
          </tr>
        </thead>
        <tbody>
          {fieldstable}
        </tbody>
      </table>);
  }
});

export {DirectDumpView};
