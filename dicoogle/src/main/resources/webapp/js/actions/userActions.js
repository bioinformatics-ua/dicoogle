import Reflux from 'reflux';
const UserActions = {
  login: Reflux.createAction(),
  logout: Reflux.createAction()
};
export const login = UserActions.login;
export const logout = UserActions.logout;
