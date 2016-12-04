/** Dicoogle DIM query request application in Node.js
 *
 * Usage:
 * node app.js [-s server_location] QUERY
 *
 * @author Eduardo Pinho (eduardopinho@ua.pt)
 */

var server = null;
var query;
var keyword = false;

for (var i = 2 ; i < process.argv.length ; i++) {
  if (process.argv[i] === '--keyword' || process.argv[i] === '-k') {
    keyword = true;
  } else if (process.argv[i] === '-s') {
    server = process.argv[++i];
  } else {
    query = process.argv[i];
  }
}

if (!query) {
  console.log("Usage:\n\tnode app.js [-k] [-s server_location] QUERY");
  process.exit(-1);
}

console.log("Querying:", query);
var dicoogleClient = require("./node/dicoogle-client");

var Dicoogle = dicoogleClient(server);
var queryFn = keyword ? Dicoogle.queryAdvanced : Dicoogle.queryFreeText;
queryFn(query, 
  function cb(error, result) {
    if (error) { console.log(error); }
    else console.log(JSON.stringify(result));
  });
