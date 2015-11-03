/* todo.jsx - To-do list for Dicoogle
 */
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});

var _createClass = (function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ('value' in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; })();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError('Cannot call a class as a function'); } }

var React = require('react');

if (!React) {
  console.error("React is not supported in this page!");
}

var TodoItems = React.createClass({
  displayName: 'TodoItems',

  render: function render() {
    var createItem = function createItem(itemText) {
      return React.createElement(
        'li',
        null,
        itemText
      );
    };
    return React.createElement(
      'ul',
      null,
      this.props.items.map(createItem)
    );
  }
});

var TodoApp = React.createClass({
  displayName: 'TodoApp',

  getInitialState: function getInitialState() {
    return { items: [], text: '' };
  },
  onChange: function onChange(e) {
    this.setState({ text: e.target.value });
  },
  handleSubmit: function handleSubmit(e) {
    e.preventDefault();
    var nextItems = this.state.items.concat([this.state.text]);
    var nextText = '';
    this.setState({ items: nextItems, text: nextText });
  },
  render: function render() {
    return React.createElement(
      'div',
      null,
      React.createElement(
        'h3',
        null,
        'TODO List:'
      ),
      React.createElement(TodoItems, { items: this.state.items }),
      React.createElement(
        'form',
        { onSubmit: this.handleSubmit },
        React.createElement('input', { onChange: this.onChange, value: this.state.text }),
        React.createElement(
          'button',
          null,
          'Add #' + (this.state.items.length + 1)
        )
      )
    );
  }
});

var Todo = (function () {
  function Todo() {
    _classCallCheck(this, Todo);

    this.r = React.createElement(TodoApp, null);
  }

  _createClass(Todo, [{
    key: 'render',
    value: function render(parent) {
      React.render(this.r, parent);
    }
  }]);

  return Todo;
})();

exports['default'] = Todo;
;
module.exports = exports['default'];

