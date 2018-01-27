package be.creacore;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    /**
     *  Clause "SQL" manipulations
     */
    public static String appendClause(String clause, String append) {
        if(clause != null && clause.length() > 0) {
            return clause + " AND " + append;
        } else {
            return append;
        }
    }

    public static String appendFilterToClause(Filter filter, String clause) {
        return appendClause(clause, filter.getName() + filter.getOperator() + '?');
    }

    /**
     * Dates manipulations
     */
    public static Timestamp convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(str_date);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static boolean isValidDate(String input) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        return dateFormat.parse(input, new ParsePosition(0)) != null;
    }
}