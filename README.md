English | [中文](README_CN.md)

# Nginx Config Analysis

A powerful Nginx configuration file parsing library that parses Nginx config files into Java objects, supporting read, modify, and re-serialization.

[![Maven Central](https://img.shields.io/maven-central/v/ink.icoding/nginx-analysis.svg)](https://central.sonatype.com/artifact/ink.icoding/nginx-analysis)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

## Features

- Complete parsing of all Nginx configuration file elements
- Support for all Nginx block types (http, server, location, upstream, etc.)
- Preserves original format (comments, empty lines, indentation)
- Supports modification and re-serialization
- Type-safe API with specialized methods for different block types
- Zero dependencies, pure Java implementation

## Requirements

- Java 17 or higher

## Installation

### Maven

```xml
<dependency>
    <groupId>ink.icoding</groupId>
    <artifactId>nginx-analysis</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Gradle

```groovy
implementation 'ink.icoding:nginx-analysis:1.0.2'
```

## Quick Start

### 1. Parse Configuration File

```java
import ink.icoding.nginx.core.NginxConfig;
import ink.icoding.nginx.entity.*;

// Parse from string
String nginxConf = """
    http {
        server {
            listen 80;
            server_name example.com;
            location / {
                root /var/www/html;
            }
        }
    }
    """;
NginxConfig config = NginxConfig.parse(nginxConf);

// Parse from file
String content = Files.readString(Path.of("/etc/nginx/nginx.conf"));
NginxConfig config = NginxConfig.parse(content);
```

### 2. Iterate Configuration Items

```java
for (NginxConfItem item : config.getItems()) {
    System.out.println(item.getName());
}
```

### 3. Serialize Back to String

```java
String output = config.toString();
System.out.println(output);
```

## Entity Class Structure

```
NginxConfItem (Interface)
├── NginxInlineConfItem      # Inline config (listen 80;)
├── NginxCommentsConfItem    # Comment (# comment)
├── NginxEmptyLineConfItem   # Empty line
└── NginxBlockConfItem       # Block config (parent class)
    ├── NginxHttpConfItem    # http { ... }
    ├── NginxServerConfItem  # server { ... }
    ├── NginxLocationConfItem # location /path { ... }
    ├── NginxUpstreamConfItem # upstream backend { ... }
    ├── NginxEventsConfItem  # events { ... }
    ├── NginxStreamConfItem  # stream { ... }
    ├── NginxMapConfItem     # map $var $name { ... }
    ├── NginxGeoConfItem     # geo $var { ... }
    ├── NginxIfConfItem      # if ($condition) { ... }
    ├── NginxTypesConfItem   # types { ... }
    └── NginxLimitExceptConfItem # limit_except GET { ... }
```

## Usage Examples

### Example 1: Get All Server Configurations

```java
NginxConfig config = NginxConfig.parse(nginxConf);

// Iterate top-level configurations
for (NginxConfItem item : config.getItems()) {
    if (item instanceof NginxHttpConfItem) {
        NginxHttpConfItem http = (NginxHttpConfItem) item;

        // Get all servers
        for (NginxServerConfItem server : http.getServers()) {
            System.out.println("Server: " + server.getServerNames());
            System.out.println("Ports: " + server.getListenPorts());
            System.out.println("SSL: " + server.isSslEnabled());
        }
    }
}
```

### Example 2: Modify Server Configuration

```java
NginxConfig config = NginxConfig.parse(nginxConf);

// Get first http block
NginxHttpConfItem http = (NginxHttpConfItem) config.getItems().get(0);

// Get first server
NginxServerConfItem server = http.getServers().get(0);

// Modify server_name
server.setServerNames("new-domain.com", "www.new-domain.com");

// Add new listen port
server.addListenPort("8080");

// Output modified configuration
System.out.println(config.toString());
```

### Example 3: Handle Location Configuration

```java
NginxServerConfItem server = ...;

// Get all locations
for (NginxLocationConfItem location : server.getLocations()) {
    System.out.println("Path: " + location.getPath());
    System.out.println("Modifier: " + location.getModifier());
    System.out.println("Is Regex: " + location.isRegex());
    System.out.println("Is Exact: " + location.isExact());
}
```

### Example 4: Configure Upstream

```java
NginxHttpConfItem http = ...;

// Get all upstreams
for (NginxUpstreamConfItem upstream : http.getUpstreams()) {
    System.out.println("Name: " + upstream.getName());
    System.out.println("Method: " + upstream.getLoadBalancingMethod());
    System.out.println("Servers: " + upstream.getServerAddresses());
}

// Create new upstream
NginxUpstreamConfItem newUpstream = new NginxUpstreamConfItem("upstream new_backend {\n}");
newUpstream.addServer("192.168.1.10:8080", "weight=3");
newUpstream.addServer("192.168.1.11:8080");
newUpstream.setLoadBalancingMethod("least_conn");
http.addItem(newUpstream);
```

### Example 5: Use Map Configuration

```java
NginxHttpConfItem http = ...;

for (NginxConfItem item : http.getItems()) {
    if (item instanceof NginxMapConfItem) {
        NginxMapConfItem map = (NginxMapConfItem) item;
        System.out.println("Source: " + map.getSourceVariable());
        System.out.println("Target: " + map.getTargetVariable());
        System.out.println("Entries: " + map.getMapEntries());
    }
}
```

### Example 6: Conditional Judgment (if)

```java
NginxServerConfItem server = ...;

for (NginxConfItem item : server.listSubItems()) {
    if (item instanceof NginxIfConfItem) {
        NginxIfConfItem ifBlock = (NginxIfConfItem) item;
        System.out.println("Condition: " + ifBlock.getCondition());
        System.out.println("Variable: " + ifBlock.getConditionVariable());
        System.out.println("Operator: " + ifBlock.getConditionOperator());
        System.out.println("Value: " + ifBlock.getConditionValue());
    }
}
```

### Example 7: Find Specific Configuration Items

```java
NginxBlockConfItem block = ...;

// Find single configuration item
NginxConfItem listen = block.getItem("listen");
if (listen instanceof NginxInlineConfItem) {
    System.out.println("Listen: " + ((NginxInlineConfItem) listen).getValue());
}

// Find all items with same name
List<NginxConfItem> serverNames = block.getItems("server_name");
for (NginxConfItem item : serverNames) {
    System.out.println("Server Name: " + ((NginxInlineConfItem) item).getValue());
}
```

### Example 8: Add and Delete Configuration Items

```java
NginxServerConfItem server = ...;

// Add new configuration item
server.addItem(new NginxInlineConfItem("worker_connections 1024;"));

// Delete configuration item
server.removeItem(server.getItem("old_setting"));

// Add comment
server.addItem(new NginxCommentsConfItem("# This is a comment"));
```

### Example 9: Stream Configuration (TCP/UDP Proxy)

```java
NginxConfig config = NginxConfig.parse(streamConf);

for (NginxConfItem item : config.getItems()) {
    if (item instanceof NginxStreamConfItem) {
        NginxStreamConfItem stream = (NginxStreamConfItem) item;

        for (NginxServerConfItem server : stream.getServers()) {
            System.out.println("Listen: " + server.getListenPorts());
        }

        for (NginxUpstreamConfItem upstream : stream.getUpstreams()) {
            System.out.println("Upstream: " + upstream.getName());
            System.out.println("Servers: " + upstream.getServerAddresses());
        }
    }
}
```

## Complete Example: Read, Modify, Save

```java
import ink.icoding.nginx.core.NginxConfig;
import ink.icoding.nginx.entity.*;

import java.io.IOException;
import java.nio.file.*;

public class NginxConfigModifier {
    public static void main(String[] args) throws IOException {
        // 1. Read configuration file
        String content = Files.readString(Path.of("/etc/nginx/nginx.conf"));
        NginxConfig config = NginxConfig.parse(content);

        // 2. Modify configuration
        for (NginxConfItem item : config.getItems()) {
            if (item instanceof NginxHttpConfItem) {
                modifyHttpBlock((NginxHttpConfItem) item);
            }
        }

        // 3. Save modified configuration
        Files.writeString(Path.of("/etc/nginx/nginx.conf.new"), config.toString());
    }

    private static void modifyHttpBlock(NginxHttpConfItem http) {
        for (NginxServerConfItem server : http.getServers()) {
            // Modify server_name
            List<String> names = server.getServerNames();
            if (names.contains("old-domain.com")) {
                server.setServerNames("new-domain.com");
            }

            // Add security headers
            server.addItem(new NginxInlineConfItem(
                "add_header X-Frame-Options \"SAMEORIGIN\" always;"));
        }
    }
}
```

## API Reference

### NginxConfig

| Method | Return Type | Description |
|--------|-------------|-------------|
| `parse(String content)` | `NginxConfig` | Static method, parse configuration content |
| `getItems()` | `List<NginxConfItem>` | Get all top-level configuration items |
| `toString()` | `String` | Serialize to configuration string |

### NginxBlockConfItem

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getName()` | `String` | Get block name |
| `getValues()` | `List<String>` | Get block parameters |
| `getFirstValue()` | `String` | Get first parameter |
| `listSubItems()` | `List<NginxConfItem>` | Get sub-configuration items |
| `getItem(String name)` | `NginxConfItem` | Find sub-item by name |
| `getItems(String name)` | `List<NginxConfItem>` | Find all sub-items by name |
| `addItem(NginxConfItem)` | `void` | Add sub-configuration item |
| `removeItem(NginxConfItem)` | `boolean` | Remove sub-configuration item |

### NginxServerConfItem

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getListenPorts()` | `List<String>` | Get listen ports |
| `getServerNames()` | `List<String>` | Get server names |
| `setServerNames(String...)` | `void` | Set server names |
| `getLocations()` | `List<NginxLocationConfItem>` | Get all locations |
| `isSslEnabled()` | `boolean` | Check if SSL is enabled |
| `getRoot()` | `String` | Get root directory |
| `setRoot(String)` | `void` | Set root directory |

### NginxLocationConfItem

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPath()` | `String` | Get match path |
| `getModifier()` | `String` | Get match modifier |
| `isRegex()` | `boolean` | Check if regex match |
| `isExact()` | `boolean` | Check if exact match |
| `isNamed()` | `boolean` | Check if named location |

### NginxUpstreamConfItem

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getServerAddresses()` | `List<String>` | Get all server addresses |
| `addServer(String, String...)` | `void` | Add server |
| `removeServer(String)` | `boolean` | Remove server |
| `getLoadBalancingMethod()` | `String` | Get load balancing method |
| `setLoadBalancingMethod(String)` | `void` | Set load balancing method |

### NginxHttpConfItem

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getServers()` | `List<NginxServerConfItem>` | Get all servers |
| `getUpstreams()` | `List<NginxUpstreamConfItem>` | Get all upstreams |
| `getTypes()` | `NginxTypesConfItem` | Get types block |
| `isGzipEnabled()` | `boolean` | Check if gzip is enabled |

## Supported Nginx Configuration Elements

| Element | Entity Class | Example |
|---------|--------------|---------|
| http | `NginxHttpConfItem` | `http { ... }` |
| server | `NginxServerConfItem` | `server { ... }` |
| location | `NginxLocationConfItem` | `location /api/ { ... }` |
| upstream | `NginxUpstreamConfItem` | `upstream backend { ... }` |
| events | `NginxEventsConfItem` | `events { ... }` |
| stream | `NginxStreamConfItem` | `stream { ... }` |
| map | `NginxMapConfItem` | `map $uri $new { ... }` |
| geo | `NginxGeoConfItem` | `geo $geo { ... }` |
| if | `NginxIfConfItem` | `if ($host = 'example.com') { ... }` |
| types | `NginxTypesConfItem` | `types { ... }` |
| limit_except | `NginxLimitExceptConfItem` | `limit_except GET { ... }` |
| comment | `NginxCommentsConfItem` | `# comment` |
| empty line | `NginxEmptyLineConfItem` | `` |
| inline config | `NginxInlineConfItem` | `listen 80;` |

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

## Contributing

Issues and Pull Requests are welcome!

## Author

- guoshengkai (719348277@qq.com)
