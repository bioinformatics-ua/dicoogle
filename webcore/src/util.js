
/** Create a new merged object.
 * @param {...object} var_args the objects to merge
 * @return {object} a new object with all owned properties of the arguments
 */
export function merge() {
    let out = {};
    for (let i = 0; i < arguments.length; i++) {
        Object.assign(out, arguments[i]);
    }
}

/** Camelize a plugin name (only the first word is not capitalized).
 * @param {string} name the name of the plugin
 * @return {string} the camelized version of the name
 */
export function camelize(name) {
    let words = name.split('-');
    if (words.length === 0) return '';
    let t = words[0];
    for (let i = 1; i < words.length; i++) {
        if (words[i].length !== 0) {
            t += words[i][0].toUpperCase() + words[i].substring(1);
        }
    }
    return t;
}
