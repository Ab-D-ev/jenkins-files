pipeline {
    agent any

    tools {
        // Specify the Maven version configured in Global Tool Configuration
        maven 'Maven 3.9.8'
    }

     environment {
        SSH_KEY_PATH = '/mnt/c/Users/Admin/Downloads/Java2_key.pem' // Update with your actual key path
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'manju', url: 'git@github.com:manju35/Job_Portal_PROD.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Deploy') {
            steps {
               sh 'scp -o StrictHostKeyChecking=no -i ${SSH_KEY_PATH} /var/lib/jenkins/workspace/job-java-uat/target/Recruitment.war Java2@20.235.243.236:/home/Java2/job-test.war'
                sshagent(['Java2']) {
                    sh 'ssh -o StrictHostKeyChecking=no Java2@20.235.243.236 "sudo mv /home/Java2/job-test.war /opt/tomcat/webapps/job-test.war "'
                    }

            }
        }
    }

   post {
        success {
             emailext body: '''<html><body><h2>Job UAT WAR Deployed - Version # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <h2>Deployed on apis.actifyzone.com/job-test</h2>
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
             subject:'Job UAT WAR Deployed Successfully - $PROJECT_NAME',
             to: 'manju.nishad@dextero.in , abdallah.kammruddin@dextero.in ',
             mimeType: 'text/html'
        }
        failure {
            emailext body: '''<html><body><h2>Job UAT WAR Failed - Build # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
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
                     subject:'Job UAT WAR Deployment failed :$PROJECT_NAME' , 
                     to: 'abdallah.kammruddin@dextero.in , manju.nishad@dextero.in',
                     mimeType: 'text/html'
        }
    }
}
