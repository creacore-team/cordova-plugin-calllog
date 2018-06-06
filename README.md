# cordova-plugin-callLog

Android only
Cordova plugin to access the call history on a device. Results can be filtered through several parameters.

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

## Filters available

- date : date in milliseconds since the epoch
- number : phone number
- duration : call duration
- type : type of call (see https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE)
- subscription_id : id of the sim card (useful for dual sim)

## Operators available
\>, >=, <, <=, ==, like

Here is an example with the operator like

    let filters = [{
        "name": "number",
        "value": "+32%",
        "operator": "like",
    }]

This will return all calls from/to the numbers beginning with '+32'

## Value

Value can be a string, or can be an array, here is an example:

    let filters = [{
        "name": "number",
        "value": ["+32477000000", "+32478000000"],
        "operator": "==",
    }]

This will return all calls from/to the numbers +32477000000 and +32478000000

## Returned values

getCallLog() returns an array of objects with these values
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

(Android API >= 21)

A contact lookup is also performed on the phone number of each log item, which adds the following values (from https://developer.android.com/reference/android/provider/ContactsContract.Data) if found
- DISPLAY_NAME
- CONTACT_ID
- PHOTO_URI
- PHOTO_THUMBNAIL_URI
