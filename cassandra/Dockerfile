FROM cassandra:3.11.3
RUN echo "authenticator: PasswordAuthenticator" >> /etc/cassandra/cassandra.yaml && \
 sed -i -e 's/AllowAllAuthorizer/org.apache.cassandra.auth.CassandraAuthorizer/g' /etc/cassandra/cassandra.yaml