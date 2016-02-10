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

export function getPatients(freetext, isKeyword, provider, callbackSuccess, callbackError){
    console.log("getPatients: ", freetext);
    if(freetext.length === 0)
    {
      freetext = "*:*";
      isKeyword = true;
    }
    if (provider === 'all') { // FIXME function should not accept 'all' in the first place
      provider = undefined;
    }
    const searchOpt = {
      dim: true,
      keyword: isKeyword,
      provider
    };
    Dicoogle.search(freetext, searchOpt, (error, outcome) => {
      if (error) {
        callbackError(error);
      } else {
        callbackSuccess(outcome);
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

export function remove(uri, callbackSuccess, callbackError){
    console.log("Unindex param: ", uri);

    Dicoogle.remove(uri, function(error) {
      if (error) {
        callbackError(error);
      } else {
        callbackSuccess();
      }
    });
}

export function getImageInfo(uid, callbackSuccess, callbackError){
  console.log("getImageInfo: ", uid);

  Dicoogle.dump(uid, function(error, data){
    if (error) {
      callbackError(error);
    } else {
      callbackSuccess(data);
    }
  });
}

export function getVersion(callbackSuccess, callbackError){

  Dicoogle.getVersion(function(error, data) {
    if (error) {
      callbackError(error);
    } else {
      callbackSuccess(data);
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
