package com.yellowbambara.tatafo;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 04/07/2015.
 */
public class Utility {

    public static String stripXMLTags(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        StringBuilder plainString = new StringBuilder();
        StringBuilder tagString = new StringBuilder();
        int length = value.length();
        boolean inTag = false;
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '<') {
                inTag = true;
                continue;
            } else if (c == '>') {
                inTag = false;
                String s;
                String tag = tagString.toString();
                if(isNewLine(tag)){
                    s = "\n";
                }else {
                    s = extractImageFromSrc(tag);
                }
                tagString.delete(0, tagString.length()); // Clear
                plainString.append(s);
                continue;
            }

            if (inTag) {
                tagString.append(c);
            }else{
                plainString.append(c);
            }
        }

        return plainString.toString();
    }

    private static boolean isNewLine(String tagString){
        return tagString.matches("[Bb][Rr]/?");
    }

    private static String extractImageFromSrc(String tagString){
        if(tagString.matches("[\\s\\S]*[iI][mM][gG][\\s\\S]*")){
            int index = tagString.indexOf("src");
            if(index >= 0 && index < tagString.length()){
                StringBuilder s = new StringBuilder();
                int quoteCount = 0;
                for (int i = index; i < tagString.length(); i++){
                    char c = tagString.charAt(i);
                    if (c == '"') {
                        quoteCount++;
                        continue;
                    }

                    if(quoteCount >= 2){
                        break;
                    }

                    if(quoteCount > 0) {
                        s.append(c);
                    }
                }
                return "[Image " + s.toString() + " ]";
            }else{
                return "";
            }
        }else{
            return "";
        }
    }

    public static String getFriendlyDate(Date date) {
        Calendar today = Calendar.getInstance();
        //Reset today
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String time = c.get(Calendar.HOUR) + ":"
                + c.get(Calendar.MINUTE) + " "
                + c.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.ENGLISH);
        if (c.after(today)) {
            return "Today by " + time;
        } else {
            today.add(Calendar.DAY_OF_YEAR, -1); //Offset different by a day
            if (c.after(today)) {
                return "Yesterday by " + time;
            }
            today.add(Calendar.DAY_OF_YEAR, -6); //Offset different by one week
            if (c.after(today)) {
                return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH) + ", " + time;
            } else {
                return c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH)
                        + ", "
                        + c.get(Calendar.DAY_OF_MONTH)
                        + " by "
                        + time;
            }
        }
    }
}
