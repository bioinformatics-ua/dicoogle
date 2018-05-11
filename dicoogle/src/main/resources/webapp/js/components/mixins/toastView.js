import React from 'react';
import { Transition } from 'react-transition-group'

const ToastView = React.createClass({
  render() {
    let message = this.props.message ? this.props.message : "Saved.";

    return (
      <Transition in={this.props.show} timeout={0}>
        {(state) => (
          <div style={{
            transition: `opacity 400ms ease-in-out`,
            opacity: state === 'entered' ? 1 : 0
          }}>
            <div className="toast">{message}</div>
          </div>
        )}
      </Transition>
    );
  }
});

export { ToastView };
