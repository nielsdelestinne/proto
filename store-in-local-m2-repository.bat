REM Install Jar Artifact into the local Maven repository (m2)
mvn install:install-file -Dfile=employment-service-contract/artifacts/employment-service-contract-1.0.0-jar-with-dependencies.jar -DgroupId=be.niedel -DartifactId=employment-service-contract -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true
