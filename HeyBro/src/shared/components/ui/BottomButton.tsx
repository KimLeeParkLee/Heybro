import React from 'react';
import { TouchableOpacity, Text, StyleSheet, View } from 'react-native';

interface BottomButtonProps {
  title: string;
  onPress: () => void;
  disabled?: boolean;
}

const BottomButton = ({ title, onPress, disabled = false }: BottomButtonProps) => {
  return (
    <View style={styles.container}>
      <TouchableOpacity
        onPress={onPress}
        disabled={disabled}
        style={[styles.button, disabled ? styles.disabledButton : styles.enabledButton]}
      >
        <Text style={styles.buttonText}>{title}</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    paddingHorizontal: 16,
    paddingBottom: 34, // For iPhone X and newer
    paddingTop: 10,
    backgroundColor: 'white',
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
  },
  button: {
    width: '100%',
    paddingVertical: 16,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
  enabledButton: {
    backgroundColor: '#3B82F6',
  },
  disabledButton: {
    backgroundColor: '#9CA3AF',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export { BottomButton };
