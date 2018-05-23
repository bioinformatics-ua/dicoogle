/** Split a duration into human-readable parts.
 *
 * @param {number} time a number representing the number of milliseconds
 * @return {array[]} an array representing the parts of the given duration,
 * in least significant order: ms, s, m, h, d. The returned array will have
 * a shorter length if the remaining parts are 0 (but the array will always
 * have at least one element).
 */
export function splitTime(time) {
  const o = [];
  o.push(time % 1000); // ms
  time = (time / 1000) | 0;
  if (time === 0) return o;

  o.push(time % 60); // s
  time = (time / 60) | 0;
  if (time === 0) return o;

  o.push(time % 60); // m
  time = (time / 60) | 0;
  if (time === 0) return o;

  o.push(time % 24); // h
  time = (time / 24) | 0;
  if (time === 0) return o;

  o.push(time); // d
  return o;
}

/** Convert a duration into human-readable text
 * @param {number} timeValue a number representing the number of milliseconds
 * @return {string} a text representing the duration. Example: "2h 17m 20s 837ms"
 */
export function toHumanReadable(timeValue) {
  const suffix = ["ms", "s", "m", "h", "d"];

  const parts = splitTime(timeValue);

  return parts
    .map((v, i) => v + suffix[i])
    .reduceRight((pval, cval) => pval + " " + cval);
}
