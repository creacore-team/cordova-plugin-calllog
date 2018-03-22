package be.creacore.calllog;

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
    public static String appendClause(String clause, String append, String operation) {
        if(clause != null && clause.length() > 0) {
            return clause + ' ' + operation + ' ' + append;
        } else {
            return append;
        }
    }

    public static String appendFilterToClause(Filter filter, String clause) {
        return appendClause(clause, filter.getName() + ' ' + filter.getOperator() + " ?", filter.getOperation());
    }
}