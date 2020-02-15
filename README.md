IOC Checker Server
==================

Build
-----

Building this software requires JDK 1.8 or later (OpenJDK is sufficient).

Open a terminal in the project directory and
- on a Windows machine run
    ```bash 	
    gradlew.bat clean shadowJar
    ```
- on a Linux machine run
    ```bash 	
    ./gradlew clean shadowJar
    ```
  
Running
-------

Run with default configuration

```$bash
cd build/libs

java -jar ioc-checker-server-[VERSION].jar
```

This will launch an instance with H2 empty in-memory database listening on port *8080*.

Run with non-empty database 
```bash
java -jar ioc-checker-server.jar --admin-email admin@admin.com --admin-pass admin --probe-name probe1 --probe-key probeKey --dummy-ioc --default-feed-sources
``` 
The command above will create:
- Admin user with login *admin@admin.com* and password admin
- Probe credentials with name *probe1* and api key *probeKey*
    - These credentials are used by **ioc-checker-probe** to authenticate
    - Some dummy IOC 
    - Register a default feed source that will populate IOC from [MISP Project](https://www.misp-project.org/).
      By default, the first feed harvest will start a few seconds after the launch with 2 hour period. 

### Running in production with PostgreSQL and SSL

#### Enabling SSL 
To enable SSL you can:

* Use some reverse proxy like [nginx](https://www.nginx.com/), see for example [this guide](https://ktor.io/quickstart/guides/ssl.html#docker) usingLet's encrypt certificate (navigate to *Option 2*) 

or

* Configure server itself using for example self signed certificate. 
  This option will be assumed from now on.
  * Create a [self signed certificate](https://blogs.oracle.com/blogbypuneeth/steps-to-create-a-self-signed-certificate-using-openssl) for Java
  

#### Setup PostgreSQL

Let's assume you have an running PostgreSQL with
    * User with login privilege and some password (user = login role in PostgreSQL jargon)
    * An empty database with granted privileges to user *iocchecker-user*

#### Configuring the ioc-checker-server
    
* Create a new file called *application.conf* in the same directory a this app's executable *jar*.
  Read it thoroughly and configure it as you fit.  

    ```hocon
    ktor {
      deployment {
        port = 8080 // HTTP port
        sslPort = 8443 // HTTPS port 
        watch = [ http2 ]
      }
      application {
        modules = [com.lifars.ioc.server.ApplicationKt.module]
      }
      security { // ATENTION HERE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ATTENTION HERE
        ssl {
          keyStore = Place the JKS keystore path
          keyAlias = Place the JKS keystore alias
          keyStorePassword = Place the JKS keystore password
          privateKeyPassword = Place the private key password
        }
      }
    }
    
    // PostgreSQL, persistent // ATENTION HERE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ATTENTION HERE
    db {
        driver = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://URL-TO-THE-DATABASE:5432/DATABASE-NAME"
        username = PostgreSQL login name 
        password = PostgreSQL login password
    }
    
    feed {
        every = 2 // hours // How often feeds should be harvested
        storage = "data/feeds.db" // Internal database with visited sites. Just keep it as is.
    }
    
    auth {
      probe {
        pepper = "Pepper for the probe authentication. Change it to something random."
        realm = "IOC Server Probes" // Leave it as is
      }
      user {
        pepper = "Pepper for the user authentication. Change it to something random."
        realm = "IOC Server Users" // Leave it as is
        jwt {
          secret = "Jwt secret. Change this to something random."
          timeout = 600 // minutes // JWT token expiration
          issuer = "IOC-Server" // Leave it as is
          audience = "IOC-Server-Users" // Leave it as is
        }
      }
    }
    ```
  
Run with the new configuration file

```$bash
cd build/libs

java -jar ioc-checker-server-[VERSION].jar -config=application.conf 
```

Run with the new configuration file & populate database tables. 
```bash
java -jar ioc-checker-server.jar -config=application.conf --admin-email admin@admin.com --admin-pass admin --probe-name probe1 --probe-key probeKey --dummy-ioc --default-feed-sources
``` 