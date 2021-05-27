#!/bin/bash
echo "publish------------"

#检查有没有已经启动的进程 如果有 就杀死
process_id=`ps -ef | grep wiki.jar | grep -v grep |awk '{print $2}'`
if [ $process_id ] ; then
  sudo kill -9 $process_id
  fi

  source /etc/profile
#  后台启动项目
  nohup java -jar -Dspring.profiles.active=prod ~/wiki/wiki.jar > /dev/null 2>&1 &

  echo "end publish"