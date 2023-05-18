docker-compose exec kafka bash -c "
  ../bin/kafka-console-producer \
  --bootstrap-server kafka:9092 \
  --topic customers \
  --property 'parse.key=true' \
  --property 'key.separator=|' < ../data/customers.json"


