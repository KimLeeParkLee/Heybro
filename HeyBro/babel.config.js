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
      'react-native-worklets/plugin',
      // 항상 마지막
      'react-native-reanimated/plugin',
    ],
  };
};
