# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```



LINK TO PHASE 2:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgCwKYGcUwLLBWJUBQADsFGAJYhnEB2Y8ANmUrUSeZTXQIICuYCAZQCeOJAFtWpClWC0YAcWBikw0RIAmwMMABG2JDAAiW3fvz4A5lAg9CAYmiyLBgEpILZHFC1kI1fIio6FiiUAC0AHxwjMxgAFwwAKK0eDCgIDZyZNQAZtBiPn4BMbSRvPyquGIJbh6hABQ8KHjUSkgANDDEaADu0Oqd4sBk9ACU+OWCIlWRRiZ6zQmBIADWME0tbfjA9GB2jBYIYITZawCqzVCtyjBIAB6eYCj4xtoLBmFlfFNqCRepeR41HUE2+lXEME+URK8RgtUe3nIfhgOWG9CQIOiTFKkUCaEw2FwUD+l2uBk8MGyADcdmR1DBoGl6FAkMB1EJKdR1s18Eh6M0YP8rm00jSRrp0S95vpIV8KtNxAkAHIQGAFMAgBCg+VqSKvUyLeAsrQGdKZMDan4zCL694JeTMPAmtLfGCQFbMS3gsRQrGxGruBGFLkoHggEBBYrYsC+5D4kJEhKGTyEejADnAV3uz3MEG58xWGz2A5HHT0HgGAAyEA8-jxwUJeFxMISySJ3M2ygA9N0UH0oJiYXKrYqYNXa41SW12r3++pxpNvXrpYaHdxw+btrt9mRDsdTjAAJLUGmMekbYXd2f9KVvfRQxcK6owFVqrSar1P30tsc17IotEMSjWJYyCAlQgSY9TzpQUpxuRkAAVsD7G8+QFAA1WlzzgpAe2QudbwNJAHzBJ8Ei4DcgToQFgU-XUbRXJAlmNXAYGaNBfC5bN-EfXVm2jOJx3-UNw0jP0cSiOMGwgmAAAlgCpAwciBLo0yEbILAAQl5Wj80saxbB3PchD5egIB6X8LBsC163Aol+P9JIUhZekzKsvhgNKCJeKqBJx2s+pM34AAVCAPWoBdSPo219ADMQIEUtigk4wj3hInVfJgAQwwjNA6JmBzaD8mtnHpazPLoUD40bYk5IU00YCsiB6U0IRtL0wtDJLMArCQZgjVZVjFGUAIpLsptoQEgbnWoJALIsLZxJjCJhpUMjptwVbAu+ULws6BblCVNpxlWpcGLvQ10LwMgcg5diUBSnY9m6k5qDWKCsKSjiilOr9CthSZoDIAAvYNbigawCCWqrpMTSz-xZABHHgyBc3l+QMTCzy+h6fraM6YsNAR6sakV6gOpAjuUcZCY+SJft+BQ2kPQx8AZqpZUmxzVpgEAWKA6HcTGhM8HtMmKappBRl5-m8102j9KLIyjl6jkACkIH-VbRrAkXwi5oqsr5JBwFJm5ZHpDIzKhocVvx9aACtNeoLagoQXbmH25nDE6K3oBO+3osYhJnDAcnjq3Z7dyOV61h56gIGozIQXZ8Rvymj7sYpiqYfG2qefUCBUBgBO6HuR50YFHmaJTwOOeXC6mLhVA8ESv3YBQMyLVpznU+fM5CE0XB6Wzvv08cjXsgxM2kBzoXdZqhIAHlCH6imYB0CASDlneCwM4to56ll+vQpgLIEDIWWeWy9chA3YVPuax1ZdQ8E37eKrvvuQ6QMAL+gVA9QA7KG9HfWmP8w4oEvqgGmjFe511HP-FkMBGA4DZggn0-0EhIIMKgmyQ557VRksmFAqZ0zPzZG-LeA4dIgkVl1Q+ZYKzN3iolCiGQqI6yIfZe+cUEqmkoiwJad8fKjjcKwpAQptohTCswcYT1lb7jekeE8n17opVEZg3h8MuRIxRmjQWkkF4QQwE0OgOhcElWnv+SAMAWQSLSIIi0aFMZqOSkUTRYDg4wAANqGESJWRIwVEgAF03QIE8Ow80QD8pp3OkRBIfiAlBNCUbb6XIh7AFiWITmS1yJOLseIfhg5oy5z1vkjhcgEDYA3n1XRRTFK7z0uYcwQA
