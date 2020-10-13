# How to contribute to the project

Feel free to create an issue, or a pull request.

## Release process

1. Perform maven release by 
```
mvn clean release:prepare release:perform
```
2. Create a release in github from the git tag.
This triggers the [maven deploy action](.github/workflows/maven-publish.yml) to github packages.