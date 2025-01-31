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
                git branch: 'SouravAuto', credentialsId: 'Susari123-credentials-id', url: 'https://github.com/Susari123/AutomateProject1.git'
            }
        }

        stage('Build') {
            steps {
                script {
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
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }

        stage('Publish TestNG Results') {
            steps {
                script {
                    if (fileExists("${TEST_RESULTS_DIR}/testng-results.xml")) {
                        echo '✅ Publishing TestNG results...'

                        // Use JUnit to process TestNG XML results
                        junit '**/target/surefire-reports/testng-results.xml'

                        // Archive all test reports, including HTML, for debugging
                        archiveArtifacts artifacts: '**/target/surefire-reports/*', fingerprint: true
                    } else {
                        echo '❌ ERROR: TestNG results file not found!'
                        error('Test execution failed: No test results found.')
                    }
                }
            }
        }
    }
}
