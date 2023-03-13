

//#import "RTNMyDownloaderSpec.h"
#import "RTNMyDownloader.h"
#import "rnapp-Swift.h"
@implementation RTNMyDownloader

RCT_EXPORT_MODULE()


MyDownloader *myDownloader = [[MyDownloader alloc] init];

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
(const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeMyDownloaderSpecJSI>(params);
}




- (void)downloadFile:(NSString *)url filename:(NSString *)filename extension:(NSString *)extension resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [myDownloader downloadFileWithUrlString:url fileName:filename fileExtension:extension];
  resolve(@"");
}

@end
