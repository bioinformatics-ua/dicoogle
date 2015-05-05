/* todo.jsx - To-do list for Dicoogle
 */
var React = require('react');

if (!React) {
  console.error("React is not supported in this page!");
}

var TodoItems = React.createClass({displayName: "TodoItems",
  render: function() {
    var createItem = function(itemText) {
      return React.createElement("li", null, itemText);
    };
    return React.createElement("ul", null, this.props.items.map(createItem));
  }
});

var TodoApp = React.createClass({displayName: "TodoApp",
  getInitialState: function() {
    return {items: [], text: ''};
  },
  onChange: function(e) {
    this.setState({text: e.target.value});
  },
  handleSubmit: function(e) {
    e.preventDefault();
    var nextItems = this.state.items.concat([this.state.text]);
    var nextText = '';
    this.setState({items: nextItems, text: nextText});
  },
  render: function() {
    return (
      React.createElement("div", null, 
        React.createElement("h3", null, "TODO"), 
        React.createElement(TodoItems, {items: this.state.items}), 
        React.createElement("form", {onSubmit: this.handleSubmit}, 
          React.createElement("input", {onChange: this.onChange, value: this.state.text}), 
          React.createElement("button", null, 'Add #' + (this.state.items.length + 1))
        )
      )
    );
  }
});

module.exports = function() {
  var r = React.createElement(TodoApp, null);
  this.render = function(parent) {
    React.render(r, parent);
  };
}
