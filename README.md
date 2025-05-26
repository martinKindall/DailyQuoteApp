# Daily Quote App

Receive every morning 1 inspiring quote in your email to kick off the day with the right foot.

## Requirements

- aws-cli/2.27.12 or greater
- AWS CDK v2.1014.0 or greater
- Java 21
- Maven 3.9.2 or greater

## Compile

```bash
cd app
mvn clean package
```

## Configure

Copy __.env.example__ and create a file called __.env__ at the root of the project. Fill it with the right information.

## Deploy

Make sure you setup your aws cli has the right credentials, or just login using sso

```bash
aws sso login
```

Then,

```bash
cdk bootstrap  # only the first time
cdk diff
cdk deploy
```
