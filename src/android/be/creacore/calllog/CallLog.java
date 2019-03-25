package be.creacore.calllog;

import android.Manifest;
import android.content.Context;
import android.provider.ContactsContract;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class CallLog extends CordovaPlugin {
    private static final String GET_CALL_LOG = "getCallLog";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";
    private static final String[] PERMISSIONS = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS};

    private static final String[] PROJECTION =
    {
        ContactsContract.Data._ID,
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Data.DISPLAY_NAME_PRIMARY :
                ContactsContract.Data.DISPLAY_NAME,
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Data.PHOTO_URI,
        ContactsContract.Data.PHOTO_THUMBNAIL_URI
    };

    private static final String SELECTION = ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " LIKE ? ";

    private CallbackContext callback;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        if(GET_CALL_LOG.equals(action)) {
            // filterNumbers Arg
            ArrayList<ArrayList<Filter>> filters = new ArrayList<ArrayList<Filter>>();
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
                        filter.setOperator(filterObject.getString("operator"));

                        // Is an array of values ?
                        try {
                            JSONArray values = filterObject.getJSONArray("value");
                            if (values != null) {
                                ArrayList<Filter> subFilters = new ArrayList<Filter>();
                                for (int j = 0; j < values.length(); j++) {
                                    Filter f = new Filter();
                                    try {
                                        f.setName(filter.getName());
                                    } catch (Exception e) {
                                        callback.error(e.getMessage());
                                    }
                                    f.setOperator(filter.getOperator());
                                    f.setValue(values.getString(j));
                                    f.setOperation("OR");
                                    subFilters.add(f);
                                }
                                filters.add(subFilters);
                            }
                        }
                        // Single value
                        catch(JSONException e) {
                            ArrayList<Filter> subFilters = new ArrayList<Filter>();
                            filter.setValue(filterObject.getString("value"));
                            subFilters.add(filter);
                            filters.add(subFilters);
                        }
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

    private void getCallLog(ArrayList<ArrayList<Filter>> filters)
    {
        if(callLogPermissionGranted(PERMISSIONS)) {
            Hashtable<String, Cursor> contacts = new Hashtable<String, Cursor>();
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
                for(ArrayList<Filter> subfilters: filters) {
                    String mSelectionSubClause = null;
                    for(Filter f: subfilters) {
                        // Detect specific version
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP &&
                                f.getName() == android.provider.CallLog.Calls.PHONE_ACCOUNT_ID) {
                            continue;
                        }
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N &&
                                f.getName() == android.provider.CallLog.Calls.VIA_NUMBER) {
                            continue;
                        }
                        mSelectionSubClause = Utils.appendFilterToClause(f, mSelectionSubClause);
                        mSelectionArgs.add(f.getValue());
                    }

                    if(mSelectionClause == null)
                    {
                        mSelectionClause = '(' + mSelectionSubClause + ')';
                    }
                    else
                    {
                        mSelectionClause += " AND (" + mSelectionSubClause + ')';
                    }
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
                    Context context = this.cordova.getActivity();
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                    String countryCode = tm.getSimCountryIso().toUpperCase();

                    while (mCursor.moveToNext()) {
                        JSONObject callLogItem = new JSONObject();
                        callLogItem.put("date", mCursor.getLong(0));
                        callLogItem.put("number", mCursor.getString(1));
                        callLogItem.put("type", mCursor.getInt(2));
                        callLogItem.put("duration", mCursor.getLong(3));
                        callLogItem.put("new", mCursor.getInt(4));
                        callLogItem.put("cachedName", mCursor.getString(5));
                        callLogItem.put("cachedNumberType", mCursor.getInt(6));
                        callLogItem.put("cachedNumberLabel", mCursor.getString(7));

                        // Detect specifics version
                        int index = 8;
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            callLogItem.put("phoneAccountId", mCursor.getString(index));
                            index++;
                        }
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            callLogItem.put("viaNumber", mCursor.getString(index));
                        }

                        // Fill in contact name
                        callLogItem.put("name", (mCursor.isNull(5))?"":mCursor.getString(5));
                        callLogItem.put("contact", "");
                        callLogItem.put("photo", "");
                        callLogItem.put("thumbPhoto", "");
                        Cursor mContactCursor;
                        if(contacts.containsKey(mCursor.getString(1)))
                        {
                            mContactCursor = contacts.get(mCursor.getString(1));
                        }
                        else
                        {
                            String number = mCursor.getString(1);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && !countryCode.isEmpty()) {
                                 number = PhoneNumberUtils.formatNumberToE164(number, countryCode);
                                 number = PhoneNumberUtils.normalizeNumber(number);
                            }

                            String[] mSelectionArgsContact = { "%" + number + "%" };
                            mContactCursor = contentResolver.query(
                                    ContactsContract.Data.CONTENT_URI,
                                    PROJECTION,
                                    SELECTION,
                                    mSelectionArgsContact,
                                    null
                            );
                            contacts.put(mCursor.getString(1), mContactCursor);
                        }
                        if (mContactCursor.moveToFirst()) {
                            if(!mContactCursor.isAfterLast()) {
                                callLogItem.put("name", mContactCursor.getString(1));
                                callLogItem.put("contact", mContactCursor.getInt(2));
                                callLogItem.put("photo", mContactCursor.getString(3));
                                callLogItem.put("thumbPhoto", mContactCursor.getString(4));
                            }
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
        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, callLogPermissionGranted(PERMISSIONS)));
    }

    private void requestReadPermission() {
        requestPermission(PERMISSIONS);
    }

    private boolean callLogPermissionGranted(String[] type) {
        boolean perm = true;
        for(int i=0; i < type.length; i++) {
            if (!cordova.hasPermission(type[i]))
                perm = false;
        }
        return perm;
    }

    private void requestPermission(String[] type) {
        if (!callLogPermissionGranted(type)) {
            cordova.requestPermissions(this, 0, type);
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
