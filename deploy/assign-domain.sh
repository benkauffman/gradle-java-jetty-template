#!/bin/bash
echo "RUNNING: assign-domain.sh"
echo "Domain: $1"
echo "Name: $2"
echo "IP: $3"

# Domain to update.
Domain=$1

# Subdomain, if empty will default to '@' update domain and update root
Name=$2

# The IP address that you want to assign, if empty will default to your current IP address
PublicIP=$3

# The file containing the key/secret for godaddy API
CredentialFullFile='./dn.secret'



#SHELL SCRIPT STARTS HERE
if [ -f "$CredentialFullFile" ]; then
    . $CredentialFullFile
    else
    echo Please enter the godaddy key and secret key
    echo Can be created at https://developer.godaddy.com/keys/
    echo MAKE CERTAIN THAT YOU USE PRODUCTION KEYS AND NOT TEST KEYS
    echo
    echo Enter the key:
    read Key
    echo
    echo Enter the secret:
    read Secret
fi



# Advanced settings - change only if you know what you're doing :-)
# Record type, as seen in the DNS setup page, default A.
Type=A

# Record name, as seen in the DNS setup page, default @.
if [ -z "${Name}" ]; then
  echo
  echo "Subdomain not specified, defaulting to root '@'"
  echo
  Name=@
fi

# Time To Live in seconds, minimum default 600 (10mins).
# If your public IP seldom changes, set it to 3600 (1hr) or more for DNS servers cache performance.
TTL=600

# External URL to check for current Public IP, must contain only a single plain text IP.
# Default http://api.ipify.org.
CheckURL=http://api.ipify.org

# Optional scripts/programs/commands to execute on successful update. Leave blank to disable.
# This variable will be evaluated at runtime but will not be parsed for errors nor execution guaranteed.
# Take note of the single quotes. If it's a script, ensure it's executable i.e. chmod 755 ./script.
# Example: SuccessExec='/bin/echo "$(date): My public IP changed to ${PublicIP}!">>/var/log/GoDaddy.sh.log'
SuccessExec=''

# Optional scripts/programs/commands to execute on update failure. Leave blank to disable.
# This variable will be evaluated at runtime but will not be parsed for errors nor execution guaranteed.
# Take note of the single quotes. If it's a script, ensure it's executable i.e. chmod 755 ./script.
# Example: FailedExec='/some/path/something-went-wrong.sh ${Update} && /some/path/email-script.sh ${PublicIP}'
FailedExec=''
# End settings

Curl=$(/usr/bin/which curl 2>/dev/null)
[ "${Curl}" = "" ] &&
echo "Error: Unable to find 'curl CLI'." && exit 1
[ -z "${Key}" ] || [ -z "${Secret}" ] &&
echo "Error: Requires API 'Key/Secret' value." && exit 1
[ -z "${Domain}" ] &&
echo "Error: Requires 'Domain' value." && exit 1
[ -z "${Type}" ] && Type=A
[ -z "${Name}" ] && Name=@
[ -z "${TTL}" ] && TTL=600
[ "${TTL}" -lt 600 ] && TTL=600
[ -z "${CheckURL}" ] && CheckURL=http://api.ipify.org
echo -n "Checking current 'Public IP' from '${CheckURL}'..."


if [ -z "${PublicIP}" ]; then
  echo
  echo "IP address not specified, I'll use yours"
  echo
  PublicIP=$(${Curl} -kLs ${CheckURL})
fi


if [ $? -eq 0 ] && [[ "${PublicIP}" =~ [0-9]{1,3}\.[0-9]{1,3} ]];then
  echo "${PublicIP}!"
else
  echo "Fail! ${PublicIP}"
  eval ${FailedExec}
  exit 1
fi

echo -n "Checking '${Domain}' IP records from 'GoDaddy'..."
Check=$(${Curl} -kLsH"Authorization: sso-key ${Key}:${Secret}" \
-H"Content-type: application/json" \
https://api.godaddy.com/v1/domains/${Domain}/records/${Type}/${Name} \
2>/dev/null|sed -r 's/.+data":"(.+)","t.+/\1/g' 2>/dev/null)
if [ $? -eq 0 ] && [ "${Check}" = "${PublicIP}" ];then
  echo -e "unchanged!\nCurrent 'Public IP' matches 'GoDaddy' records. No update required!"
else
  echo -en "changed!\nUpdating '${Domain}'..."
  Update=$(${Curl} -kLsXPUT -H"Authorization: sso-key ${Key}:${Secret}" \
  -H"Content-type: application/json" \
  https://api.godaddy.com/v1/domains/${Domain}/records/${Type}/${Name} \
  -d "{\"data\":\"${PublicIP}\",\"ttl\":${TTL}}" 2>/dev/null)
  if [ $? -eq 0 ] && [ "${Update}" = "{}" ];then
    echo "Success!"
    eval ${SuccessExec}
  else
    echo "Fail! ${Update}"
    eval ${FailedExec}
    exit 1
  fi
fi

exit $?
