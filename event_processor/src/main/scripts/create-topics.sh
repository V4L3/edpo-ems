echo "Waiting for Kafka to come online..."
cub kafka-ready -b kafka:9092 1 20
kafka-topics --create --bootstrap-server kafka:9092 --topic pv_production --replication-factor 1 --partitions 1
kafka-topics --create --bootstrap-server kafka:9092 --topic energy_consumption --replication-factor 1 --partitions 1
sleep infinity