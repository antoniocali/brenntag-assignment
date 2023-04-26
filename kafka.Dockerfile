FROM eclipse-temurin:11.0.16.1_1-jre-jammy

RUN wget https://downloads.apache.org/kafka/3.2.1/kafka_2.13-3.2.1.tgz && tar -xvf kafka_2.13-3.2.1.tgz && rm kafka_2.13-3.2.1.tgz && mkdir -p /usr/local/kafka && mv ./kafka_2.13-3.2.1/* /usr/local/kafka/

WORKDIR /usr/local/kafka

COPY ./kafka_scripts/start.sh /usr/local/kafka/start.sh
COPY ./kafka_scripts/server.properties /usr/local/kafka/config/server.properties

CMD ["/usr/local/kafka/start.sh"]

