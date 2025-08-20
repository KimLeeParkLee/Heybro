import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ActivityIndicator } from 'react-native';
import { getMonthlyAchievements } from '../../routine/services/routineService';

// Helper to get color based on completion rate
const getColorForRate = (rate: number) => {
  if (rate === 100) return '#4ADE80'; // Green 400
  if (rate > 0) return '#A7F3D0';   // Green 200
  return '#F3F4F6'; // Gray 100 (default)
};

export const AchievementCalendar = () => {
  const [achievements, setAchievements] = useState<Record<string, number>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [currentMonth, setCurrentMonth] = useState('');
  const [calendarGrid, setCalendarGrid] = useState<(number | null)[]>([]);

  useEffect(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;
    const monthString = `${year}-${String(month).padStart(2, '0')}`;
    setCurrentMonth(monthString);

    // Generate Calendar Grid
    const firstDayOfMonth = new Date(year, month - 1, 1).getDay(); // 0=Sun, 1=Mon,...
    const daysInMonth = new Date(year, month, 0).getDate();
    const grid: (number | null)[] = [];
    for (let i = 0; i < firstDayOfMonth; i++) grid.push(null);
    for (let i = 1; i <= daysInMonth; i++) grid.push(i);
    setCalendarGrid(grid);

    // Fetch Data
    const loadAchievements = async () => {
      try {
        setIsLoading(true);
        const data = await getMonthlyAchievements(monthString);
        // Transform array into a map for easy lookup: { '2025-08-20': 100 }
        const achievementsMap = data.reduce((acc, item) => {
          acc[item.date] = item.completed_rate;
          return acc;
        }, {} as Record<string, number>);
        setAchievements(achievementsMap);
      } catch (error) {
        console.error('Failed to fetch monthly achievements:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadAchievements();
  }, []);

  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{currentMonth} 달성률</Text>
      {isLoading ? (
        <ActivityIndicator size="large" color="#FBBF24" />
      ) : (
        <View style={styles.calendarContainer}>
          <View style={styles.weekdaysContainer}>
            {weekdays.map(day => (
              <Text key={day} style={styles.weekdayText}>{day}</Text>
            ))}
          </View>
          <View style={styles.daysContainer}>
            {calendarGrid.map((day, index) => {
              const dateString = day ? `${currentMonth}-${String(day).padStart(2, '0')}` : '';
              const completionRate = achievements[dateString] ?? 0;
              
              return (
                <View key={index} style={styles.dayCell}>
                  {day && (
                    <View style={[styles.day, { backgroundColor: getColorForRate(completionRate) }]}>
                      <Text style={styles.dayText}>{day}</Text>
                    </View>
                  )}
                </View>
              );
            })}
          </View>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#FEF3C7',
    marginHorizontal: 16,
    marginTop: 16,
    padding: 16,
    borderRadius: 12,
    minHeight: 300,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  calendarContainer: {
    flex: 1,
  },
  weekdaysContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginBottom: 8,
  },
  weekdayText: {
    width: '14.2%',
    textAlign: 'center',
    fontWeight: '600',
  },
  daysContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  dayCell: {
    width: '14.2%',
    aspectRatio: 1, // Make it a square
    justifyContent: 'center',
    alignItems: 'center',
    padding: 2,
  },
  day: {
    flex: 1,
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 6,
  },
  dayText: {
    fontSize: 14,
  },
});

