This service uses Stormpath API Keys and SSL.
You will need a Stormpath account with an API Key and API Key Secret.

You will need to set the Stormpath api key environment variables:

* ```export stormpath_apiKey_id=<some-value>```
* ```export stormpath_apiKey_secret=<some-value>```


In order to setup SSL, you must setup your own SSL keystore file and password set environment variables to tell the service where the file is and what the password is: 
* ```export server_ssl_key_store=some-keystore-file```
* ```export server_ssl_key_store_password=some-password```

See here for setting up keystore:  https://thoughtfulsoftware.wordpress.com/2014/01/05/setting-up-https-for-spring-boot/ 


When making calls to the service, you will need to pass the Stormpath API Key and API Key Secret, like demonstrated below.

To run service:

./gradlew clean build run

To call service:

curl -u my-api-key-id:my-api-key-secret  --insecure -H "Accept: application/json"  -L https://localhost:9000/greeting
