#!groovy
node("slave") {
    stage "Get the source"
    
    checkout scm
    
    stage "Build Test and Package"
    
    def mvnHome = tool 'M3'

    def mavencommand = "${mvnHome}/bin/mvn clean license:format test package"
    if (isUnix()) {
        sh(mavencommand) 
    } else {
        bat(mavencommand) 
    }

    stage "Check Tech Debt"

    if (env.QASONAR) {

        println env.QASONAR;
        def sonarcommand = "@\"./../../../tools/hudson.plugins.sonar.SonarRunnerInstallation/Main_Classic/bin/sonar-scanner\""
        withCredentials([[$class: 'StringBinding', credentialsId: env.SonarOAuthCredentianalID, variable: 'SonarOAuth']]) {
            sonarcommand = sonarcommand + " -Dsonar.host.url=http://sonar.silverbulleters.org -Dsonar.login=${env.SonarOAuth}"
        }
        
        def makeAnalyzis = true
        if (env.BRANCH_NAME == "develop") {
            echo 'Analysing develop branch'
        } else if (env.BRANCH_NAME.startsWith("release/")) {
            sonarcommand = sonarcommand + " -Dsonar.branch=${BRANCH_NAME}"
        } else if (env.BRANCH_NAME.startsWith("PR-")) {
            // Report PR issues           
            def PRNumber = env.BRANCH_NAME.tokenize("PR-")[0]
            def gitURLcommand = 'git config --local remote.origin.url'
            def gitURL = ""
            if (isUnix()) {
                gitURL = sh(returnStdout: true, script: gitURLcommand).trim() 
            } else {
                gitURL = bat(returnStdout: true, script: gitURLcommand).trim() 
            }
            def repository = gitURL.tokenize("/")[2] + "/" + gitURL.tokenize("/")[3]
            repository = repository.tokenize(".")[0]
            withCredentials([[$class: 'StringBinding', credentialsId: env.GithubOAuthCredentianalID, variable: 'githubOAuth']]) {
                sonarcommand = sonarcommand + " -Dsonar.analysis.mode=issues -Dsonar.github.pullRequest=${PRNumber} -Dsonar.github.repository=${repository} -Dsonar.github.oauth=${env.githubOAuth}"
            }
        } else {
            makeAnalyzis = false
        }
        if (makeAnalyzis) {
            if (isUnix()) {
                sh '${sonarcommand}'
            } else {
                bat "${sonarcommand}"
            }
        }
    } else {
        echo "QA runner not installed"
    }

    stage "Start UAT Environ"

    def envpath = "./testenv/";
    if (env.ENVPATH){
        srcpath = env.ENVPATH;
    }

    stage "Perform UAT"

    //TODO we need what checks are correct	

    stage "GitHub Publish"

    echo "stable if master, pre-release if have release, nigthbuild if develop"

}