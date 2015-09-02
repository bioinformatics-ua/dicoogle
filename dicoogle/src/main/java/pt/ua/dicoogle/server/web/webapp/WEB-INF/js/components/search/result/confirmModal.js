
var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Modal = ReactBootstrap.Modal;

var ConfirmModal = React.createClass({
    getInitialState: function(){
        return { showModal: false };
    },
    close: function(){
        this.setState({ showModal: false });
    },
    open: function(){
        this.setState({ showModal: true });
    },
    onConfirm: function(){
        this.props.onConfirm();
        this.props.onRequestHide();
    },
    render: function() {
        return (
        <Modal {...this.props} title="Are you sure?" animation={false}>
          <div className="modal-body">
            The following files will be unindex. This operation might be irreversible. 
          </div>
          <div className="modal-footer">
              <button className="btn btn_dicoogle" onClick={this.props.onRequestHide}> Cancel</button>
              <button className="btn btn-warning" onClick={this.onConfirm}> Confirm</button>
          </div>
        </Modal>
      );
  }
});

export {ConfirmModal};