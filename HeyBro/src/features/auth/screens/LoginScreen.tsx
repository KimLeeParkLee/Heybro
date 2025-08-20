import React, { useEffect, useState } from 'react';
import { Alert, Platform } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import {
  GoogleSignin,
  statusCodes,
  isErrorWithCode,
} from '@react-native-google-signin/google-signin';
import { login as kakaoLogin } from '@react-native-seoul/kakao-login';
import { View, Text, Button } from '../../../shared/components/ui';
import { TextInput } from '../../../shared/components/ui/TextInput';
import { useAuth } from '../hooks/useAuth';
import { HttpError } from '../../../shared/api/http';
import { AuthStackParamList } from '../../../app/navigation/RootNavigator';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

type LoginScreenNavigationProp = NativeStackNavigationProp<
  AuthStackParamList,
  'Login'
>;

const LoginScreen = () => {
  const { login, loginWithOAuth } = useAuth();
  const navigation = useNavigation<LoginScreenNavigationProp>();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // useEffect(() => {
  //   GoogleSignin.configure({
  //     iosClientId: '8291726260-h6vohm7efc7t5tqcl4on6qlh2c49mjvl.apps.googleusercontent.com',
  //     webClientId: '8291726260-u9mqmn89desca5pvitjobktseqq8hlah.apps.googleusercontent.com',
  //     offlineAccess: true,  
  //     scopes: ['email'],
  //   });
  // }, []);

  const handleEmailPasswordLogin = async () => {
    if (!email || !password) {
      Alert.alert('입력 오류', '이메일과 비밀번호를 모두 입력해주세요.');
      return;
    }
    setIsLoading(true);
    try {
      await login({ email, password });
      // Successful login will automatically switch navigators via session state change
    } catch (error) {
      const errorMessage =
        error instanceof HttpError
          ? error.message
          : '로그인 중 오류가 발생했습니다.';
      Alert.alert('로그인 실패', errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNavigateToSignup = () => {
    navigation.navigate('Register'); // 'Register' 스크린으로 이동
  };

  const handleGoogleLogin = async () => {
    setIsLoading(true);
    try {
      if (Platform.OS === 'android') {
        await GoogleSignin.hasPlayServices({ showPlayServicesUpdateDialog: true });
      }
      const response = await GoogleSignin.signIn();
      const serverAuthCode = response.data?.serverAuthCode?.trim();

      if (!serverAuthCode) {
        throw new Error('Google authorization code not found');
      }

      const result = await loginWithOAuth({
        provider: 'google',
        oauth_token: serverAuthCode,
      });
      if (result?.is_new_user) {
        navigation.navigate('Register', {
          email: result.email,
          provider: result.provider,
        });
      }
    } catch (error: any) {
      if (isErrorWithCode(error)) {
        switch (error.code) {
          case statusCodes.SIGN_IN_CANCELLED:
            console.log('Google sign-in cancelled');
            break;
          case statusCodes.IN_PROGRESS:
            console.log('Google sign-in in progress');
            break;
          case statusCodes.PLAY_SERVICES_NOT_AVAILABLE:
            Alert.alert('업데이트 필요', 'Google Play 서비스가 필요합니다.');
            break;
          default:
            Alert.alert('Google 로그인 실패', '로그인 중 오류가 발생했습니다.');
        }
      } else {
        Alert.alert('Google 로그인 실패', '알 수 없는 오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleKakaoLogin = async () => {
    setIsLoading(true);
    try {
      const token = await kakaoLogin();
      const result = await loginWithOAuth({
        provider: 'kakao',
        oauth_token: token.accessToken.trim(),
      });

      if (result?.is_new_user) {
        // Navigate to Register screen with OAuth data
        navigation.navigate('Register', {
          email: result.email,
          provider: result.provider,
        });
      }
    } catch (error) {
      Alert.alert('카카오 로그인 실패', '로그인 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View className="flex-1 items-center justify-center p-4">
      <Text className="text-2xl font-bold mb-8">로그인</Text>

      <TextInput
        label="이메일"
        value={email}
        onChangeText={setEmail}
        placeholder="email@example.com"
        keyboardType="email-address"
        autoCapitalize="none"
        editable={!isLoading}
      />
      <TextInput
        label="비밀번호"
        value={password}
        onChangeText={setPassword}
        placeholder="비밀번호"
        secureTextEntry
        editable={!isLoading}
      />

      <View className="w-full mt-2 mb-4">
        <Button
          title={isLoading ? '로그인 중...' : '로그인'}
          onPress={handleEmailPasswordLogin}
          disabled={isLoading}
        />
        <View className="mt-2">
          <Button
            title="회원가입"
            onPress={handleNavigateToSignup}
            disabled={isLoading}
          />
        </View>
      </View>

      <View className="w-full mt-8">
        <Button
          title="Google로 로그인"
          onPress={handleGoogleLogin}
          disabled={isLoading}
        />
      </View>
      <View className="w-full mt-4">
        <Button
          title="카카오로 로그인"
          onPress={handleKakaoLogin}
          disabled={isLoading}
        />
      </View>
    </View>
  );
};

export default LoginScreen;
