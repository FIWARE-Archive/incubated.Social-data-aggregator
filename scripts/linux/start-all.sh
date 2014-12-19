echo "Called SDA start-all.."

echo "loading env.."
./start-spark-env.sh

echo "starting tw-connector.."
./connector-tw/start-tw-connector.sh ./connector-tw

echo "End"