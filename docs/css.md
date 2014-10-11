The CSS files in [`/bootstrap/dist/css/`](https://github.com/twbs/bootstrap/tree/master/dist/css) are compiled from the [Less](http://lesscss.org/) source files in [`/bootstrap/less/`](https://github.com/twbs/bootstrap/tree/master/less) via a [Grunt](http://gruntjs.com) task that invokes the [Less preprocessor](http://lesscss.org/). These compiled CSS files **should not be edited manually** when developing Bootstrap itself. Any such edits will be **overwritten and lost** the next time the Grunt task runs.

You should edit the `.less` source files in [`/bootstrap/less/`](https://github.com/twbs/bootstrap/tree/master/less) instead.

Bootstrap isn't written directly in CSS, but is instead written in [Less](http://lesscss.org/), a stylesheet language that compiles down to CSS.
