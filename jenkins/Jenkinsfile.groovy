def createLambdaStack(String region, String stack, String vpc, String s3){
    sh "aws cloudformation --region ${region} validate-template --template-body file://aws-lambda-infra.json"
    sh "aws cloudformation --region ${region} create-stack --stack-name ${stack} --template-body \
        file://aws-lambda-infra.json --parameters ParameterKey=VPCStackName,ParameterValue=${vpc} \
        ParameterKey=S3Stack,ParameterValue=${s3}"
    sh "aws cloudformation --region ${region} wait stack-create-complete --stack-name ${stack}"
    sh "aws cloudformation --region ${region} describe-stack-events --stack-name ${stack} \
        --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
        --output table"
}

def createS3Stack(String region, String stack, String vpc){
    sh "aws cloudformation --region ${region} validate-template --template-body file://aws-lambda-deploy-bucket.json"
    sh "aws cloudformation --region ${region} create-stack --stack-name ${stack} --template-body file://aws-lambda-deploy-bucket.json"
    sh "aws cloudformation --region ${region} wait stack-create-complete --stack-name ${stack}"
    sh "aws cloudformation --region ${region} describe-stack-events --stack-name ${stack} \
        --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
        --output table"
}

pipeline {
    agent any
    options {
        timestamps()
    }
    parameters {
        string(name: 'LAMBDAROLE', defaultValue: 'lambda-access-role', description: 'Name of VPC Created')
        string(name: 'LAMBDAPOLICY', defaultValue: 'lambda-access-policy', description: 'Name of VPC Created')
        string(name: 'REGION', defaultValue: 'us-east-1', description:'worspace to use in Terraform')
        string(name: 'ACC_NUM', defaultValue: '', description:'worspace to use in Terraform')
        string(name: 'LAMBDASTACK', defaultValue: 'lambda-by-vivek', description:'worspace to use in Terraform')
        string(name: 'VPCSTACK', defaultValue: 'vpc-subnet-network-by-vivek', description:'worspace to use in Terraform')
        string(name: 'DBSTACK', defaultValue: 'rds-by-vivek', description:'RDS for Web Server')
        string(name: 'S3STACK', defaultValue: 's3-by-vivek', description:'RDS for Web Server')
    }
    stages {
        stage('lambda-role'){
            steps {
                dir('cloudformation/access/lambda-role-policy/'){
                    script {
                        def apply = true
                        def status = null
                        try {
                            sh "aws iam get-role --role-name ${params.LAMBDAROLE}"
                            apply = true
                        } catch(err){
                             apply = false
                             sh "echo Creating IAM Role"
                             sh "aws iam create-role --role-name ${params.LAMBDAROLE} \
                                 --assume-role-policy-document file://aws-lambda-role.json"
                        }
                        if(apply){
                            try {
                                 sh "echo Stack exists, attempting update..."
                                 sh "aws iam update-assume-role-policy --role-name ${params.LAMBDAROLE} \
                                    --policy-document file://aws-lambda-role.json"
                            } catch(err){
                                sh "echo Finished create/update - no updates to be performed"
                            }
                        }
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
        stage('lambda-policy'){
            steps {
                dir('cloudformation/access/lambda-role-policy/'){
                    script {
                        def apply = true
                        def policyArn = null
                        try {
                            policyArn = sh(script: "aws iam create-policy --policy-name ${params.LAMBDAPOLICY} \
                                --policy-document file://aws-lambda-access-policy.json --query 'Arn' --output text", returnStdout: true)
                            apply = true
                        } catch(err){
                            apply = false
                            sh "echo Policy already exist updating policy...."
                            sh "aws iam create-policy-version --policy-document file://aws-lambda-access-policy.json \
                                --set-as-default --policy-arn $policyArn"
                        }
                        sh "aws iam attach-role-policy --role-name ${params.LAMBDAROLE} --policy-arn $policyArn"
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
        stage('Code-Deploy-Bucket'){
            steps {
                dir('cloudformation/lambda/'){
                    script {
                        def apply = true
                        def status = null
                        def region = params.REGION
                        def s3StackName = params.S3STACK
                        def vpc = params.VPCSTACK
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                                --stack-name ${params.S3STACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            if (status == 'DELETE_FAILED' || 'ROLLBACK_COMPLETE' || 'ROLLBACK_FAILED' || 'UPDATE_ROLLBACK_FAILED') {
                                deleteS3Stack(region, s3StackName)
                                apply = flase
                                createS3Stack(region, s3StackName, vpc)
                            }
                        } catch(err){
                            if(apply){
                                sh 'echo Creating S3 Bucket for first time....'
                                createS3Stack(region, s3StackName, vpc)
                            }
                        }
                        sh "aws s3 --region $region cp $WORKSPACE/SampleApp_Linux.zip s3://double-digit-devl/"
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
        stage('lambda-infra'){
            steps {
                dir('cloudformation/lambda/'){
                    script {
                        def apply = true
                        def status = null
                        def region = params.REGION
                        def lambdaStackName = params.LAMBDASTACK
                        def vpc = params.VPCSTACK
                        def s3 = params.S3STACK
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                                --stack-name ${params.LAMBDASTACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            if (status == 'DELETE_FAILED' || 'ROLLBACK_COMPLETE' || 'ROLLBACK_FAILED' || 'UPDATE_ROLLBACK_FAILED') {
                                deleteLambdaStack(region, lambdaStackName, vpc, s3)
                                sh 'echo Creating Lambda, S3, SQS for serverless computing after deleting....'
                                createLambdaStack(region, lambdaStackName, vpc, s3)
                                apply = false
                            }
                        } catch(err){
                            if(apply){
                                sh 'echo Creating Lambda infra for serverless application for first time....'
                                createLambdaStack(region, lambdaStackName, vpc, s3)
                                apply = false
                            }
                        }
                        if(apply){
                            try {
                                sh "echo Stack exists, attempting update..."
                                sh "aws cloudformation --region ${params.REGION} update-stack --stack-name \
                                    ${params.LAMBDASTACK} --template-body file://aws-lambda-infra.json \
                                    --parameters ParameterKey=VPCStackName,ParameterValue=${params.VPCSTACK}"
                            } catch(err){
                                sh "echo Finished create/update - no updates to be performed"
                            }
                        }
                        sh "echo Finished create/update successfully!"
                    }
                }
            }
        }
    }
}

def deleteLambdaStack(String region, String lambdaStackName) {
    sh "aws cloudformation delete-stack --stack-name ${lambdaStackName} --region ${region}"
    sh 'echo Waiting for stack to delete....'
    sh "aws cloudformation --region ${region} wait stack-delete-complete --stack-name ${lambdaStackName}"
}

def deleteS3Stack(String region, String s3StackName) {
    sh "aws cloudformation delete-stack --stack-name ${s3StackName} --region ${region}"
    sh 'echo Waiting for stack to delete....'
    sh "aws cloudformation --region ${region} wait stack-delete-complete --stack-name ${s3StackName}"
    sh 'echo Creating S3 bucket for code deployment after deleting....'
}