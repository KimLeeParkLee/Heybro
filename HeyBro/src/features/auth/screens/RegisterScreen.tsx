import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Switch, Alert } from 'react-native';
import { Header } from '../../../shared/components/layout/Header';
import { BottomButton } from '../../../shared/components/ui/BottomButton';
import { TextInput } from '../../../shared/components/ui/TextInput';
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { AuthStackParamList } from '../../../app/navigation/RootNavigator';
import { useAuth } from '../hooks/useAuth';
import { RegisterCredentials } from '../types/auth.types';
import { SafeAreaView } from 'react-native-safe-area-context';

type RegisterScreenRouteProp = RouteProp<AuthStackParamList, 'Register'>;

const RegisterScreen = () => {
  const navigation = useNavigation<NativeStackNavigationProp<AuthStackParamList>>();
  const route = useRoute<RegisterScreenRouteProp>();
  const { registerAndLogin, completeOAuthRegistration } = useAuth();

  // Route params for OAuth
  const oauthParams = route.params;
  const isOAuth = !!oauthParams?.provider;

  const [step, setStep] = useState(1);
  const [isLoading, setIsLoading] = useState(false);

  // Step 1: Email and Password
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');

  // Step 2: User Information
  const [name, setName] = useState('');
  const [birthDate, setBirthDate] = useState('');
  const [gender, setGender] = useState<'male' | 'female'>('male');
  const [phone, setPhone] = useState('');
  const [agreePrivacy, setAgreePrivacy] = useState(false);
  const [agreeMarketing, setAgreeMarketing] = useState(false);

  // Step 3: Nickname
  const [nickname, setNickname] = useState('');

  useEffect(() => {
    if (oauthParams?.email) {
      setEmail(oauthParams.email);
    }
  }, [oauthParams]);

  const handleNextStep = () => {
    setStep(prev => prev + 1);
  };

  const handleRegister = async () => {
    setIsLoading(true);
    try {
      if (isOAuth && oauthParams) {
        // OAuth registration completion
        await completeOAuthRegistration(
          {
            user_name: name,
            nickname,
            gender,
            birth_date: birthDate,
            phone,
            privacy_consent: agreePrivacy,
            marketing_consent: agreeMarketing,
            notification_enabled: true,
          }
        );
      } else {
        // Standard email/password registration
        const credentials: RegisterCredentials = {
          email,
          password,
          user_name: name,
          nickname,
          gender,
          birth_date: birthDate,
          phone,
          privacy_consent: agreePrivacy,
          marketing_consent: agreeMarketing,
          notification_enabled: true,
        };
        await registerAndLogin(credentials);
      }
      // On success, RootNavigator will handle the navigation to Onboarding
    } catch (error) {
      console.error('Registration failed:', error);
      Alert.alert('회원가입 실패', '오류가 발생했습니다. 다시 시도해주세요.');
    } finally {
      setIsLoading(false);
    }
  };

  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <>
            <Text style={styles.title}>
              {isOAuth ? '이메일 확인' : '아이디와 비밀번호를 입력해주세요'}
            </Text>
            <TextInput
              label="이메일"
              value={email}
              onChangeText={setEmail}
              keyboardType="email-address"
              autoCapitalize="none"
              textContentType="emailAddress"
              autoComplete="email"
              editable={!isOAuth} // Disable if OAuth
            />
            {!isOAuth && (
              <>
                <TextInput
                  label="비밀번호"
                  value={password}
                  onChangeText={setPassword}
                  secureTextEntry
                  textContentType="newPassword"
                  autoComplete="password-new"
                />
                <TextInput
                  label="비밀번호 확인"
                  value={passwordConfirm}
                  onChangeText={setPasswordConfirm}
                  secureTextEntry
                  textContentType="newPassword"
                  autoComplete="password-new"
                />
              </>
            )}
          </>
        );
      case 2:
        return (
          <>
            <Text style={styles.title}>간단한 정보를 알려주세요</Text>
            <TextInput label="이름" value={name} onChangeText={setName} />
            <TextInput
              label="생년월일"
              value={birthDate}
              onChangeText={setBirthDate}
              placeholder="YYYY-MM-DD"
            />
            <View style={styles.toggleContainer}>
              <Text>성별:</Text>
              <Text
                style={gender === 'male' ? styles.activeGender : styles.inactiveGender}
                onPress={() => setGender('male')}>
                남
              </Text>
              <Text> / </Text>
              <Text
                style={gender === 'female' ? styles.activeGender : styles.inactiveGender}
                onPress={() => setGender('female')}>
                여
              </Text>
            </View>
            <TextInput
              label="휴대폰 번호"
              value={phone}
              onChangeText={setPhone}
              keyboardType="phone-pad"
            />
            <View style={styles.checkboxContainer}>
              <Switch value={agreePrivacy} onValueChange={setAgreePrivacy} />
              <Text>개인정보동의여부(필수)</Text>
            </View>
            <View style={styles.checkboxContainer}>
              <Switch value={agreeMarketing} onValueChange={setAgreeMarketing} />
              <Text>마케팅 동의여부(선택)</Text>
            </View>
          </>
        );
      case 3:
        return (
          <>
            <Text style={styles.title}>닉네임을 입력해주세요</Text>
            <TextInput label="닉네임" value={nickname} onChangeText={setNickname} />
          </>
        );
      default:
        return null;
    }
  };

  const isStepValid = () => {
    switch (step) {
      case 1:
        if (isOAuth) return email.length > 0;
        return email.length > 0 && password.length > 0 && password === passwordConfirm;
      case 2:
        return name.length > 0 && birthDate.length > 0 && phone.length > 0 && agreePrivacy;
      case 3:
        return nickname.length > 0;
      default:
        return false;
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <Header title="회원가입" />
      <View style={styles.content}>{renderStepContent()}</View>
      <BottomButton
        title={step === 3 ? '회원가입 완료' : '다음'}
        onPress={step === 3 ? handleRegister : handleNextStep}
        disabled={!isStepValid() || isLoading}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  content: {
    flex: 1,
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 24,
  },
  toggleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  activeGender: {
    fontWeight: 'bold',
    color: '#3B82F6',
  },
  inactiveGender: {
    color: '#6B7280',
  },
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
});

export default RegisterScreen;
