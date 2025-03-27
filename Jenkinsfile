pipeline {

    triggers {
        githubPush()
    }

    agent any

    tools {
        jdk 'jdk21'
    }

   environment {
    DOCKER_CREDENTIALS = credentials('docker-credentials')
    VERSION = "${env.BUILD_NUMBER}"
    DOCKER_USERNAME = "aliaksandrshydlouski"
    SERVICES = "cloud-service,gateway-service,notification-service"
}

     options {
        githubProjectProperty(
            projectUrlStr: 'https://github.com/bitesaitzz/quickcloud/'
        )
    }
    stages {
        stage('Setup Tools') {
            steps {
                script {
                    deleteDir()
                    sh '''
                        mkdir -p ~/bin
                        curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                        chmod +x kubectl
                        mv kubectl ~/bin/
                        export PATH=$PATH:~/bin
                        ~/bin/kubectl version --client

                        curl -sL https://github.com/digitalocean/doctl/releases/download/v1.102.0/doctl-1.102.0-linux-amd64.tar.gz | tar -xzv
                        mv doctl ~/bin/
                        chmod +x ~/bin/doctl
                        ~/bin/doctl version
                    '''
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'GitHub-credentials',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASS'
                    )]) {
                        sh """
                            git config --global user.name "Jenkins"
                            git config --global user.email "jenkins@example.com"
                            git clone https://${GIT_USER}:${GIT_PASS}@github.com/bitesaitzz/quickcloud.git .
                        """
                    }
                }
            }
        }

        stage('Detect Changes') {

    steps {
        script {
            def changedServices = []
            def serviceList = env.SERVICES.split(',')

            for (service in serviceList) {
                def hasChanges = sh(
                    script: "git diff --name-only HEAD~1 HEAD | grep '^${service}/'",
                    returnStatus: true
                ) == 0

                if (hasChanges) {
                    changedServices.add(service)
                }
            }


            if (changedServices.isEmpty()) {
                echo "No changes detected in services. Skipping build."
                currentBuild.result = 'SUCCESS'

                return
            }

            env.CHANGED_SERVICES = changedServices.join(',')
            echo "Services to build: ${env.CHANGED_SERVICES}"
        }
    }
}

        stage('Build Services') {
            when {
        expression {
            return env.CHANGED_SERVICES != null && !env.CHANGED_SERVICES.isEmpty()
        }
    }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')

                    for (service in services) {
                        dir(service) {
                            sh 'java -version'
                            sh './mvnw clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build and Push Docker Images') {
            when {
        expression {
            return env.CHANGED_SERVICES != null && !env.CHANGED_SERVICES.isEmpty()
        }
    }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    def dockerRegistry = 'https://index.docker.io/v1/'

                    docker.withRegistry(dockerRegistry, 'docker-credentials') {
                for (service in services) {
                    dir(service) {
                        def dockerImage = "${DOCKER_USERNAME}/${service}:2.${env.VERSION}"
                        def customImage = docker.build(dockerImage)

                        customImage.push()
                        customImage.push('latest')


                        def oldVersion = "2.${env.BUILD_NUMBER.toInteger() - 2}"
                        try {
                            def oldImage = docker.image("${DOCKER_USERNAME}/${service}:${oldVersion}")
                            oldImage.pull()
                            oldImage.remove()
                            echo "Successfully removed old image ${DOCKER_USERNAME}/${service}:${oldVersion}"
                        } catch (Exception e) {
                            echo "Could not remove old image ${DOCKER_USERNAME}/${service}:${oldVersion}: ${e.getMessage()}"
                        }
                    }
                }
            }
                }
            }
        }

        stage('Update Kubernetes') {
            when {
        expression {
            return env.CHANGED_SERVICES != null && !env.CHANGED_SERVICES.isEmpty()
        }
    }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')

                    withCredentials([
                        string(credentialsId: 'do-access-token', variable: 'DO_ACCESS_TOKEN'),
                        usernamePassword(
                            credentialsId: 'GitHub-credentials',
                            usernameVariable: 'GIT_USER',
                            passwordVariable: 'GIT_PASS'
                        )
                    ]) {
                        for (service in services) {
                            def k8sDir = "k8s"
                            def deploymentFile = "${k8sDir}/${service}-deployment.yaml"
                            def imageName = "${DOCKER_USERNAME}/${service}:2.${env.VERSION}"
                            def currentVersion = "2.${env.VERSION}"
                            def oldVersion = "2.${env.BUILD_NUMBER.toInteger() - 2}"

                            if (!fileExists(deploymentFile)) {
                                error "Deployment file ${deploymentFile} not found in k8s directory!"
                            }


                            sh """
                                sed -i "s|image: ${DOCKER_USERNAME}/${service}:.*|image: ${imageName}|g" ${deploymentFile}
                            """


                            sh """
                                git config user.name "Jenkins"
                                git config user.email "jenkins@example.com"
                                git add ${deploymentFile}
                                git commit -m "Update ${service} to version 2.${env.VERSION} [skip ci]"
                                git push https://${GIT_USER}:${GIT_PASS}@github.com/bitesaitzz/quickcloud.git HEAD:main
                            """


                            sh """
                                ~/bin/doctl auth init --access-token ${DO_ACCESS_TOKEN}

                                ~/bin/kubectl --validate=false apply -f k8s/${service}-deployment.yaml
                                ~/bin/kubectl rollout status deployment/${service}
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}