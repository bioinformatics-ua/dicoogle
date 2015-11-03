/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import $ from 'jquery';
import {UserActions} from '../actions/userActions';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';

var UserStore = Reflux.createStore({
    listenables: UserActions,
    init: function () {
       this._contents = {};

       this._isLoggedIn = false;
       this._username = "";
       this._isAdmin = false;
    },


    onLogin : function(user,pass){
      console.log("onLogin");
      var self = this;

      var formData = {username: user,password:pass}; //Array
      $.ajax({
          url : Endpoints.base + "/login",
          type: "POST",
          dataType: 'json',
          data : formData,
          success: function(data, textStatus, jqXHR)
          {
            console.log(data);
            self._username = data.user;
            self._isAdmin = data.admin;
            self._isLoggedIn = true;

            self.trigger({
              isLoggedIn:self._isLoggedIn,
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

    onIsLoggedIn : function(){
      var self = this;
      if(this._isLoggedIn == false)
      {
        var li;
        $.ajax({
        type: "GET",
        url: Endpoints.base + "/login",
        dataType: 'json',
        async: true,
        success: function (result) {
            /* if result is a JSon object */
            self._username = result.user;
            self._isAdmin = result.admin;
            self._isLoggedIn = true;

            console.log("SIM",result);
            li = true;
            setTimeout(function(){
              self.trigger({
                isLoggedIn:self._isLoggedIn,
                success: true
              });
            }, 500)

        },
        error: function(){
          console.log("NAO");
          li=false;
          self.trigger({
            isLoggedIn:self._isLoggedIn,
            success: true
          });
        }
      });
      //return li;
      }
      else
      {
        //return this._isLoggedIn;
        self.trigger({
          isLoggedIn:self._isLoggedIn,
          success: true
        });
    }
      /*if(this._isLoggedIn == true){
        this._isLoggedIn = true;
        return true;
      }
      else
      {
        $.ajax({

          url: "http://localhost:8080/login",
          dataType: 'json',
          success: function(data) {
            console.log(data);

            return true;
          },
          error: function(xhr, status, err) {
            console.log("not loggedin");
            return false;

          }
        });
      }*/
    },

    getUsername : function(){
      return this._username;
    },
    getLogginState : function(){
      console.log("ATAO: ", this._isLoggedIn);
      return this._isLoggedIn;
    }
});

export {UserStore};
