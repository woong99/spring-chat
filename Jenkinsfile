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

                    // module-common은 모든 모듈의 의존성이므로 전부 빌드
                    if ("module-common" in changedModules) {
                        echo "Building module-common.."
                        sh "./gradlew :module-common:buildDependents"

                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    // module-security는 모든 모듈의 의존성이므로 전부 빌드
                    if ("module-security" in changedModules) {
                        echo "Building module-security.."
                        sh "./gradlew :module-security:buildDependents"

                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    // 변경된 모듈만 빌드
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
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                sshagent (credentials : ['ssh']) {
                  sh '''
                      for jar in $(find . -path "*/build/libs/*.jar" -not -name "*-plain.jar"); do
                          scp -P 10022 "$jar" root@potatowoong.iptime.org:/containers/spring-10k-chat-server/jar
                      done

                      scp -P 10022 deploy.sh root@potatowoong.iptime.org:/containers/spring-10k-chat-server/deploy.sh
                      ssh -p 10022 root@potatowoong.iptime.org << EOF
                      JAR_FILES="$JAR_FILES" bash /containers/spring-10k-chat-server/deploy.sh
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