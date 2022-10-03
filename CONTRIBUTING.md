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

