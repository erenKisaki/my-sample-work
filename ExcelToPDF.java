{
  "AWSTemplateFormatVersion": "2010-09-09",
  "Description": "Step Function to process paginated S3 data via Lambda",

  "Resources": {
    "ProcessS3StepFunction": {
      "Type": "AWS::StepFunctions::StateMachine",
      "Properties": {
        "StateMachineName": "ProcessS3Paginated",
        "RoleArn": {
          "Fn::GetAtt": ["StepFunctionExecutionRole", "Arn"]
        },
        "DefinitionString": {
          "Fn::Sub": [
            "{\n  \"Comment\": \"Step Function to process S3 in pages using Lambda\",\n  \"StartAt\": \"ListAndProcess\",\n  \"States\": {\n    \"ListAndProcess\": {\n      \"Type\": \"Task\",\n      \"Resource\": \"arn:aws:lambda:${AWS::Region}:${AWS::AccountId}:function:ProcessS3Page\",\n      \"ResultPath\": \"$.result\",\n      \"Next\": \"CheckMore\"\n    },\n    \"CheckMore\": {\n      \"Type\": \"Choice\",\n      \"Choices\": [\n        {\n          \"Variable\": \"$.result.isTruncated\",\n          \"BooleanEquals\": true,\n          \"Next\": \"ListAndProcessNext\"\n        }\n      ],\n      \"Default\": \"Done\"\n    },\n    \"ListAndProcessNext\": {\n      \"Type\": \"Task\",\n      \"Resource\": \"arn:aws:lambda:${AWS::Region}:${AWS::AccountId}:function:ProcessS3Page\",\n      \"Parameters\": {\n        \"continuationToken.$\": \"$.result.nextContinuationToken\"\n      },\n      \"ResultPath\": \"$.result\",\n      \"Next\": \"CheckMore\"\n    },\n    \"Done\": {\n      \"Type\": \"Succeed\"\n    }\n  }\n}",
            {}
          ]
        }
      }
    },

    "StepFunctionExecutionRole": {
      "Type": "AWS::IAM::Role",
      "Properties": {
        "RoleName": "StepFunctionS3ProcessingRole",
        "AssumeRolePolicyDocument": {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": {
                "Service": "states.amazonaws.com"
              },
              "Action": "sts:AssumeRole"
            }
          ]
        },
        "Policies": [
          {
            "PolicyName": "StepFunctionLambdaInvokePolicy",
            "PolicyDocument": {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Action": [
                    "lambda:InvokeFunction"
                  ],
                  "Resource": "arn:aws:lambda:*:*:function:ProcessS3Page"
                }
              ]
            }
          }
        ]
      }
    }
  }
}
