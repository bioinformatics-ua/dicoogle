import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient();

export function getDICOMFieldList(callback) {
  Dicoogle.request('GET', 'export/list').end((err, resp) => {
    if (err) callback(err);
    else callback(null, resp.text);
  });
}

export function getTransferSettings(callback) {
  Dicoogle.getTransferSyntaxSettings(callback);
}

export function getIndexerSettings(callback) {
  Dicoogle.getIndexerSettings(callback);
}

export function getPatients(freeText, isKeyword, provider, callbackSuccess, callbackError) {
    console.log("getPatients: ", freeText);
    if(freeText.length === 0)
    {
      freeText = "*:*";
      isKeyword = true;
    }

    if (provider === 'all') {
      provider = undefined;
    }

    const searchOptions = {
      keyword: isKeyword,
      provider
    };

    Dicoogle.searchDIM(freeText, searchOptions, (error, data) => {
      if (error) {
        callbackError(error);
      } else {
        callbackSuccess(data);
      }
    });
}

export function unindex(uri, provider, callbackSuccess, callbackError) {
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

export function remove(uri, callbackSuccess, callbackError) {
    console.log("Unindex param: ", uri);

    Dicoogle.remove(uri, function(error) {
      if (error) {
        callbackError(error);
      } else {
        callbackSuccess();
      }
    });
}

export function getImageInfo(uid, callbackSuccess, callbackError) {
    console.log("getImageInfo: ", uid);

    Dicoogle.dump(uid, function(error, data){
      if (error) {
        callbackError(error);
      } else {
        callbackSuccess(data);
      }
    });
}

export function getVersion(callbackSuccess, callbackError) {
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
    console.log("setWatcher:" + state);

    const cb = callback ? callback : () => {};
    Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.WATCHER, state, cb);
}

export function setZip(state, callback) {
    console.log("setZip:" + state);

    const cb = callback ? callback : () => {};
    Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.ZIP, state, cb);
}

export function setSaveT(state, callback) {
    console.log("setSaveThumbnail:" + state);

    const cb = callback ? callback : () => {};
    Dicoogle.setIndexerSettings(Dicoogle.IndexerSettings.INDEX_THUMBNAIL, state, cb);
}

export function saveIndexOptions(path, watcher, zip, thumbnail, effort, thumbnailSize) {
    Dicoogle.setIndexerSettings({
      path, watcher, zip, thumbnail, effort, thumbnailSize
    }, (error) => {
      if (error) console.error('Dicoogle service failure', error);
    });
}

export function forceIndex(uri, providers, callback) {
    Dicoogle.index(uri, providers, (error) => {
      if (error) console.error('Dicoogle service failure', error);
      if (callback) callback(error);
    });
}
