import type {TurboModule} from 'react-native/Libraries/TurboModule/RCTExport';
import {TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  getPermissionForAndroid(): Promise<void>;
  showNotification(title: string): Promise<void>;
  checkIfAndroidTOrAbove(): Promise<boolean>;
}

export default TurboModuleRegistry.get<Spec>(
  'RTNMyNotification',
) as Spec | null;
