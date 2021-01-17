import React from "react";
import createReactClass from "create-react-class";
import { Transition } from "react-transition-group";

const ToastView = createReactClass({
  render() {
    let { message, duration, toastType } = this.props;

    message = message && message.title ? message : { title: "Saved" };
    duration = duration ? duration : 300;
    toastType = toastType ? toastType : "default";

    const transitionStyles = {
      entering: {
        transition: `opacity 400ms ease-in-out`
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
        {state => (
          <div
            style={{
              ...transitionStyles[state]
            }}
          >
            <div className={`toast toast-${toastType}`}>
              <h3 className="panel-title">{message.title}</h3>

              {message.body && <div className="toast-body">{message.body}</div>}
            </div>
          </div>
        )}
      </Transition>
    );
  }
});

export { ToastView };
