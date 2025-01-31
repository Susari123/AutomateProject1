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
            if (fileExists("target/surefire-reports/testng-results.xml")) {
                echo '✅ Publishing TestNG results...'

                // Publish TestNG results
                publishTestNGResults testResultsPattern: '**/target/surefire-reports/testng-results.xml'
            } else {
                echo '❌ TestNG results file not found!'
                error('TestNG results file is missing.')
            }
        }
    }
}
    }
}
