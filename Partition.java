Calendar cal = Calendar.getInstance();
cal.setTime(reAuthDate);

cal.set(Calendar.HOUR_OF_DAY, 0);
cal.set(Calendar.MINUTE, 0);
cal.set(Calendar.SECOND, 0);
cal.set(Calendar.MILLISECOND, 0);

// update SAME object
reAuthDate.setTime(cal.getTimeInMillis());
