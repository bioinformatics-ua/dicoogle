import {Endpoints} from '../constants/endpoints';
function getPatients(freetext, isKeyword,provider,callbackSucccess, callbackError){
        console.log("store param: ", freetext);

        //'http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene'
        if(freetext.length ==0)
        {
          freetext = "*:*";
          isKeyword = true;
        }

        var url = Endpoints.base + '/searchDIM?query='+freetext+'&keyword='+isKeyword;
        if(provider != "all")
        {
          url = url + "&provider=" + provider;
        }
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

function request(url, callbackSucccess, callbackError){
    console.log("request: "+url);
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

/*
INDEXER
*/
function setWatcher(state){
  console.log(state);
  $.post(Endpoints.base + "/management/settings/index/watcher",
  {
    watcher: state
  },
    function(data, status){
      //Response
      console.log("Data: " + data + "\nStatus: " + status);
    });

}
function setZip(state){
  console.log(state);
  $.post(Endpoints.base + "/management/settings/index/zip",
  {
    zip: state
  },
    function(data, status){
      //Response
      console.log("Data: " + data + "\nStatus: " + status);
    });

}
function setSaveT(state){
  console.log(state);
  $.post(Endpoints.base + "/management/settings/index/thumbnail",
  {
    thumbnail: state
  },
    function(data, status){
      //Response
      console.log("Data: " + data + "\nStatus: " + status);
    });

}

function saveIndexOptions(path, watcher, zip, saveThumbnail,effort,thumbnailSize){
  //console.log(state);
  $.post(Endpoints.base + "/management/settings/index",
  {
    path: path,
    watcher: watcher,
    zip: zip,
    saveThumbnail: saveThumbnail,
    effort: effort,
    thumbnailSize: thumbnailSize
  },
    function(data, status){
      //Response
      console.log("Data: " + data + "\nStatus: " + status);
    });

}

function forceIndex(uri){
  //console.log(state);
  $.post(Endpoints.base + "/management/tasks/index",
  {
    uri: uri
  },
    function(data, status){
      //Response
      console.log("Status:", status);
    });

}

export {getPatients, getImageInfo, request, setWatcher,setZip,setSaveT,saveIndexOptions,forceIndex};
