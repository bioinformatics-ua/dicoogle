import {Endpoints} from '../constants/endpoints';
import $ from 'jquery';
import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient(); // already configured, retrieve object

/** @deprecated Please use Dicoogle#request instead. */
export function request(url, callbackSuccess, callbackError){
    console.log("request: " + url);
    $.ajax({
      url: url,
      dataType: 'json',
      success: callbackSuccess,
      error: function(xhr, status, err) {
        callbackError(xhr);
      }
    });
}

export function getPatients(freetext, isKeyword, provider, callbackSucccess, callbackError){
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
      url = url + "&provider=" + provider;
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

export function unindex(uri, provider, callbackSuccess, callbackError){
    console.log("Unindex param: ", uri);

    if(provider === 'all') {
      provider = undefined;
    }

    Dicoogle.unindex(uri, provider, function(error) {
      if (error) {
        callbackError(error);
      } else {
        callbackSuccess();
      }
    });
}

export function remove(uri, callbackSucccess, callbackError){
    console.log("Unindex param: ", uri);

    Dicoogle.remove(uri, function(error) {
      if (error) {
        callbackError(error);
      } else {
        callbackSucccess();
      }
    });
}

export function getImageInfo(uid, callbackSucccess, callbackError){
  console.log("getImageInfo: ", uid);

  Dicoogle.dump(uid, function(error, data){
    if (error) {
      callbackError(error);
    } else {
      callbackSucccess(data);
    }
  });
}

export function getVersion(callbackSucccess, callbackError){

  Dicoogle.getVersion(function(error, data) {
    if (error) {
      callbackError(error);
    } else {
      callbackSucccess(data);
    }
  });
}

/*
INDEXER
*/
export function setWatcher(state){
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
export function setZip(state){
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

export function setSaveT(state){
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

export function saveIndexOptions(path, watcher, zip, saveThumbnail, effort, thumbnailSize){
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

export function forceIndex(uri, callback){
  //console.log(state);
  Dicoogle.index(uri, function(error, data) {
    console.log("Status:", status);
    if (callback) callback(error, data);
  });
}
