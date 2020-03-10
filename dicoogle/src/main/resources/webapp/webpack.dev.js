/* eslint-env node */

const merge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const common = require("./webpack.common");
module.exports = merge(common, {
  mode: "development",
  devtool: "eval-source-map",
  output: {
    filename: "dist/bundle.js",
    path: __dirname
  },
  plugins: [
    new HtmlWebpackPlugin({
      filename: "index.html",
      template: "./index-template.html",
      chunks: ["bundle"],
      chunksSortMode: "manual",
      minify: {
        removeAttributeQuotes: false,
        collapseWhitespace: false,
        html5: false,
        minifyCSS: false,
        minifyURLs: false,
        removeComments: false,
        removeEmptyAttributes: false
      },
      hash: true
    })
  ]
});
