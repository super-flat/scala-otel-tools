# otel-tools
[![GitHub Workflow Status (branch)](https://img.shields.io/github/actions/workflow/status/super-flat/scala-otel-tools/publish.yml?branch=main&style=flat-square)](https://github.com/super-flat/scala-otel-tools/actions/workflows/publish.yml)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.superflat/otel-tools_2.13/badge.svg)]((https://maven-badges.herokuapp.com/maven-central/io.superflat/otel-tools_2.13))
[![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots]

## Overview
Otel-tools is a set of open-telemetry gRPC interceptors libraries that can be used in any scala gRPC based project.

## Features

- GrpcHeadersInterceptor
- StatusClientInterceptor
- StatusServerInterceptor
- TracedExecutorService

## Usage
```scala
libraryDependencies += "io.superflat" % "otel-tools_2.13" % "0.1.10"
```

## License

This software is licensed under the Apache 2 license, quoted below.

Copyright © 2020 superflat

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0]
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.

[Link-SonatypeSnapshots]: https://oss.sonatype.org/content/repositories/snapshots/io/superflat/otel-tools_2.13/ "Sonatype Snapshots"

[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/oss.sonatype.org/io.superflat/otel-tools_2.13.svg "Sonatype Snapshots"
