import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, ActivityIndicator, Image } from 'react-native';
import { useRoute, RouteProp } from '@react-navigation/native';
import { AppStackParamList } from '../../../app/navigation/RootNavigator';
import { getRoutineElementDetail } from '../services/routineService';
import { RoutineSubElement } from '../types/routine.types';
import { Header } from '../../../shared/components/layout/Header';
import { SafeAreaView } from 'react-native-safe-area-context';

type RoutineDetailScreenRouteProp = RouteProp<
  AppStackParamList,
  'RoutineDetail'
>;

const RoutineDetailScreen = () => {
  const route = useRoute<RoutineDetailScreenRouteProp>();
  const { routine_element_id, routine_element_name } = route.params;

  const [details, setDetails] = useState<RoutineSubElement[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        setIsLoading(true);
        const data = await getRoutineElementDetail(routine_element_id);
        setDetails(data.sort((a, b) => a.step - b.step)); // Sort by step
      } catch (error) {
        console.error('Failed to fetch routine details:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchDetails();
  }, [routine_element_id]);

  return (
    <SafeAreaView style={styles.container}>
      <Header title={routine_element_name} />
      {isLoading ? (
        <ActivityIndicator style={styles.centered} size="large" />
      ) : (
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          {details.map(item => (
            <View key={item.routine_sub_element_id} style={styles.stepContainer}>
              <Text style={styles.stepTitle}>{`Step ${item.step}: ${item.routine_sub_element_name}`}</Text>
              <Image source={{ uri: item.routine_detail_image }} style={styles.image} />
              <Text style={styles.content}>{item.routine_content}</Text>
              <View style={styles.tipsContainer}>
                <Text style={styles.tipTitle}>Tips:</Text>
                {item.tips.map(tip => (
                  <Text key={tip.routine_element_tip_id} style={styles.tipText}>
                    • {tip.tip_content}
                  </Text>
                ))}
              </View>
            </View>
          ))}
        </ScrollView>
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: 'white' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  scrollContainer: { padding: 16 },
  stepContainer: {
    marginBottom: 24,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 12,
    padding: 16,
  },
  stepTitle: { fontSize: 20, fontWeight: 'bold', marginBottom: 12 },
  image: { width: '100%', height: 200, borderRadius: 8, marginBottom: 12 },
  content: { fontSize: 16, lineHeight: 24, marginBottom: 12 },
  tipsContainer: {
    backgroundColor: '#F9FAFB',
    padding: 12,
    borderRadius: 8,
  },
  tipTitle: { fontSize: 16, fontWeight: '600', marginBottom: 8 },
  tipText: { fontSize: 14, color: '#374151', marginBottom: 4 },
});

export default RoutineDetailScreen;
