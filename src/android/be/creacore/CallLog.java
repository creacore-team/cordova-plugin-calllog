package be.creacore;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CallLog extends CordovaPlugin {
    private static final String GET_CALL_LOG = "getCallLog";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

    private CallbackContext callback;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        if(GET_CALL_LOG.equals(action)) {
            String dateFrom = "";
            String dateTo = "";

            // dateFrom Arg
            if(Utils.isValidDate(args.getString(0))) {
                dateFrom = args.getString(0);
            }

            // dateTo Arg
            if(Utils.isValidDate(args.getString(1))) {
                dateTo = args.getString(1);
            }

            // filterNumbers Arg
            List<Filter> filters = new ArrayList<Filter>();
            if(!args.isNull(2)) {
                JSONArray tmpFilter = args.getJSONArray(2);
                if (tmpFilter.length() > 0) {
                    for (int i = 0; i < tmpFilter.length(); i++) {
                        Filter filter = new Filter();
                        JSONObject filterObject = tmpFilter.getJSONObject(i);
                        try {
                            filter.setName(filterObject.getString("name"));
                        } catch (Exception e) {
                            callback.error(e.getMessage());
                        }
                        filter.setValue(filterObject.getString("value"));
                        filter.setOperator(filterObject.getString("operator"));
                        filters.add(filter);
                    }
                }
            }

            getCallLog(
                dateFrom,
                dateTo,
                filters
            );
            return true;
        } else if(HAS_READ_PERMISSION.equals(action)) {
            hasReadPermission();
            return true;
        } else if(REQUEST_READ_PERMISSION.equals(action)) {
            requestReadPermission();
            return true;
        } else {
            return false;
        }
    }

    private void getCallLog(String dateFrom, String dateTo, List<Filter> filters)
    {
        if(callLogPermissionGranted(Manifest.permission.READ_CALL_LOG)) {

            String[] fields = {
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DURATION,
                android.provider.CallLog.Calls.NEW,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.CACHED_NUMBER_TYPE,
                android.provider.CallLog.Calls.CACHED_NUMBER_LABEL,
                android.provider.CallLog.Calls.VIA_NUMBER,
                android.provider.CallLog.Calls.PHONE_ACCOUNT_ID,
            };

            List<String> mSelectionArgs = new ArrayList<String>();
            String mSelectionClause = null;
            Timestamp time = null;

            // Date From parameter
            if(dateFrom.length() > 0)
            {
                mSelectionClause = Utils.appendClause(mSelectionClause, android.provider.CallLog.Calls.DATE + " >= ?");
                time = Utils.convertStringToTimestamp(dateFrom);
                if(time != null) {
                    mSelectionArgs.add(String.valueOf(time.getTime()));
                }
            }

            // Date To parameter
            if(dateTo.length() > 0) {
                mSelectionClause = Utils.appendClause(mSelectionClause, android.provider.CallLog.Calls.DATE + " <= ?");
                time = Utils.convertStringToTimestamp(dateTo);
                if(time != null) {
                    mSelectionArgs.add(String.valueOf(time.getTime()));
                }
            }

            // Other filters parameter
            if(filters.size() > 0) {
                for(Filter f: filters) {
                    mSelectionClause = Utils.appendFilterToClause(f, mSelectionClause);
                    mSelectionArgs.add(f.getValue());
                }
            }

            try {
                ContentResolver contentResolver = cordova.getActivity().getContentResolver();
                Cursor mCursor = contentResolver.query(android.provider.CallLog.Calls.CONTENT_URI,
                    fields,
                    mSelectionClause,
                    mSelectionArgs.toArray(new String[0]),
                    android.provider.CallLog.Calls.DEFAULT_SORT_ORDER
                );

                JSONArray result = new JSONArray();

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        JSONObject callLogItem = new JSONObject();
                        callLogItem.put("date", mCursor.getLong(0));
                        callLogItem.put("number", mCursor.getString(1));
                        callLogItem.put("type", mCursor.getInt(2));
                        callLogItem.put("duration", mCursor.getLong(3));
                        callLogItem.put("new", mCursor.getInt(4));
                        callLogItem.put("cachedName", mCursor.getString(5));
                        callLogItem.put("cachedNumberType", mCursor.getInt(6));
                        callLogItem.put("cachedNumberLabel", mCursor.getInt(7));
                        callLogItem.put("viaNumber", mCursor.getString(8));
                        callLogItem.put("phoneAccountId", mCursor.getString(9));

                        result.put(callLogItem);
                    }
                }
                callback.success(result);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void hasReadPermission() {
        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, callLogPermissionGranted(Manifest.permission.READ_CALL_LOG)));
    }

    private void requestReadPermission() {
        requestPermission(Manifest.permission.READ_CALL_LOG);
    }

    private boolean callLogPermissionGranted(String type) {
        return cordova.hasPermission(type);
    }

    private void requestPermission(String type) {
        if (!callLogPermissionGranted(type)) {
            cordova.requestPermission(this, 12345, type);
        } else {
            this.callback.success();
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.callback.success();
        } else {
            this.callback.error("Permission denied");
        }
    }
}