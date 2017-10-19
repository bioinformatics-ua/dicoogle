import Reflux from 'reflux';
import $ from 'jquery';
import {UserActions} from '../actions/userActions';
import {Endpoints} from '../constants/endpoints';

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

    },

    saveLocalStore: function(){
        localStorage.setItem("user", JSON.stringify({
            isAdmin: this._isAdmin,
            'username': this._username,
            'roles': this._roles,
            'token': this._token
        }));

    },
    loadLocalStore(user) {
        if (!user) {
            user = localStorage.getItem('user');
        }
        if (user) {
            console.log('Loading previous session from local store');
            let userData = JSON.parse(user);
            this._isAdmin = userData.isAdmin;
            this._username = userData.username;
            this._roles = userData.roles;
            this._token = userData.token;
            this._isLoggedIn = true;
        }
        this.trigger({
            isLoggedIn: this._isLoggedIn,
            success: true
        });
    },
    onLogin: function(user, pass){
      console.log("onLogin");

      let Dicoogle = dicoogleClient(Endpoints.base);

      Dicoogle.login(user, pass, (error, data) => {
          if (error)
          {
              this.trigger({
                success: false,
                loginFailed: true
              });
              return;
          }
          this._username = data.user;
          this._isAdmin = data.admin;
          this._token = data.token;
          this._roles = data.roles;
          this._isLoggedIn = true;
          localStorage.token = this._token;
          console.log("Saving token to local storage:", localStorage.token);
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
            console.log("Using existing token")
            this.loadLocalStore();
        } else {
            console.log(`Token is ${localStorage.token} - checking login state...`);

            $.ajax({
                type: "GET",
                url: Endpoints.base + "/login",
                dataType: 'json',
                async: true,
                success: (result) => {
                /* if result is a JSon object */
                this._username = result.user;
                this._isAdmin = result.admin;
                this._isLoggedIn = true;

                this.saveLocalStore();
                    this.trigger({
                        isLoggedIn: this._isLoggedIn,
                        success: true
                });
            }, error: () => {
                this.trigger({
                    isLoggedIn: this._isLoggedIn,
                    success: false
                });
            }});
        }

    } else {
        //return this._isLoggedIn;
        if (localStorage.token !== undefined) {
            this.loadLocalStore();
        } else {
            this.trigger({
                isLoggedIn: self._isLoggedIn,
                success: true
            });
        }
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
