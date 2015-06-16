# Running the Service Broker


*   the IP of the external address (relative to the VM running inside VirtualBox) is `http://10.0.2.2:8080`
*   in order to register this broker, stand it up first,
    then register it as follows: `cf create-service-broker ftp admin admin http://10.0.2.2:8080/`.
*   You don't have to re-register it each time, but you can delete it
    using `cf delete-service-broker ftp` if it was registered under
    the same name before. If it wasn't, then you can purge it out right using: ``
*   it is not available by default. You can enable it using `cf enable-service-access ftp -p ftp-free -o joshlong`
    where `-o` is the organization, `-p` is the plan name, and `ftp` is the service-broker name.
*   run `cf service-brokers` to see the output and confirm that the `ftp` service is available.
*   you can see what the API is returning for the catalog by hitting the catalog REST API directly (sending, of course, the right
    credentials (`admin`/`admin`): `curl -u admin:admin http://localhost:8080/v2/catalog`