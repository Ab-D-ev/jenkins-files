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
        success {
            script {
                emailext body: '''<html><body><h2>Ace Windows React Deployed - Version # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <h2>Deployed on ace.actifyzone.com</h2>
                        <br/>
                        <h3><b>Check console <a href="$BUILD_URL">output</a> to view full results.</b></h3><br/>
                        <b><i>If you cannot connect to the build server, check the attached logs.</i></b><br/>
                        <br/>
                        --<br/>
                        Following is the last 50 lines of the log.<br/>
                        <br/>
                        <b>--LOG-BEGIN--</b><br/>
                        <pre style='line-height: 22px; display: block; color: #333; font-family: Monaco,Menlo,Consolas,"Courier New",monospace; padding: 10.5px; margin: 0 0 11px; font-size: 13px; word-break: break-all; word-wrap: break-word; white-space: pre-wrap; background-color: #f5f5f5; border: 1px solid #ccc; border: 1px solid rgba(0,0,0,.15); -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px;'>
                        ${BUILD_LOG, maxLines=50, escapeHtml=true}
                        </pre></body></html>''',
                    subject: 'Ace Windows React Deployed Successfully - $PROJECT_NAME',
                    to: 'sibananda.sahu@dextero.in, abdallah.kammruddin@dextero.in',
                    mimeType: 'text/html'
            }
        }
        failure {
            script {
                emailext body: '''<html><body><h2>Ace Windows React Failed - Build # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <b>Check console <a href="$BUILD_URL">output</a> to view full results.</b><br/>
                        <b><i>If you cannot connect to the build server, check the attached logs.</i></b><br/>
                        <br/>
                        --<br/>
                        Following is the last 50 lines of the log.<br/>
                        <br/>
                        <b>--LOG-BEGIN--</b><br/>
                        <pre style='line-height: 22px; display: block; color: #333; font-family: Monaco,Menlo,Consolas,"Courier New",monospace; padding: 10.5px; margin: 0 0 11px; font-size: 13px; word-break: break-all; word-wrap: break-word; white-space: pre-wrap; background-color: #f5f5f5; border: 1px solid #ccc; border: 1px solid rgba(0,0,0,.15); -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px;'>
                        ${BUILD_LOG, maxLines=50, escapeHtml=true}
                        </pre><b>--LOG-END--</b></body></html>''',
                    subject: 'Ace Windows React Deployment failed : $PROJECT_NAME',
                    to: 'abdallahq989@gmail.com, sibananda.sahu@dextero.in',
                    mimeType: 'text/html'
            }
        }
    }

}
