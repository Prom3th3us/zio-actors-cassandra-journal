# Cassandra Journal Plugin for ZIO Actors Persistance
| Project Stage | CI | Release | Licence | Issues |
| --- | --- | --- | --- | --- |
| ![Project stage][Stage] | [![CI][Badge-CI]][Link-CI] | [![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases] | [![License][Badge-Licence]][Link-Licence] | [![Issues][Badge-Issues]][Link-Issues] |

|Technology   | Version
|-------------|---------- |
|Scala        | 2.13 |
|SBT          | 1.7.1 |
|JAVA         | 17 |
|ZIO          | 2.0.2 |

## Summary
This plugin uses `quill-cassandra` as cql client to connect to the database.

The schema follows the [akka-persistance standard](https://doc.akka.io/docs/akka-persistence-cassandra/current/journal.html#schema) to favor apps willing to migrate.

Others can simply sit on top, or fork the project to fit their custom needs.

<hr/>

## sbt
Add the library to your project dependencies:

```scala
libraryDependencies += "io.github.prom3th3us" %% "zio-actors-cassandra-journal" % "<version>"
```

### ⚔️ Known issues
Because of `zio.actors.persistence.journal.Journal` plugin limitations, we cannot configure custom serializers for our events.
For this reason, we decided to use `jackson` here. This forces us to declare nasty `@JsonTypeInfo` annotations on top of our event case classe for it to work, but it does the job quite good, fast and reliable.

## 🤝 Contributing

The best way to contribute right now is to provide feedback.

When contributing to this project and interacting with others, please follow our [Contributing Guidelines](./CONTRIBUTING.md).


[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/io.github.prom3th3us/zio-actors-cassandra-journal_2.13.svg?server=https%3A%2F%2Fs01.oss.sonatype.org "Sonatype Releases"
[Link-SonatypeReleases]: https://s01.oss.sonatype.org/content/repositories/releases/io/github/prom3th3us/zio-actors-cassandra-journal_2.13/ "Sonatype Releases"
[Badge-CI]: https://github.com/prom3th3us/zio-actors-cassandra-journal/actions/workflows/ci.yml/badge.svg
[Link-CI]: https://github.com/prom3th3us/zio-actors-cassandra-journal/actions/workflows/ci.yml
[Stage]: https://img.shields.io/badge/Project%20Stage-Experimental-yellow.svg
[Badge-Licence]: https://img.shields.io/badge/License-Apache_2.0-blue.svg 
[Link-Licence]: https://opensource.org/licenses/Apache-2.0
[Badge-Issues]: https://img.shields.io/github/issues/prom3th3us/zio-actors-cassandra-journal
[Link-Issues]: https://isitmaintained.com/project/Prom3th3us/zio-actors-cassandra-journal
