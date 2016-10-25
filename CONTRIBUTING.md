## Contributing

We accept all types of contributions and we are very welcoming to first time contributors.

### Code of Conduct

This project adheres to the Contributor Covenant [code of conduct](CODE_OF_CONDUCT.md).
By participating, you are expected to uphold this code.
Please report unacceptable behavior to [streamplify@beryx.org](mailto:streamplify@beryx.org).

### How Can I Contribute?
 - report bugs
 - suggest enhancements
 - improve / write documentation
 - answer questions
 - promote the project
 - submit new unit tests
 - submit new examples
 - fix bugs
 - implement new features

#### up-for-grabs issues

Issues labeled ![up-for-grabs](https://img.shields.io/badge/-up----for----grabs-blue.svg?logoWidth=-10) indicate tasks specifically chosen to be implemented by contributors. In most cases, these are new features and enhancements with clearly defined requirements, which makes them newcomer-friendly.

Please leave a message before starting to work on a task.
This way, we can mark the issue as grabbed in order to prevent that two people work on the same thing.

If you claimed an issue and are no longer able to work on it, please tell us to make it available again.

Advice for newcomers: Don't be afraid to grab an issue, even if you're not sure you can implement it.
Try your hand at it and don't hesitate to ask for clarification and guidance.

#### Code contributions
If you implement a new feature, your pull request should also contain:
- unit tests - we use [Spock](http://spockframework.org/)
- examples - should be placed in [streamplify-examples](../master/streamplify-examples/src/main/java/org/beryx/streamplify/example)
- documentation - we use [Asciidoctor](http://asciidoctor.org/)

#### Working on your first Pull Request?

You can learn how from this free series: [How to Contribute to an Open Source Project on GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github).


### Development Tools

#### Command Line Build
Streamplify is built with [Gradle](http://www.gradle.org/) and requires JDK 8 or higher.
Clone the GitHub repository, `cd` into the top directory and start the build:

<blockquote><code>git clone https://github.com/beryx/streamplify.git</code>
<br/><code>cd streamplify</code>
<br/><code>./gradlew clean build</code>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
(On Windows: <code>gradlew clean build</code>)
</blockquote>

#### IntelliJ Idea

- make sure that the Groovy plugin is enabled
- open <i>build.gradle</i>

#### Eclipse

- install the [Groovy plugin](https://github.com/groovy/groovy-eclipse/wiki);
  update site: http://dist.springsource.org/snapshot/GRECLIPSE/e4.6/
- install Buildship 1.0.21 or newer;
  update site: http://download.eclipse.org/buildship/updates/e46/releases/1.0/1.0.21.v20161010-1640/
- import the project using the _Gradle Project_ wizard
