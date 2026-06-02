pipeline {
  agent any

  environment {
    NEXT_TELEMETRY_DISABLED = '1'
  }

  stages {
    stage('Install Frontend Dependencies') {
      steps {
        sh 'npm install'
      }
    }

    stage('Check Formatting') {
      steps {
        sh 'npm run format:check'
      }
    }

    stage('Typecheck Web Apps') {
      steps {
        sh 'npm run typecheck:web'
      }
    }

    stage('Build Web Apps') {
      steps {
        sh 'npm run build:web'
      }
    }

    stage('Verify Backend') {
      steps {
        sh 'mvn -B -f backend/pom.xml verify'
      }
    }

    stage('Validate Docker Compose') {
      steps {
        sh 'docker compose config'
      }
    }
  }

  post {
    always {
      echo 'CI pipeline finished. Review stage output for failures.'
    }
  }
}
