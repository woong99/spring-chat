pipeline {
    agent any
    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }
        stage('Check Changed Modules') {
            steps {
                script {
                    // 변경된 파일 목록 가져오기
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim().split("\n")
                    echo "Changed Files:\n${changedFiles}"

                    // 모듈 리스트 정의
                    def modules = [
                        'module-api',
                        'module-common',
                        'module-domain:domain-mongo',
                        'module-domain:domain-redis',
                        'module-domain:domain-rdb',
                        'module-eureka',
                        'module-security',
                        'module-sse',
                        'module-websocket'
                    ]

                    // 변경된 파일이 속한 모듈 찾기
                    def changedModules = []
                    for (file in changedFiles) {
                        for (module in modules) {
                            if (file.startsWith(module)) {
                                echo "Changed Module: ${module}"
                                changedModules.add(module)
                            }
                        }
                    }

                    // 중복 제거
                    changedModules = changedModules.unique()

                    // 빌드할 모듈이 없으면 종료
                    if (changedModules.isEmpty()) {
                        echo 'No modules to build'
                        currentBuild.result = 'ABORTED'
                        return
                    }

                    // 환경 변수로 전달
                    env.CHANGED_MODULES = changedModules.join(',')
                }
            }
        }
        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew'

                script {
                    def changedModules = env.CHANGED_MODULES.split(',')
                    if ("module-common" in changedModules) {
                        echo "Building module-common.."
                        sh "./gradlew :module-common:buildDependents"

                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    if ("module-security" in changedModules) {
                        echo "Building module-security.."
                        sh "./gradlew :module-security:buildDependents"

                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    for (module in env.CHANGED_MODULES.split(',')) {
                        echo "Building ${module}.."
                        sh "./gradlew :${module}:build"
                    }
                }
            }
        }
        stage('List Built JARs') {
            steps {
                sh 'find . -path "*/build/libs/*.jar" -not -name "*-plain.jar"'

                // 빌드된 JAR 파일 목록을 환경 변수로 전달
                script {
                    def jarFiles = sh(script: 'find . -path "*/build/libs/*.jar" -not -name "*-plain.jar"', returnStdout: true).trim().split("\n")
                    env.JAR_FILES = jarFiles.join(',')
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                sshagent (credentials : ['ssh']) {
                  sh '''
                      for jar in $(find . -path "*/build/libs/*.jar" -not -name "*-plain.jar"); do
                          scp -P 10022 "$jar" root@potatowoong.iptime.org:/containers/spring-10k-chat-server/jar
                      done

                      ssh -p 10022 root@potatowoong.iptime.org

                      if [ "${JAR_FILES}" == *"module-websocket"* ]; then
                          cd /containers/spring-10k-chat-server

                          if [ $(docker ps | grep blue | wc -l) -gt 0 ]; then
                              echo "Docker container with 'blue' is running"
                              docker-compose -f docker-compose-websocket.yaml up -d $( docker-compose -f docker-compose-websocket.yaml config --services | grep "green*")
                              docker-compose -f docker-compose-websocket.yaml down $(docker-compose -f docker-compose-websocket.yaml config --services | grep "blue*")
                          else
                              echo "No Docker container with 'blue' is running"
                              docker-compose -f docker-compose-websocket.yaml up -d $( docker-compose -f docker-compose-websocket.yaml config --services | grep "blue*")
                              docker-compose -f docker-compose-websocket.yaml down $(docker-compose -f docker-compose-websocket.yaml config --services | grep "green*")
                          fi
                      else
                          echo "JAR files do not contain the specific string"
                      fi
                      pwd
                      ls
                  '''
                }
            }
        }
        stage('Clean Jar') {
            steps {
                sh 'rm -rf ./**/build/libs/*.jar'
            }
        }
    }
}