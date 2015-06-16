# A Custom Cloud Foundry FTP Service Broker

Cloud Foundry offers many extension planes. One of them is the service broker. Service brokers implement a REST API that
Cloud Foundry knows how to talk to. You _could_ of course stand these services up using straight Lattice && Docker images
but the goal here is to demonstrate that you gain a lot by making it real and operationalizing it. Teach the cloud
the tricks it needs.

The goal here is to demonstrate how existing applications can be accomodated by forking them to the PaaS and - where necessary -
extending the PaaS to know about the legacy services. I wanted something enterprise-y to play with so I chose (S?)FTP(S?).

Users can create and bind instances of this FTP service with ease:

```sh
cf marketplace | grep ftp || echo "you've not successfully registered the FTP service with Cloud Foundry."
cf create-service ftp ftp-free m-ftp
cf bind-service hi m-ftp
```

## What's in the Box

 - `ftp-service-broker` is a Spring Boot-based service-broker that conforms to the Cloud Foundry service-broker API. When CF clients issue
    `cf marketplace`, `cf services`, `cf create-service`, etc., they're using callbacks that go to this API. This ultimately maps incoming
     service instances (an FTP-exposed filesystem that can be shared across multiple applications whose root is named for the service instance)
     to users (service instance binding IDs, basically).
 - `ftp-service` provides a Java process that looks at the  `FTP_USER` table and responds to FTP requests for users
    identifying themselves as a user in that table.
 - `ftp-service-provisioner` - at the moment, this just responds to incoming AMQP requests and adds users to the
    right table and sends reply messages back containing a valid FTP URI.
 - `ftp-service-provisioner-client` is the client to the AMQP-based provisioner service. It's exposed as a Spring Boot-based auto-configuration. With it,
     any client (including our custom service broker) can make requests to provision a new FTP service and user / workspace.
 - `installer` simply starts up and then runs the DB migrations and installs the requisite RabbitMQ exchange, queue, and binding.

# Miles to Go..

 -  There is work to be done to support using this on a IaaS like Amazon Web Services. The FTP implementation has a pluggable
    SPI for persistence. Currently it's using a native file system, but of course on AWS we'll want to use S3 or MongoDB's GridFS, instead.
 -  Additionally, the provisioner doesn't provision all that much at the moment. Ultimately it'll need to automaticaly spin up AWS AMIs containing the FTP service,
    or - ideally - Lattice Docker-images behind an elastic DNS entry.


## Building

As of June 16, 2015, I had to build my own installation of [the Spring Boot Cloud Foundry service-broker
auto-configuration and template](https://github.com/cloudfoundry-community/spring-boot-cf-service-broker/commit/202fb58c38bfff5370a8654384eedaca61c90cd2).

The code, as it is in master, should be fine and no longer require any patches, but you do need to build it because there's no well-known Maven build for it.

Also, we're configuring Cloud Foundry, and that requires a Cloud Foundry installation to which you have admin privileges.
You won't have those privileges on Pivotal Web Services, though. It's not hard to install BOSH-lite and Cloud Foundry on
a local machine [if you follow the steps in this installation script](https://gist.github.com/joshlong/fa63b5625ec4f36335f7).


