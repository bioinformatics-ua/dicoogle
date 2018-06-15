FROM ubuntu

WORKDIR /root

RUN apt-get update && \
    apt-get install apt-utils -y && \
    apt-get upgrade -y

RUN apt-get install -y software-properties-common build-essential openjdk-8-jre git maven curl
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && apt-get install -y nodejs

RUN git clone https://github.com/bioinformatics-ua/dicoogle
RUN ( cd dicoogle && mvn install && ln -s /root/dicoogle/dicoogle/target/dicoogle.jar /root/ )

CMD ["java","-jar","dicoogle.jar","-s"]
