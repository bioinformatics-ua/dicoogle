# dicoogle-client

This is a web service client API to [Dicoogle](http://www.dicoogle.com), the open-source P2P PACS, for use in JavaScript applications. Both browser JavaScript and Node.js are supported.

## How to use

In Node.js, install "dicoogle-client" with `npm` and `require` the "dicoogle-client" module.

```JavaScript
var dicoogleClient = require("dicoogle-client");
```

In a browser, include the "dicoogle-client.js" file in the browser/build folder as a script. The module also supports AMD.

```HTML
<script src='./dicoogle-client.js'></script>
```

Afterwards, invoke the module to obtain an access object. The object may be used multiple times.

```JavaScript
var Dicoogle = dicoogleClient("localhost:8080");

...

Dicoogle.queryFreeText("(PatientName:Pinho^Eduardo)", function(error, result) {
  if (error) {
    console.log(error);
    return;
  }
  // use result
});
```

The repository includes two examples of dicoogle-client for simple querying:

 - "app.js" is a stand-alone Node.js application that outputs the result to the standard output.
 - "app.html" is a browser application that prints the result to the web page.

## Further Notice

This library is using the latest Dicoogle web service API, which is not fully stable.  This client wrapper will be developed as the API matures.

## License

Copyright (C) 2015  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/

Dicoogle/dicoogle-client is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Dicoogle/dicoogle-client is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.

