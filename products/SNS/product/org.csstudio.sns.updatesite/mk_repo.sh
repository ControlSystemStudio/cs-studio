# Re-create the repository
#
# When adding the 'latest' to the existing repo, this should be enough:
# sh mirror_repo.sh repo2.0.2 `pwd`
#
# Kay Kasemir

# Assemble a new repo from individual versions
rm -rf new_repo
sh mirror_repo.sh repo1.9.9 new_repo
sh mirror_repo.sh repo2.0.0 new_repo
sh mirror_repo.sh repo2.0.1 new_repo
sh mirror_repo.sh repo2.0.2 new_repo

# Create backup of the current repository
rm -rf old_repo
mkdir old_repo
mv artifacts.xml content.xml binary features plugins old_repo

# Use the new repository
mv new_repo/* .
