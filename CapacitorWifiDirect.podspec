
  Pod::Spec.new do |s|
    s.name = 'CapacitorWifiDirect'
    s.version = '0.0.1'
    s.summary = 'Capacitor plugin to use Wifi Direct Android feature'
    s.license = 'GNU'
    s.homepage = 'https://gitlab.com/ArcheoCodix/capacitor-wifi-direct.git'
    s.author = 'Nils Toularastel'
    s.source = { :git => 'https://gitlab.com/ArcheoCodix/capacitor-wifi-direct.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end
