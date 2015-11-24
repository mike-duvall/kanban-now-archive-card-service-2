

* Must add keystore.p12 for this to work
* Must ```export server_ssl_key_store_password=some-password```
* Must ```export server_ssl_key_store=some-keystore-file```


To use:

curl -u my-api-key-id:my-api-key-secret  --insecure -H "Accept: application/json"  -L https://localhost:9000/greeting
