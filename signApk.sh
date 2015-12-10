mv bin/ShlyMirror.apk bin/temp.apk
java -jar sign/signapk.jar sign/platform.x509.pem sign/platform.pk8 bin/temp.apk bin/ShlyMirror.apk
rm bin/temp.apk
adb root
adb remount
adb install -r bin/ShlyMirror.apk
