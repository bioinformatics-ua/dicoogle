import React from "react";

import { TransferStore } from "../../stores/transferStore";
import * as TransferActions from "../../actions/transferActions";

/**
 * @typedef {Object} SOPClassTransferRecord
 * @property {string} uid
 * @property {string} sop_name
 * @property {Array<TransferSyntaxRecord>} options
 */

/**
 * @typedef {Object} TransferSyntaxRecord
 * @property {string} uid
 * @property {string} sop_name
 */

class TransferOptionsView extends React.Component {

  constructor(props) {
    super(props);
    this.selectAllOn = true;
    this.selectSOPOn = true;
    this.state = {
      /** @type {{data: Array<SOPClassTransferRecord>}} */
      data: {},
      status: "loading",
      selectedIndex: 0
    };
    
    this._onChange = this._onChange.bind(this);
    this.handleSelectSop = this.handleSelectSop.bind(this);
    this.handleSelectAll = this.handleSelectAll.bind(this);
    this.onSopSelected = this.onSopSelected.bind(this);
  }

  componentDidMount() {
    TransferActions.get();
  }

  componentWillMount() {
    // Subscribe to the store.
    this.unsubscribe = TransferStore.listen(this._onChange);
  }

  componentWillUnmount() {
    this.unsubscribe();
  }

  _onChange(data) {
    if (!data.success) {
      this.props.showToastMessage("error", {
        title: "Error",
        body: data.status
      });
    } else if (data.success && this.state.status === "done") {
      this.props.showToastMessage("success", { title: "Saved" });
    }

    this.setState({ data: data, status: "done" });
  }

  render() {
    if (this.state.status === "loading") {
      return (
        <div className="loader-inner ball-pulse">
          <div />
          <div />
          <div />
        </div>
      );
    }

    let array = this.state.data.data;
    let options = array[this.state.selectedIndex].options.map(
      (item, index) => {
        return (
          <div key={index} className="data-table-row">
            <label className="checkbox" title="1.2.840.10008.1.2.1.99">
              <input
                type="checkbox"
                id={item.name}
                name="GlobalTransferStorageTransferStorage0"
                checked={item.value}
                onChange={this.handleChange.bind(this, item.name, index)}
              />
              {item.name} -- {item.uid}
            </label>
          </div>
        );
      }
    );

    var sopclasses = array.map((item, index) => {
      return (
        <option key={index}>
          {item.sop_name} -- {item.uid}
        </option>
      );
    });

    return (
      <div>
        <div className="tab-pane" id="transfer">
          <div className="panel panel-primary topMargin">
            <div className="panel-heading">
              <h3 className="panel-title">
                SOP Class Global Transfer Storage Options
              </h3>
            </div>
            <div className="panel-body">
              <ul className="list-group">
                <select
                  id="sop_select"
                  className="form-control"
                  onChange={this.onSopSelected}
                >
                  {sopclasses}
                </select>
                <li className="list-group-item list-group-item-management">
                  <div className="row">
                    <div className="col-xs-6 col-sm-8">
                      <div id="GlobalTransferStorage" className="data-table manage-ts-options">
                        {options}
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
              <div>
                <button
                  className="btn btn_dicoogle"
                  onClick={this.handleSelectSop}
                >
                  {this.selectSOPOn ? "Select all for this SOP class" : "Unselect all for this SOP class"}
                </button>

                <button
                  className="btn btn_dicoogle"
                  onClick={this.handleSelectAll}
                >
                  {this.selectAllOn ? "Select all for ALL SOP classes" : "Unselect all for ALL SOP classes"}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  handleSelectAll() {
    if (this.selectAllOn) TransferActions.selectAll();
    else TransferActions.unSelectAll();

    this.selectAllOn = !this.selectAllOn;
  }

  handleSelectSop(e) {
    let sop = this.state.data.data[this.state.selectedIndex]
    let sopClassUid = sop.uid;
    if (this.selectSOPOn) {
      TransferActions.selectAllSOP(sopClassUid);
    } else {
      TransferActions.unSelectAllSOP(sopClassUid);
    }

    this.selectSOPOn = !this.selectSOPOn;
  }

  handleChange(id, index) {
    let uid = this.state.data.data[
      document.getElementById("sop_select").selectedIndex
    ].uid;
    let value = document.getElementById(id).checked;
    TransferActions.set(this.state.selectedIndex, index, uid, id, value);
  }

  onSopSelected() {
    let selectedId = document.getElementById("sop_select").selectedIndex;
    this.setState({ selectedIndex: selectedId });
  }
}

export { TransferOptionsView };
