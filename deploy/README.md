# Deployment Notes

## GitHub Secrets

Set these repository secrets before enabling the deployment workflow:

- `EC2_HOST`
- `EC2_SSH_KEY`
- `ENV_FILE`

`ENV_FILE` should contain the full runtime `.env` content.

The workflow currently hardcodes:

- SSH user: `ec2-user`
- SSH port: `22`
- deploy path: `/home/ec2-user/apps/tagvote`

Example:

```env
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
APP_HOST_PORT=8081
MYSQL_ROOT_PASSWORD=change-me-root
MYSQL_DATABASE=tagvote
MYSQL_USER=tagvote
MYSQL_PASSWORD=change-me-app
```

## Server Prerequisites

- Docker and Docker Compose plugin installed
- Nginx installed on the host
- TLS certificate already issued for `vote.newdawnsoi.site`
- The SSH user can run `sudo nginx -t` and `sudo systemctl reload nginx`

## Nginx

The workflow copies `deploy/nginx/vote.newdawnsoi.site.conf` to:

- `/etc/nginx/sites-available/vote.newdawnsoi.site.conf`

and enables it with:

- `/etc/nginx/sites-enabled/vote.newdawnsoi.site.conf`

If your EC2 host uses `/etc/nginx/conf.d` instead, adjust the workflow script before first deploy.
