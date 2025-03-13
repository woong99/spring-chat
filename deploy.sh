# WebSocket 서버인 경우 Blue-Green 배포 실행
if echo "${JAR_FILES}" | grep -q "module-websocket"; then
  cd /containers/spring-10k-chat-server/websocket
  if docker ps | grep -q "blue"; then
    echo "Docker container with 'blue' is running"

    echo "Switching to 'green'.."
    docker-compose up -d green-spring-10k-chat-websocket1 green-spring-10k-chat-websocket2 green-spring-10k-chat-websocket3

    sleep 5

    ws-length = $(curl -s localhost:8761/helper/info | jq '. | length')
    if [ "$length" -eq 6]; then
      echo "Stopping 'blue'.."''
      curl http://localhost:13305/remove-ip-when-deploy?deploy=blue
      docker stop blue-spring-10k-chat-websocket1 blue-spring-10k-chat-websocket2 blue-spring-10k-chat-websocket3
    fi
  else
    echo "No Docker container with 'green' is running"

    echo "Switching to 'blue'.."
    docker-compose up -d blue-spring-10k-chat-websocket1 blue-spring-10k-chat-websocket2 blue-spring-10k-chat-websocket3

    sleep 5

    ws-length = $(curl -s localhost:8761/helper/info | jq '. | length')
    if [ "$length" -eq 6]; then
      echo "Stopping 'green'.."
      curl http://localhost:13305/remove-ip-when-deploy?deploy=blue
      docker stop green-spring-10k-chat-websocket1 green-spring-10k-chat-websocket2 green-spring-10k-chat-websocket3
    fi
  fi
else
  echo "JAR files do not contain the specific string"
fi