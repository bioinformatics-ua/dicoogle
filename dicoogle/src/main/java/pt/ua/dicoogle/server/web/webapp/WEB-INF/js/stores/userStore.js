'use strict';

import Reflux from 'reflux';

import $ from 'jquery';
import {UserActions} from '../actions/userActions';
import {Endpoints} from '../constants/endpoints';

var UserStore = Reflux.createStore({
    listenables: UserActions,
    init: function () {
       this._contents = {};

       this._isLoggedIn = false;
       this._username = "";
       this._isAdmin = false;
       this._roles = [];
       this._token = '';
       this.loadLocalStore()
    },

    saveLocalStore: function(){
        localStorage.setItem("user",  JSON.stringify({
            isAdmin: this.isAdmin,
            'username': this._username,
            'roles': this._roles,
            'token': this._token
        }));


    },
    loadLocalStore: function(){
        console.log("loadLocalStoreloadLocalStore");
        if (localStorage.token!=null)
        {
            let user =  JSON.parse(localStorage.getItem("user"));
            console.log(user);
            this._isAdmin = user._isAdmin ;
            this._username = user.username;
            this._roles = user.roles;
            this._token = user.token ;
            this._isLoggedIn = true;
        }

    },
    onLogin: function(user, pass){
      console.log("onLogin");
      var self = this;

      var formData = {username: user, password: pass}; //Array
      $.ajax({
          url: Endpoints.base + "/login",
          type: "POST",
          dataType: 'json',
          data: formData,
          success: function(data, textStatus, jqXHR)
          {
            console.log(data);
            self._username = data.user;
            self._isAdmin = data.admin;
            self._token = data.token;
            self._roles = data.roles;
            self._isLoggedIn = true;
            localStorage.token = self._token;
            self.saveLocalStore();

            console.log("Localstorage token: " + localStorage.token);
            self.trigger({
              isLoggedIn: self._isLoggedIn,
              success: true
            });
          },
          error: function (jqXHR, textStatus, errorThrown)
          {
            //TODO: HANDLE LOGIN FAILED
            console.log("Login Failed");
            self.trigger({
              failed: true
            });
          }
      });
    },

    onIsLoggedIn: function(){
      console.log("Verify onIsLoggedIn");
        console.log(this._isLoggedIn);
      if(this._isLoggedIn === false)
      {
        console.log("Verify onIsLoggedIn1");
          console.log(this._isLoggedIn);
          console.log(localStorage.token);
        if (localStorage.token !=null) {
            console.log("Verify loadLocalStore");
            this.loadLocalStore();
            this.trigger({
                isLoggedIn: self._isLoggedIn,
                success: true
            });
        }else{
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
            }
        });
        }



      } else {
        //return this._isLoggedIn;
          if (localStorage.token !==undefined) {
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
