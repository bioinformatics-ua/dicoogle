/* dummy.js - Dummy module file
 */
console.log("Hello, I'm dummy!");

module.exports = function DummyModule() {
  // derp
  var index = null;
  var providers = [];
  var exhibit = document.createElement('b');
  exhibit.style['font-size'] = '16px';
  exhibit.style['font-family'] = 'Courier New';
  var interv = null;
  var button = document.createElement('input');
   
  function onClick() {
     if (!interv) {
       Dicoogle.getQueryProviders(function(error, result) {
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
         }, 1000);
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
  
  this.render = function(parent) {
     var d = document.createElement('div');
     d.innerHTML = '<h3>DUMMY Module</h3>';
     d.innerHTML +=
         '<p>This is a test Dicoogle web UI module to let developers understand how to'
       + ' develop Dicoogle Web UI\'s.<br>\n'
       + 'Since it\'s best to show something useful and interactive,'
       + ' try pushing the button below. It will request all query providers and sequentially'
       + ' cycle between their names. This is as rudimentary as it can get, but modules are'
       + ' free to use the vast range of web components to make them more interesting.</p>';
     
     d.appendChild(button);
     var labelProviders = document.createElement('span');
     labelProviders.style['font-size'] = '16px';
     labelProviders.style.display = 'block-inline';
     labelProviders.innerHTML = 'Query Providers: ';
     d.appendChild(document.createElement('br'));
     d.appendChild(labelProviders);
     d.appendChild(exhibit);
     parent.appendChild(d);
   };
};
 
