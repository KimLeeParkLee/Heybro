import React from 'react';
import { ScrollView, StyleSheet } from 'react-native';
import { UserInfo } from '../components/UserInfo';
import { TodayRoutine } from '../components/TodayRoutine';
import { AchievementCalendar } from '../components/AchievementCalendar';
import { PopularPosts } from '../components/PopularPosts';

const HomeScreen = () => {
  return (
    <ScrollView style={styles.container}>
      <UserInfo />
      <TodayRoutine />
      <AchievementCalendar />
      <PopularPosts />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB', // 전체 배경색
  },
});

export default HomeScreen;
