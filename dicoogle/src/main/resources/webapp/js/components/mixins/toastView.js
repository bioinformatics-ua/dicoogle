import React from "react";
import { Transition } from "react-transition-group";

const ToastView = React.createClass({
  render() {
    const message = this.props.message ? this.props.message : "Saved.";
    const duration = this.props.duration ? this.props.duration : 300;
    const toastType = this.props.toastType ? this.props.toastType : "default";

    const transitionStyles = {
      entering: {
        transition: `opacity 400ms ease-in-out`,
      },
      exiting: {
        transition: `opacity 400ms ease-in-out`,
        opacity: 0
      },
      exited: {
        opacity: 0
      }
    };

    return (
      <Transition in={this.props.show} timeout={duration}>
        {(state) => (
          <div
            style={{
              ...transitionStyles[state]
            }}
          >
            <div className={`toast toast-${toastType}`}>{message}</div>
          </div>
        )}
      </Transition>
    );
  }
});

export { ToastView };
