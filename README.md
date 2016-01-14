This service uses Stormpath API Keys and SSL.
You will need a Stormpath account with an API Key and API Key Secret.

You will need to set the Stormpath api key environment variables:

* ```export stormpath_apiKey_id=<some-value>```
* ```export stormpath_apiKey_secret=<some-value>```



When making calls to the service, you will need to pass the Stormpath API Key and API Key Secret, like demonstrated below.

To run service:

./gradlew clean build bootrun

To call service:

curl -u my-api-key-id:my-api-key-secret  --insecure -H "Accept: application/json"  -L https://localhost:9000/archivedCards


