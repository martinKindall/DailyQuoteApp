import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as events from 'aws-cdk-lib/aws-events';
import * as targets from 'aws-cdk-lib/aws-events-targets';
import * as iam from 'aws-cdk-lib/aws-iam';

import * as dotenv from 'dotenv';
dotenv.config();


export class QuotesAppStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // S3
    const storeBucket = new s3.Bucket(this, "Morsa_Programando_AppQuotes");

    // Lambda
    const lambdaLogic = new lambda.Function(this, "AppQuotesLambda", {
      runtime: lambda.Runtime.JAVA_21,
      handler: "org.morsaprogramando.app_quotes.App::handleRequest",
      code: lambda.Code.fromAsset("./app/target/app.jar"),
      memorySize: 512,
      timeout: cdk.Duration.seconds(30),
      environment: {
        "bucketName": storeBucket.bucketName,
        "SES_ARN": process.env.SES_ARN!,
        "EMAIL_ADDR": process.env.EMAIL_ADDR!,
        "TO_ADDR": process.env.TO_ADDR!
      }
    });

    const sesArnSender = process.env.SES_ARN!;
    const sesArnReceiver = process.env.SES_ARN_RECEIVER!;

    const sesPolicy = new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'ses:SendEmail'
      ],
      resources: [sesArnSender, sesArnReceiver],
    });

    lambdaLogic.addToRolePolicy(sesPolicy);
    storeBucket.grantReadWrite(lambdaLogic);

    // Eventbridge
    const rule = new events.Rule(this, 'Daily8amRule', {
      schedule: events.Schedule.cron({
        minute: '0',
        hour: '6',
      }),
    });

    // Set Lambda as target
    rule.addTarget(new targets.LambdaFunction(lambdaLogic));
  }
}
