#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { QuotesAppStack } from '../lib/quotes_app-stack';

const app = new cdk.App();
new QuotesAppStack(app, 'QuotesAppStack', {env: {region: 'eu-central-1'}});
