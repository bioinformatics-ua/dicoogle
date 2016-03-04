import {Endpoints} from '../constants/endpoints';
import $ from 'jquery';
import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient(); // already configured, retrieve object

export function getDICOMFieldList(callback) {
    Dicoogle.request('GET', 'export/list', callback);
}

export function getTransferSettings(callback) {
  Dicoogle.request('GET', 'management/settings/transfer', callback);
}

export function getIndexerSettings(callback) {
  Dicoogle.getIndexerSettings(callback);
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
export function setWatcher(state, callback) {
  const cb = callback ? callback : () => {};
  Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.WATCHER, state, cb);
}
export function setZip(state, callback) {
  const cb = callback ? callback : () => {};
  Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.ZIP, state, cb);
}

export function setSaveT(state) {
  const cb = callback ? callback : () => {};
  Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.INDEX_THUMBNAIL, state, cb);
}

export function saveIndexOptions(path, watcher, zip, saveThumbnail, effort, thumbnailSize){
  // TODO use Dicoogle in the future
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
