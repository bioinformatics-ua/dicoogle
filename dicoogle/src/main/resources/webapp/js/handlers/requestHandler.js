import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient(); // already configured, retrieve object

export {Dicoogle};

export function getDICOMFieldList(callback) {
    Dicoogle.request('GET', 'export/list').end((err, resp) => {
      if (err) callback(err);
      else callback(null, resp.body);
    });
}

export function getTransferSettings(callback) {
  Dicoogle.getTransferSyntaxSettings(callback);
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
      keyword: isKeyword,
      provider
    };
    Dicoogle.searchDIM(freetext, searchOpt, (error, outcome) => {
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

export function setSaveT(state, callback) {
  const cb = callback ? callback : () => {};
  Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.INDEX_THUMBNAIL, state, cb);
}

export function saveIndexOptions(path, watcher, zip, saveThumbnail, effort, thumbnailSize){
  Dicoogle.setIndexerSettings({
    path, watcher, zip, saveThumbnail, effort, thumbnailSize
  }, (error) => {
    if (error) {
      console.error('Dicoogle service failure', error);
    }
  });
}

export function forceIndex(uri, callback){
  //console.log(state);
  Dicoogle.index(uri, function(error, data) {
    console.log("Status:", status);
    if (callback) callback(error, data);
  });
}
