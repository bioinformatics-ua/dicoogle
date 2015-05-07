/* simple-query.js - Simple query module
 */
  
module.exports = function() {
  var input = document.createElement('input');
  var button = document.createElement('input');
  var chkKeyword = document.createElement('input'); 
  var lblKeyword = document.createElement('label');

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

  lblKeyword.appendChild(chkKeyword);
  lblKeyword.appendChild(document.createTextNode("keyword"));
 
  button.type = 'button';
  button.value = 'Search';
  button.onclick = onClick;
  
  this.render = function(parent) {
     var d = document.createElement('div');
     d.appendChild(input);
     d.appendChild(lblKeyword);
     d.appendChild(button);
     parent.appendChild(d);
   };
};
