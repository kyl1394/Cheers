# Cheers
Is a social drink sharing application built on the premise of discovering new drinks and seeing who's at the bars and where. Combined with the power to ensure a safe trip home, Cheers is an fun and safe way for people of all (legal) ages to enjoy their friday nights and for their loved ones to have some piece of mind.

## Setup:

### Generate Key Hash:
* Download openssl
    
    https://code.google.com/archive/p/openssl-for-windows/downloads
    
* Run this command from your jdk installation's bin folder: 

    keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore | C:\openssl\bin\openssl.exe sha1 -binary | C:\openssl\bin\openssl.exe base64>
    
* Paste code into keyhashes here:

    https://developers.facebook.com/quickstarts/302513443465700/?platform=android
