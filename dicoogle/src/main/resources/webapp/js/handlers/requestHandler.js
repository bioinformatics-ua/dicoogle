import {Endpoints} from '../constants/endpoints';
import $ from 'jquery';

function getPatients(freetext, isKeyword, provider, callbackSucccess, callbackError){
        console.log("store param: ", freetext);
        // ??? use dicoogle client?

        //'http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene'
        if(freetext.length === 0)
        {
          freetext = "*:*";
          isKeyword = true;
        }

        var url = Endpoints.base + '/searchDIM?query=' + freetext + '&keyword=' + isKeyword;
        if(provider !== "all")
        {
          provider = Array.prototype.concat.apply([], provider);
          for (const p of provider) {
            url += "&provider=" + p;
          }
        }
        console.log("store url;", url);

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

function unindex(uri, provider, callbackSuccess, callbackError){
    console.log("Unindex param: ", uri);

    var url = Endpoints.base + '/management/tasks/unindex';
    var data = {'uri': uri}
    if(provider !== 'all')
      data['provider'] = provider;

    // TODO use dicoogle client
    $.ajax({
      url: url,
      data: data,
      method: 'post',
      traditional: true,
      success: callbackSuccess,
      error: function(xhr, status, err) {
        callbackError(xhr);
      }
    });
}

function remove(uri, callbackSucccess, callbackError){
    console.log("Unindex param: ", uri);

    var url = Endpoints.base + '/management/tasks/remove';
    var data = {'uri': uri}

    // TODO use dicoogle client
    $.ajax({
      url: url,
      data: data,
      method: 'post',
      traditional: true,
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
        var url = Endpoints.base + '/dump?uid=' + uid;

        // TODO use dicoogle client
        $.ajax({
          url: url,
          method: 'get',
          dataType: 'json',
          success: function(data) {
            callbackSucccess(data);
          },
          error: function(xhr, status, err) {
            callbackError(xhr);
          }
        });
}

function getVersion(callbackSucccess, callbackError){

    var url = Endpoints.base + '/ext/version';
    // TODO use dicoogle client
    $.ajax({
        url: url,
        method: 'get',
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
    console.log("request: " + url);
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

function saveIndexOptions(path, watcher, zip, saveThumbnail, effort, thumbnailSize){
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

function forceIndex(uri, plugin){
  //console.log(state);
  // TODO use dicoogle client
  $.post(Endpoints.base + "/management/tasks/index",
  {
    uri: uri,
    plugin: plugin
  },
  function(data, status){
    //Response
    console.log("Status:", status);
  });
}

export {
  getPatients, unindex, remove, getImageInfo, request, setWatcher,
  setZip, setSaveT, saveIndexOptions, forceIndex, getVersion};
