docker network create ai-assist

docker run -d --network ai-assist --name cassandra5.0.4  -v C:/other/cassandra:/var/lib/cassandra -p 9042:9042 cassandra:5.0.4