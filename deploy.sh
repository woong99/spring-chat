# 새 컨테이너 시작
start_new_container() {
  local color=$1
  echo "$(date) - Starting '$color'.."
  docker-compose up -d $(docker-compose config --services | grep ^$color | tr '\n' ' ')
  echo "$(date) - $color up"
}

# 이전 컨테이너 중지
stop_old_container() {
  local color=$1
  echo "$(date) - Stopping '$color'.."
  curl "http://localhost:13305/remove-ip-when-deploy?deploy=${color^^}"
  docker stop $(docker ps | grep $color | awk '{print $NF}' | tr '\n' ' ')
}

# 컨테이너들이 정상적으로 실행되었는지 확인
check_ws_container() {
  ws_length=0
  counter=0
  max_attempts=10

  while [ "$ws_length" -ne 6 ] && [ "$counter" -lt "$max_attempts" ]; do
    ws_length=$(curl -s localhost:8761/helper/info | jq '. | length')
    echo "$(date) - ws_length: $ws_length"
    sleep 3
    counter=$((counter + 1))
  done

  if [ "$ws_length" -eq 6 ]; then
    return 0
  else
    return 1
  fi
}

# WebSocket 서버인 경우 Blue-Green 배포 실행
if echo "${JAR_FILES}" | grep -q "module-websocket"; then
  cd /containers/spring-10k-chat-server/websocket
  if docker ps | grep -q "blue"; then
    echo "Docker container with 'blue' is running"

    # Green 컨테이너 실행
    start_new_container "green"

    # Green 컨테이너가 정상적으로 실행되었는지 확인(최대 10번 시도) 후 Blue 컨테이너 중지
    if check_ws_container; then
      stop_old_container "blue"
    fi
  else
    echo "No Docker container with 'green' is running"

    # Blue 컨테이너 실행
    start_new_container "blue"

    # Blue 컨테이너가 정상적으로 실행되었는지 확인(최대 10번 시도) 후 Green 컨테이너 중지
    if check_ws_container; then
      stop_old_container "green"
    fi
  fi
else
  echo "JAR files do not contain the specific string"
fi