pipeline {
    agent any

    tools {
        maven 'Maven_3.8.6'   // Ensure Maven is installed in Jenkins
        jdk 'JDK_17'          // Ensure JDK 17 is installed in Jenkins
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Clone your GitHub repository
                git branch: 'main', credentialsId: 'Susari123-credentials-id', url: 'https://github.com/Susari123/AutomateProject1.git'
            }
        }

        stage('Build') {
            steps {
                // Clean and compile the Maven project
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                // Execute tests using Maven
                sh 'mvn test'
            }
        }

        stage('Publish Results') {
            steps {
                // Publish TestNG results in Jenkins
                publishTestNGResults testResultsPattern: '**/test-output/testng-results.xml'
            }
        }
    }
}
