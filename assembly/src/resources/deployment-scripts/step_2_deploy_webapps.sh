source `dirname $BASH_SOURCE`/common.sh

$SSH sudo cp ${PROJECT_NAME}-webapps/* ${WEBAPP_DEPLOY_FOLDER}

log_last_exit_code "$0"

$SSH sudo chgrp tomcat ${WEBAPP_DEPLOY_FOLDER}/*.war

log_last_exit_code "$0"

$SSH sudo chmod g+w ${WEBAPP_DEPLOY_FOLDER}/*.war

log_last_exit_code "$0"

