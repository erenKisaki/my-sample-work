locals {
  apigw_log_map = {
    requestId              = "$context.requestId"
    ip                     = "$context.identity.sourceIp"
    caller                 = "$context.identity.caller"
    user                   = "$context.identity.user"
    requestTime            = "$context.requestTime"
    httpMethod             = "$context.httpMethod"
    resourcePath           = "$context.resourcePath"
    status                 = "$context.status"
    protocol               = "$context.protocol"
    responseLength         = "$context.responseLength"
    apiId                  = "$context.apiId"
    domainName             = "$context.domainName"
    domainPrefix           = "$context.domainPrefix"
    errorMessage           = "$context.error.message"
    validationErrorMessage = "$context.error.validationErrorString"
    userAgent              = "$context.identity.userAgent"
    resourceId             = "$context.resourceId"
    wafAclId               = "$context.webaclArn"
    awsEndpointRequestId   = "$context.awsEndpointRequestId"

    integrationError     = "$context.integration.error"
    integrationStatus    = "$context.integration.status"
    integrationRequestId = "$context.integration.requestId"
    integrationLatency   = "$context.integration.latency"

    authorizerError   = "$context.authorizer.error"
    authorizerLatency = "$context.authorizer.latency"
    authorizerStatus  = "$context.authorizer.status"

    authenticateError   = "$context.authenticate.error"
    authenticateLatency = "$context.authenticate.latency"
    authenticateStatus  = "$context.authenticate.status"
  }
}
