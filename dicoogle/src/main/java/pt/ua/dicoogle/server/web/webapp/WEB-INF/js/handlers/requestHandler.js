import {Endpoints} from '../constants/endpoints';
function getPatients(freetext, isKeyword,callbackSucccess, callbackError){
        console.log("store param: ", freetext);

        //'http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene'
        if(freetext.length ==0)
        {
          freetext = "*:*";
          isKeyword = true;
        }

        var url = Endpoints.base + '/searchDIM?query='+freetext+'&keyword='+isKeyword+'&provicer=lucene';
        console.log("store url;",url);

        $.ajax({

          url: url,
          dataType: 'json',
          success: function(data) {

        	callbackSucccess(data);

          },
          error: function(xhr, status, err) {
            callbackError(xhr);
          }
        });
}

function getImageInfo(uid, callbackSucccess, callbackError){
        console.log("getImageInfo: ", uid);

        //'http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene'
        var url = Endpoints.base + '/dump?uid='+uid;
        //console.log("store url;",url);

        $.ajax({

          url: url,
          dataType: 'json',
          success: function(data) {

          callbackSucccess(data);

          },
          error: function(xhr, status, err) {
            callbackError(xhr);
          }
        });
}



export {getPatients, getImageInfo};
