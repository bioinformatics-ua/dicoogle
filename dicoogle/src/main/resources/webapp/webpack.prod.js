/* eslint-env node */

const merge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const common = require("./webpack.common");
module.exports = merge(common, {
  mode: "production",
  devtool: "source-map",
  output: {
    filename: "dist/bundle.min.js",
    path: __dirname
  },
  plugins: [
    new HtmlWebpackPlugin({
      filename: "index.html",
      template: "./index-template.html",
      chunks: ["bundle"],
      chunksSortMode: "manual",
      minify: {
        removeAttributeQuotes: true,
        collapseWhitespace: true,
        html5: true,
        minifyCSS: true,
        minifyJS: true,
        minifyURLs: true,
        removeComments: true,
        removeEmptyAttributes: true
      },
      hash: false
    })
  ]
});
