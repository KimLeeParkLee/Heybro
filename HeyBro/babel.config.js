module.exports = function(api) {
  api.cache(true);
  return {
    presets: [
      'module:@react-native/babel-preset',
      'nativewind/babel', 
    ],
    plugins: [ 
      [
        'module-resolver',
        {
          root: ['./src'],
          alias: {
            '@app': './src/app',
            '@shared': './src/shared',
            '@features': './src/features',
          },
        },
      ],
      ['react-native-worklets/plugin', {}, 'rn-worklets'],

      ['react-native-reanimated/plugin', {}, 'rn-reanimated'],
    ],
  };
};
