source `dirname $BASH_SOURCE`/common.sh

# clean up previuos deploy
$SSH "if [ -d ${PROJECT_NAME}-webapps ]; then rm -r ${PROJECT_NAME}-webapps ; fi"

log_last_exit_code "$0"

# copy new webapps
scp -r `dirname $BASH_SOURCE`/../webapps ${USERNAME}@${WEBAPP_SERVER_HOSTNAME}:${PROJECT_NAME}-webapps

log_last_exit_code "$0"

