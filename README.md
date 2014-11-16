Rorschach
=========
[![Build Status](https://travis-ci.org/twbs/rorschach.svg?branch=master)](https://travis-ci.org/twbs/rorschach)

Rorschach ([@twbs-rorschach](https://github.com/twbs-rorschach)) is a [Bootstrap](https://github.com/twbs/bootstrap/) pull request sanity checker bot.

Rorschach watches for new or modified pull requests, runs sanity checks on them, and if a sanity check fails, it leaves an informative comment on the pull request explaining the mistake and how to fix it, and then closes the pull request.

## Motivation

You're a member of a popular open source project that involves front-end Web technologies. Cool.

But due to the project's popularity, you will get some pull requests proposed by folks who don't read the contributing guidelines, folks who don't understand asset pipelines, or just plain newbies (hey, everybody's gotta start somewhere).

By automating the process of sanity-checking proposed pull requests:
* Feedback can be given to the pull requester *extremely* quickly, decreasing the turnaround time on fixing the pull request and decreasing friction for the requester.
* The requester can be referred to canonical write-ups explaining the common mistakes in detail, so that they have full context for understanding the reported mistakes.
* Issue triagers will have less work to deal with, and human error in failing to zealously check every single pull request for every common mistake is eliminated.

## Checks performed

* [Pull requests must never be against the `gh-pages` branch.](docs/against-gh-pages.md)
* [Pull requests must not attempt to merge the `gh-pages` branch into the `master` branch.](docs/gh-pages-into-master.md)
* [Pull requests must never edit `/dist/js/bootstrap.js` without also editing `/js/*.js`](docs/js.md).
* [Pull requests must never edit `/dist/css/bootstrap.css` without also editing `/less/*.less`.](docs/css.md)
* [Pull requests must never modify the `CNAME` file.](docs/cname.md)

## GitHub webhook configuration

* Payload URL: `http://your-domain.example/rorschach`
* Content type: `application/json`
* Secret: Same as your `web-hook-secret-key` config value
* Which events would you like to trigger this webhook?: "Pull Request"

## Acknowledgments
We all stand on the shoulders of giants and get by with a little help from our friends. Rorschach is written in [Scala](http://www.scala-lang.org) and built on top of:
* [Akka](http://akka.io) & [Spray](http://spray.io), for async processing & HTTP
* [Eclipse EGit GitHub library](https://github.com/eclipse/egit-github), for working with [the GitHub API](https://developer.github.com/v3/)

## See also
* [LMVTFY](https://github.com/cvrebert/lmvtfy), Rorschach's sister bot who does HTML validation
* [Savage](https://github.com/cvrebert/savage), Rorschach's sister bot who runs cross-browser JS tests on Sauce Labs
