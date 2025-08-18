import React from 'react';
import { Pressable, PressableProps, Text } from 'react-native';

interface ButtonProps extends PressableProps {
  title: string;
}

export const Button = ({ title, ...props }: ButtonProps) => {
  return (
    <Pressable
      className="bg-blue-500 active:bg-blue-600 py-3 px-4 rounded-md items-center justify-center"
      {...props}
    >
      <Text className="text-white font-bold">{title}</Text>
    </Pressable>
  );
};