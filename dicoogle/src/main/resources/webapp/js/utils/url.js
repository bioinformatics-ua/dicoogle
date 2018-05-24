function getUrlVars() {
  const hashes = window.location.href
    .slice(window.location.href.indexOf("?") + 1)
    .split("&");
  let vars = [];
  let hash;
  for (var i = 0; i < hashes.length; i++) {
    hash = hashes[i].split("=");
    vars.push(hash[0]);
    vars[hash[0]] = hash[1];
  }
  return vars;
}

export { getUrlVars };
