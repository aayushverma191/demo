def call(String branch, String repoUrl) {
    checkout([$class: 'GitSCM',
              branches: [[name: "*/${branch}"]],
              userRemoteConfigs: [[url: repoUrl]]])
}
