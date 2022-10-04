# Cassandra Journal Plugin for ZIO Actors Persistance
|Technology   | Version
|-------------|---------- |
|Scala        | 2.13 |
|SBT          | 1.7.1 |
|JAVA         | 17 |
|ZIO          | 2.0.2 |

This plugin uses `quill-cassandra` as cql client to connect to the database.

The schema follows the akka-persistance standard to favor apps willing to migrate.

Others can simply sit on top, or fork the project to fit their custom needs.

<hr/>

### ⚔️ Known issues
Because of `zio.actors.persistence.journal.Journal` plugin limitations, we cannot configure custom serializers for our events.
For this reason, we decided to use `jackson` here. This forces us to declare nasty `@JsonTypeInfo` annotations on top of our event case classe for it to work, but it does the job quite good, fast and reliable.

## 🤝 Contributing

The best way to contribute right now is to provide feedback.

When contributing to this project and interacting with others, please follow our [Contributing Guidelines](./CONTRIBUTING.md).
