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
  
  constructor(props) {
    super(props);
    this.state = {
      element: null
    };
    this.handleLoaded = this.handleLoaded.bind(this);
  }
  
  shouldComponentUpdate(nextProps, nextState) {
    return nextProps.plugin !== this.props.plugin
        || nextProps.params !== this.props.params
        || nextState.element !== this.state.element;
  }
  
  handleLoaded(element) {
    if (React.isValidElement(element)) {
      this.setState({
        element
      });
    }
  }

  render() {
    const plugin = this.props.plugin || this.props.params.plugin;
    return (
      <div className={this.props.className}>
        {this.state.element ?
        <div>{this.state.element}</div> :
        <dicoogle-slot data-slot-id="menu" data-plugin-name={plugin} data-on-loaded={this.handleLoaded}>
          <div className="loader-inner ball-pulse">
            <div/><div/><div/>
          </div>
        </dicoogle-slot>}
      </div>
    );
  }
}

export default PluginView;
