import type {TurboModule} from 'react-native/Libraries/TurboModule/RCTExport';
import {TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  downloadFile(
    url: string,
    filename: string,
    extension: string,
  ): Promise<boolean>;
}

export default TurboModuleRegistry.get<Spec>('RTNMyDownloader') as Spec | null;
