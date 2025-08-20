import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { UserRoutine } from '../../routine/types/routine.types';
import { getUserRoutines } from '../../routine/services/routineService';
import { AppStackParamList } from '../../../app/navigation/RootNavigator';

type NavigationProp = NativeStackNavigationProp<AppStackParamList>;

export const TodayRoutine = () => {
  const navigation = useNavigation<NavigationProp>();
  const [routines, setRoutines] = useState<UserRoutine[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadRoutines = async () => {
      try {
        setIsLoading(true);
        const today = new Date().toISOString().split('T')[0];
        const data = await getUserRoutines({ period_type: 'day', date: today });
        setRoutines(data);
      } catch (error) {
        console.error("Failed to fetch today's routines:", error);
      } finally {
        setIsLoading(false);
      }
    };

    loadRoutines();
  }, []);

  const handleNavigateToDetail = (routine_element_id: number, routine_element_name: string) => {
    navigation.navigate('RoutineDetail', {
      routine_element_id,
      routine_element_name,
    });
  };

  const allElements = routines.flatMap(routine => routine.elements);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>오늘의 루틴</Text>
      {isLoading ? (
        <ActivityIndicator size="large" color="#10B981" />
      ) : allElements.length > 0 ? (
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.scrollView}>
          {allElements.map(element => (
            <View key={element.user_routine_element_id} style={[styles.routineCard, element.is_completed ? styles.completedCard : {}]}>
              <Text style={styles.routineName}>{element.routine_element_name}</Text>
              <View style={styles.buttonContainer}>
                <TouchableOpacity
                  style={styles.button}
                  onPress={() => handleNavigateToDetail(element.routine_element_id, element.routine_element_name)}
                >
                  <Text style={styles.buttonText}>상세보기</Text>
                </TouchableOpacity>
                <TouchableOpacity style={[styles.button, styles.completeButton]}>
                  <Text style={styles.buttonText}>달성</Text>
                </TouchableOpacity>
              </View>
            </View>
          ))}
        </ScrollView>
      ) : (
        <Text style={styles.emptyText}>오늘 등록된 루틴이 없습니다.</Text>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#D1FAE5',
    marginHorizontal: 16,
    marginTop: 16,
    padding: 16,
    borderRadius: 12,
    minHeight: 200,
    justifyContent: 'center',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 16,
    alignSelf: 'flex-start',
  },
  scrollView: {
    paddingRight: 16,
  },
  routineCard: {
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 16,
    width: 180,
    marginRight: 12,
    justifyContent: 'space-between',
  },
  completedCard: {
    backgroundColor: '#A7F3D0',
  },
  routineName: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 24,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  button: {
    backgroundColor: '#E5E7EB',
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 6,
  },
  completeButton: {
    backgroundColor: '#10B981',
  },
  buttonText: {
    color: '#1F2937',
    fontWeight: '500',
  },
  emptyText: {
    textAlign: 'center',
    color: '#374151',
  },
});
