'use strict';

var _createClass = (function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; })(); /* todo.jsx - To-do list for Dicoogle
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        */

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var TodoItems = _react2.default.createClass({
  render: function render() {
    var createItem = function createItem(itemText, i) {
      return _react2.default.createElement(
        'li',
        { key: i },
        itemText
      );
    };
    return _react2.default.createElement(
      'ul',
      null,
      this.props.items.map(createItem)
    );
  }
});

var TodoApp = _react2.default.createClass({
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
    return _react2.default.createElement(
      'div',
      null,
      _react2.default.createElement(
        'h3',
        null,
        'TODO List:'
      ),
      _react2.default.createElement(TodoItems, { items: this.state.items }),
      _react2.default.createElement(
        'form',
        { onSubmit: this.handleSubmit },
        _react2.default.createElement('input', { onChange: this.onChange, value: this.state.text }),
        _react2.default.createElement(
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
  }

  _createClass(Todo, [{
    key: 'render',
    value: function render(parent) {
      //React.render(this.r, parent);
      return _react2.default.createElement(TodoApp, null);
    }
  }]);

  return Todo;
})();

exports.default = Todo;

