echo "Waiting for Kafka to come online..."
cub kafka-ready -b kafka:9092 1 20
echo "producing customer data on topic customers"
kafka-console-producer --bootstrap-server kafka:9092 --topic customers --property 'parse.key=true' --property 'key.separator=|' < ./data/customers.json