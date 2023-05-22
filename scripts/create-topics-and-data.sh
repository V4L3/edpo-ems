echo "Waiting for Kafka to come online..."
cub kafka-ready -b kafka:9092 1 20
# create the game pv_production topic
kafka-topics --create --bootstrap-server kafka:9092 --topic pv_production --replication-factor 1 --partitions 1
# create the energy_consumption topic
kafka-topics --create --bootstrap-server kafka:9092 --topic energy_consumption --replication-factor 1 --partitions 1
# create the customers topic
kafka-topics --create --bootstrap-server kafka:9092 --topic customers --replication-factor 1 --partitions 1
echo "producing customer data on topic customers"
kafka-console-producer --bootstrap-server kafka:9092 --topic customers --property 'parse.key=true' --property 'key.separator=|' < ./data/customers.json
echo "topics and data created"
sleep infinity