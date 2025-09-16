resource "aws_api_gateway_rest_api" "api" {
  name = "${var.project_name}-rest"
  body = file("${path.module}/../Wires_import_recipients_API.yaml")

  endpoint_configuration {
    types = ["PRIVATE"]
  }

  tags = var.tags
}

resource "aws_vpc_endpoint" "execute_api" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${var.aws_region}.execute-api"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.vpce_subnet_ids
  security_group_ids  = [aws_security_group.vpce_sg.id]
  private_dns_enabled = false

  tags = merge(var.tags, { Name = "${var.project_name}-api-gw-vpce" })
}

data "aws_iam_policy_document" "private_api_policy" {
  statement {
    sid     = "AllowVPCEInvoke"
    actions = ["execute-api:Invoke", "execute-api:ManageConnections"]
    resources = ["arn:aws:execute-api:${var.aws_region}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.api.id}/*/*/*"]

    principals {
      type        = "AWS"
      identifiers = ["*"]
    }

    condition {
      test     = "StringEquals"
      variable = "aws:SourceVpce"
      values   = [aws_vpc_endpoint.execute_api.id]
    }
  }
}

resource "aws_api_gateway_rest_api_policy" "policy" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  policy      = data.aws_iam_policy_document.private_api_policy.json
}

resource "aws_api_gateway_deployment" "dep" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  triggers = {
    redeploy = sha1(file("${path.module}/../Wires_import_recipients_API.yaml"))
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "stage" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  deployment_id = aws_api_gateway_deployment.dep.id
  stage_name    = var.stage_name

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.apigw_access.arn
    format          = jsonencode(local.apigw_log_map)
  }

  xray_tracing_enabled = false

  method_settings {
    path               = "*/*"
    metrics_enabled    = true
    logging_level      = "INFO"
    data_trace_enabled = true
  }

  tags = var.tags
}
