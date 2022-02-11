FROM openjdk:8
RUN apt-get update
RUN apt-get install -y netcat
RUN apt-get install -y jq
RUN apt-get install -y telnet

ENV SCALA_VERSION 2.12.2
ENV SBT_VERSION 1.5.5

RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://repo.scala-sbt.org/scalasbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt -Dsbt.rootdir=true sbtVersion

WORKDIR /SparkService
ADD . /SparkService
RUN sbt assembly
COPY start.sh /usr/bin/SparkService-run.sh
RUN chmod +x /usr/bin/SparkService-run.sh
CMD ["/usr/bin/SparkService-run.sh"]
