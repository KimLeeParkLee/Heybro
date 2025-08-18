/**
 * @format
 */

import './src/global.css';
import { AppRegistry } from 'react-native';
import { GoogleSignin } from '@react-native-google-signin/google-signin';
import App from './App';
import { name as appName } from './app.json';

// --- Google Sign-In Configuration ---

GoogleSignin.configure({

  iosClientId: '8291726260-h6vohm7efc7t5tqcl4on6qlh2c49mjvl.apps.googleusercontent.com',
});

AppRegistry.registerComponent(appName, () => App);
