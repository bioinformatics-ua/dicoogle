import * as Reflux from 'reflux';
import * as UserActions from '../actions/userActions';

import dicoogleClient from 'dicoogle-client';

const UserStore = Reflux.createStore({
    listenables: UserActions,
    init: function () {
       this._contents = {};

       this._isLoggedIn = false;
       this._username = "";
       this._isAdmin = false;
       this._roles = [];
       this._token = '';
       this.dicoogle = dicoogleClient();
    },

    saveLocalStore: function(){
        localStorage.setItem("user", JSON.stringify({
            isAdmin: this._isAdmin,
            'username': this._username,
            'roles': this._roles,
            'token': this._token
        }));

    },
    loadLocalStore: function(user) {
        if (!user) {
            user = JSON.parse(localStorage.getItem("user"));
        }
        if (user) {
            console.log(`Loading previous session from local store`);
            this._isAdmin = user.isAdmin;
            this._username = user.username;
            this._roles = user.roles;
            this._token = user.token;
            this._isLoggedIn = true;
            this.trigger({
                isLoggedIn: this._isLoggedIn,
                success: true
            });
        }
    },
    onLogin: function(user, pass){
      console.log("onLogin");

      this.dicoogle.login(user, pass, (error, data) => {
          if (error || !data.token) {
              this.trigger({
                success: false
              });
              return;
          }
          this._username = data.user;
          this._isAdmin = data.admin;
          this._token = data.token;
          this._roles = data.roles;
          this._isLoggedIn = true;
          localStorage.token = this._token;
          console.log(`Saving token to local storage: ${localStorage.token}`);
          this.saveLocalStore();

          this.trigger({
              isLoggedIn: true,
              success: true
          });
      });

    },

    onIsLoggedIn: function(){

      if(this._isLoggedIn === false)
      {

        if (localStorage.token) {

            this.dicoogle.restoreSession(localStorage.token, (error, info) => {
                if (error) {
                    this.trigger({
                        error,
                        success: false
                    });
                    return;
                }
                info.token = localStorage.token;
                this._username = info.user;
                this._isAdmin = info.admin;
                this._isLoggedIn = true;
                this.saveLocalStore();
            });

        } else {
            this.dicoogle.request('GET', 'login')
                .type('application/json')
                .end((error, outcome) => {
                    if (error) {
                        this.trigger({
                            isLoggedIn: this._isLoggedIn,
                            success: false
                        });
                        return;
                    }
                    const result = outcome.body;
                    this._username = result.user;
                    this._isAdmin = result.admin;
                    this._isLoggedIn = true;
                    this.saveLocalStore();
                });
        }
      } else {
        //return this._isLoggedIn;
        if (localStorage.token !== undefined) {
          this.loadLocalStore();
        }
        this.trigger({
          isLoggedIn: self._isLoggedIn,
          success: true
        });
      }
    },

    onLogout: function() {
        delete localStorage.token;
        delete localStorage.user;
    },

    getUsername: function(){
      return this._username;
    },
    isAdmin: function(){
        return this._isAdmin;
    },
    getLogginState: function(){
      return this._isLoggedIn;
    }
});

export default UserStore;
