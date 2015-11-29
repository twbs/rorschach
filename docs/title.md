This warning is triggered if the title of a pull request:

* Is exactly "master" (ignoring case)
* Is exactly "Merge pull request #1 from twbs/master"
* Starts with "Create" (which is used [when creating a new file from the GitHub web UI](https://help.github.com/articles/creating-new-files/))

These sorts of titles are strongly correlated with pull requests which were created to test out Git or GitHub, rather than proposing any legitimate change to Bootstrap.

If you want to test out Git or GitHub, **[create your own personal repository](https://guides.github.com/activities/hello-world/) and conduct your experiments there instead**, where they won't bother anyone. Using the repositories of public projects (such as Bootstrap) for such experiments wastes the time of those projects' maintainers and is thus considered rude & annoying. Which is why your pull request was automatically closed.

To learn more about using Git or GitHub, check out the links on the following GitHub Help page: https://help.github.com/categories/bootcamp/

(If your legitimate pull request accidentally triggered this message: Our apologies for the inconvenience! You can either open a new pull request with a different title, or ask one of [the Bootstrap Core Team members](http://getbootstrap.com/about/#team) to manually reopen your auto-closed pull request. And thanks for contributing to Bootstrap!)
