// src/shared/utils/date.ts

import moment from 'moment'; // Assuming moment.js is installed or will be installed

export const formatDateTime = (date: Date | string, format: string = 'YYYY-MM-DD HH:mm:ss'): string => {
  return moment(date).format(format);
};

export const getDaysDifference = (date1: Date | string, date2: Date | string): number => {
  const a = moment(date1);
  const b = moment(date2);
  return a.diff(b, 'days');
};

export const isToday = (date: Date | string): boolean => {
  return moment(date).isSame(moment(), 'day');
};
