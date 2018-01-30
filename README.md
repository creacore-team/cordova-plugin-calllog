# cordova-plugin-callLog

Android only
Cordova plugin to access the call history on a device and that can be filtered

## Installation

    cordova plugin add cordova-plugin-calllog

## Methods

- getCallLog(filters, callbackSuccess, callbackError);
- hasReadPermission(successCallback, errorCallback);
- requestReadPermission(successCallback, errorCallback);

## Usage

First of all you must check / request permissions with

    window.plugins.callLog.hasReadPermission(...,...)
    window.plugins.callLog.requestReadPermission(...,...)

Then you can use the main function getCallLog(), here is an example:

    let filters = [{
        "name": "number",
        "value": "+32477000000",
        "operator": "==",
    },
    {
        "name": "date",
        "value": 1517266800000,
        "operator": ">="
    }];

    window.plugins.callLog.getCallLog(filters, function(data) {
         console.log(data);
    }, function() {
         // Error
    });

This will return all calls from/to the number +32477000000 since 2018-01-30

## Filter availables

- date : date in milliseconds since the epoch
- number : phone number
- duration : call duration
- type : type of call (see https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE)
- subscription_id : id of the sim card (useful for dual sim)

## Returned values

getCallLog() return an array of objects with these values
(see https://developer.android.com/reference/android/provider/CallLog.Calls.html)

- DATE
- NUMBER
- TYPE
- DURATION
- NEW
- CACHED_NAME
- CACHED_NUMBER_TYPE
- CACHED_NUMBER_LABEL

(Android API >= 21)
- PHONE_ACCOUNT_ID

(Android API >= 24)
- VIA_NUMBER