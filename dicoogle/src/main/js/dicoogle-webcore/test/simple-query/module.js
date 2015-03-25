(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.simpleQuery = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/* module.js - Dummy module file
 */
module.exports = function() {
  var input = document.createElement('input');
  var button = document.createElement('input');
  var chkKeyword = document.createElement('input');
  
  function onClick() { 
    var query = input.value;
    DicoogleWeb.issueQuery(query, {
      keyword: chkKeyword.checked,
      provider: ['lucene']
    }, function(error, result) {
      if (error) {
        console.error('An error occurred: ', error);
        return;
      }
      console.log('Complete.');
    });
  }
   
  input.type = 'text';
  input.placeholder = 'Search query...';
  input.onkeypress = function searchKeyPress(event) {
    if (event.keyCode == 13) {
      onClick();
    }
  };
  
  chkKeyword.type = 'checkbox';
  chkKeyword.value = 'keyword';
  chkKeyword.checked = true;
  chkKeyword.innerHTML = 'keywords';
  
  button.type = 'button';
  button.value = 'Search';
  button.onclick = onClick;
  
  this.render = function() {
     var d = document.createElement('div');
     d.appendChild(input);
     d.appendChild(chkKeyword);
     d.appendChild(button);
     return d;
   };
};

},{}]},{},[1])(1)
});