# Contributing
Thanks for considering contributing.

The best way to contribute right now is to try things out and provide feedback,
but we also accept contributions to the documentation and obviously to the
code itself.

This document contains guidelines to help you get started and how to make sure
your contribution gets accepted.

### Repo setup
Something handy to have in your git hooks is the one in `./scripts/githooks/pre-commit`
which validates coding standards when trying to run a `git commit` command.

install pre-commit: https://pre-commit.com/

and launch:
```shell
$ cd scripts/githooks
$ pre-commit install
```

### Bug reports

[Submit an issue](https://github.com/ffakenz/zio-actors-shardcake/issues/new)

For bug reports, it's very important to explain
* what version you used,
* steps to reproduce (or steps you took),
* what behavior you saw (ideally supported by logs), and
* what behavior you expected.

### Run the tests
To run the integration-tests we have to make sure you have a cassandra instance up and running.
For that reason there is a provided [docker manifest](./docker/docker-compose.yml) you can use to fire up your instance (`docker-compose up -d`).

Once your instance is ready we must continue by setting up the database.
The idea is to mimic the akka-persistance environment to facilite projects willing to migrate out of it.
To do that we need to run the [script provided](./scripts/cassandra.sh) as `./cassandra.sh setup`.
This will create all the necessary tables following the [akka-persistance schema](https://doc.akka.io/docs/akka-persistence-cassandra/current/journal.html#schema).

Finally we can execute `sbt test` to spin them up.

### Release process
1/ merge/push to main
1/ create tag: ```$ git tag -a vX.Y.Z -m "vX.Y.Z"```
2/ push the tag: ```$ git push origin vX.Y.Z```

Repository link: https://s01.oss.sonatype.org/#nexus-search;quick~io.github.prom3th3us