import React from 'react';
import DicoogleWebcore from 'dicoogle-webcore';

class PluginView extends React.Component {

  static get propTypes() {
    return {
      // React router fills this with a plugin name
      params: React.PropTypes.object.isRequired,
      // the plugin name
      plugin: React.PropTypes.string
    };
  }

  render() {
    const plugin = this.props.plugin || this.props.params.plugin;
    return (
      <div className={this.props.className}>
        <dicoogle-slot data-slot-id="menu" data-plugin-name={plugin}>
          ...
        </dicoogle-slot>
      </div>
    );
  }
}

export default PluginView;
