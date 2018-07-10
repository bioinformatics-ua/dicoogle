FROM ubuntu

WORKDIR /root

RUN apt-get update && \
    apt-get install apt-utils -y && \
    apt-get upgrade -y

RUN apt-get install -y software-properties-common build-essential openjdk-8-jre git maven

RUN git clone https://github.com/bioinformatics-ua/dicoogle
RUN ( cd dicoogle && mvn install && ln -s /root/dicoogle/dicoogle/target/dicoogle.jar /root/ )

CMD ["java","-jar","dicoogle.jar","-s"]
