terraform {
  required_version = ">= 1.6.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.60"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_caller_identity" "current" {}
data "aws_region"          "current" {}

resource "aws_cloudwatch_log_group" "apigw_access" {
  name              = "/aws/apigw/${var.project_name}/access"
  retention_in_days = var.log_retention_days
}

resource "aws_security_group" "vpce_sg" {
  name        = "${var.project_name}-vpce-sg"
  description = "Allow ALB to reach VPCE and health checks"
  vpc_id      = var.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, { Name = "${var.project_name}-vpce-sg" })
}

resource "aws_security_group" "alb_sg" {
  name        = "${var.project_name}-alb-sg"
  description = "Allow inbound HTTPS to ALB"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = var.alb_ingress_cidrs
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, { Name = "${var.project_name}-alb-sg" })
}

resource "aws_security_group_rule" "vpce_from_alb_443" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  security_group_id        = aws_security_group.vpce_sg.id
  source_security_group_id = aws_security_group.alb_sg.id
  description              = "ALB to VPCE HTTPS"
}
