web: target/universal/stage/bin/extreme-play -Dhttp.port=$PORT


web: bin/myapp -Dhttp.port=${PORT} ${JAVA_OPTS} -DapplyEvolutions.default=true -Ddb.default.driver=org.postgresql.Driver -Ddb.default.url=${DATABASE_URL}
