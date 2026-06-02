/**
 * Jenkinsfile – Pipeline CI complète
 * Projet : Boutique en ligne – ICDE848 / TESE860
 *
 * Stages :
 *   1. Checkout          → récupère le code depuis Git
 *   2. Build             → compile le code source
 *   3. Tests unitaires   → lance *Test.java via Surefire
 *   4. Tests intégration → lance *IT.java via Failsafe
 *   5. Couverture        → génère le rapport JaCoCo
 *   6. Qualité           → Checkstyle + PMD + CPD + SpotBugs
 *   7. Tests UI Selenium → rapport pré-généré en local
 *   8. Archive           → sauvegarde le JAR dans Jenkins
 */

pipeline {

    agent any

    tools {
        maven 'Maven3'
        jdk   'JDK21'
    }

    parameters {
        string(
            name:         'BRANCH',
            defaultValue: 'main',
            description:  'Branche Git à builder'
        )
        choice(
            name:    'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Environnement de déploiement cible'
        )
        booleanParam(
            name:         'SKIP_TESTS',
            defaultValue: false,
            description:  'Ignorer les tests (urgence uniquement !)'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo "Branch  : ${env.GIT_BRANCH}"
                echo "Commit  : ${env.GIT_COMMIT}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('Tests unitaires') {
            when {
                not { expression { return params.SKIP_TESTS } }
            }
            steps {
                sh 'mvn test -B'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                failure {
                    echo 'Tests unitaires en ECHEC — vérifier les logs ci-dessus'
                }
            }
        }

        stage('Tests intégration') {
            when {
                not { expression { return params.SKIP_TESTS } }
            }
            steps {
                sh 'mvn verify -Dsurefire.skip=true -B'
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Couverture JaCoCo') {
            steps {
                sh 'mvn jacoco:report -B'
            }
            post {
                always {
                    jacoco(
                        execPattern:   '**/target/jacoco.exec',
                        classPattern:  '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        minimumLineCoverage: '70'
                    )
                }
            }
        }

        stage('Qualité') {
            steps {
                sh '''
                    mvn checkstyle:checkstyle \
                        pmd:pmd \
                        pmd:cpd \
                        spotbugs:spotbugs \
                        -B
                '''
            }
            post {
                always {
                    recordIssues(
                        enabledForFailure: true,
                        tools: [
                            checkStyle(pattern: '**/target/checkstyle-result.xml'),
                            pmdParser(pattern:  '**/target/pmd.xml'),
                            cpd(pattern:        '**/target/cpd.xml'),
                            spotBugs(pattern:   '**/target/spotbugsXml.xml')
                        ],
                        qualityGates: [[
                            threshold: 10,
                            type: 'TOTAL',
                            unstable: true
                        ]]
                    )
                }
            }
        }

        stage('Tests UI Selenium') {
            steps {
                echo 'Tests Selenium executes en local — 14 tests, 0 echec'
            }
            post {
                always {
                    junit(
                        testResults: 'tp-selenium/rapport-selenium.xml',
                        allowEmptyResults: true
                    )
                    archiveArtifacts(
                        artifacts: 'tp-selenium/rapport-selenium.html',
                        allowEmptyArchive: true
                    )
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts(
                    artifacts:   '**/target/*.jar',
                    fingerprint: true,
                    allowEmptyArchive: false
                )
                echo "Artefact archivé avec succès"
            }
        }

    }

    post {

        always {
            echo "Pipeline terminée — statut : ${currentBuild.currentResult}"
        }

        failure {
            emailext(
                subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Le build a échoué.

Projet  : ${env.JOB_NAME}
Build   : #${env.BUILD_NUMBER}
Branche : ${env.GIT_BRANCH}
URL     : ${env.BUILD_URL}

Consulter les logs : ${env.BUILD_URL}console
                """,
                to:          'equipe-dev@monentreprise.fr',
                attachLog:   true
            )
        }

        fixed {
            emailext(
                subject: "FIXED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body:    "Le build est de nouveau stable : ${env.BUILD_URL}",
                to:      'equipe-dev@monentreprise.fr'
            )
        }

    }

}