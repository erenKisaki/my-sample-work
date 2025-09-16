data "aws_route53_zone" "hosted" {
  name         = var.route53_zone_name
  private_zone = true
}

resource "aws_route53_record" "alb_alias" {
  zone_id = data.aws_route53_zone.hosted.zone_id
  name    = var.route53_record_name
  type    = "A"

  alias {
    name                   = aws_lb.this.dns_name
    zone_id                = aws_lb.this.zone_id
    evaluate_target_health = false
  }
}
