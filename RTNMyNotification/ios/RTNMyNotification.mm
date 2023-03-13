

#import "RTNMyNotificationSpec.h"
#import "RTNMyNotification.h"

@implementation RTNMyNotification

RCT_EXPORT_MODULE()




- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
(const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeMyNotificationSpecJSI>(params);
}

- (void)showNotification:(NSString *)title resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  UNMutableNotificationContent *content = [UNMutableNotificationContent new];
  content.title = title;
  content.body = @"Buy some milk";
  content.sound = [UNNotificationSound defaultSound];
  UNTimeIntervalNotificationTrigger *trigger = [UNTimeIntervalNotificationTrigger
                                                triggerWithTimeInterval:1 repeats:NO];
  NSString *identifier = @"UYLLocalNotification";
  UNNotificationRequest *request = [UNNotificationRequest requestWithIdentifier:identifier
                                                                        content:content trigger:trigger];
  UNUserNotificationCenter* center = [UNUserNotificationCenter currentNotificationCenter];
  [center addNotificationRequest:request withCompletionHandler:^(NSError * _Nullable error) {
    if (error != nil) {
      reject(@"Fail",error.description,nil);
    } else {
      NSDictionary *dictRes = @{
        @"success": @"success",
      };
      resolve(dictRes);
    }
  }];
}

- (void)checkIfAndroidTOrAbove:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    reject(@"Fail",@"Fail",nil);
}


- (void)getPermissionForAndroid:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    reject(@"Fail",@"Fail",nil);
}

@end
