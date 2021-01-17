import * as React from 'react';
/* global Dicoogle */

function TodoItems(props) {
    const createItem = (itemText, i) => {
        return <li key={i}>{itemText}</li>;
    };
    return <ul>{props.items.map(createItem)}</ul>;
}

class TodoApp extends React.Component {
    constructor(props) {
      super(props);
      this.state = {items: [], text: ''};
    }
    onChange(e) {
      this.setState({text: e.target.value});
    }
    handleSubmit(e) {
      e.preventDefault();
      let nextItems = this.state.items.concat([this.state.text]);
      let nextText = '';
      this.setState({items: nextItems, text: nextText});
    }
    render() {
      return (
        <div>
          <h3>TODO List:</h3>
          <TodoItems items={this.state.items} />
          <form onSubmit={this.handleSubmit}>
            <input onChange={this.onChange} value={this.state.text} />
            <button>{'Add #' + (this.state.items.length + 1)}</button>
          </form>
        </div>
      );
    }
  }
  
export default class MyPlugin {
    
    constructor() {
        this.r = null;
    }
    
    /**
     * @param {DOMElement} parent
     * @param {DOMElement} slot
     */
    render(parent, slot) {
        this.r = <TodoApp />;
        React.render(this.r, parent);
    }
}
