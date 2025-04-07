States:
  ListAndProcess:
    Type: Task
    Resource: arn:aws:lambda:your-region:your-account:function:ProcessS3Page
    ResultPath: $.result
    Next: CheckMore

  CheckMore:
    Type: Choice
    Choices:
      - Variable: $.result.isTruncated
        BooleanEquals: true
        Next: ListAndProcessNext
    Default: Done

  ListAndProcessNext:
    Type: Task
    Resource: arn:aws:lambda:your-region:your-account:function:ProcessS3Page
    Parameters:
      continuationToken.$: $.result.nextContinuationToken
    ResultPath: $.result
    Next: CheckMore

  Done:
    Type: Succeed
