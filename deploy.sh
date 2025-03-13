# WebSocket 서버인 경우 Blue-Green 배포 실행
if echo "${JAR_FILES}" | grep -q "module-websocket"; then
  cd /containers/spring-10k-chat-server/websocket
  if docker ps | grep -q "blue"; then
    echo "Docker container with 'blue' is running"

    echo "$(date) - Switching to 'green'.."
    docker-compose up -d green-spring-10k-chat-websocket1 green-spring-10k-chat-websocket2 green-spring-10k-chat-websocket3
    echo "$(date) - green up"
    sleep 20

    ws_length=$(curl -s localhost:8761/helper/info | jq '. | length')
    echo "$(date) - ws_length: $ws_length"
    if [ "$ws_length" -eq 6 ]; then
      echo "$(date) - Stopping 'blue'.."
      curl http://localhost:13305/remove-ip-when-deploy?deploy=BLUE
      docker stop blue-spring-10k-chat-websocket1 blue-spring-10k-chat-websocket2 blue-spring-10k-chat-websocket3
    fi
  else
    echo "No Docker container with 'green' is running"

    echo "$(date) - Switching to 'blue'.."
    docker-compose up -d blue-spring-10k-chat-websocket1 blue-spring-10k-chat-websocket2 blue-spring-10k-chat-websocket3
    echo "blue up"echo "$(date) - blue up"

    sleep 20

    ws_length=$(curl -s localhost:8761/helper/info | jq '. | length')
    echo "$(date) - ws_length: $ws_length"
    if [ "$ws_length" -eq 6 ]; then
      echo "$(date) - Stopping 'green'.."
      curl http://localhost:13305/remove-ip-when-deploy?deploy=GREEN
      docker stop green-spring-10k-chat-websocket1 green-spring-10k-chat-websocket2 green-spring-10k-chat-websocket3
    fi
  fi
else
  echo "JAR files do not contain the specific string"
fi