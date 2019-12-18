# CoapSimulate

# Setup

## Eclipse
- add the external library(./extlib) to project "coap_server" and "coap_client".

## For MacOS

The multicast server demo may cause the error suck like "`Can't assign requested address` java.net.SocketException using Ehcache multicast".

In Eclipse, you can right click your project and select "Run As", then select "Run Configurations...". Append "-Djava.net.preferIPv4Stack=true" into VM arguments and click "Apply".

# Reference

- [stackoverflow#cant_assign_request_address](https://stackoverflow.com/questions/18747134/getting-cant-assign-requested-address-java-net-socketexception-using-ehcache)
