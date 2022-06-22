import React from "react";
import * as PropTypes from "prop-types";

export default class PluginView extends React.Component {
  static get propTypes() {
    return {
      // React router fills this with a plugin name
      params: PropTypes.object,
      // the plugin name
      plugin: PropTypes.string,
      slotId: PropTypes.string,
      data: PropTypes.object
    };
  }

  static get defaultProps() {
    return {
      slotId: "menu",
      data: {}
    };
  }

  constructor(props) {
    super(props);
    this.state = {
      elements: {}
    };
    this.handleMounted = this.handleMounted.bind(this);
    this.handleLoaded = this.handleLoaded.bind(this);
  }

  handleMounted(component) {
    if (component) {
      const node = component;
      node.addEventListener("plugin-load", e => {
        //console.log('[plugin-load]', e);
        if (e && e.detail) {
          this.handleLoaded(e.detail);
        }
      });
      node.data = this.props.data;
    }
  }

  handleLoaded(element) {
    if (React.isValidElement(element)) {
      const elements = {};
      elements[this.getPluginName()] = element;
      for (const name in this.state.elements) {
        elements[name] = this.state.elements[name];
      }
      this.setState({
        elements
      });
    }
  }

  getPluginName() {
    return this.props.plugin || (this.props.params && this.props.params.plugin);
  }

  render() {
    const plugin = this.getPluginName();
    return (
      <div className={this.props.className} style={this.props.style}>
        {this.state.elements[plugin] ? (
          <div>{this.state.elements[plugin]}</div>
        ) : (
          <dicoogle-slot
            key={`${this.props.slotId}.${plugin}`}
            {...this.props.data}
            ref={this.handleMounted}
            data-slot-id={this.props.slotId}
            data-plugin-name={plugin}
          >
            {plugin && (
              <div className="loader-inner ball-pulse">
                <div />
                <div />
                <div />
              </div>
            )}
          </dicoogle-slot>
        )}
      </div>
    );
  }
}
