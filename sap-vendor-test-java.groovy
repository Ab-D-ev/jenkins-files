pipeline {
    agent any

    tools {
        // Specify the Maven version configured in Global Tool Configuration
        maven 'Maven 3.9.8'
    }

    environment {
        SSH_KEY_PATH = '/mnt/c/Users/Admin/Downloads/java3.pem' // Update with your actual key path
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'Omkar', url: 'git@github.com:OmkarMore-1999/SAPVendorPortal.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Deploy') {
            steps {
               sh 'scp -o StrictHostKeyChecking=no -i ${SSH_KEY_PATH} /var/lib/jenkins/workspace/sap-vendor-test-java/target/VendorPortal.war java3@20.244.110.102:/home/java3/vendor-test.war'
                sshagent(['Java3']) {
                    sh 'ssh -o StrictHostKeyChecking=no java3@20.244.110.102 "sudo mv /home/java3/vendor-test.war /opt/tomcat/webapps/vendor-test.war"'
                }
            }
        }
    }

    post {
        success {
            script {
                emailext body: '''<html><body><h2>SAP Vendor Test WAR Deployed - Version # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
                        <br/>
                        <h2>Deployed on prod-api.actifyzone.com/vendor-test</h2>
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
                    subject: 'SAP Vendor Test WAR Deployed Successfully - $PROJECT_NAME',
                    to: 'omkar.more@dextero.in, abdallah.kammruddin@dextero.in',
                    mimeType: 'text/html'
            }
        }
        failure {
            script {
                emailext body: '''<html><body><h2>SAP Vendor Test WAR Failed - Build # $BUILD_NUMBER - Deployment $BUILD_STATUS.</h2><br/>
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
                    subject: 'SAP Vendor Test WAR Deployment failed : $PROJECT_NAME',
                    to: 'abdallahq989@gmail.com, omkar.more@dextero.in',
                    mimeType: 'text/html'
            }
        }
    }
}
