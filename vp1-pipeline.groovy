pipeline {
    agent any
    
     tools {
        nodejs 'NodeJS 22' // Ensure you have a NodeJS tool configured in Jenkins
    }

     environment {
        SSH_KEY_PATH = '/mnt/c/Users/Admin/Downloads/java3.pem' // Update with your actual key path
    }
    
    stages {
        stage('Checkout') {
            steps {
                // git branch: 'main', url: params.REPO_URL
                git branch: 'main', credentialsId: 'Siba', url: 'git@github.com:sibananda485/ug-sap.git'
            }
        }
        
        stage('Build') {
            steps {
                sh 'pwd'
                sh 'npm i'
                sh 'npm run build'
            }
        }
        
        stage('Deploy') {
            steps {
                sshagent(['Java3']) {
                sh 'ssh -o StrictHostKeyChecking=no java3@20.244.110.102 "sudo rm -r /var/www/html/VP1"'
                }
                sh 'scp -o StrictHostKeyChecking=no -i ${SSH_KEY_PATH} -r dist java3@20.244.110.102:/home/java3/VP1'
                
               
            }
        }
        
        stage('Test VM'){
            steps{
                sshagent(['Java3']) {
                sh 'ssh -o StrictHostKeyChecking=no java3@20.244.110.102 "sudo mv /home/java3/VP1 /var/www/html/"'
                sh 'ssh -o StrictHostKeyChecking=no java3@20.244.110.102 "sudo systemctl restart nginx"'
               }
            }
        }
    }
    post {
        success {
             emailext body: '''<html><body><h2>Vendor Portal - Version # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <h2>Deployed on vp1.actifyzone.com</h2>
                        <br/>
                       <h3><b> Check console <a href="$BUILD_URL">output</a> to view full results.</b></h3><br/>
                        <b><i>If you cannot connect to the build server, check the attached logs.</i></b><br/>
                        <br/>
                        --<br/>
                        Following is the last 50 lines of the log.<br/>
                        <br/>
                        <b>--LOG-BEGIN--</b><br/>
                        <pre style='line-height: 22px; display: block; color: #333; font-family: Monaco,Menlo,Consolas,"Courier New",monospace; padding: 10.5px; margin: 0 0 11px; font-size: 13px; word-break: break-all; word-wrap: break-word; white-space: pre-wrap; background-color: #f5f5f5; border: 1px solid #ccc; border: 1px solid rgba(0,0,0,.15); -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px;'>
                        ${BUILD_LOG, maxLines=50, escapeHtml=true}
                        </pre>
                        <b>--LOG-END--</b></body></html>''',
             subject:'Vendor Deployed Successfully - $PROJECT_NAME',
             to: 'abdallah.kammruddin@dextero.in , sibananda.sahu@dextero.in',
             mimeType: 'text/html'
        }
        failure {
            emailext body: '''<html><body><h2>CRM Portal - Build # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                       <b> Check console <a href="$BUILD_URL">output</a> to view full results.</b><br/>
                        <b><i>If you cannot connect to the build server, check the attached logs.</i></b><br/>
                        <br/>
                        --<br/>
                        Following is the last 50 lines of the log.<br/>
                        <br/>
                        <b>--LOG-BEGIN--</b><br/>
                        <pre style='line-height: 22px; display: block; color: #333; font-family: Monaco,Menlo,Consolas,"Courier New",monospace; padding: 10.5px; margin: 0 0 11px; font-size: 13px; word-break: break-all; word-wrap: break-word; white-space: pre-wrap; background-color: #f5f5f5; border: 1px solid #ccc; border: 1px solid rgba(0,0,0,.15); -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px;'>
                        ${BUILD_LOG, maxLines=50, escapeHtml=true}
                        </pre>
                        <b>--LOG-END--</b></body></html>''',
                     subject:'Vendor Deployment failed :$PROJECT_NAME' , 
                     to: 'abdallah.kammruddin@dextero.in , sibananda.sahu@dextero.in',
                     mimeType: 'text/html'
        }
    }
}
