# Project stats based on git history

Analyze project history to detect some project quality measures. 

E.g. it searches for the top 10 most committed files.

Usually such files are either a source of bugs, therefore updated often, or are too large, hance
contradicting with single-responsibility principle and should be divided to smaller ones.


## Build

```
mvn clean package
```
This will build an executable jar: `target/hotspots-1.0-SNAPSHOT.jar`

## Run

```
java -jar target/hotspots-1.0-SNAPSHOT.jar <path to local git repo>
```

NOTE: it requires git repo to be checked out locally


Example output after running it on [spring-framework](https://github.com/spring-projects/spring-framework) master branch.

```
...

build.gradle - 1374
src/asciidoc/index.adoc - 444
spring-core/src/main/java/org/springframework/core/annotation/AnnotationUtils.java - 375
spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultListableBeanFactory.java - 374
spring-context/src/main/java/org/springframework/context/annotation/ConfigurationClassParser.java - 362
spring-webmvc/src/main/java/org/springframework/web/servlet/config/annotation/WebMvcConfigurationSupport.java - 332
spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.java - 323
spring-web/src/main/java/org/springframework/http/HttpHeaders.java - 323
spring-web/src/main/java/org/springframework/web/client/RestTemplate.java - 320
spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.java - 320

```

It lists the file path and a number of commits.
