import { Injectable } from '@angular/core';
import { Cordova, Plugin, IonicNativePlugin } from '@ionic-native/core';

@Plugin({
  pluginName: 'CallLog',
  plugin: 'cordova-plugin-calllog',
  pluginRef: 'plugins.calllog',
  repo: 'https://github.com/creacore-team/cordova-plugin-calllog',
  platforms: ['Android']
})
@Injectable()
export class CallLog extends IonicNativePlugin {
  /**
   * Returns info from the SIM card.
   * @returns {Promise<any>}
   */
  @Cordova()
  getCallLog(): Promise<any> { return; }

  /**
   * Check permission
   * @returns {Promise<any>}
   */
  @Cordova({
    platforms: ['Android']
  })
  hasReadPermission(): Promise<any> { return; }

  /**
   * Request permission
   * @returns {Promise<any>}
   */
  @Cordova({
    platforms: ['Android']
  })
  requestReadPermission(): Promise<any> { return; }
}
