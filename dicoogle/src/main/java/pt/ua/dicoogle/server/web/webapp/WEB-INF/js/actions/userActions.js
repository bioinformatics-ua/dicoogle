import Reflux from 'reflux';
export const UserActions = {
  login: Reflux.createAction(),
  logout: Reflux.createAction(),
  isLoggedIn: Reflux.createAction()
};
