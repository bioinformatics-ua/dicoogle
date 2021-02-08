import React from "react";
import * as PropTypes from "prop-types";
import { ResultsSelected } from "../../stores/resultSelected";
import Webcore from "dicoogle-webcore";

export default class PluginForm extends React.Component {
  static get propTypes() {
    return {
      slotId: PropTypes.string.isRequired,
      plugin: PropTypes.shape({
        name: PropTypes.string.isRequired,
        caption: PropTypes.string
      }),
      data: PropTypes.object,
      onHide: PropTypes.func.isRequired
    };
  }

  static get defaultProps() {
    return {
      data: {}
    };
  }

  constructor(props) {
    super(props);
    this.handleMounted = this.handleMounted.bind(this);
    this.handleHideSignal = this.handleHideSignal.bind(this);
  }

  onConfirm() {
    this.props.onHide();
  }

  handleMounted(component) {
    if (component) {
      const node = component;
      node.data = this.props.data;
      node.addEventListener("hide", this.handleHideSignal);

      Webcore.emitSlotSignal(
        node,
        "result-selection-ready",
        ResultsSelected.get()
      );
    }
  }

  handleHideSignal({ target }) {
    console.log("Plugin requested to hide");
    target.removeEventListener("hide", this.handleHideSignal);
    this.props.onHide();
  }

  render() {
    const { plugin } = this.props;
    return (
      plugin && (
        <dicoogle-slot
          {...this.props.data}
          ref={this.handleMounted}
          data-slot-id={this.props.slotId}
          data-plugin-name={plugin.name}
        >
          {plugin.name && (
            <div className="loader-inner ball-pulse">
              <div />
              <div />
              <div />
            </div>
          )}
        </dicoogle-slot>
      )
    );
  }
}
