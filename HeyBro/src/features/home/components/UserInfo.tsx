import React from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import { useAuth } from '../../auth/hooks/useAuth';

export const UserInfo = () => {
  const { user } = useAuth();

  // Placeholder for the quote of the day
  const quote = '브로! 건강한 하루는 작은 습관에서 시작된다!';

  return (
    <View style={styles.container}>
      {/* Left side: Image */}
      <View style={styles.imageContainer}>
        {/* This will be replaced with a real image from assets later */}
        <View style={styles.imagePlaceholder} />
      </View>

      {/* Right side: User Details */}
      <View style={styles.detailsContainer}>
        <Text style={styles.nickname}>{user?.nickname || '사용자'}님, 안녕하세요!</Text>
        
        <View style={styles.statsContainer}>
          <Text style={styles.statsText}>Lv. {user?.bro_level || '?'}</Text>
          <Text style={styles.statsText}> | </Text>
          <Text style={styles.statsText}>{user?.bro_point || 0} P</Text>
        </View>

        <View style={styles.quoteContainer}>
          <Text style={styles.quoteText}>{quote}</Text>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    backgroundColor: '#E0F2FE',
    marginHorizontal: 16,
    marginTop: 16,
    padding: 16,
    borderRadius: 12,
    minHeight: 150,
    alignItems: 'center',
  },
  imageContainer: {
    marginRight: 16,
  },
  imagePlaceholder: {
    width: 80,
    height: 80,
    borderRadius: 40, // Make it a circle
    backgroundColor: '#A5B4FC', // Placeholder color
    justifyContent: 'center',
    alignItems: 'center',
  },
  detailsContainer: {
    flex: 1,
    justifyContent: 'center',
  },
  nickname: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  statsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  statsText: {
    fontSize: 16,
    color: '#374151',
  },
  quoteContainer: {
    padding: 10,
    backgroundColor: 'rgba(255, 255, 255, 0.5)',
    borderRadius: 8,
  },
  quoteText: {
    fontSize: 14,
    fontStyle: 'italic',
    color: '#1F2937',
  },
});
