import React from "react";
import { Modal } from "react-bootstrap";

class ConfirmModal extends React.Component {
  constructor(props) {
    super(props);
    this.onConfirm = this.onConfirm.bind(this);
  }

  onConfirm() {
    this.props.onConfirm();
    this.props.onHide();
  }

  render() {
    var body_message =
      this.props.message ||
      "The following files will be unindexed. This operation might be irreversible.";
    return (
      <Modal {...this.props} animation={false}>
        <Modal.Header>
          <Modal.Title>Are you sure?</Modal.Title>
        </Modal.Header>
        <div className="modal-body">{body_message}</div>
        <div className="modal-footer">
          <button className="btn btn_dicoogle" onClick={this.props.onHide}>
            {" "}
            Cancel
          </button>
          <button className="btn btn-warning" onClick={this.onConfirm}>
            {" "}
            Confirm
          </button>
        </div>
      </Modal>
    );
  }
}

export default ConfirmModal;
