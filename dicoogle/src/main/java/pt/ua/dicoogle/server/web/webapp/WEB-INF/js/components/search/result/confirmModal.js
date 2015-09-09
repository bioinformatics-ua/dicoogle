
var React = require('react');
import {Modal} from 'react-bootstrap';

var ConfirmModal = React.createClass({
    getInitialState: function(){
        return {};
    },
    onConfirm: function(){
        this.props.onConfirm();
        this.props.onHide();
    },
    render: function() {
        return (
        <Modal {...this.props} title="Are you sure?" animation={false}>
          <div className="modal-body">
            The following files will be unindexed. This operation might be irreversible. 
          </div>
          <div className="modal-footer">
              <button className="btn btn_dicoogle" onClick={this.props.onHide}> Cancel</button>
              <button className="btn btn-warning" onClick={this.onConfirm}> Confirm</button>
          </div>
        </Modal>
      );
  }
});

export default ConfirmModal;