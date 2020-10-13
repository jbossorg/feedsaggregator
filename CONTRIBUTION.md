# How to contribute to the project

Feel free to create an issue, or a pull request.

## Release process

1. Perform maven release by 
```
mvn clean release:prepare release:perform
```
2. Wait till [Deploy Maven Action](https://github.com/jbossorg/feedsaggregator/actions?query=workflow%3A%22Deploy+Maven+Package%22) completes.
3. Create a [Github release](https://github.com/jbossorg/feedsaggregator/releases) based on the latest tag and document the release.
