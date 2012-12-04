source settings.sh

cd $BUILDDIR

#scp -i ~/.ssh/id_web4 -r buildRepo kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/repo$VERSION
rsync -av -e "ssh -i $HOME/.ssh/id_web4" --delete buildRepo/ kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/repo$VERSION/

#scp -i ~/.ssh/id_web4 -r apps/*$VERSION* kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/apps
rsync -av -e "ssh -i $HOME/.ssh/id_web4" apps/*$VERSION* kasemir@ics-web.sns.ornl.gov:/var/www/html/css/updates/apps




