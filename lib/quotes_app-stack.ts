import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as lambda from 'aws-cdk-lib/aws-lambda';


export class QuotesAppStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const storeBucket = new s3.Bucket(this, "Morsa_Programando_AppQuotes");

    const lambdaLogic = new lambda.Function(this, "AppQuotesLambda", {
      runtime: lambda.Runtime.JAVA_21,
      handler: "com.codigomorsa.app_quotes.App::handleRequest",
      code: lambda.Code.fromAsset("./app/build/libs/apps..."),   // TODO
      memorySize: 256,
      timeout: cdk.Duration.seconds(30)
    });

    storeBucket.grantReadWrite(lambdaLogic);
  }
}
