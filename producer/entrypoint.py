import sys
import socket
import uuid

from typing import AnyStr, List
from confluent_kafka import Producer


def main(args: List[AnyStr]) -> None:
    topic = args[1]
    conf = {
        "bootstrap.servers": "localhost:9092",
        "client.id": socket.gethostname(),
    }

    producer = Producer(conf)

    with open("mock_data/apache_mock_logs.txt", "r") as logs:
        log_lines = logs.readlines()

        for log_line in log_lines:
            key = str(uuid.uuid4()).encode("utf-8")
            log_as_bytes = log_line.encode("utf-8")

            producer.produce(topic, key=key, value=log_as_bytes)

    producer.flush()


if __name__ == "__main__":
    sys.exit(main(sys.argv))
