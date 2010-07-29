source settings.sh

scp -r apps/*$VERSION* kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/apps

scp -r build/buildRepo kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/repo$VERSION
