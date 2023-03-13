import React from 'react';
import {TouchableOpacity, Text} from 'react-native';
import RTNMyNotification from 'rtn-my-notification/js/NativeMyNotification';

const MyIOSView = () => {
  const sendLocalNotification = async () => {
    await RTNMyNotification?.showNotification('Hello from reacgt native');
  };

  return (
    <TouchableOpacity onPress={sendLocalNotification}>
      <Text>Send Notification</Text>
    </TouchableOpacity>
  );
};

export default MyIOSView;
