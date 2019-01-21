# the-bike-lock
Realization of a bicycle lock connected to an application on smartphone. Integrating a geolocation in real time and a communication with protocol SIGFOX. In addition, opening and closing will be automated thanks to an integrated motor.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See installing for notes on how to deploy the project on a live system. 

### Prerequisites

You must be connected to the wifi network and own the nfc technology on his phone.

### Installing Android App

A step by step series that tell you how to get a development env running

Download zip file and unzip in file system

```bash
TheBikeLock.zip 
```

In Android Studio open the SoManager project

```bash
File -> Open -> folder path
```

Once the project opens connect your android mobile, then launch the TheBikelock app, you now have access to app.

### Using his own SIGFOX module

If you want to use your own SIGFOX data then it is necessary to change these lines of code below :

```java
@Override
public Map<String, String> getHeaders() throws AuthFailureError {
  ...
  //String credentials = "nameofmodule:password"
  String credentials = "5bc99faa0499f53744d6974e:3812c36ad79b818c1658d7e55ded1b2a";
}
```

and change the URL

```java
private void parseJSON() {
  //String url = "nameofmodule:password@backend.sigfox.com/api/devicetypes/nameofmodule/messages
  String url = "https://5bc99faa0499f53744d6974e:3812c36ad79b818c1658d7e55ded1b2a@backend.sigfox.com/api/devicetypes/5c3f05ece833d917af9eb207/messages";
  ...
}
```
