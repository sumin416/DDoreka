
pipeline {
	agent none
	options { skipDefaultCheckout(true) }
	stages {
		stage('Build and Test') {
			agent any
			steps {
				sh 'docker build --tag review-server:1.0 ./backend/Review'
				sh 'docker build --tag face-server:1.0 ./backend/Face'
			}
		}
	}
}