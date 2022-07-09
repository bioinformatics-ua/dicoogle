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
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: [
          {
            loader: 'babel-loader',
            options: {
              presets: [
                "@babel/preset-react",
                ["@babel/env",{"targets":{"browsers":["> 1%","last 2 versions"]}}]
              ]
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
