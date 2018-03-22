package be.creacore.calllog;

import android.provider.*;
import android.provider.CallLog;

import java.util.Arrays;

public class Filter {
    private String name;
    private String value;
    private String operator;
    private String operation = "AND";

    public static String[] validNames = {
        android.provider.CallLog.Calls.DATE,
        android.provider.CallLog.Calls.NUMBER,
        android.provider.CallLog.Calls.DURATION,
        android.provider.CallLog.Calls.TYPE,
        android.provider.CallLog.Calls.PHONE_ACCOUNT_ID,
    };

    public String getName() {
        return name;
    }

    public void setName(String name) throws Exception {
        if(Arrays.asList(validNames).contains(name)) {
            this.name = name;
        } else {
            throw new Exception("Invalid filter name");
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator.isEmpty() ? "=" : operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }
}