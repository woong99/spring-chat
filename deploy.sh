echo "Deploying.."
echo "JAR files: ${JAR_FILES}"
cd /containers/spring-10k-chat-server
pwd
if echo "${JAR_FILES}" | grep -q "module-websocket"; then
    if docker ps | grep -q "blue"; then
        echo "Docker container with 'blue' is running"

        echo "Switching to 'green'.."
        docker-compose -p websocket-green -f docker-compose-websocket.yaml up -d green-spring-10k-chat-websocket1 green-spring-10k-chat-websocket2 green-spring-10k-chat-websocket3

        while docker ps | grep -q "blue"; do
            echo "Waiting for 'blue' to stop.."
            sleep 1
        done

        echo "Stopping 'blue'.."''
        docker stop blue-spring-10k-chat-websocket1 blue-spring-10k-chat-websocket2 blue-spring-10k-chat-websocket3
    else
        echo "No Docker container with 'green' is running"

        echo "Switching to 'blue'.."
        docker-compose -p websocket-blue -f docker-compose-websocket.yaml up -d blue-spring-10k-chat-websocket1 blue-spring-10k-chat-websocket2 blue-spring-10k-chat-websocket3

        while docker ps | grep -q "green"; do
            echo "Waiting for 'green' to stop.."
            sleep 1
        done

        echo "Stopping 'green'.."
        docker stop green-spring-10k-chat-websocket1 green-spring-10k-chat-websocket2 green-spring-10k-chat-websocket3
    fi
else
    echo "JAR files do not contain the specific string"
fi
pwd
ls