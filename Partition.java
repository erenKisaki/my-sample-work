long time = reAuthDate.getTime();
TimeZone tz = TimeZone.getDefault();

reAuthDate.setTime(time - (time + tz.getOffset(time)) % (24 * 60 * 60 * 1000));
