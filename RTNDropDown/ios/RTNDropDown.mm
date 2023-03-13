#import "RTNDropDown.h"

#import <react/renderer/components/RTNDropDownSpecs/ComponentDescriptors.h>
#import <react/renderer/components/RTNDropDownSpecs/EventEmitters.h>
#import <react/renderer/components/RTNDropDownSpecs/Props.h>
#import <react/renderer/components/RTNDropDownSpecs/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface RTNDropDown () <RCTRTNDropDownViewProtocol>
@end

@implementation RTNDropDown {
  UIView *_view;
  UIPickerView *_pickerView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RTNDropDownComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RTNDropDownProps>();
    _props = defaultProps;
    
    _view = [[UIView alloc] init];
    _pickerView = [[UIPickerView alloc]init];
    _pickerView.delegate = self;
    _pickerView.dataSource = self;
    [_view addSubview:_pickerView];
    _pickerView.translatesAutoresizingMaskIntoConstraints = false;
    [NSLayoutConstraint activateConstraints:@[
      [_pickerView.leadingAnchor constraintEqualToAnchor:_view.leadingAnchor],
      [_pickerView.topAnchor constraintEqualToAnchor:_view.topAnchor],
      [_pickerView.trailingAnchor constraintEqualToAnchor:_view.trailingAnchor],
      [_pickerView.bottomAnchor constraintEqualToAnchor:_view.bottomAnchor],
    ]];
    
    self.contentView = _view;
  }
  
  return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  
  const auto &oldViewProps = *std::static_pointer_cast<RTNDropDownProps const>(_props);
  const auto &newViewProps = *std::static_pointer_cast<RTNDropDownProps const>(props);
  
  if (oldViewProps.values != newViewProps.values) {
    NSMutableArray *colorArray = [[NSMutableArray alloc] init];
    for (int i = 0; i <= newViewProps.values.capacity() - 1; i++)
    {
      NSString *valueToConvert = [[NSString alloc] initWithUTF8String: newViewProps.values[i].c_str()];
      [colorArray addObject:valueToConvert];
    }
    self.pickerNames = colorArray;
  }
  
  [super updateProps:props oldProps:oldProps];
}

- (NSInteger)numberOfComponentsInPickerView:(nonnull UIPickerView *)pickerView {
  return 1;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    return self.pickerNames[row];
}

- (NSInteger)pickerView:(nonnull UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
  return [self.pickerNames count];
}


-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
  std::dynamic_pointer_cast<const facebook::react::RTNDropDownEventEmitter>(_eventEmitter)->onSelectionChnaged(facebook::react::RTNDropDownEventEmitter::OnSelectionChnaged{
    .value = std::string([self.pickerNames[row] UTF8String])
  });
  
}


@end

Class<RCTComponentViewProtocol> RTNDropDownCls(void)
{
  return RTNDropDown.class;
}
