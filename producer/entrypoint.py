import sys
import socket
import random
import time

from confluent_kafka import Producer
from concurrent.futures import ThreadPoolExecutor
from faker import Faker

faker = Faker()
KAFKA_TOPIC = sys.argv[1]


def generate_log():
    ip = faker.ipv4()
    method = faker.http_method()
    url = faker.url()
    status = faker.http_status_code()
    size = faker.random_int(200, 5000)
    referer = faker.url()
    user_agent = faker.user_agent()
    now = faker.date_time_this_decade()

    log_entry = f'{ip} - - [{now}] "{method} {url} HTTP/1.1" {status} {size} "{referer}" "{user_agent}"'
    encoded_log = log_entry.encode("utf-8")

    return encoded_log


def worker(producer):
    while True:
        delay = random.uniform(0.005, 0.025)
        data = generate_log()

        print("SEND", data)

        try:
            producer.produce(KAFKA_TOPIC, value=data)
            producer.flush()
        except Exception as e:
            print(e)

        time.sleep(delay)


def main():
    conf = {
        "bootstrap.servers": "localhost:9092",
        "client.id": socket.gethostname(),
    }

    producer = Producer(conf)

    with ThreadPoolExecutor(max_workers=5) as executor:
        for _ in range(5):
            executor.submit(worker, producer)


if __name__ == "__main__":
    sys.exit(main())
