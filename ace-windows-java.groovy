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
                            sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/ace-windows-java/target/ace-test.war ACEPortal1@98.70.57.5:'"C:/Program Files/Apache Software Foundation/Tomcat 9.0/webapps/"'
                        """


                    

                }
                
            }
        }
    }
    
   post {
        success {
            script {
                emailext body: '''<html><body><h2>Ace Windows Test WAR Deployed - Version # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <h2>Deployed on ace-t.actifyzone.com</h2>
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
                    subject: 'Ace Windows Test WAR Deployed Successfully - $PROJECT_NAME',
                    to: 'omkar.more@dextero.in, abdallah.kammruddin@dextero.in',
                    mimeType: 'text/html'
            }
        }
        failure {
            script {
                emailext body: '''<html><body><h2>Ace Windows Test WAR Failed - Build # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
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
                    subject: 'Ace Windows Test WAR Deployment failed : $PROJECT_NAME',
                    to: 'abdallahq989@gmail.com, omkar.more@dextero.in',
                    mimeType: 'text/html'
            }
        }
    }
}
