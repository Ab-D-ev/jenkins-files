pipeline {
    agent any
    
    // parameters {
    //     string(name: 'REPO_URL', defaultValue: 'https://github.com/AshishSingh-30/hr', description: 'URL of the Git repository')
    //     // string(name: 'SERVER_NAME', defaultValue: 'app2.actifyzone.com', description: 'Server name for NGINX')
    //     // string(name: 'NGINX_WEB_ROOT', defaultValue: '/home/Python4/jobportal', description: 'NGINX web root directory')
    // }
     tools {
        nodejs 'NodeJS 22' // Ensure you have a NodeJS tool configured in Jenkins
    }

     environment {
        SSH_KEY_PATH = '/mnt/c/Users/Admin/Downloads/Java2_key.pem' // Update with your actual key path
    }
    
    stages {
        stage('Checkout') {
            steps {
                // git branch: 'main', url: params.REPO_URL
                git branch: 'main', credentialsId: 'Siba', url: 'git@github.com:sibananda485/jobportal.git'
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
                sshagent(['Java2']) {
                sh 'ssh -o StrictHostKeyChecking=no Java2@20.235.243.236 "rm -r /home/Java2/job-test"'
                }
                sh 'scp -o StrictHostKeyChecking=no -i ${SSH_KEY_PATH} -r dist Java2@20.235.243.236:/home/Java2/job-test'

               
            }
        }
        
        stage('Test VM'){
            steps{
                sshagent(['Java2']) {
                // sh 'ssh -o StrictHostKeyChecking=no Java2@20.235.243.236 "ls"'
                sh 'ssh -o StrictHostKeyChecking=no Java2@20.235.243.236 "sudo systemctl restart nginx"'
               }
            }
        }
    }
    post {
        success {
             emailext body: '''<html><body><h2>Job Portal Test  - Version # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <h2>Deployed on jobs1.actifyzone.com</h2>
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
             subject:'Job Portal Test Deployed Successfully - $PROJECT_NAME',
             to: 'manju.nishad@dextero.in , abdallah.kammruddin@dextero.in , sibananda.sahu@dextero.in',
             mimeType: 'text/html'
        }
        failure {
            emailext body: '''<html><body><h2>Job Portal Test - Build # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
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
                     subject:'Job Portal Test Deployment failed :$PROJECT_NAME' , 
                     to: 'abdallah.kammruddin@dextero.in , sibananda.sahu@dextero.in , manju.nishad@dextero.in',
                     mimeType: 'text/html'
        }
    }
}
