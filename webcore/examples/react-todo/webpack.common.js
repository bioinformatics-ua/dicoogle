module.exports = {
  entry: './src/index.jsx',
  output: {
    filename: 'module.js',
    path: __dirname,
    libraryTarget: 'commonjs2'
  },
  module: {
    rules: [
      {
        test: /src\/.*\.jsx?$/,
        exclude: /node_modules/,
        use: [
          {
            loader: 'babel-loader',
            options: {
              presets: [["@babel/env",{"targets":{"browsers":["> 1%","last 2 versions"]}}],"@babel/react"]
            }
          }
        ]
      }
    ]
  },
  externals: [
    'react', 'react-dom', 'dicoogle-client', 'dicoole-webcore', 'reflux', 'react-bootstrap',
    'react-bootstrap-table', 'react-imageloader', 'react-router', 'react-router-bootstrap'
  ]
};
