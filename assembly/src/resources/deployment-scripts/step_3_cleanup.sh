source `dirname $BASH_SOURCE`/common.sh

$SSH rm -r ${PROJECT_NAME}-webapps

log_last_exit_code "$0"

