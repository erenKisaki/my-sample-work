data "aws_network_interface" "vpce_enis" {
  for_each = toset(aws_vpc_endpoint.execute_api.network_interface_ids)
  id       = each.key
}

locals {
  vpce_ips = [for eni in data.aws_network_interface.vpce_enis : eni.private_ip]
}

resource "aws_lb" "this" {
  name               = "${var.project_name}-alb"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = var.alb_subnet_ids
  tags               = var.tags
}

resource "aws_lb_target_group" "vpce" {
  name        = "${var.project_name}-tg"
  port        = 443
  protocol    = "HTTPS"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    interval            = 60
    path                = "/ping"
    port                = "443"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    protocol            = "HTTPS"

    matcher {
      http_code = "200-399"
    }
  }

  tags = var.tags
}

resource "aws_lb_target_group_attachment" "vpce_attachments" {
  for_each         = toset(local.vpce_ips)
  target_group_arn = aws_lb_target_group.vpce.arn
  target_id        = each.key
  port             = 443
}

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.this.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = var.alb_certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.vpce.arn
  }
}
