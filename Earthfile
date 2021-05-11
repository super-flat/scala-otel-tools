FROM busybox:1.32

all:
    BUILD +test-all
    BUILD +publish

sbt:
    FROM openjdk:11-jdk-stretch

    USER root

    # create directories
    RUN mkdir /sbt && chmod 777 /sbt
    RUN mkdir /logs && chmod 777 /logs

    # Install sbt
    ARG SBT_VERSION=1.5.2
    ARG SBT_URL="https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz"

    # Install sbt, add symlink
    RUN \
      curl -fsL "$SBT_URL" | tar xfz - -C /usr/share && \
      chown -R root:root /usr/share/sbt && \
      chmod -R 755 /usr/share/sbt && \
      chmod +x /usr/share/sbt && \
      ln -s /usr/share/sbt/bin/sbt /usr/local/bin/sbt

    # Switch working directory
    WORKDIR /sbt

    # This triggers a bunch of useful downloads.
    RUN sbt sbtVersion

    # install docker tools
    # https://docs.docker.com/engine/install/debian/
    RUN apt-get remove -y docker docker-engine docker.io containerd runc || true

    RUN apt-get update

    RUN apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg-agent \
        software-properties-common

    RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    RUN echo \
        "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian \
        $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

    RUN apt-get update
    RUN apt-get install -y docker-ce docker-ce-cli containerd.io

dependencies:
    # copy relevant files in, save as a base image
    FROM +sbt

    # create user & working dir for sbt
    ARG BUILD_DIR="/build"

    USER root

    RUN mkdir $BUILD_DIR && \
        chmod 777 /$BUILD_DIR

    WORKDIR $BUILD_DIR

    # copy configurations
    COPY .scalafmt.conf build.sbt .
    COPY --dir project .

    # clean & install dependencies
    RUN sbt clean cleanFiles update

code:
    FROM +dependencies
    # copy proto definitions & generate
    COPY --dir proto .
    # copy code
    COPY --dir code .

test-all:
    FROM +code

    USER root

    RUN sbt coverage test coverageReport

publish:
    RUN --push \
        --secret PGP_PASSPHRASE=+secrets/PGP_PASSPHRASE \
        --secret PGP_SECRET=+secrets/PGP_SECRET \
        --secret SONATYPE_PASSWORD=+secrets/SONATYPE_PASSWORD \
        --secret SONATYPE_USERNAME=+secrets/SONATYPE_USERNAME \
        sbt ci-release
