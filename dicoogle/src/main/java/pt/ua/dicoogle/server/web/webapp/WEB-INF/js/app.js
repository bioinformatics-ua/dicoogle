window.$ = window.jQuery = require('jquery');

var React = require('react');
var Routing = require('./components/routing');

require('bootstrap');
require('jquery-ui');


// React.render(<div/>,
//     document.getElementById('container')
// );

// Call the routing.
Routing();
