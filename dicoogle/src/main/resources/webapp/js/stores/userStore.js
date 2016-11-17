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
    loadLocalStore: function(){
        if (localStorage.token) {
            console.log("loadLocalStore");
            let user = JSON.parse(localStorage.getItem("user"));
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
                failed: true
              });
              return;
          }
          this._username = data.user;
          this._isAdmin = data.admin;
          this._token = data.token;
          this._roles = data.roles;
          this._isLoggedIn = true;
          localStorage.token = this._token;
          this.saveLocalStore();

          console.log("Localstorage token: " + localStorage.token);
          this.trigger({
              isLoggedIn: this._isLoggedIn,
              success: true
          });
      });

    },

    onIsLoggedIn: function(){

      if(this._isLoggedIn === false)
      {

        if (localStorage.token) {
            this.loadLocalStore();
            this.trigger({
                isLoggedIn: self._isLoggedIn,
                success: true
            });
        } else {
            console.log("Verify ajax");

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
                setTimeout(() => {
                  this.trigger({
                    isLoggedIn: this._isLoggedIn,
                    success: true
                  });
                }, 500)
            },
            error: () => {
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
              this.trigger({
                  isLoggedIn: self._isLoggedIn,
                  success: true
              });
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

export {UserStore};
