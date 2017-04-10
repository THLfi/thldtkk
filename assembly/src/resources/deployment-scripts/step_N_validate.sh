if [ -f exit_codes ]
then
    SUM=`awk -F' ' 'BEGIN { val=0; } { val = val + $1; } END { print val }' exit_codes`
    if [ $SUM -ne 0 ]; then log_exit_code_and_exit "FAILED" 1 ; else echo ":-)" ; fi
fi

