variable "project_name"          { type = string }
variable "aws_region"            { type = string }
variable "vpc_id"                { type = string }
variable "vpce_subnet_ids"       { type = list(string) }
variable "alb_subnet_ids"        { type = list(string) }
variable "alb_certificate_arn"   { type = string }
variable "alb_ingress_cidrs"     { type = list(string) }
variable "route53_zone_name"     { type = string }
variable "route53_record_name"   { type = string }
variable "stage_name"            { type = string  default = "test" }
variable "log_retention_days"    { type = number  default = 30 }
variable "tags"                  { type = map(string) default = {} }
