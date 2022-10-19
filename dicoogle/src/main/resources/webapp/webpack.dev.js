/* eslint-env node */

const path = require('path');
const {merge} = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");

process.env.NODE_ENV = "development";
const common = require("./webpack.common");
module.exports = merge(common, {
  mode: "development",
  devtool: "eval-source-map",
  output: {
    filename: "dist/bundle.js",
    path: __dirname,
  },
  devServer: {
    static: [
      {
        directory: path.join(__dirname, 'assets'),
        publicPath: '/assets',
      },
      {
        directory: path.join(__dirname, 'bootstrap'),
        publicPath: '/bootstrap',
      },
      {
        directory: path.join(__dirname, 'fonts'),
        publicPath: '/fonts',
      },
      {
        directory: path.join(__dirname, 'css'),
        publicPath: '/css',
      },
    ],
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
