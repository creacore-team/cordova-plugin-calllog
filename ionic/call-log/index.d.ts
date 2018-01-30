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
     * This function return the call logs
     * @param filters {object[]} array of object to filter the query
     * Object must respect this structure {'name':'...', 'value': '...', 'operator': '=='}
     * (see https://github.com/creacore-team/cordova-plugin-calllog for more details)
     * @return {Promise<any>}
     */
    getCallLog(filters: object[]): Promise<any>;
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
