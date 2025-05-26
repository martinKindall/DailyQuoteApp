# Daily Quote App

Receive every morning 1 inspiring quote in your email to kick off the day with the right foot.

## Architecture

- S3 for storing quotes
- SES for sending emails
- Eventbridge to schedule an email every morning
- Lambda as the glue of all above

## Requirements

- aws-cli/2.27.12 or greater
- AWS CDK v2.1014.0 or greater
- Java 21
- Maven 3.9.2 or greater
- AWS SES (Simple Email Service) identity with a Domain you own and a verified email.

## Compile

```bash
cd app
mvn clean package
```

## Configure

Copy __.env.example__ and create a file called __.env__ at the root of the project. Fill it with the right information from your AWS SES identity.

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

Important! The first deploy will create the S3 bucket. You will need to upload a json file called _inspirational_quotes.json_ at the root of the S3. The content of this json contains the quotes following the format of the class __Quotes.java__, basically:

```json
{
  "data": [
    "id": 1, "text": "an inspirational quote", "read": false,
    "id": 2, "text": "another inspirational quote", "read": false,
    "id": 3, ...
  ]
}
```

Initially all quotes must be initialized with the field _read_ set to false.