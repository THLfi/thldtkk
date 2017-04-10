#
# reads environment variables from parent directory
# the file is created by jenkins job
#
source `dirname $BASH_SOURCE`/../build_env.sh

# Set options to stop execution on error
set -e
set -o pipefail

# \$1 = message
log_last_exit_code() {
    echo "$? $1" >> exit_codes
}

# \$1 = message
# \$2 = exit code
log_exit_code_and_exit() {
    echo $1
    echo "$2 $1" >> exit_codes
    exit $2
}

# \$1 = sleep
wait_a_while() {
    if [ -z ${1} ]
    then
        echo "$1 is not set. Sleeping 10s..."
	sleep 10s
    else
        echo "Sleeping ${1}..."
        sleep $1
    fi
}

if [ ! $USERNAME ]
then
    log_exit_code_and_exit "Missing USERNAME" 1
fi

if [ ! $PROJECT_NAME ]
then
    log_exit_code_and_exit "Missing PROJECT_NAME" 1
fi

if [ ! $WEBAPP_SERVER_HOSTNAME ]
then
    log_exit_code_and_exit "Missing WEBAPP_SERVER_HOSTNAME" 1    
fi

if [ ! $WEBAPP_DEPLOY_FOLDER ]
then
    log_exit_code_and_exit "Missing WEBAPP_DEPLOY_FOLDER" 1
fi

if [ ! $WEBAPP_SERVICE ]
then
    log_exit_code_and_exit "Missing WEBAPP_SERVICE" 1
fi

SSH="ssh ${USERNAME}@${WEBAPP_SERVER_HOSTNAME}"

