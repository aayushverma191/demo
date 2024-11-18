def call(String branch, String repoUrl) {
    pipeline {
        agent any

        stages {
            stage('clone') {
                steps {
                    echo "Cloning repository..."
                    checkout([$class: 'GitSCM',
                              branches: [[name: "*/${branch}"]],
                              userRemoteConfigs: [[url: repoUrl]]])
                }
            }
            stage('test') {
                steps {
                    echo "Running tests..."
                    sh "mvn test"
                }
            }
            stage('dependency') {
                steps {
                    echo "Resolving dependencies..."
                    sh "mvn dependency:resolve"
                    dependencyCheckPublisher(pattern: '*.xml')
                }
            }
            stage('checkstyle') {
                steps {
                    echo "Checking code style..."
                    sh "mvn checkstyle:checkstyle"
                    recordIssues(sourceCodeRetention: 'LAST_BUILD', tools: [checkStyle(pattern: '**/checkstyle-result.xml')])
                }
            }
            stage('code coverage') {
                steps {
                    echo "Generating code coverage report..."
                    sh "mvn jacoco:report"
                    jacoco()
                }
            }
        }
    }
}
