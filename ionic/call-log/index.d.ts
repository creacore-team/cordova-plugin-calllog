import { IonicNativePlugin } from '@ionic-native/core';
/**
 * @name Call Log
 * @description
 * This plugin access the call history on a device and that can be filtered
 *
 * @usage
 * ```typescript
 * import { CallLog } from '@ionic-native/call-log';
 *
 *
 * constructor(private callLog: CallLog) { }
 *
 * ...
 *
 */
export declare class CallLog extends IonicNativePlugin {
    /**
     * This function does something
     * @param dateFrom {string} get call logs from this date (format yyyy-dd-mm)
     * @param dateTo {string} get call logs until this date (format yyyy-dd-mm)
     * @param filters {object[]} array of object to filter the query
     * Object must respect this structure {'name':'...', 'value': '...', 'operator': '=='}
     * (see https://github.com/creacore-team/cordova-plugin-calllog for more details)
     * @return {Promise<any>}
     */
    getCallLog(dateFrom: string, dateTo: string, filters: object[]): Promise<any>;
    /**
     * Check permission
     * @returns {Promise<any>}
     */
    hasReadPermission(): Promise<any>;
    /**
     * Request permission
     * @returns {Promise<any>}
     */
    requestReadPermission(): Promise<any>;
}
