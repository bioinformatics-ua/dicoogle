var Reflux = require('reflux');
var UserActions = exports;
UserActions.login = Reflux.createAction();
UserActions.logout = Reflux.createAction();
UserActions.isLoggedIn = Reflux.createAction();

export { UserActions };
