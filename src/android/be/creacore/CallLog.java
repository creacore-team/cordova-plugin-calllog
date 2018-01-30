package be.creacore;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

public class CallLog extends CordovaPlugin {
    private static final String GET_CALL_LOG = "getCallLog";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

    private CallbackContext callback;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        if(GET_CALL_LOG.equals(action)) {
            // filterNumbers Arg
            List<Filter> filters = new ArrayList<Filter>();
            if(!args.isNull(0)) {
                JSONArray tmpFilter = args.getJSONArray(0);
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

            getCallLog(filters);
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

    private void getCallLog(List<Filter> filters)
    {
        if(callLogPermissionGranted(Manifest.permission.READ_CALL_LOG)) {
            List<String> fields = new ArrayList<String>();
            String[] fields_names = new String[]{
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DURATION,
                android.provider.CallLog.Calls.NEW,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.CACHED_NUMBER_TYPE,
                android.provider.CallLog.Calls.CACHED_NUMBER_LABEL
            };
            Collections.addAll(fields, fields_names);

            // Detect specifics version
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fields.add(android.provider.CallLog.Calls.PHONE_ACCOUNT_ID);
            }
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fields.add(android.provider.CallLog.Calls.VIA_NUMBER);
            }

            List<String> mSelectionArgs = new ArrayList<String>();
            String mSelectionClause = null;

            // Filters parameter
            if(filters.size() > 0) {
                for(Filter f: filters) {
                    // Detect specific version
                    if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP &&
                            f.getName() == android.provider.CallLog.Calls.PHONE_ACCOUNT_ID) {
                        continue;
                    }
                    if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N &&
                            f.getName() == android.provider.CallLog.Calls.VIA_NUMBER) {
                        continue;
                    }

                    mSelectionClause = Utils.appendFilterToClause(f, mSelectionClause);
                    mSelectionArgs.add(f.getValue());
                }
            }

            try {
                ContentResolver contentResolver = cordova.getActivity().getContentResolver();
                Cursor mCursor = contentResolver.query(android.provider.CallLog.Calls.CONTENT_URI,
                    fields.toArray(new String[0]),
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

                        // Detect specifics version
                        int index = 8;
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            callLogItem.put("phoneAccountId", mCursor.getString(index));
                            index++;
                        }
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            callLogItem.put("viaNumber", mCursor.getString(index));
                        }

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