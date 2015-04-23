/* todo.jsx - To-do list for Dicoogle
 */
define('react-todo', function(require) {
  var React = require('react');

  if (!React) {
    console.error("React is not supported in this page!");
  }

  var TodoItems = React.createClass({
    render: function() {
      var createItem = function(itemText) {
        return <li>{itemText}</li>;
      };
      return <ul>{this.props.items.map(createItem)}</ul>;
    }
  });

  var TodoApp = React.createClass({
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
        <div>
          <h3>TODO</h3>
          <TodoItems items={this.state.items} />
          <form onSubmit={this.handleSubmit}>
            <input onChange={this.onChange} value={this.state.text} />
            <button>{'Add #' + (this.state.items.length + 1)}</button>
          </form>
        </div>
      );
    }
  });

  var Module = function() {
    var r = <TodoApp />;
    this.render = function() {
      var e = document.createElement('div');
      React.render(r, e);
      return e;
    };
  }

  return Module;
});