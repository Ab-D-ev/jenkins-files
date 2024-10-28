pipeline {
    agent any

    tools {
        // Specify the Maven version configured in Global Tool Configuration
        nodejs 'NodeJS 22' 
    }
     environment {
        // SERVER_USER = 'ACEPortal1'
        // SERVER_IP = '98.70.57.5'
        SERVER_PASSWORD = 'Dextero@2022'  // Replace with the actual password
        // FILE_TO_COPY = '/path/to/your/file.txt'  // Replace with the actual file path
        // REMOTE_DEST = 'C:/Program Files/Apache Software Foundation/Tomcat 9.0/webapps' // Replace with the remote destination path
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: 'Siba', url: 'git@github.com:sibananda485/ace-portal.git' 
            }
        }
        
        stage('Build') {
            steps {
                sh 'npm i'
                sh 'npm run build'
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    // Using sshpass to SCP the file with the password
                    sh """
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ACEPortal1@98.70.57.5 'powershell -Command "Remove-Item -Path \'C:/Users/ACEPortal1/Downloads/ACE-PROD/*\' -Recurse -Force"'
                        """
                    sh """
                            sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no -r /var/lib/jenkins/workspace/ace-windows-react-prod/dist ACEPortal1@98.70.57.5:'"C:/Users/ACEPortal1/Downloads/ACE-PROD/"' 
                        """


                    

                }
                
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
