def createLambdaStack(String region, String stack, String vpc){
    sh "aws cloudformation --region ${region} validate-template --template-body file://aws-lambda-infra.json"
    sh "aws cloudformation --region ${region} create-stack --stack-name ${stack} --template-body \
        file://aws-lambda-infra.json --parameters ParameterKey=VPCStackName,ParameterValue=${vpc}"
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
                        def policyArn = sh(script: "aws iam list-policies --query 'Policies[?PolicyName==`${params.LAMBDAPOLICY}`].Arn' --output text", returnStdout: true)
                        try {
                           policyArn = sh(script: "aws iam create-policy --policy-name ${params.LAMBDAPOLICY} \
                                --policy-document file://aws-lambda-access-policy.json 'Arn' --output text", returnStdout: true)
                            apply = true
                        } catch(err){
                            apply = false
                            sh "echo updating IAM Policy"
                            sh "aws iam create-policy-version --policy-document file://aws-lambda-access-policy.json \
                                --set-as-default --policy-arn $policyArn"
                        }
                        sh "aws iam attach-role-policy --role-name ${params.LAMBDAROLE} --policy-arn $policyArn"
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
                        def stackName = params.LAMBDASTACK
                        def vpc = params.VPCSTACK
                        try {
                            status = sh(script: "aws cloudformation describe-stacks --region ${params.REGION} \
                                --stack-name ${params.LAMBDASTACK} --query Stacks[0].StackStatus --output text", returnStdout: true)
                            if (status == 'DELETE_FAILED' || 'ROLLBACK_COMPLETE' || 'ROLLBACK_FAILED' || 'UPDATE_ROLLBACK_FAILED') {
                                sh "aws cloudformation delete-stack --stack-name ${params.LAMBDASTACK} --region ${params.REGION}"
                                sh "Waiting for stack to delete...."
                                sh "aws cloudformation --region ${params.REGION} wait stack-delete-complete --stack-name ${params.LAMBDASTACK}"
                                sh 'echo Creating ASG group and configuration for web application after deleting....'
                                createLambdaStack(region, stackName, vpc)
                            }
                            apply = true
                        } catch(err){
                            apply = false
                            sh 'echo Creating Lambda infra for serverless application for first time....'
                            createLambdaStack(region, stackName, vpc)
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