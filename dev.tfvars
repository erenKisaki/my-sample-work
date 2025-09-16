project_name        = "wires-bulk-apigw"
aws_region          = "us-east-1"

vpc_id              = "vpc-xxxxxxxx"
vpce_subnet_ids     = ["subnet-aaa", "subnet-bbb"]
alb_subnet_ids      = ["subnet-ccc", "subnet-ddd"]

alb_certificate_arn = "arn:aws:acm:us-east-1:111111111111:certificate/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
alb_ingress_cidrs   = ["100.78.136.93/32","100.78.136.110/32"]

route53_zone_name   = "dev.aws.example.net."
route53_record_name = "ccep-apigw.dev.aws.example.net"

tags = {
  App   = "Wires"
  Env   = "dev"
  Owner = "Payments"
}
