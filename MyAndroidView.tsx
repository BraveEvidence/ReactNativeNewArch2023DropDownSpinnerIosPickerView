import React, {useEffect} from 'react';
import {DeviceEventEmitter, Text, TouchableOpacity} from 'react-native';
import RTNMyNotification from 'rtn-my-notification/js/NativeMyNotification';

const MyAndroidView = () => {
  useEffect(() => {
    DeviceEventEmitter.addListener('result', async message => {
      if (message) {
        await RTNMyNotification?.showNotification('Hello from react native');
      }
    });

    return () => {
      DeviceEventEmitter.removeAllListeners();
    };
  }, []);

  const sendLocalNotification = async () => {
    const isAndroidTOrAbove = await RTNMyNotification?.checkIfAndroidTOrAbove();
    if (isAndroidTOrAbove) {
      await RTNMyNotification?.getPermissionForAndroid();
    } else {
      await RTNMyNotification?.showNotification('Hello from react native');
    }
  };

  return (
    <TouchableOpacity onPress={sendLocalNotification}>
      <Text>Send Notification</Text>
    </TouchableOpacity>
  );
};

export default MyAndroidView;
