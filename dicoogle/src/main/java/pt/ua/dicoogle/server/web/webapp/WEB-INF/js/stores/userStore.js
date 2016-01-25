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
            self._isLoggedIn = true;

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
      if(this._isLoggedIn === false)
      {
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
            success: true
          });
        }
        });
      } else {
        //return this._isLoggedIn;
        this.trigger({
          isLoggedIn: self._isLoggedIn,
          success: true
        });
      }
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
