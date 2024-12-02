/* eslint-env node */

const path = require("path");
const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
  entry: {
    bundle: [
      path.resolve(__dirname, "js/app.js"),
      path.resolve(__dirname, "js/external-requires.js")
    ]
  },
  node: {
    global: false
  },
  plugins: [
    new webpack.EnvironmentPlugin({
      'NODE_ENV': 'development',
      'DICOOGLE_BASE_URL': '',
      'GUEST_USERNAME': '',
      'GUEST_PASSWORD': '',
    }),
    new webpack.DefinePlugin({
        'global': 'window',
    }),
    new MiniCssExtractPlugin({
      filename: "dist/[name].css",
      chunkFilename: "dist/[id].css"
    }),
    new webpack.ProvidePlugin({
      // required for Bootstrap to work
      jQuery: "jquery"
    })
  ],
  module: {
    rules: [
      {
        test: /\.jsx?$/i,
        include: path.resolve(__dirname, "js"),
        use: ["babel-loader"]
      },
      {
        test: /\.s[ac]ss$/i,
        include: path.resolve(__dirname, "sass"),
        use: [
          // Loads CSS and minifies it
          MiniCssExtractPlugin.loader,
          // Translates CSS into CommonJS
          "css-loader",
          // Compiles Sass to CSS
          "sass-loader"
        ]
      }
    ]
  },
  resolve: {
    alias: {
      // force webcore/dicoogle-client to resolve to the same as the webapp's
      'dicoogle-client': __dirname + '/node_modules/dicoogle-client'
    },
    extensions: [".js", ".jsx", ".json"]
  }
};
