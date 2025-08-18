import React, { forwardRef, memo } from 'react';
import {
  TextInput as RNTextInput,
  TextInputProps,
  View,
  Text,
  ViewStyle,
  StyleProp,
} from 'react-native';

interface Props extends TextInputProps {
  label?: string;
  error?: string;
  containerStyle?: StyleProp<ViewStyle>;
  /** NativeWind용 */
  containerClassName?: string;
  inputClassName?: string;
}

const _TextInput = forwardRef<RNTextInput, Props>(
  (
    {
      label,
      error,
      containerStyle,
      containerClassName,
      inputClassName,
      // 텍스트 입력 기본값들(원하면 삭제 가능)
      autoCorrect = false,
      enablesReturnKeyAutomatically = true,
      ...props
    },
    ref
  ) => {
    return (
      <View style={containerStyle} className={`w-full mb-4 ${containerClassName ?? ''}`}>
        {label ? <Text className="text-gray-700 mb-1">{label}</Text> : null}

        <RNTextInput
          ref={ref}
          className={`w-full border ${
            error ? 'border-red-500' : 'border-gray-300'
          } rounded-md p-3 bg-white ${inputClassName ?? ''}`}
          placeholderTextColor="#9CA3AF"
          autoCorrect={autoCorrect}
          enablesReturnKeyAutomatically={enablesReturnKeyAutomatically}
          {...props}
        />

        {error ? <Text className="text-red-500 text-sm mt-1">{error}</Text> : null}
      </View>
    );
  }
);

_TextInput.displayName = 'TextInput';

export const TextInput = memo(_TextInput);
