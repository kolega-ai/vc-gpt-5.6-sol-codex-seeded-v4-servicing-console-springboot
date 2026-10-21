pipeline {
  agent any
  stages {
    stage('Build and test') { steps { sh './gradlew clean test bootJar --no-daemon' } }
    stage('Reports') { steps { junit allowEmptyResults: false, testResults: '**/build/test-results/test/*.xml' } }
  }
}
