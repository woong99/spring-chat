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

                sh './gradlew --version'
                echo 'Building..'
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
            }
        }
    }
}