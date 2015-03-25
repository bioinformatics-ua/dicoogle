(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.dummy = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/* module.js - Dummy module file
 */
var DummyModule = function() {
  // derp
  var index = null;
  var providers = [];
  var exhibit = document.createElement('b');
  var interv = null;
  var button = document.createElement('input');
   
  function onClick() {
     if (!interv) {
       DicoogleWeb.request('providers', function(error, result) {
         if (error) {
           console.error('An error occurred: ', error);
           return;
         }
         providers = result;
         index = 0;
         exhibit.innerHTML = providers[0];
         interv = setInterval(function(){
           index = (index+1)%providers.length;
           exhibit.innerHTML = providers[index];
         }, 1200);
         button.value = 'Stop!';
       });
     } else {
       clearInterval(interv);
       exhibit.innerHTML = '';
       interv = null;
       index = null;
       button.value = 'Click me!';
     }
  }
   
  button.type = 'button';
  button.value = 'Click me!';
  button.onclick = onClick;
  
  this.render = function() {
     var d = document.createElement('div');
     d.innerHTML = '<h3>DUMMY Module</h3>';
     d.innerHTML +=
         '<p>This is a test Dicoogle web UI module to let developers understand how to'
       + ' develop Dicoogle Web UI\'s.<br>\n'
       + 'Since it\'s best to show something useful and interactive,'
       + ' try pushing the button below. It will request all query providers and sequentially'
       + ' cycle between their names. This is as rudimentary as it can get, but modules can'
       + ' embed some web interface technology, like React, to make them more interesting.</p>';
     
     d.appendChild(button);
     d.appendChild(document.createElement('br'));
     d.appendChild(exhibit);
     return d;
   };
 };
 
 module.exports = DummyModule;
},{}]},{},[1])(1)
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyaWZ5L25vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJkdW1teS5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQ0FBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwiZmlsZSI6ImdlbmVyYXRlZC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzQ29udGVudCI6WyIoZnVuY3Rpb24gZSh0LG4scil7ZnVuY3Rpb24gcyhvLHUpe2lmKCFuW29dKXtpZighdFtvXSl7dmFyIGE9dHlwZW9mIHJlcXVpcmU9PVwiZnVuY3Rpb25cIiYmcmVxdWlyZTtpZighdSYmYSlyZXR1cm4gYShvLCEwKTtpZihpKXJldHVybiBpKG8sITApO3ZhciBmPW5ldyBFcnJvcihcIkNhbm5vdCBmaW5kIG1vZHVsZSAnXCIrbytcIidcIik7dGhyb3cgZi5jb2RlPVwiTU9EVUxFX05PVF9GT1VORFwiLGZ9dmFyIGw9bltvXT17ZXhwb3J0czp7fX07dFtvXVswXS5jYWxsKGwuZXhwb3J0cyxmdW5jdGlvbihlKXt2YXIgbj10W29dWzFdW2VdO3JldHVybiBzKG4/bjplKX0sbCxsLmV4cG9ydHMsZSx0LG4scil9cmV0dXJuIG5bb10uZXhwb3J0c312YXIgaT10eXBlb2YgcmVxdWlyZT09XCJmdW5jdGlvblwiJiZyZXF1aXJlO2Zvcih2YXIgbz0wO288ci5sZW5ndGg7bysrKXMocltvXSk7cmV0dXJuIHN9KSIsIi8qIG1vZHVsZS5qcyAtIER1bW15IG1vZHVsZSBmaWxlXG4gKi9cbnZhciBEdW1teU1vZHVsZSA9IGZ1bmN0aW9uKCkge1xuICAvLyBkZXJwXG4gIHZhciBpbmRleCA9IG51bGw7XG4gIHZhciBwcm92aWRlcnMgPSBbXTtcbiAgdmFyIGV4aGliaXQgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdiJyk7XG4gIHZhciBpbnRlcnYgPSBudWxsO1xuICB2YXIgYnV0dG9uID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnaW5wdXQnKTtcbiAgIFxuICBmdW5jdGlvbiBvbkNsaWNrKCkge1xuICAgICBpZiAoIWludGVydikge1xuICAgICAgIERpY29vZ2xlV2ViLnJlcXVlc3QoJ3Byb3ZpZGVycycsIGZ1bmN0aW9uKGVycm9yLCByZXN1bHQpIHtcbiAgICAgICAgIGlmIChlcnJvcikge1xuICAgICAgICAgICBjb25zb2xlLmVycm9yKCdBbiBlcnJvciBvY2N1cnJlZDogJywgZXJyb3IpO1xuICAgICAgICAgICByZXR1cm47XG4gICAgICAgICB9XG4gICAgICAgICBwcm92aWRlcnMgPSByZXN1bHQ7XG4gICAgICAgICBpbmRleCA9IDA7XG4gICAgICAgICBleGhpYml0LmlubmVySFRNTCA9IHByb3ZpZGVyc1swXTtcbiAgICAgICAgIGludGVydiA9IHNldEludGVydmFsKGZ1bmN0aW9uKCl7XG4gICAgICAgICAgIGluZGV4ID0gKGluZGV4KzEpJXByb3ZpZGVycy5sZW5ndGg7XG4gICAgICAgICAgIGV4aGliaXQuaW5uZXJIVE1MID0gcHJvdmlkZXJzW2luZGV4XTtcbiAgICAgICAgIH0sIDEyMDApO1xuICAgICAgICAgYnV0dG9uLnZhbHVlID0gJ1N0b3AhJztcbiAgICAgICB9KTtcbiAgICAgfSBlbHNlIHtcbiAgICAgICBjbGVhckludGVydmFsKGludGVydik7XG4gICAgICAgZXhoaWJpdC5pbm5lckhUTUwgPSAnJztcbiAgICAgICBpbnRlcnYgPSBudWxsO1xuICAgICAgIGluZGV4ID0gbnVsbDtcbiAgICAgICBidXR0b24udmFsdWUgPSAnQ2xpY2sgbWUhJztcbiAgICAgfVxuICB9XG4gICBcbiAgYnV0dG9uLnR5cGUgPSAnYnV0dG9uJztcbiAgYnV0dG9uLnZhbHVlID0gJ0NsaWNrIG1lISc7XG4gIGJ1dHRvbi5vbmNsaWNrID0gb25DbGljaztcbiAgXG4gIHRoaXMucmVuZGVyID0gZnVuY3Rpb24oKSB7XG4gICAgIHZhciBkID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2Jyk7XG4gICAgIGQuaW5uZXJIVE1MID0gJzxoMz5EVU1NWSBNb2R1bGU8L2gzPic7XG4gICAgIGQuaW5uZXJIVE1MICs9XG4gICAgICAgICAnPHA+VGhpcyBpcyBhIHRlc3QgRGljb29nbGUgd2ViIFVJIG1vZHVsZSB0byBsZXQgZGV2ZWxvcGVycyB1bmRlcnN0YW5kIGhvdyB0bydcbiAgICAgICArICcgZGV2ZWxvcCBEaWNvb2dsZSBXZWIgVUlcXCdzLjxicj5cXG4nXG4gICAgICAgKyAnU2luY2UgaXRcXCdzIGJlc3QgdG8gc2hvdyBzb21ldGhpbmcgdXNlZnVsIGFuZCBpbnRlcmFjdGl2ZSwnXG4gICAgICAgKyAnIHRyeSBwdXNoaW5nIHRoZSBidXR0b24gYmVsb3cuIEl0IHdpbGwgcmVxdWVzdCBhbGwgcXVlcnkgcHJvdmlkZXJzIGFuZCBzZXF1ZW50aWFsbHknXG4gICAgICAgKyAnIGN5Y2xlIGJldHdlZW4gdGhlaXIgbmFtZXMuIFRoaXMgaXMgYXMgcnVkaW1lbnRhcnkgYXMgaXQgY2FuIGdldCwgYnV0IG1vZHVsZXMgY2FuJ1xuICAgICAgICsgJyBlbWJlZCBzb21lIHdlYiBpbnRlcmZhY2UgdGVjaG5vbG9neSwgbGlrZSBSZWFjdCwgdG8gbWFrZSB0aGVtIG1vcmUgaW50ZXJlc3RpbmcuPC9wPic7XG4gICAgIFxuICAgICBkLmFwcGVuZENoaWxkKGJ1dHRvbik7XG4gICAgIGQuYXBwZW5kQ2hpbGQoZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnYnInKSk7XG4gICAgIGQuYXBwZW5kQ2hpbGQoZXhoaWJpdCk7XG4gICAgIHJldHVybiBkO1xuICAgfTtcbiB9O1xuIFxuIG1vZHVsZS5leHBvcnRzID0gRHVtbXlNb2R1bGU7Il19
