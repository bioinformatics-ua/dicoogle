/* todo.jsx - To-do list for Dicoogle
 */
import React from 'react';

const TodoItems = React.createClass({
  render() {
    const createItem = (itemText, i) => {
      return <li key={i}>{itemText}</li>;
    };
    return <ul>{this.props.items.map(createItem)}</ul>;
  }
});

const TodoApp = React.createClass({
  getInitialState() {
    return {items: [], text: ''};
  },
  onChange(e) {
    this.setState({text: e.target.value});
  },
  handleSubmit(e) {
    e.preventDefault();
    let nextItems = this.state.items.concat([this.state.text]);
    let nextText = '';
    this.setState({items: nextItems, text: nextText});
  },
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
});

export default class Todo {

  render(parent) {
    //React.render(this.r, parent);
    return <TodoApp />;
  }
}
