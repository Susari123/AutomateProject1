pipeline {
    agent any

    tools {
        maven 'Maven_3.8.6'   // Ensure Maven is installed in Jenkins
        jdk 'JDK_17'          // Ensure JDK 17 is installed in Jenkins
    }

    environment {
        TEST_RESULTS_DIR = 'target/surefire-reports'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out code from Git repository...'
                git branch: 'SouravAuto', credentialsId: 'Susari123-credentials-id', url: 'https://github.com/Susari123/AutomateProject1.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building the project using Maven...'
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo 'Running tests using Maven...'
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }

        stage('Publish Test Results') {
            steps {
                script {
                    echo 'Checking for TestNG results...'
                    // Ensure the TestNG results file exists
                    if (fileExists("${TEST_RESULTS_DIR}/testng-results.xml")) {
                        echo '✅ TestNG results found. Publishing results...'

                        // Use JUnit to publish TestNG results
                        junit '**/target/surefire-reports/testng-results.xml'

                        // Archive all test results for future reference
                        archiveArtifacts artifacts: '**/target/surefire-reports/*', fingerprint: true
                    } else {
                        echo '❌ TestNG results file not found!'
                        error('TestNG results file is missing.')
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo '✅ Pipeline executed successfully.'
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs for details.'
        }
    }
}
