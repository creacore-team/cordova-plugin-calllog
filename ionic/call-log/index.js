var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Injectable } from '@angular/core';
import { Plugin, Cordova, IonicNativePlugin } from '@ionic-native/core';
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
var CallLog = (function (_super) {
    __extends(CallLog, _super);
    function CallLog() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    /**
     * This function return the call logs
     * @param filters {object[]} array of object to filter the query
     * Object must respect this structure {'name':'...', 'value': '...', 'operator': '=='}
     * (see https://github.com/creacore-team/cordova-plugin-calllog for more details)
     * @return {Promise<any>}
     */
    CallLog.prototype.getCallLog = function (filters) { return; };
    /**
     * Check permission
     * @returns {Promise<any>}
     */
    CallLog.prototype.hasReadPermission = function () { return; };
    /**
     * Request permission
     * @returns {Promise<any>}
     */
    CallLog.prototype.requestReadPermission = function () { return; };
    CallLog.decorators = [
        { type: Injectable },
    ];
    /** @nocollapse */
    CallLog.ctorParameters = function () { return []; };
    __decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", [Array]),
        __metadata("design:returntype", Promise)
    ], CallLog.prototype, "getCallLog", null);
    __decorate([
        Cordova({
            platforms: ['Android']
        }),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", Promise)
    ], CallLog.prototype, "hasReadPermission", null);
    __decorate([
        Cordova({
            platforms: ['Android']
        }),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", Promise)
    ], CallLog.prototype, "requestReadPermission", null);
    CallLog = __decorate([
        Plugin({
            pluginName: 'CallLog',
            plugin: 'cordova-plugin-calllog',
            pluginRef: 'plugins.callLog',
            repo: 'https://github.com/creacore-team/cordova-plugin-calllog',
            platforms: ['Android']
        })
    ], CallLog);
    return CallLog;
}(IonicNativePlugin));
export { CallLog };
//# sourceMappingURL=index.js.map