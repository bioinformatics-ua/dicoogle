const babelParser = require("@babel/eslint-parser");
const globals = require("globals");
const { configs } = require("@eslint/js");
const reactPlugin = require("eslint-plugin-react");
const importPlugin = require("eslint-plugin-import");

module.exports = [
  configs.recommended,
  {
    languageOptions: {
      parser: babelParser,
      parserOptions: {
        ecmaVersion: 2017,
        sourceType: "module",
        ecmaFeatures: {
          modules: true,
          jsx: true
        }
      },

      globals: {
        ...globals.browser,
        ...globals.node,
        process: false
      }
    },
    plugins: {
      react: reactPlugin,
      import: importPlugin
    },
    settings: {
      "import/resolver": {
        node: {
          extensions: [".js", ".jsx"]
        }
      },
      react: {
        pragma: "React",
        version: "15"
      }
    },
    rules: {
      "no-console": 0,
      quotes: 0,
      camelcase: 0,
      curly: 0,
      "new-cap": 0,
      "no-trailing-spaces": 0,
      "comma-spacing": 0,
      "no-mixed-spaces-and-tabs": 0,
      "key-spacing": 0,
      "space-infix-ops": 0,
      "no-multi-spaces": 0,
      "comma-dangle": 0,
      "semi-spacing": 0,
      eqeqeq: 1,
      "no-alert": 1,
      "no-unused-vars": [
        1,
        {
          vars: "all",
          args: "none",
          varsIgnorePattern: "^_",
          argsIgnorePattern: "^_",
          ignoreRestSiblings: true
        }
      ],
      "no-extra-semi": 1,
      "eol-last": 1,
      "no-empty": 1,
      "no-dupe-args": 2,
      "no-dupe-keys": 2,
      "no-undef": 2,
      "import/no-unresolved": [2, { commonjs: true }],
      "import/named": 2,
      "import/namespace": 2,
      "import/default": 2,
      "import/export": 2,
      "import/no-extraneous-dependencies": 1,
      "import/no-mutable-exports": 1,
      "react/no-deprecated": 1,
      "react/no-did-mount-set-state": 1,
      "react/no-did-update-set-state": 1,
      "react/jsx-boolean-value": 1,
      "react/jsx-no-duplicate-props": 2,
      "react/jsx-no-undef": 2,
      "react/jsx-uses-react": 2,
      "react/jsx-uses-vars": 2,
      "react/no-danger": 2,
      "react/no-direct-mutation-state": 2,
      "react/no-unknown-property": 2,
      "react/react-in-jsx-scope": 2,
      "react/no-is-mounted": 2
    }
  }
];
