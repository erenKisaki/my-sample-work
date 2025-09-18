Security Groups Fixed – Split ALB and VPC Endpoint into separate security groups and added a proper ingress rule so the ALB can talk to the API Gateway VPC Endpoint over HTTPS (443).

Access Logs Corrected – Replaced invalid variable usage with a local JSON map and enabled detailed CloudWatch access logging for API Gateway.

ALB Health Check Updated – Used the correct matcher block for /ping health checks to ensure the ALB can verify the API Gateway endpoint health.

Structured Terraform Code – Organized the configuration into separate files (main.tf, apigw.tf, alb.tf, route53.tf, logging.tf, etc.) and added environment variables through dev.tfvars.

Automatic Redeploys – Added a trigger so that API Gateway redeploys automatically whenever the OpenAPI spec (Wires_import_recipients_API.yaml) changes.

Standardized Configuration – Added defaults for stage name, tagging, and Route53 alias configuration for cleaner and reusable infrastructure.
