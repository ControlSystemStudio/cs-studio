source settings.sh

scp -i ~/.ssh/id_web4 -r build/buildRepo kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/repo$VERSION
scp -i ~/.ssh/id_web4 -r apps/*$VERSION* kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/apps
