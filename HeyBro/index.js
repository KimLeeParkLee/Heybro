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
  webClientId: '8291726260-u9mqmn89desca5pvitjobktseqq8hlah.apps.googleusercontent.com',
  offlineAccess: true,  
  scopes: ['email'],
});

AppRegistry.registerComponent(appName, () => App);
