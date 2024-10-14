pipeline {
    agent any

    tools {
        // Specify the Maven version configured in Global Tool Configuration
        maven 'Maven 3.9.8'
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
                git branch: 'main', credentialsId: 'Omkar', url: 'git@github.com:OmkarMore-1999/AcePortalAPI.git' 
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    // Using sshpass to SCP the file with the password
                    // sh '''
                    //     sshpass -p "${SERVER_PASSWORD}" scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/sap-vendor-java-test/target/ROOT.war ACEPortal1@98.70.57.5:"C:/Program\ Files/Apache\ Software\ Foundation/Tomcat\ 9.0/webapps/"
                    //     '''
                    sh """
                            sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/sap-vendor-java-test/target/ROOT.war ACEPortal1@98.70.57.5:\"C:/Program\\ Files/Apache\\ Software\\ Foundation/Tomcat\\ 9.0/webapps/\"
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
