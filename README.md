# ZIO Actors + Shardcake
|Technology   | Version
|-------------|---------- |
|Scala        | 2.13 |
|SBT          | 1.7.1 |
|JAVA         | 17 |
|ZIO          | 2.0.2 |

This is an effort on trying to combine the usage of these two libraries:
- [zio-actors](https://zio.github.io/zio-actors/)
- [shardcake](https://devsisters.github.io/shardcake/)

The goal of this project is to serve as a toy example for:
- filing issues to the libraries
- to inspire others to try out this new stack.
- opening discussions on the discord 👾 channels:
    + [zio-actors](https://discord.gg/fQdPv8JX)
    + [shardcake](https://discord.gg/4MuKJryZ)

The legend says:
> the power from fusing (👉👈) these two libraries will be a killer for akka persistance + sharding.

And we are here to prove that! 😈

### Run the example
The app provided combines two examples given by the libraries:
- https://zio.github.io/zio-actors/docs/overview/overview_persistence
- https://devsisters.github.io/shardcake/docs/#an-example

To run the example:
- [terminal-1] ```$ cd docker; docker-compose up redis -d```
- [terminal-2] ```$ sbt "zio/runMain example.infra.ShardManagerApp"```
- [terminal-3] ```$ sbt "zio/runMain example.app.GuildESApp"```

## 🤝 Contributing

The best way to contribute right now is to provide feedback.
Give the demo a test drive.

When contributing to this project and interacting with others, please follow our [Contributing Guidelines](./CONTRIBUTING.md).
