FROM python:3.10.6-slim-bullseye as build

ENV STATIC_LIB_libzstd=/usr/lib/aarch64-linux-gnu/libzstd.a
RUN apt-get update && apt-get install -y wget python3-dev build-essential cmake ninja-build libssl-dev zlib1g-dev libsasl2-dev libzstd-dev pkg-config software-properties-common git curl zip unzip tar
RUN wget https://github.com/edenhill/librdkafka/archive/refs/tags/v1.9.2.tar.gz && tar -xzf v1.9.2.tar.gz && cd librdkafka-1.9.2 && ./configure --enable-static --install-deps --source-deps-only &&  make && make install && cd .. && rm v1.9.2.tar.gz && rm -r librdkafka-1.9.2

COPY order-generator/requirements.txt /usr/app/
WORKDIR /usr/app
RUN python3 -m pip install -r requirements.txt

FROM python:3.10.6-slim-bullseye as main

COPY order-generator/*.py /usr/app/
COPY data/Customers.csv /usr/app/data/Customers.csv
COPY data/Products.csv /usr/app/data/Products.csv
COPY --from=build /usr/local/lib /usr/local/lib
COPY --from=build /usr/lib/python3/dist-packages /usr/lib/python3/dist-packages

RUN apt update
RUN apt install -y libsasl2-dev

WORKDIR /usr/app

ENV LD_LIBRARY_PATH=/usr/local/lib

ENTRYPOINT [ "python3" ]

CMD ["/usr/app/main.py", "--customers", "/usr/app/data/Customers.csv", "--products", "/usr/app/data/Products.csv"]
