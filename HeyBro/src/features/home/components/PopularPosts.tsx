import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ActivityIndicator, TouchableOpacity } from 'react-native';
import { getQuestions } from '../../qna/services/qnaService';
import { Question } from '../../qna/types/qna.types';

// Helper function to format time difference
const timeSince = (date: string): string => {
  const seconds = Math.floor((new Date().getTime() - new Date(date).getTime()) / 1000);
  let interval = seconds / 31536000;
  if (interval > 1) return Math.floor(interval) + "년 전";
  interval = seconds / 2592000;
  if (interval > 1) return Math.floor(interval) + "달 전";
  interval = seconds / 86400;
  if (interval > 1) return Math.floor(interval) + "일 전";
  interval = seconds / 3600;
  if (interval > 1) return Math.floor(interval) + "시간 전";
  interval = seconds / 60;
  if (interval > 1) return Math.floor(interval) + "분 전";
  return "방금 전";
};

export const PopularPosts = () => {
  const [posts, setPosts] = useState<Question[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchPopularPosts = async () => {
      try {
        setIsLoading(true);
        const data = await getQuestions({ sort: 'view_desc', number: 3 });
        setPosts(data);
      } catch (error) {
        console.error('Failed to fetch popular posts:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchPopularPosts();
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>오늘의 인기글</Text>
      {isLoading ? (
        <ActivityIndicator size="large" color="#F87171" />
      ) : (
        <View>
          {posts.map((post, index) => (
            <TouchableOpacity key={post.question_id} style={styles.postContainer}>
              <View style={styles.rankContainer}>
                <Text style={styles.rankText}>{index + 1}</Text>
              </View>
              <View style={styles.postDetails}>
                <Text style={styles.postTitle} numberOfLines={1}>{post.title}</Text>
                <Text style={styles.postMeta}>{timeSince(post.created_at)}</Text>
                <View style={styles.postStats}>
                  <Text style={styles.statsText}>조회수 {post.views}</Text>
                  <Text style={styles.statsText}>답변 {post.answer_count}</Text>
                </View>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#FEE2E2',
    margin: 16,
    padding: 16,
    borderRadius: 12,
    minHeight: 250,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  postContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'white',
    borderRadius: 8,
    padding: 12,
    marginBottom: 10,
  },
  rankContainer: {
    marginRight: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  rankText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#EF4444',
  },
  postDetails: {
    flex: 1,
  },
  postTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
  },
  postMeta: {
    fontSize: 12,
    color: '#6B7280',
    marginBottom: 8,
  },
  postStats: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  statsText: {
    fontSize: 12,
    color: '#374151',
    marginLeft: 12,
  },
});
