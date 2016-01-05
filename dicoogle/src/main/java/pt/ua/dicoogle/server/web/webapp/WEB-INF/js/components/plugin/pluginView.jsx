import React from 'react';
import DicoogleWebcore from 'dicoogle-webcore';

class PluginView extends React.Component {

  static get propTypes() {
    return {
      // React router fills this with a plugin name
      params: React.PropTypes.object,
      // the plugin name
      plugin: React.PropTypes.string,
      slotId: React.PropTypes.string,
      data: React.PropTypes.object
    };
  }

  static get defaultProps() {
    return {
      slotId: 'menu'
    };
  }
  
  constructor(props) {
    super(props);
    this.state = {
      element: null
    };
    this.handleMounted = this.handleMounted.bind(this);
    this.handleLoaded = this.handleLoaded.bind(this);
  }
  
  componentWillReceiveProps(nextProps) {
    this.setState({
      element: null
    });
  }
  
  handleMounted(component) {
    if (component) {
      const node = component.getDOMNode();
      node.addEventListener('plugin-load', ({detail}) => {
        if (detail) {
          this.handleLoaded(detail);
        }
      });
    }
  }
  
  handleLoaded(element) {
    console.log("handleLoaded!");
    if (React.isValidElement(element)) {
      this.setState({
        element
      });
    }
  }
  
  getPluginName() {
    return this.props.plugin || (this.props.params && this.props.params.plugin);
  }

  render() {
    const plugin = this.getPluginName();
    return (
      <div className={this.props.className}>
        {this.state.element ?
        <div>{this.state.element}</div> :
        <dicoogle-slot {...this.props.data} ref={this.handleMounted} data-slot-id={this.props.slotId} data-plugin-name={plugin}>
          {plugin && <div className="loader-inner ball-pulse">
            <div/><div/><div/>
          </div>}
        </dicoogle-slot>}
      </div>
    );
  }
}

export default PluginView;
